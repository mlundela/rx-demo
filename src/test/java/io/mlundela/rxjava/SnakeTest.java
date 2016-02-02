package io.mlundela.rxjava;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.inOrder;

public class SnakeTest {


    @Mock
    Observer<Object> observer;
    @Mock
    Observer<Long> observer2;

    TestScheduler scheduler;
    Snake app;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        scheduler = new TestScheduler();
        app = new Snake();
    }

    @Test
    public void testExercise1() throws Exception {
        TestSubscriber<Direction> subscriber = new TestSubscriber<>();
        Observable<KeyEvent> keyEvents = Observable.from(
                Arrays.asList(
                        getKeyEvent(KeyCode.UP),
                        getKeyEvent(KeyCode.LEFT),
                        getKeyEvent(KeyCode.RIGHT),
                        getKeyEvent(KeyCode.RIGHT),
                        getKeyEvent(KeyCode.W),
                        getKeyEvent(KeyCode.DOWN)
                )
        );
        app.getDirections(keyEvents).subscribe(subscriber);
        subscriber.assertNoErrors();
        subscriber.assertReceivedOnNext(Arrays.asList(
                Direction.UP,
                Direction.LEFT,
                Direction.RIGHT,
                Direction.RIGHT,
                Direction.DOWN
        ));
    }

    @Test
    public void testExercise2() throws Exception {

        Observable<Long> ticks = app.getTicks(scheduler);
        Subscription sub = ticks.subscribe(observer);

        verify(observer, never()).onNext(0L);
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        scheduler.advanceTimeTo(160, TimeUnit.MILLISECONDS);

        InOrder inOrder = inOrder(observer);
        inOrder.verify(observer, times(1)).onNext(0L);
        inOrder.verify(observer, times(1)).onNext(1L);
        inOrder.verify(observer, never()).onNext(2L);
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        sub.unsubscribe();
        scheduler.advanceTimeTo(320, TimeUnit.MILLISECONDS);
        verify(observer, never()).onNext(2L);
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));
    }

    @Test
    public void testExercise3() throws Exception {

        Observable<Direction> directions = Observable
                .interval(150, 150, TimeUnit.MILLISECONDS, scheduler)
                .map(i -> i == 0 ? Direction.UP : Direction.DOWN);
        Observable<Long> ticks = app.getTicks(scheduler);
        Observable<Direction> directionTicks = app.getDirectionTicks(ticks, directions);

        Subscription sub = directionTicks.subscribe(observer);

        scheduler.advanceTimeTo(320, TimeUnit.MILLISECONDS);

        InOrder inOrder = inOrder(observer);
        inOrder.verify(observer, times(3)).onNext(Direction.UP);
        inOrder.verify(observer, times(2)).onNext(Direction.DOWN);
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Throwable.class));

        sub.unsubscribe();
    }


//    @Test
//    public void testExercise4() throws Exception {
//
//        TestSubscriber<Snake.State> subscriber = new TestSubscriber<>();
//        Observable<Direction> directionTicks = Observable.from(
//                Arrays.asList(
//                        Direction.UP,
//                        Direction.LEFT,
//                        Direction.DOWN
//                )
//        );
//        app.getStateObservable(directionTicks).subscribe(subscriber);
//        subscriber.assertNoErrors();
//
//        Snake.State s0 = app.initialState();
//        Snake.State s1 = app.updateState(s0, Direction.UP);
//        Snake.State s2 = app.updateState(s1, Direction.LEFT);
//        Snake.State s3 = app.updateState(s2, Direction.DOWN);
//
//        subscriber.assertReceivedOnNext(Arrays.asList(s0, s1, s2, s3));
//    }

    private KeyEvent getKeyEvent(KeyCode up) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", up, false, false, false, false);
    }
}
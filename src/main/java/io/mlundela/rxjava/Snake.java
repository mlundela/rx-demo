package io.mlundela.rxjava;

import com.google.common.collect.ImmutableList;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Snake extends Application {

    final int size = 600;
    final int radius = 30;

    private Stage stage;
    private GraphicsContext gc;


    /**
     * Bonus exercise:
     * Display a 'GAME OVER' message when the snake collides with its self,
     * and start a new game when a new arrow key is pressed.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        setupScene(primaryStage);
        states().forEach(this::drawScene);
    }

    private Observable<KeyEvent> getKeyEvents() {
        return Observable.create(subscriber ->
                stage.addEventHandler(KeyEvent.KEY_PRESSED, subscriber::onNext));
    }

    /**
     * Exercise 1:
     * Create an observable that emits Directions.
     *
     * key-pressed: ---KEY_UP-----Ø--X---Y---------KEY_LEFT-----KEY_DOWN------------>
     * directions:  ------UP-------------------------LEFT----------DOWN------------>
     *
     * @return Observable that emits key events.
     */
    Observable<Direction> getDirections(Observable<KeyEvent> keyEvents) {
        return Observable.empty();
    }

    /**
     * Exercise 2:
     * Create an observable that emits a new tick every 80 millisecond.
     *
     *         0:08  0:16  0:24  0:32  0:40
     * ticks: --0-----1-----2-----3-----4--->
     *
     * @return Ticks
     */
    Observable<Long> getTicks(Scheduler scheduler) {
        return Observable.empty();
    }

    /**
     * Exercise 3:
     * Combine the output from exercise 1 and 2 to create an observable
     * that emits direction ticks.
     *
     * ticks:      --0-----1-----2-----3-----4-----5-----6-----7----->
     *
     * directions: ----UP-----------------------LEFT----------------->
     *
     * result:     --------UP----UP----UP----UP--LEFT---LEFT--LEFT--->
     *
     * @return Direction ticks
     */
    Observable<Direction> getDirectionTicks(Observable<Long> ticks, Observable<Direction> directions) {
        return Observable.empty();
    }

    /**
     * Exercise 4:
     * Create an observable that emits the most recent game state.
     * Hint: {@link #initialState} and {@link #updateState} might come in handy.
     *
     * direction ticks: --------UP----UP----UP----UP--LEFT--->
     * state:           ---S0---S1----S2----S3----S4----S5--->
     *
     * @return Game state
     */
    Observable<State> getStateObservable(Observable<Direction> directionTicks) {
        return Observable.empty();
    }

    State initialState() {
        List<Point2D> snake = IntStream
                .range(1, 5)
                .mapToObj(x -> snakelet(size / 2, size / 2))
                .collect(Collectors.toList());

        return new State(ImmutableList.copyOf(snake), candy());
    }

    State updateState(State state, Direction direction) {
        return move()
                .andThen(handleCollision())
                .andThen(handleCollisionWithSelf())
                .andThen(eatCandy())
                .apply(state, direction);
    }

    private BiFunction<State, Direction, State> move() {
        return (state, direction) -> {
            Point2D head = state.snake.get(0).add(shift(direction));
            ImmutableList<Point2D> snake = ImmutableList
                    .<Point2D>builder()
                    .add(head)
                    .addAll(state.snake.subList(0, state.snake.size() - 1))
                    .build();
            return new State(snake, state.candy);
        };
    }

    private Function<State, State> handleCollisionWithSelf() {
        return state -> {
            Point2D head = state.snake.get(0);
            if (state.snake.stream().skip(1).anyMatch(p -> p.getX() == head.getX() && p.getY() == head.getY())) {
                throw new RuntimeException("Game over");
            } else return state;
        };
    }

    private Function<State, State> handleCollision() {
        return state -> {
            int x = (int) (size + state.snake.get(0).getX()) % size;
            int y = (int) (size + state.snake.get(0).getY()) % size;
            ImmutableList<Point2D> snake = ImmutableList
                    .<Point2D>builder()
                    .add(snakelet(x, y))
                    .addAll(state.snake.subList(1, state.snake.size()))
                    .build();
            return new State(snake, state.candy);
        };
    }

    private Function<State, State> eatCandy() {
        return state -> {
            if (state.snake.get(0).distance(state.candy) < radius) {
                Point2D tail = state.snake.get(state.snake.size() - 1);
                ImmutableList<Point2D> snake = ImmutableList
                        .<Point2D>builder()
                        .addAll(state.snake)
                        .add(tail, tail)
                        .build();
                return new State(snake, candy());
            } else {
                return state;
            }
        };
    }

    private void drawScene(State state) {
        gc.clearRect(0, 0, size, size);
        gc.setFill(Color.GREENYELLOW);
        state.snake.stream().forEach(p -> gc.fillOval(p.getX(), p.getY(), radius, radius));
        gc.setFill(Color.RED);
        gc.fillOval(state.candy.getX(), state.candy.getY(), radius, radius);
    }

    private Observable<State> gameOver(Throwable t) {
        return states();
    }

    private Observable<State> states() {
        return getStateObservable(getDirectionTicks(getTicks(Schedulers.computation()), getDirections(getKeyEvents())))
                .observeOn(FxScheduler.getInstance())
                .onErrorResumeNext(this::gameOver);
    }

    private Point2D shift(Direction direction) {
        switch (direction) {
            case UP:
                return snakelet(0, -radius);
            case DOWN:
                return snakelet(0, radius);
            case LEFT:
                return snakelet(-radius, 0);
            case RIGHT:
                return snakelet(radius, 0);
            default:
                return snakelet(0, 0);
        }
    }

    private Direction toDirection(KeyEvent e) {
        return Direction.valueOf(e.getCode().toString());
    }

    private Boolean isArrowKey(KeyEvent keyEvent) {
        return keyEvent.getCode() == KeyCode.UP ||
                keyEvent.getCode() == KeyCode.DOWN ||
                keyEvent.getCode() == KeyCode.LEFT ||
                keyEvent.getCode() == KeyCode.RIGHT;
    }

    class State {

        final ImmutableList<Point2D> snake;
        final Point2D candy;

        State(ImmutableList<Point2D> snake, Point2D candy) {
            this.snake = snake;
            this.candy = candy;
        }
    }

    private Point2D candy() {
        int x = (int) (radius + Math.random() * (size - 2 * radius));
        int y = (int) (radius + Math.random() * (size - 2 * radius));
        return new Point2D(x - x % radius, y - y % radius);
    }

    private Point2D snakelet(int x, int y) {
        return new Point2D(x, y);
    }

    private void setupScene(Stage stage) {
        this.stage = stage;
        Canvas canvas = new Canvas(size, size);
        gc = canvas.getGraphicsContext2D();

        Group root = new Group();
        root.getChildren().add(canvas);

        stage.setTitle("RxSnake");
        stage.setScene(new Scene(root));
        stage.show();
    }
}

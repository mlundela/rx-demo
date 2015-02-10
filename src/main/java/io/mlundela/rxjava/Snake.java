package io.mlundela.rxjava;

import com.google.common.collect.ImmutableList;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import rx.Observable;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Snake extends Application {

    final int sceneWidth = 800;
    final int sceneHeight = 600;
    final int radius = 25;

    private GraphicsContext gc;


    @Override
    public void start(Stage stage) throws Exception {

        Canvas canvas = new Canvas(sceneWidth, sceneHeight);
        gc = canvas.getGraphicsContext2D();

        Group root = new Group();
        root.getChildren().add(canvas);

        stage.setTitle("RxSnake");
        stage.setScene(new Scene(root));
        stage.show();

        List<Point2D> snake = IntStream
                .range(1, 5)
                .mapToObj(x -> snakelet(sceneWidth / 2, sceneHeight / 2))
                .collect(Collectors.toList());

        State initialState = new State(ImmutableList.copyOf(snake), candy());

        Observable<KeyEvent> keyEvents =
                Observable.create(subscriber ->
                                stage.addEventHandler(KeyEvent.KEY_PRESSED, subscriber::onNext)
                );


        Observable<Long> ticks = Observable.interval(80, TimeUnit.MILLISECONDS);

        Observable<Direction> directionTicks = Observable.combineLatest(ticks, keyEvents.filter(this::isKeyEvent).map(this::toDirection), (t, d) -> d);

        Observable<State> state = directionTicks.scan(initialState, this::updateState);

        state.observeOn(FxScheduler.getInstance()).forEach(this::drawScene);

    }

    private State updateState(State state, Direction direction) {
        return move()
                .andThen(handleCollision())
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

    private Function<State, State> handleCollision() {
        return state -> {
            int x = (int) (sceneWidth + state.snake.get(0).getX()) % sceneWidth;
            int y = (int) (sceneHeight + state.snake.get(0).getY()) % sceneHeight;
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
                ImmutableList<Point2D> snake = ImmutableList
                        .<Point2D>builder()
                        .addAll(state.snake)
                        .add(state.snake.get(state.snake.size() - 1))
                        .build();
                return new State(snake, candy());
            } else {
                return state;
            }
        };
    }

    private void drawScene(State state) {
        gc.clearRect(0, 0, sceneWidth, sceneHeight);
        gc.setFill(Color.LIME);
        state.snake.stream().forEach(p -> gc.fillOval(p.getX(), p.getY(), radius, radius));
        gc.setFill(Color.PINK);
        gc.fillOval(state.candy.getX(), state.candy.getY(), radius, radius);
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

    private Boolean isKeyEvent(KeyEvent keyEvent) {
        switch (keyEvent.getCode().toString()) {
            case "UP":
            case "DOWN":
            case "LEFT":
            case "RIGHT":
                return true;
            default:
                return false;
        }
    }

    private Direction toDirection(KeyEvent e) {
        return Direction.valueOf(e.getCode().toString());
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
        int x = (int) (radius + Math.random() * (sceneWidth - 2 * radius));
        int y = (int) (radius + Math.random() * (sceneHeight - 2 * radius));
        return new Point2D(x - x % radius, y - y % radius);
    }

    private Point2D snakelet(int x, int y) {
        return new Point2D(x, y);
    }
}

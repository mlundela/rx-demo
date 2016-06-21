package io.mlundela.rxjava;

import rx.Observable;

import java.util.concurrent.TimeUnit;

public class GroupByExample {


    public static void main(String[] args) throws InterruptedException {
        infiniteStream()
                .groupBy(x -> x)
                .flatMap(obs -> obs
                        .takeUntil(Observable.timer(1, TimeUnit.SECONDS))
                        .doOnCompleted(() -> System.out.println("slutt på strøm: " + obs.getKey()))
                )
                .subscribe(
                        x -> System.out.println(Thread.currentThread().getName() + " - " + x),
                        System.out::println,
                        () -> System.out.println("Observable done")
                );

        Thread.sleep(10000);
        System.out.println(Thread.currentThread().getName() + " - Terminated");
    }

    private static Observable<Integer> infiniteStream() {
        return Observable.interval(500, TimeUnit.MILLISECONDS)
                .map(x -> x.intValue() / 2);
    }
}

package io.mlundela.rxjava;


import rx.Observable;

import java.util.concurrent.TimeUnit;

public class Recursion {

    private static int i = 1;

    public static void main(String[] args) throws InterruptedException {
        infiniteStream()
                .subscribe(
                        x -> System.out.println(Thread.currentThread().getName() + " - " + x),
                        System.out::println,
                        () -> System.out.println("Observable done")
                );

        System.out.println(Thread.currentThread().getName() + " - Wait 10 seconds...");
        Thread.sleep(10000);
        System.out.println(Thread.currentThread().getName() + " - Terminated");
    }

    private static Observable<Integer> infiniteStream() {

        Observable<Integer> obs = Observable.create(subscriber -> {
            if (Math.random() < 0.4) {
                subscriber.onError(new RuntimeException("Failed to compute next value"));
            } else {
                subscriber.onNext(i++);
                subscriber.onCompleted();
            }
        });


        return obs
//                .retry((integer, throwable) -> {
//                    System.out.println("Retry after error: " + throwable.getMessage());
//                    return true;
//                })
//                .retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i).flatMap(i -> {
//                    System.out.println("delay retry by " + i + " second(s)");
//                    return Observable.timer(i, TimeUnit.SECONDS);
//                }))
                .retryWhen(attempts -> attempts.flatMap(throwable -> {
                    System.out.println("delay retry by 3 second");
                    return Observable.timer(3000, TimeUnit.MILLISECONDS);
                }))
//                .retryWhen(attempts -> {
//                    System.out.println("delay retry by 1 second");
//                    return Observable.timer(100, TimeUnit.MILLISECONDS);
//                })
                .delay(1000, TimeUnit.MILLISECONDS)
                .repeat();
    }
}

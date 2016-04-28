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
        return Observable.<Integer>create(subscriber -> {
            if (Math.random() < 0.3) {
                subscriber.onError(new RuntimeException("Failed to compute next value"));
            } else {
                subscriber.onNext(i++);
                subscriber.onCompleted();
            }
        }).retry((integer, throwable) -> {
            System.out.println("Retry after error: " + throwable.getMessage());
            return true;
        }).delay(500, TimeUnit.MILLISECONDS).repeat();
    }
}

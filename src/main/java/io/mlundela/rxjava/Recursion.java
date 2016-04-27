package io.mlundela.rxjava;


import rx.Observable;
import rx.schedulers.Schedulers;

public class Recursion {

    private static int i = 1;

    public static void main(String[] args) throws InterruptedException {
        process()
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                        x -> System.out.println(Thread.currentThread().getName() + " - " + x),
                        System.out::println,
                        () -> System.out.println("Observable done")
                );

        System.out.println(Thread.currentThread().getName() + " - Wait 10 seconds...");
        Thread.sleep(10000);
        System.out.println(Thread.currentThread().getName() + " - Done");
    }


    private static Observable<Integer> process() {
        Observable<Integer> obs = Observable.create(subscriber -> {
            try {
                Thread.sleep(500);
                if (Math.random() < 0.2) {
                    subscriber.onError(new RuntimeException("Failed for some strange reason..."));
                }
                subscriber.onNext(i++);
                subscriber.onCompleted();
            } catch (InterruptedException e) {
                subscriber.onError(e);
            }
        });
        return obs.retry((integer, throwable) -> {
            System.out.println("Retry after error: " + throwable);
            return true;
        }).repeat();
    }
}

RxSnake
=======


Exercise 1:
-----------
Create an observable that emits key events.
  
    private Observable<KeyEvent> getKeyEvents() = ???

Exercise 2:
-----------
Create an observable that emits a new tick every 80 millisecond.

    private Observable<Long> getTicks() = ???

Exercise 3:
-----------
Combine the output from exercise 1 and 2 to create an observable
that emits direction ticks.

    private Observable<Direction> getDirectionTicks() = ???

Exercise 4:
-----------
Create an observable that emits the most recent game state.

    private Observable<State> getStateObservable() = ???
    
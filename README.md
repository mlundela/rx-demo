RxSnake
=======

Your task is to complete the implementation of RxSnake. When all exercises are done, you should be able to play using your arrow keys to control the snake. The user interface is built with JavaFx, but all methods exept the ones that returns Observables are implemented for you.

NB: RxJava's [documentation](https://github.com/ReactiveX/RxJava/wiki) will be useful!


Exercise 1:
-----------
Create an observable that emits key events.
    
    ----UP---LEFT------------------DOWN------->
    
    Observable<KeyEvent> getKeyEvents()

Exercise 2:
-----------
Create an observable that emits a new tick every 80 millisecond.

    ----0---1---2---3---4--->
    
    Observable<Long> getTicks()

Exercise 3:
-----------
Combine the output from exercise 1 and 2 to create an observable
that emits direction ticks.
    
    ----UP---LEFT---LEFT---LEFT---DOWN---DOWN--->
    
    Observable<Direction> getDirectionTicks()

Exercise 4:
-----------
Create an observable that emits the most recent game state.

    ----S0---S1---S2---S3--->
    
    Observable<State> getStateObservable()

Bonus exercise:
-----------
Display a 'GAME OVER' message when the snake collides with its self, and start a 
new game when a new arrow key is pressed.
    

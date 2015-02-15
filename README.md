RxSnake
=======


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
Display a 'GAME OVER' message when the snake collides with its self.
    

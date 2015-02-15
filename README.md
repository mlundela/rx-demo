RxSnake
=======

        /**
         * Exercise 1:
         * Create an observable that emits key events.
         * Hint: Use {@link rx.Observable#create} and {@link Stage#addEventHandler}
         *
         * @return Observable that emits key events.
         */
        private Observable<KeyEvent> getKeyEvents() {
            throw new RuntimeException("Not implemented yet");
        }

        /**
         * Exercise 2:
         * Create an observable that emits a new tick every 80 millisecond.
         *
         * @return Ticks
         */
        private Observable<Long> getTicks() {
            throw new RuntimeException("Not implemented yet");
        }

        /**
         * Exercise 3:
         * Combine the output from exercise 1 and 2 to create an observable
         * that emits direction ticks.
         *
         * @return Direction ticks
         */
        private Observable<Direction> getDirectionTicks() {
            throw new RuntimeException("Not implemented yet");
        }

        /**
         * Exercise 4:
         * Create an observable that emits the most recent game state.
         * Hint: {@link #initialState} and {@link #updateState} might come in handy.
         *
         * @return Game state
         */
        private Observable<State> getStateObservable() {
            throw new RuntimeException("Not implemented yet");
        }
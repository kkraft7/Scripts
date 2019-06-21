package vlocity1;

import java.lang.Math;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/*
** Programming exercise:
** There are N number of balls, all of equal weight except for one which is heavier than the rest.
** Write a program in Java that accepts the weights as an Array and identifies the heaviest ball in the Array.
** To do this as efficiently as possible I have used threading to partition the input array (provided it is
** above a certain size threshold).
*/
public class FindHeavierBall {
    private int[] _weights;
    private int _numThreads;
    private int _threadThreshold;
    private int _weightOfFirstBall;
    public static int DEFAULT_THREAD_THRESHOLD = 100;

    public FindHeavierBall( int[] weights, int threadThreshold ) {
        if ( weights.length == 0 ) {
            throw new ArrayOfSizeZeroException();
        }
        if ( threadThreshold < 1 ) {
            throw new ThreadThresholdOfSizeZeroException();
        }
        this._weights = weights;
        this._weightOfFirstBall = _weights[0];
        this._threadThreshold = threadThreshold;
        this._numThreads = calculateNumberOfThreads( _weights.length, _threadThreshold );
    }

    public FindHeavierBall( int[] weights ) {
        this( weights, DEFAULT_THREAD_THRESHOLD );
    }

    public Integer search() throws Exception {
        Integer position = null;
        if ( _weights.length == 1 ) {
            position = 0;
         }
        else {
            int minIndex = 1;
            ExecutorService threadPool = Executors.newFixedThreadPool(_numThreads);
            Set<Future<Integer>> resultSet = new HashSet<Future<Integer>>();
            for ( int i = 0; i < _numThreads; i++ ) {
                // For the last partition maxIndex cannot be greater than _weights.length
                int maxIndex = (minIndex + _threadThreshold > _weights.length) ?
                        _weights.length : minIndex + _threadThreshold;
                Callable<Integer> searchThread = new SearchForHeavierBall( minIndex, maxIndex );
                Future<Integer> future = threadPool.submit( searchThread );
                resultSet.add( future );
                minIndex = maxIndex;
            }
            for ( Future<Integer> future : resultSet ) {
                if ( future.get() != null ) {
                    position = future.get();
                    break;
                }
            }
        }
        if ( position != null ) {
            if ( _weights[position] < _weightOfFirstBall ) {
                position = 0;
            }
            System.out.println( "Found heavier ball of weight " + _weights[position] + " at index " + position );
        }
        return position;
    }

    public int getNumThreads() {
       return _numThreads;
    }

    /*
    ** Since I treat a one-element array as a special case, and start the thread processing at the 2nd array index (1),
    ** the threshold will actually be offset by one in relation to the index.
    */
    private int calculateNumberOfThreads( int numberOfBalls, int threadThreshold ) {
        return numberOfBalls < 2 ? 0 : (int)Math.ceil(( numberOfBalls - 1 )/(double)threadThreshold );
    }

    class SearchForHeavierBall implements Callable<Integer> {
        private int _i1, _i2;
        public SearchForHeavierBall( int i1, int i2 ) {
            this._i1 = i1;
            this._i2 = i2;
        }

        public Integer call() {
            for ( int i = _i1; i < _i2; i++ ) {
                // I check which ball is heavier in search()
                // This just returns a non-null value is it finds a weight different from _weights[0]
                if ( _weights[i] != _weightOfFirstBall ) {
                    return i;
                }
            }
            return null;
        }
    }

    public class ArrayOfSizeZeroException extends RuntimeException {
        public ArrayOfSizeZeroException() {
            super( "Array must have at least one element" );
        }
    }

    public class ThreadThresholdOfSizeZeroException extends RuntimeException {
        public ThreadThresholdOfSizeZeroException() {
            super( "Thread threshold must be greater than zero" );
        }
    }
}

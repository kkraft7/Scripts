package vlocity1;

import org.testng.Assert;
import org.testng.annotations.Test;

public class FindHeavierBallTest {
    private static int REGULAR_WEIGHT = 10;
    private static int HEAVIER_WEIGHT = 20;

    @Test( expectedExceptions = FindHeavierBall.ArrayOfSizeZeroException.class )
    public void testArraySizeEqualsZero() {
        new FindHeavierBall( new int[] {} );
    }

    @Test( expectedExceptions = FindHeavierBall.ThreadThresholdOfSizeZeroException.class )
    public void testThreadThresholdEqualsZero() {
        new FindHeavierBall( new int[] { 1 }, 0 );
    }

    @Test
    public void testArraySizeEqualsOne() throws Exception {
        FindHeavierBall test = new FindHeavierBall( new int[] { HEAVIER_WEIGHT } );
        Assert.assertEquals( (int)test.search(), 0, "Index should be 0 for one-element array" );
    }

    @Test
    public void testArraySizeEqualsTwoWithHeavierBallInFirstPosition() throws Exception {
        FindHeavierBall test = new FindHeavierBall( new int[] { HEAVIER_WEIGHT, REGULAR_WEIGHT } );
        Assert.assertEquals( (int)test.search(), 0,
                "Index should be 0 for two-element array with heavier ball in first position" );
    }

    @Test
    public void testArraySizeEqualsTwoWithHeavierBallInSecondPosition() throws Exception {
        FindHeavierBall test = new FindHeavierBall( new int[] { REGULAR_WEIGHT, HEAVIER_WEIGHT } );
        Assert.assertEquals( (int)test.search(), 1,
                "Index should be 1 for two-element array with heavier ball in second position" );
    }

    @Test()
    public void testSmallArrayLessThanThreshold() throws Exception {
        FindHeavierBall test = new FindHeavierBall( new int[] { REGULAR_WEIGHT, HEAVIER_WEIGHT, REGULAR_WEIGHT } );
        Assert.assertEquals( (int)test.search(), 1, "Expected heavier weight at index 1" );
    }

    @Test()
    public void testSmallArrayWithHeavierWeightAtFirstIndex() throws Exception {
        FindHeavierBall test = new FindHeavierBall( new int[] { HEAVIER_WEIGHT, REGULAR_WEIGHT, REGULAR_WEIGHT } );
        Assert.assertEquals( (int)test.search(), 0, "Expected heavier weight at index 0" );
    }

    @Test()
    public void testSmallArrayWithHeavierWeightAtLastIndex() throws Exception {
        FindHeavierBall test = new FindHeavierBall( new int[] { REGULAR_WEIGHT, REGULAR_WEIGHT, HEAVIER_WEIGHT } );
        Assert.assertEquals( (int)test.search(), 2, "Expected heavier weight at index 2" );
    }

    @Test()
    public void testArraySizeOneLessThanThreshold() throws Exception {
        int expectedPosition = 5;
        int[] weights = initializeWeights( FindHeavierBall.DEFAULT_THREAD_THRESHOLD - 1, expectedPosition );
        FindHeavierBall test = new FindHeavierBall( weights );
        Assert.assertEquals( test.getNumThreads(), 1, "Should be one thread for size less than threshold" );
        Assert.assertEquals( (int)test.search(), expectedPosition, "Expected heavier weight at index " + expectedPosition );
    }

    @Test()
    public void testArraySizeEqualThreshold() throws Exception {
        int expectedPosition = 5;
        int[] weights = initializeWeights( FindHeavierBall.DEFAULT_THREAD_THRESHOLD, expectedPosition );
        FindHeavierBall test = new FindHeavierBall( weights );
        Assert.assertEquals( test.getNumThreads(), 1, "Should be one thread for size equal threshold" );
        Assert.assertEquals( (int)test.search(), expectedPosition, "Expected heavier weight at index " + expectedPosition );
    }

    /*
    ** Since I treat a one-element array as a special case, and start the thread processing at the 2nd array index (1),
    ** the threshold will actually be offset by one in relation to the index.
    */
    @Test()
    public void testArraySizeOneMoreThanThreshold() throws Exception {
        int expectedPosition = 5;
        int[] weights = initializeWeights( FindHeavierBall.DEFAULT_THREAD_THRESHOLD + 1, expectedPosition );
        FindHeavierBall test = new FindHeavierBall( weights );
        Assert.assertEquals( test.getNumThreads(), 1, "Should be one thread for size one more than threshold" );
        Assert.assertEquals( (int)test.search(), expectedPosition, "Expected heavier weight at index " + expectedPosition );
    }

    @Test()
    public void testArraySizeTwoMoreThanThreshold() throws Exception {
        int expectedPosition = 5;
        int[] weights = initializeWeights( FindHeavierBall.DEFAULT_THREAD_THRESHOLD + 2, expectedPosition );
        FindHeavierBall test = new FindHeavierBall( weights );
        Assert.assertEquals( test.getNumThreads(), 2, "Should be two threads for size two more than threshold" );
        Assert.assertEquals( (int)test.search(), expectedPosition, "Expected heavier weight at index " + expectedPosition );
    }

    // The value of this test actually turned out to be testing a target index outside the first thread range
    @Test()
    public void testArrayOfSizeOneLessThanRequiredForFiveThreads() throws Exception {
        int arraySize = FindHeavierBall.DEFAULT_THREAD_THRESHOLD*4 + 1;
        int expectedPosition = arraySize - 1;
        int[] weights = initializeWeights( arraySize, expectedPosition );
        FindHeavierBall test = new FindHeavierBall( weights );
        Assert.assertEquals( test.getNumThreads(), 4, "Should be 4 threads for size threshold*4 + 1" );
        Assert.assertEquals( (int)test.search(), expectedPosition, "Expected heavier weight at index " + expectedPosition );
    }

    @Test()
    public void testArrayOfExactSizeRequiredForFiveThreads() throws Exception {
        int arraySize = FindHeavierBall.DEFAULT_THREAD_THRESHOLD*4 + 2;
        int expectedPosition = 0;
        int[] weights = initializeWeights( arraySize, expectedPosition );
        FindHeavierBall test = new FindHeavierBall( weights );
        Assert.assertEquals( test.getNumThreads(), 5, "Should be 5 threads for size threshold*4 + 2" );
        Assert.assertEquals( (int)test.search(), expectedPosition, "Expected heavier weight at index " + expectedPosition );
    }

    @Test()
    public void testArrayWithAllBallsOfEqualWeight() throws Exception {
        int[] weights = initializeWeights( FindHeavierBall.DEFAULT_THREAD_THRESHOLD*3, 0 );
        weights[0] = REGULAR_WEIGHT; // Set all weights to same value
        FindHeavierBall test = new FindHeavierBall( weights );
        Assert.assertEquals( test.search(), null, "Expected null result for array with all elements of equal weight" );
    }

    private int[] initializeWeights( int numberOfBalls, int positionOfHeavierBall ) {
        // Check for numberOfBalls < 1 and positionOfHeavierBall not in correct index range
        int[] weights = new int[numberOfBalls];
        for ( int i = 0; i < weights.length; i++ ) {
            weights[i] = REGULAR_WEIGHT;
        }
        weights[positionOfHeavierBall] = HEAVIER_WEIGHT;
        return weights;
    }
}

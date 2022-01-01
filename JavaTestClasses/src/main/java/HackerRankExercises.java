
import helper.ExerciseBaseClass;

import java.util.*;

/**
 * Class for HackerRank exercises.
 */
public class HackerRankExercises extends ExerciseBaseClass {

  // s, t, a, and b are points along a line. s and t represent the left and
  // right edge of a house. a represents the location of an apple tree, to
  // the left of the house, while b represents the location of an orange tree,
  // to the right of the house. The apples and oranges arrays represent the
  // distance away the fruit falls from each tree. Given this information
  // determine how many apples and oranges fall on the house.
  static void countApplesAndOranges(int s, int t, int a, int b, int[] apples, int[] oranges) {
    System.out.println(countFruit(s, t, a, apples));
    System.out.println(countFruit(s, t, b, oranges));
  }

  private static int countFruit(int s, int t, int treeLocation, int[] fruit) {
    int count = 0;
    for (int f : fruit) {
      int location = treeLocation + f;
      if (location >= s && location <= t) {
        count++;
      }
    }
    return count;
  }

  // In this class 40 is a passing grade. The rule is that the professor will
  // round up the grade if it is 2 or less points away from a multiple of 5
  // (as long as it is greater than 37).
  static int[] roundUpGrades(int[] grades) {
    for ( int i = 0; i < grades.length; i++ ) {
      if (grades[i] > 37) {
        int diff = 5 - (grades[i] % 5);
        if (diff < 3) {
          grades[i] += diff;
        }
      }
    }
    return grades;
  }

  // Given a time in 12-hour AM/PM format, convert it to military (24-hour) time.
  static String timeConversion(String ts) {
    // Could write a regex to validate the timestamp format
    Boolean pm = ts.endsWith("PM");
    ts = pm ? ts.replace("PM", "") : ts.replace("AM", "");
    String[] hms = ts.split(":");
    if (hms.length != 3) {
      System.out.println("Invalid timestamp format for string: " + ts);
      System.exit(1);
    }
    int hour = (Integer.parseInt(hms[0]));
    if (!pm && hour == 12) {
      hour = 0;
    }
    else if (pm && hour != 12) {
      hour += 12;
    }
    return String.format("%02d:%s:%s", hour, hms[1], hms[2]);
  }

  /*
  ** You are in charge of the cake for your niece's birthday and have decided the cake will have
  ** one candle for each year of her total age. When she blows out the candles, she’ll only be
  ** able to blow out the tallest ones. Your task is to find out how many candles she can successfully
  ** blow out. For example, if your niece is turning 4 years old, and the cake has 4 candles of height
  ** 4, 4, 1, 3, she will be able to blow out 2 candles successfully, since the tallest candles are
  ** of height 4 and there are 2 such candles.
  */
  static int birthdayCandles(int[] candleSizes) {
    Map<Integer, Integer> candleData = new HashMap<>();
    Integer maxKey = candleSizes[0];
    for (int i : candleSizes) {
      candleData.put(i, candleData.getOrDefault(i, 0) + 1);
      if (candleData.get(i) > candleData.get(maxKey)) {
        maxKey = i;
      }
    }
    return candleData.get(maxKey);
  }

  // Take in an array of 5 positive integers and print the minimum
  // and maximum sum of 4 of the elements. The exercise recommended
  // using 64-bit integers (i.e. long) to avoid overflow.
  static void minMaxSum(int[] intArray) {
    if ( intArray.length != 5 ) {
      System.out.println("minMaxSum(): The array length is required to be 5 (actual length is " +
          intArray.length + ")");
    }
    Arrays.sort(intArray);
    long minSum = intArray[0];
    long maxSum = intArray[4];
    for ( int i = 1; i < 4; i++ ) {
      minSum += intArray[i];
      maxSum += intArray[i];
    }
    System.out.println(minSum + " " + maxSum);
  }

  // Print out a staircase consisting of '#' characters
  static void staircase(int height) {
    if ( height < 1 || height > 100 ) {
      System.out.println("Illegal value for staircase height: " + height);
      return;
    }

    for ( int i = 1; i <= height; i++ ) {
      printChars(' ', height - i);
      printChars('#', i);
      System.out.println();
    }
  }

  private static void printChars( char c, int count ) {
    for ( int i = 0; i < count; i++ ) {
      System.out.print(c);
    }
  }

  // Test data: [1, 1, 0, -1, -1], [-4, 3, -9, 0, 4, 1]
  // Print out the ratio of positive numbers, negative numbers, and zeroes
  static void plusMinusRatios(int[] intArray) {
    int plusCount = 0;
    int minusCount = 0;
    int zeroCount = 0;

    for ( int item : intArray ) {
      if ( item > 0) {
        plusCount++;
      }
      else if ( item < 0 ) {
        minusCount++;
      }
      else {
        zeroCount++;
      }
    }

    for ( int count : new int[] {plusCount, minusCount, zeroCount} ){
      System.out.println(String.format("%.5f", count/(double)intArray.length));
    }
  }

  // Move these into a test class
  // Note that the main program must be public in order to run it
  public static void main(String[] args) {

    System.out.println("Testing minMaxSum():");
    int[] minMaxSumData = new int[] {1, 3, 5, 7, 9};
    minMaxSum(minMaxSumData);

    System.out.println("Testing staircase():");
    int[] staircaseData = new int[] { 3, 5, 7, 10 };
    for ( int n : staircaseData ) {
      System.out.println("Staircase size = " + n);
      staircase(n);
    }

    System.out.println("Testing plusMinusRatios():");
    int[][] plusMinusTestData = new int[][] {{1, 1, 0, -1, -1}, {-4, 3, -9, 0, 4, 1}};
    for ( int[] testData : plusMinusTestData ) {
      System.out.println("Test data: " + Arrays.toString(testData));
      plusMinusRatios(testData);
    }
  }
}

package euler;

import helper.ExerciseBaseClass;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ProjectEulerTest {
  // 001. Multiples of 3 and 5
  @Test
  public void test001MultiplesOf3And5() {
    Assert.assertEquals(ProjectEuler.multiplesOf3And5(), 233168,
        "Incorrect answer for sum of multiples of 3 and 5 up to " + ProjectEuler.MAX_3_AND_5_FACTORS);
  }

  // 002. Even Fibonacci numbers
  @Test
  public void test002EvenFibonacciNumbers() {
    Assert.assertEquals(ProjectEuler.evenFibonacciNumbers(), 4613732,
        "Incorrect answer for sum of even Fibonacci numbers up to " + ProjectEuler.MAX_FIB);
  }

  // General tests for factoring utilities (note that they omit 1 and the number)
  long[] numbersToFactor = new long[] {2, 6, 12, 17, 100};
  ArrayList<HashSet<Long>> getAnswersForNumbersToFactor() {
    ArrayList<HashSet<Long>> answers = new ArrayList<>();
    answers.add(0, new HashSet<>());
    answers.add(1, new HashSet<>(Arrays.asList(2L, 3L)));
    answers.add(2, new HashSet<>(Arrays.asList(2L, 3L, 4L, 6L)));
    answers.add(3, new HashSet<>());
    answers.add(4, new HashSet<>(Arrays.asList(2L, 4L, 5L, 10L, 20L, 25L, 50L)));
    return answers;
  }

  @Test
  public void testFactorNumber() {
    ArrayList<HashSet<Long>> expectedResults = getAnswersForNumbersToFactor();
    for (int i = 0; i < numbersToFactor.length; i++) {
      Assert.assertEquals(ProjectEuler.factorNumber(numbersToFactor[i]), expectedResults.get(i),
          "Incorrect answer for factors of " + numbersToFactor[i]);
    }
  }

  @Test
  public void testFactorUtility() {
    ArrayList<HashSet<Long>> expectedResults = getAnswersForNumbersToFactor();
    for (int i = 0; i < numbersToFactor.length; i++) {
      Assert.assertEquals(ProjectEuler.factorUtility(numbersToFactor[i], ProjectEuler.FactorMode.DEFAULT),
          expectedResults.get(i), "Incorrect answer for factors of " + numbersToFactor[i]);
    }
  }

  // 003. Largest prime factor
  @Test
  public void test003LargestPrimeFactor() {
    Assert.assertEquals(ProjectEuler.largestPrimeFactor(), 6857L,
        "Incorrect answer for largest prime factor of " + ProjectEuler.PRIME_FACTOR_NUM);
  }

  // 004. Largest palindrome product
  @Test
  public void test004LargestPalindromeProduct() {
    Assert.assertEquals(ProjectEuler.largestPalindromeProduct(), 906609,
        "Incorrect answer for largest palindrome product of two 3-digit numbers");
  }

  // Testing various methods for calculating palindromic numbers. Originally I used
  // the "brute-force" method as the oracle for validating the others, but it is very
  // slow for any magnitude much higher than 7. Also it looks like any magnitude greater
  // than 9 causes some kind of numeric overflow and the recursive version never returns.
  // This was based on the previous Euler problem ("Largest Palindrome Product").
  @Test
  public void testGetPalindromicNumbers() {
    ExerciseBaseClass.DEBUG_LEVEL = 1;
    // Negative tests:
    // NumericPalindromes.getPalindromicNumbersByBruteForce(8);
    // NumericPalindromes.getPalindromicNumbersByDecrement(10);
    // NumericPalindromes.getPalindromicNumbersByDecrementRecursive(10);

    // Sanity test to make sure the main algorithm is reliable:
    int startingMagnitude = 7;
    System.out.println();
    System.out.println("Calculating numeric palindromes by brute force");
    ArrayList<Integer> bruteForce = NumericPalindromes.getPalindromicNumbersByBruteForce(startingMagnitude);
    System.out.println("Calculating numeric palindromes by decrement");
    ArrayList<Integer> decrement = NumericPalindromes.getPalindromicNumbersByDecrement(startingMagnitude);
    System.out.println("Comparing the brute force and decrement versions");
    Assert.assertEquals(decrement, bruteForce, "Decrement palindrome algorithm failed");

    // Testing the recursive version:
    decrement.clear();
    startingMagnitude = 9;
    System.out.println();
    System.out.println("Calculating numeric palindromes by decrement");
    decrement = NumericPalindromes.getPalindromicNumbersByDecrement(startingMagnitude);
    System.out.println("Calculating numeric palindromes by recursion");
    ArrayList<Integer> recursive = NumericPalindromes.getPalindromicNumbersByDecrementRecursive(startingMagnitude);
    System.out.println("Comparing the decrement and recursive versions");
    Assert.assertEquals(recursive, decrement, "Decrement palindrome algorithm failed");
  }

  // Ideally I should pass in an object with the number and the expected result
  int [] numbersForPalindromeTest = {0, 1 , 9, 10, 11, 123, 121, 35953, 73920, 73937};
  boolean[] answersForPalindromeTest = {true, true, true, false, true, false, true, true, false, true};
  @Test
  public void testIsNumericPalindrome() {
    for (int i = 0; i < numbersForPalindromeTest.length; i++) {
      ArrayList<Integer> digits = ProjectEuler.convertNumberToDigits(numbersForPalindromeTest[i]);
      Assert.assertEquals(ProjectEuler.isPalindrome(digits), answersForPalindromeTest[i],
          "Expected isPalindrome(" + numbersForPalindromeTest[i] + ") to be " +
          answersForPalindromeTest[i]);
    }
  }

  // 005. Smallest evenly divisible multiple
  @Test
  public void test005SmallestEvenlyDivisibleMultipleOfOneThroughTwenty() {
    int candidate = ProjectEuler.smallestEvenlyDivisibleMultipleOfOneThroughTwenty();
    Assert.assertTrue(ProjectEuler.checkForFactorsOneThroughTwenty(candidate),
        "The number " + candidate + " was expected to contain all factors from 1-20");
    Assert.assertEquals(candidate, 232792560, "Incorrect answer for smallest evenly divisible multiple of 1-20");
  }

  // 006. Difference between sum of square and square of sum
  @Test
  public void test006DifferenceBetweenSumOfSquaresAndSquareOfSum() {
    Assert.assertEquals(ProjectEuler.squareOfSumMinusSumOfSquares(100), 25164150,
        "Incorrect answer for difference between square of sum and sum of squares");
  }

  // 007. Find the 10001st prime number
  @Test
  public void test007Find10001stPrimeNumber() {
    Assert.assertEquals(ProjectEuler.find10001stPrimeNumber(), 104743, "Incorrect answer for 10001st prime number");
  }

  // 008. Find the largest product of 13 adjacent digits in a 1000-digit number
  @Test
  public void test008LargestProductOfThirteenAdjacentDigits() {
    Assert.assertEquals(ProjectEuler.largestProductOfThirteenAdjacentDigits(), 23514624000L,
        "Incorrect answer for largest product of 13 adjacent digits");
  }

  // 009. Find the Pythagorean triplet for which a + b + c = 1000
  @Test
  public void test009PythagoreanTripletWithProduct1000() {
    Assert.assertEquals(ProjectEuler.pythagoreanTripletWithSumEquals1000(), 31875000,
        "Incorrect answer for Pythagorean triplet which sums to 1000");
  }

  @Test
  public void testGetPrimeNumbersUsingSieveOfEratosthenes() {
    Assert.assertEquals(ProjectEuler.getPrimeNumbersUsingSieveOfEratosthenes(Integer.MAX_VALUE/20).size(),
        6161172, "Incorrect answer for number of primes below " + Integer.MAX_VALUE/20);
    int[] expectedPrimes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47};
    ArrayList<Integer> primeList = ProjectEuler.getPrimeNumbersUsingSieveOfEratosthenes(50);
    Assert.assertEquals(primeList.size(), expectedPrimes.length, "Size of expected and actual array do not match");
    for (int i = 0; i < primeList.size(); i++) {
      Assert.assertEquals((int)primeList.get(i), expectedPrimes[i],
          "Expected and actual array do not match at index " + (i + 1));
    }
  }

  // 010. Sum of all primes below two million (the old version took over an hour to complete)
  @Test
  public void test010SumOfAllPrimesBelowTwoMillion() {
    Assert.assertEquals(ProjectEuler.sumOfAllPrimesBelowTwoMillion(), 142913828922L,
        "Incorrect answer for sum of all primes below two million");
  }

  // 011. Largest product of four adjacent numbers
  @Test
  public void test011LargestProductOfFourAdjacentNumbers() {
    Assert.assertEquals(ProjectEuler.largestProductOfFourAdjacentNumbers(), 70600674,
        "Incorrect answer for largest product of 4 adjacent numbers"); }

  // 012. Next...
  // @Test
  // public void test012() { Assert.assertEquals(1, 0, "Incorrect answer for..."); }
}

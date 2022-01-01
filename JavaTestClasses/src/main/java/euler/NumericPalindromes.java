package euler;

import helper.ExerciseBaseClass;
import java.util.ArrayList;

// Might be fun quantifying how terrible the performance of the brute-force is.
public class NumericPalindromes {

  enum AlgorithmType { BRUTE_FORCE, DECREMENT, RECURSIVE }

  // This uses the existing isPalindrome() method plus brute force.
  // It's pretty slow for anything much over magnitude = 7.
  static ArrayList<Integer> getPalindromicNumbersByBruteForce(int magnitude) {
    ArrayList<Integer> results = new ArrayList<>();
    if (!isValidMagnitude(magnitude, AlgorithmType.BRUTE_FORCE)) { return results; }
    int start = (int)Math.pow(10.0, magnitude);
    for (int number = start; number > 10; number--) {
      if (ProjectEuler.isPalindrome(ProjectEuler.convertNumberToDigits(number))) {
        results.add(number);
      }
    }
    return results;
  }

  /*
  ** This generates a list of adjacent palindromic numbers by calculating the decrements between them.
  **
  ** The palindromic numbers follow a pattern, albeit a somewhat convoluted one. For an even number
  ** of digits you decrement the middle digit until it hits zero (for an odd number of digits you
  ** decrement the middle two digits). For example, if you start with 99999 your standard decrement
  ** will be 100 until you get to 99099 (while if you start with 999999 your standard decrement
  ** will be 1100 until you get to 990099). Once one (or two) of the palindrome's middle digits
  ** hits zero, however, the decrement changes to some multiple of 11 (e.g. 110 from 99099 to
  ** 98989, or 11 from 900009 to 899998). This "secondary decrement" can be correlated with the
  ** position of the zero-digits in the palindrome (which maps to the total number of decrements that
  ** have been applied so far. Finally, when we hit the "floor" of the current magnitude the
  ** decrement will always be 2 (e.g. 100001 -> 99999), although I have changed the algorithm
  ** to reset the palindrome to all nines at the top of each loop so I no longer need this.
  **
  ** 1. Outer loop decrements the magnitude (or power of 10) for the starting
  **    palindrome (which is set to all 9s)
  ** 2. Inner loop applies the standard decrement until the middle digit(s) hit zero
  ** 3. Then it calculates the secondary decrement using the number of decrements applied
  **    so far (which is equal to the size of the result set)
  ** 4. This loop exits when we hit the "floor" of the current magnitude (e.g. 1000001)
  ** 5. I stop calculating at 11 (although, technically, 9-0 are palindromes as well)
  */
  static ArrayList<Integer> getPalindromicNumbersByDecrement(int magnitude) {
    ArrayList<Integer> results = new ArrayList<>();
    if (!isValidMagnitude(magnitude, AlgorithmType.DECREMENT)) { return results; }
    // This loop handles numbers of decreasing magnitude
    for (int m1 = magnitude; m1 > 1; m1--) {
      int palindrome = (int)Math.pow(10.0, m1) - 1;
      int decr = (int)(Math.pow(10.0, m1/2) + ((m1 % 2 == 0) ? Math.pow(10.0, m1/2 - 1) : 0));
      headerDebugMessages(1, m1, palindrome, decr);
      // This loop decrements the middle digit(s) of numbers in the current magnitude
      while (palindrome > Math.pow(10.0, m1 - 1) + 1) {
        // Looks like the "palindrome > 10" is required
        for (int i = 0; i < 10 && palindrome > 10; i++, palindrome -= decr) {
          ExerciseBaseClass.debugLevel(3, Integer.toString(palindrome));
          results.add(palindrome);
        }
        // Determine the secondary decrement based on which magnitude digit is changing
        int zeroDigit = 1;
        for (int exp = (m1 + 1)/2 - 1; results.size() % Math.pow(10.0, exp) != 0; exp--) { zeroDigit++; }
        loopDebugMessages(2, palindrome + decr, 11*(int)Math.pow(10.0, zeroDigit - 1), zeroDigit, results.size());
        // Have to account for the extra decrement at the end of the loop
        palindrome -= (palindrome > Math.pow(10.0, m1 - 1) + 1 ? 11 * Math.pow(10.0, zeroDigit - 1) : 0) - decr;
      }
    }
    return results;
  }

  // This runs the recursive version, below, and returns the results
  static ArrayList<Integer> getPalindromicNumbersByDecrementRecursive(int magnitude) {
    ArrayList<Integer> results = new ArrayList<>();
    if (!isValidMagnitude(magnitude, AlgorithmType.RECURSIVE)) { return results; }
    getPalindromicNumbersByDecrementRecursive(magnitude, results);
    return results;
  }

  // This generates a list of adjacent palindromic numbers by calculating the decrements between them
  // using recursion.
  static void getPalindromicNumbersByDecrementRecursive(int magnitude, ArrayList<Integer> palindromeList) {
    if (magnitude == 1) { return; }    // Break the recursion

    int palindrome = (int)Math.pow(10.0, magnitude) - 1;
    int decr = (int)(Math.pow(10.0, magnitude/2) + ((magnitude % 2 == 0) ? Math.pow(10.0, magnitude/2 - 1) : 0));
    headerDebugMessages(1, magnitude, palindrome, decr);
    // This loop decrements the middle digit(s) of numbers in the current magnitude
    while (palindrome > Math.pow(10.0, magnitude - 1) + 1) {
      // Looks like the "palindrome > 10" is required
      for (int i = 0; i < 10 && palindrome > 10; i++, palindrome -= decr) {
        ExerciseBaseClass.debugLevel(3, Integer.toString(palindrome));
        palindromeList.add(palindrome);
      }
      // Determine the secondary decrement based on which magnitude digit is changing
      int zeroDigit = 1;
      for (int exp = (magnitude + 1)/2 - 1; palindromeList.size() % Math.pow(10.0, exp) != 0; exp--) { zeroDigit++; }
      loopDebugMessages(2, palindrome + decr, 11*(int)Math.pow(10.0, zeroDigit - 1), zeroDigit, palindromeList.size());
      // Have to account for the extra decrement at the end of the loop
      palindrome -= (11 * Math.pow(10.0, zeroDigit - 1) - decr);
    }
      getPalindromicNumbersByDecrementRecursive(magnitude - 1, palindromeList);
  }

  // Brute force is pretty slow for anything much over magnitude = 7.
  // You get some sort of numeric overflow for any magnitude > 9.
  private static boolean isValidMagnitude(int magnitude, AlgorithmType type) {
    boolean result;
    switch (type) {
      case BRUTE_FORCE: result = magnitude < 8; break;
      case DECREMENT:
      case RECURSIVE:
      default: result = magnitude < 10;
    }
    if (!result) {
      ExerciseBaseClass.debugLevel(0, "Invalid magnitude for the " + type + " algorithm: " + magnitude);
    }
    return result;
  }

  private static void headerDebugMessages(int debugLevel, int magnitude, int start, int decrement) {
    ExerciseBaseClass.debugLevel(debugLevel, "");
    ExerciseBaseClass.debugLevel(debugLevel, "MAGNITUDE = " + magnitude);
    ExerciseBaseClass.debugLevel(debugLevel, "STARTING PALINDROME = " + start);
    ExerciseBaseClass.debugLevel(debugLevel, "PRIMARY DECREMENT = " + decrement);
    ExerciseBaseClass.debugLevel(debugLevel, "==============================================");
  }

  private static void loopDebugMessages(int debugLevel, int palindrome, int decr2, int zeroDigit, int size) {
    ExerciseBaseClass.debugLevel(debugLevel, "");
    ExerciseBaseClass.debugLevel(debugLevel, "PALINDROME = " + palindrome);
    ExerciseBaseClass.debugLevel(debugLevel, "Secondary decrement = " + decr2 +
        " (zeroes place = " + zeroDigit + "; list size = " + size + ")");
  }

  /*
  ** ==============================================================================================
  ** Notes about other attempted implementations:
  **
  ** I tried a version with two nested for-loops. The idea was to drain the middle and adjacent
  ** digits to zero and then work outward/leftward to the more significant digits. However this
  ** assumed that the "zero digit" would steadily work it's way leftward, which wasn't actually
  ** the case (it jumped around from the middle digit outward and then back again). The basic
  ** outline of the inner loops looked like:
  **
  ** for (int zeroesPlace = (magnitude + 1)/2 - 1; zeroesPlace > 0; zeroesPlace--) {
  **   for (int d1 = 9; d1 >= 0; d1--) {  // Tracks digit one magnitude higher than middle digit being drained to 0
  **     for (int d2 = 9; d2 >= 0; d2--) {  // Tracks the current middle digit being drained to 0
  **       results.add(palindrome);
  **       palindrome -= d2 > 0 ? decr : 11*(int)Math.pow(10.0, zeroesPlace - (d1 == 0 ? 2 : 1));
  **     }
  **   }
  ** }
  **
  ** I also had an issue with an "extra" decrement at the end of the for-loops. I tried fixing this
  ** by switching to a do/while loop, but you still seem to end up with an extra decrement.
  **
  ** The implementation below was this was the first working version.
  **
  ** 1. Outer loop decrements the magnitude (or power of 10) for the starting palindrome
  ** 2. Inner loop applies the standard decrement until the middle digit(s) hit zero
  ** 3. Translates current palindrome into array of digits and uses it to find the "zero digit"
  ** 4. Calculates the secondary decrement using the position of the zero digit
  ** 5. This loop exits when we hit the "floor" of the current magnitude (e.g. 1000001)
  ** 6. At this point we always apply a decrement of 2 (e.g. 100001 -> 99999)
  ** 7. I stop calculating at 11 (although, technically, 9-0 are palindromes as well)
  */
  static ArrayList<Integer> getPalindromeNumbersByDecrementOriginal(int magnitude) {
    ExerciseBaseClass.DEBUG_LEVEL = 1;
    ArrayList<Integer> results = new ArrayList<>();
    int palindrome = (int)Math.pow(10.0, magnitude) - 1;
    // This loop handles numbers of decreasing magnitude
    for (int m1 = magnitude; m1 > 1; m1--) {
      int decr = (int)(Math.pow(10.0, m1/2) + ((m1 % 2 == 0) ? Math.pow(10.0, m1/2 - 1) : 0));
      headerDebugMessages(0, magnitude, palindrome, decr);
      // This loop decrements the middle digit(s) of numbers in the current magnitude
      // ADJUST THIS TO GUARANTEE THAT PALINDROME = Math.pow(10.0, m1 - 1) + 1 AT THE END OF THE LAST LOOP?
      while (palindrome > Math.pow(10.0, m1 - 1) + 1) {
        for (int i = 0; i < 9 && palindrome > 11; i++, palindrome -= decr) {
          ExerciseBaseClass.debugLevel(2, Integer.toString(palindrome));
          results.add(palindrome);
        }
        ExerciseBaseClass.debugLevel(1, Integer.toString(palindrome));
        results.add(palindrome);
        if (palindrome > Math.pow(10.0, m1 - 1) + 1) {
          // Determine the secondary decrement based on which magnitude digit is changing
          ArrayList<Integer> digits = ProjectEuler.convertNumberToDigits(palindrome);
          int zeroDigit = 0;
          while (zeroDigit < digits.size()/2 && digits.get(zeroDigit) != 0) { zeroDigit++; }
          ExerciseBaseClass.debugLevel(1, "Digits = " + digits);
          loopDebugMessages(1, palindrome, 11*(int)Math.pow(10.0, zeroDigit - 1), zeroDigit, results.size());
          palindrome -= 11 * Math.pow(10.0, zeroDigit - 1);
        }
      }
      palindrome -= 2;
    }
    return results;
  }
}

import java.util.Stack;

/**
 * Exercise from interview with Apple on 2017-01-18:
 *
 * Implement a program to count the longest contiguous string of well-formed
 * parentheses (i.e. where each open paren has a corresponding close paren).
 */
public class AppleExercise1 {

  public static int countLongestValidParensString1(String str) {
    // Define the Stack at the class level and reset and reuse it?
    // Try recursion (might help with recreating the string)?
    // Can I figure out when we're out of matches and return the count then?
    int parensCount = 0;
    Stack openParens = new Stack<Character>();
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == '(') {
        openParens.push(str.charAt(i));
      }
      else if (str.charAt(i) == ')' && openParens.size() > 0) {
        openParens.pop();
        parensCount += 2;
      }
      else if (parensCount > 0) {
        return parensCount;
      }
    }
    return parensCount;
  }

  public static int countLongestValidParensString2(String str) {
    Stack openParens = new Stack<Integer>();
    Integer initialOpenParenIndex = null;
    Integer finalCloseParenIndex = null;
    for (int i = 0; i < str.length(); i++) {
      if (str.charAt(i) == '(') {
        openParens.push(i);
      }
      else if (str.charAt(i) == ')' && openParens.size() > 0) {
        finalCloseParenIndex = i;
        Integer openParenIndex = (Integer)openParens.pop();
        if (initialOpenParenIndex == null || openParenIndex < initialOpenParenIndex) {
          initialOpenParenIndex = openParenIndex;
        }
      }
    }
    if (initialOpenParenIndex == null || finalCloseParenIndex == null) {
      return 0;
    }
    String wellFormedParensString = str.substring(initialOpenParenIndex, finalCloseParenIndex + 1);
    System.out.println("Found well-formed parens string: " + wellFormedParensString);
    return wellFormedParensString.length();
  }

  public static void main(String[] args) {
    // Try testing non-paren characters, empty string, null
    final String[] testStrings = { "((()", ")()())", "((()())())", "()", "(", ")", "", "a" };
    // Expected answer: 2, 4, 10, 2, 0, 0, 0, 0
    for (String str : testStrings) {
      System.out.println("Count for string (1) '" + str + "' = "
          + countLongestValidParensString1(str));
      System.out.println("Count for string (2) '" + str + "' = "
          + countLongestValidParensString2(str));
    }
  }
}

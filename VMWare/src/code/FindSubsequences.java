package code;

import java.util.ArrayList;
import java.util.Arrays;
//
public class FindSubsequences {
  // ArrayList<String> subsequences;
  static String[] findSubsequences( String input ) {
    ArrayList<String> subsequencesList = new ArrayList<String>();
    for ( int i = 0; i < input.length(); i++ ) {
      String baseString = input.substring( 0, i );
      for ( int j = i; j < input.length(); j++ ) {
        subsequencesList.add( baseString + input.charAt(j) );
      }
    }
    String[] subsequences = subsequencesList.toArray(new String[0]);
    if ( false ) {
      for (String s : subsequences) {
        System.out.println(s);
      }
    }
    Arrays.sort(subsequences);
    return subsequences;
  }

  static String[] findSubsequences1( String input ) {
    ArrayList<String> subsequencesList = new ArrayList<String>();
    for ( int i = 0; i < input.length(); i++ ) {
      for ( int j = i; j < input.length(); j++ ) {
        subsequencesList.add(input.substring(i, j));
      }
    }
    String[] subsequences = subsequencesList.toArray(new String[input.length()]);
    Arrays.sort(subsequences);
    return subsequences;
  }

  public static void main( String[] args ) {
    for ( String input : new String[] { "abc", "abcd" } ){
      System.out.println( "Output for input string " + input + ":" );
      for (String s : findSubsequences(input)) {
        System.out.println(s);
      }
    }
    // System.out.println("SUBSTRING:" + "abc".substring(0,0));
  }
}

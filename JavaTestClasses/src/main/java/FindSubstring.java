
// I am assuming that null is not a valid substring, and that the empty
// string is not valid, either (at least in this case), because there
// is no legal index to return.
public class FindSubstring {
  static int indexOfSubstring( String mainString, String subString ) {
    if ( mainString != null && subString != null && mainString.length() > 0 && subString.length() > 0 ) {
      // A match must be found before there are fewer letters left in the
      // mainString than in the subString
      for (int i = 0; mainString.length() - subString.length() >= i; i++) {
        if (mainString.charAt(i) == subString.charAt(0)) {
          for (int j = 1; j < subString.length(); j++) {
            if (mainString.charAt(j + i) != subString.charAt(j))
              break;
          }
          return i;
        }
      }
    }
    return -1;
  }
}

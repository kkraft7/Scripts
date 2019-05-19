import java.util.Arrays;
import java.util.List;

/*
** Exercise #2 from codewars.com:
** Write a function that will receive two strings (n1 and n2), each representing an integer as a binary number.
** A third parameter will be provided (o) as a string representing one of the following operators: add, subtract, multiply.
** Your task is to write the calculate function so that it will perform the arithmetic and the result returned should be a
** string representing the binary result.
*/
public class StringBinaryCalculator {
  static List<Integer> powersOfTwo = Arrays.asList( 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048 );

  // Use dependency injection to specify conversion strategy?
  // Strategy pattern? Pass in a class or a function?
  // This fails for Strings longer than 12 characters?
  public static int convertBinaryStringToInt( String binaryString ) {
    int result = 0;
    // Start at the lowest significant digit
    char nextDigit = binaryString.charAt( binaryString.length() - 1 );
    // Replace twos with powersOfTwo?
    for ( int i = 0; i < binaryString.length(); i++ ) {
      if ( nextDigit == '1' ) {
        result += powersOfTwo.get(i);
      }
      else if ( nextDigit != '0' ) {
        // THROW EXCEPTION
      }
    }
/*
    for ( int i = 0, twos = 1; i < binaryString.length(); i++, twos *= 2 ) {
      char nextDigit = binaryString.charAt( binaryString.length() -i - 1 );
      if ( nextDigit == '1' ) {
        result += twos;
      }
      else if ( nextDigit != '0' ) {
        // THROW EXCEPTION
      }
    }
*/
    return result;
  }

  // Just use int div and mod?!
  public static String convertIntToBinaryString( int value ) {
    if ( value == 0 ) {
      return "0";
    }
    String result = "";
    int total = value;
    int exp = 0;  // could also be called "digits" or numberOfDigits
    while ( powersOfTwo.get(exp) <= (int)value/2 ) { exp++; }
/*
    for ( exp = 0; powersOfTwo[exp] <= value/2; exp++ ) {
      if ( exp == powersOfTwo.length - 1 ) {
        int[] tmpArray = new int[powersOfTwo.length*2];
        for ( int j = 0; j < tmpArray.length; j++ ) {
          tmpArray[j] = j < powersOfTwo.length ? powersOfTwo[j] : tmpArray[j-1]*2;
        }
      }
    }
*/
    for ( ; exp >= 0; exp-- ) {
      result += powersOfTwo.get(exp) <= total ? '1' : '0';
      total -= powersOfTwo.get(exp);
    }
    return result;
  }

  private void extendPowersOfTwo( int numberToAdd ) {

  }
}

// Take a string value specified in binary and convert it to an integer
class StringBinary {
  int intValue = 0;
  String stringVal;

  // Store the binary value, too? Is there a class or type for that?
  public StringBinary( String stringVal ) {
    this.stringVal = stringVal;
    int twosPlace = 1;
  }
}
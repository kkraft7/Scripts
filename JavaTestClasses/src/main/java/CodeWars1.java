
public class CodeWars1 {
  String name;

  public CodeWars1(String personName) {
    name = personName;
  }

  public String greet(String yourName) {
    // The solution here was that I had to switch the order of the arguments:
    return String.format("Hi %s, my name is %s", yourName, name );
  }

  public static Double multiply(Double a, Double b) {
    // The "solution" here was a missing semi-colon at the end of this line:
    return a * b;
  }

  // Interleave the contents of two arrays not necessarily of equal size
  public static int[] compoundArray(int[] a, int[] b){
    int[] result = new int[a.length + b.length];
    int[] longArray = a.length > b.length ? a : b;
    int shortArrayLength = a.length <= b.length ? a.length : b.length;

    for ( int i = 0; i < shortArrayLength; i++ ) {
      result[2*i] = a[i];
      result[2*i + 1] = b[i];
    }
    for ( int i = shortArrayLength; i < longArray.length; i++ ) {
      result[shortArrayLength + i] = longArray[i];
    }
    return result;
  }
}

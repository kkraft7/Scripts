
import java.util.*;

/**
*** Permute.java: Take in a phone number and print out all possible strings or
***     words based on the number-to-character keypad mapping.
*** (assigned by Ning for their interview).
**/

// MAKE A STATIC/SINGLETON VERSION OF THIS CLASS
public class Permute {
    private static HashMap<Character, char[]> keypadChars
        = new HashMap<Character, char[]>();
    static {
        keypadChars.put('0', new char[] { });
        keypadChars.put('1', new char[] { });
        keypadChars.put('2', new char[] { 'a', 'b', 'c' });
        keypadChars.put('3', new char[] { 'd', 'e', 'f' });
        keypadChars.put('4', new char[] { 'g', 'h', 'i' });
        keypadChars.put('5', new char[] { 'j', 'k', 'l' });
        keypadChars.put('6', new char[] { 'm', 'n', 'o' });
        keypadChars.put('7', new char[] { 'p', 'q', 'r', 's' });
        keypadChars.put('8', new char[] { 't', 'u', 'v' });
        keypadChars.put('9', new char[] { 'w', 'x', 'y', 'z' });
    }
    // What is the difference between initializing here or in the constructor?
    private String phoneNumber;
    private static int debugLevel = 1;
    private static boolean runTest = false;

    /**
    *** Doesn't do much (make this a singleton?)
    **/
    public Permute(String phoneNumber) {
        // Error-checking? Phone number must be 7 digit characters?
        // Just warn if phone number is not 7 or 10 digit characters?
        this.phoneNumber = phoneNumber;
    }

    // Make this static - taking a string - and make class a singleton?
    public void printPhonePermutations() {
        System.out.println("Phone keypad permutations for "
            + phoneNumber + ":");
        getPhonePermutations("", phoneNumber);
//      for ( String s : getPhonePermutations("", phoneNumber))
//          System.out.println(s);
    }

//  Could use a StringBuffer for number?
//  Is there some way to return the results in a List instead?
//  private List<String> getPhonePermutations(String s, String number) {
    private void getPhonePermutations(String s, String number) {

        if ( debugLevel > 1 ) {
            System.out.printf(
                "Inside getPhonePermutations: String = %s; Number = %s\n",
                s, number);
        } 

        if ( number.length() > 0 ) {

            char[] keyChars = keypadChars.get(number.charAt(0));
            // Handles case of 0 and 1 where there is no character mapping
            if ( keyChars == null || keyChars.length == 0 )
                getPhonePermutations(s, number.substring(1));

            for ( char c : keypadChars.get(number.charAt(0)))
                getPhonePermutations(s + c, number.substring(1));
        }
        else {
            System.out.println(s);
        }
    }

    public static void runTest() {
        debugLevel = 0;
    }

    public String toString() {
        // Temporary so this will compile
        return phoneNumber;
    }

    public static void main(String[] args) {

        if ( runTest ) {
            Permute.runTest();
            System.exit(0);
        }

        String phoneNumber = "";
        try {
            // Loop over all command-line arguments to handle spaces?
            // Strip out all non-numeric characters
            String[] phoneNumberArray = args[0].split("[^0-9]");
            for ( String s : phoneNumberArray )
                phoneNumber += s;

            if ( debugLevel > 0 ) {
                System.out.println("Original phone number: " + args[0]);
                System.out.println(" Updated phone number: " + phoneNumber);
            }

            Permute p = new Permute(phoneNumber);
            p.printPhonePermutations();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}


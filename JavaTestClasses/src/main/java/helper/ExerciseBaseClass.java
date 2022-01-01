package helper;

import java.text.SimpleDateFormat;
import java.util.Scanner;

public class ExerciseBaseClass {
  public static boolean DEBUG = false;
  public static int DEBUG_LEVEL = 0;

  public static void debug(String debugMessage) { if (DEBUG) { System.out.println(debugMessage); }}

  public static void debugLevel(int level, String debugMessage) {
    if (DEBUG_LEVEL >= level) {
      // CHANGE THIS TO A LOG CALL?
      System.out.println(debugMessage);
    }
  }

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
  public static String getCurrentTimestamp() {
    return dateFormat.format(System.currentTimeMillis());
  }
  public static void waitForInput() { new Scanner(System.in).nextLine(); }
}

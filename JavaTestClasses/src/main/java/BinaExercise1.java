
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;

/*
Problem:
Take in a log file representing people (numeric ID keys) and entrances
to and exits from a room (symbols ">" or "<") during a given day. Note
that the log covers an entire day and the room is assumed to start with
zero people in it. Here is an example log file:

entrance_log.txt

1234 >
2123 >
1234 <
123 >
2123 <
123 <

Validation Rules:
No people in the room at the end of the day
Person can’t be in the room more than once at the same time
Format of line (number space > or < strictly enforced on each line)

Input: String[] of the lines of the file
Output: true if file formatting and entries are valid, else false
(might want to be more sophisticated in the error reporting)
*/
// This is version 1.0
// First error for each user is flagged
// All user data (including errors) is saved in _history
// I think it makes sense to separate format or read errors from user errors
// -- They happen to have the same form at the moment but it might not stay that way
// Keep the 3 maps in a "super map"?
// Have a method for saving a log file plus its results?
public class BinaExercise1 {

  enum ErrorType {
    INVALID_FILE,      // Cannot open input file
    READ_ERROR,        // Error reading from file
    WRONG_ARG_NUM,     // 2 arguments per line
    INVALID_USERID,    // User ID is a positive integer
    INVALID_ACTION     // Action is ">" (enter) or "<" (leave)
  }

  private Map<String, List<String>> _formatErrors = new HashMap<String, List<String>>();  // Track formatting errors
  private Map<String, List<String>> _userErrors = new HashMap<String, List<String>>();  // Track user-specific errors
  private Map<String, List<String>> _history = new HashMap<String, List<String>>(); // Track entrances and exits
  // Make _inRoom a Set?
  private List<String> _inRoom = new ArrayList<String>();   // Track who is currently in the room

  public BinaExercise1() {}

  boolean validateEntranceLog(String inputLogFile) {
    // Path logPath = Paths.get(inputLogFile);
    // http://stackoverflow.com/questions/4716503/reading-a-plain-text-file-in-java
    // https://docs.oracle.com/javase/tutorial/collections/streams/reduction.html
    BufferedReader reader;
    try {
      // Files.newInputStream(logPath).
      reader = new BufferedReader(new FileReader(inputLogFile));
      String logLine = null;
      try {
        while ((logLine = reader.readLine()) != null) {
          // System.out.println("CURRENT LINE: " + logLine);
          if (logLine.matches("\\s*") || logLine.startsWith("//") || logLine.startsWith("#")) {
            continue;
          }

          String[] splitLine = logLine.split("\\s+");
          if (splitLine.length != 2) {
            addFormatOrReadError(ErrorType.WRONG_ARG_NUM.toString(), logLine);
            continue;
          }

          String userID = splitLine[0].trim();
          if (!userID.matches("\\d+")) {
            addFormatOrReadError(ErrorType.INVALID_USERID.toString(), logLine);
            continue;
          }

          String action = splitLine[1].trim();
          if (!action.matches(">") && !action.matches("<")) {
            addFormatOrReadError(ErrorType.INVALID_ACTION.toString(), logLine);
            continue;
          }

          if (!_userErrors.containsKey(userID) &&
              (_inRoom.contains(userID) && action.equals(">")) || (!_inRoom.contains(userID) && action.equals("<"))) {
            _userErrors.put(userID, null);  // Add a marker that history for userID is invalid
            if (_inRoom.contains(userID)) {
              _inRoom.remove(userID); // Stop tracking this user
            }
          }
          addHistory(userID, action); // Data for invalid users will be transferred to the _error map later
          if (!_userErrors.containsKey(userID)) {
            if (action.equals(">")) {
              _inRoom.add(userID);
            } else {
              _inRoom.remove(userID);
            }
          }
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
        addFormatOrReadError(ErrorType.READ_ERROR.toString(), logLine);
      }
    }
    catch(FileNotFoundException ex) {
      ex.printStackTrace();
      addFormatOrReadError(ErrorType.INVALID_FILE.toString(), inputLogFile);
    }
    // Transfer history for invalid users to the _userErrors map
    for ( String userID : _userErrors.keySet() ) {
      // System.out.println("Transferring history for user with error: " + userID);
      _userErrors.put(userID, _history.get(userID));
      _history.remove(userID);
    }
    // Transfer users remaining in the room to the _userErrors map
    for ( String userID : _inRoom ) {
      // System.out.println("Transferring history for user left in room: " + userID);
      _userErrors.put(userID, _history.get(userID));
      _history.remove(userID);
    }
    return _formatErrors.size() == 0 && _inRoom.size() == 0;
  }

  void displayResults() {
    // Add an optional boolean for multi-line output?
    System.out.println("DISPLAYING HISTORY FOR " + _history.size() + " USERS:");
    displayMapData(_history);
    System.out.println("DISPLAYING " + _userErrors.size() + " USER-SPECIFIC ERRORS:");
    displayMapData(_userErrors);
    System.out.println("DISPLAYING " + _formatErrors.size() + " FORMAT OR READ ERRORS:");
    displayMapData(_formatErrors);
  }

  void clearValidator() { // To reuse the same validator on a new log file
    _formatErrors.clear();
    _userErrors.clear();
    _history.clear();
    _inRoom.clear();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  private void addFormatOrReadError(String key, String value) { addEntry(_formatErrors, key, value); }

  private void addHistory(String userID, String action) { addEntry(_history, userID, action); }

  private void addEntry(Map<String, List<String>> map, String key, String value) {
    if (map.containsKey(key)) {
      map.get(key).add(value);
    }
    else {
      List<String> entry = new ArrayList<String >();
      entry.add(value);
      map.put(key, entry);
    }
  }

  // Have an optional boolean for multi-line output?
  private void displayMapData(Map<String, List<String>> map) {
    for (String key : map.keySet()) {
      System.out.print(key + ":");
      if (map.get(key) != null) {
        for (String entry : map.get(key)) {
          System.out.print(" " + entry);
        }
        System.out.println();
      }
    }
  }

  public static void main(String[] args) {
    String[] entranceLogs = {
        "C:/Users/Kevin/Documents/GitHub/JavaTestClasses/input/bina_entrance_log.01.txt",
        "C:/Users/Kevin/Documents/GitHub/JavaTestClasses/input/bina_format_errors.txt",
        "C:/Users/Kevin/Documents/GitHub/JavaTestClasses/input/bina_user_errors.txt"
    };
    BinaExercise1 processor1 = new BinaExercise1();
    for ( String fileName : entranceLogs ) {
      System.out.println("Processing log file: " + fileName);
      boolean results = processor1.validateEntranceLog(fileName);
      System.out.println("Log file validated successfully? " + results);
      processor1.displayResults();
      processor1.clearValidator();
      System.out.println();
    }
  }
}

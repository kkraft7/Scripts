
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
// This is version 2.0
// First error for each user is flagged
// All user data (including errors) is saved in _history
// I think it makes sense to separate format or read errors from user errors
// -- They happen to have the same form at the moment but it might not stay that way
// Keep the 3 maps in a "super map"?
// Have a method for saving a log file plus its results?
public class BinaExercise2 {

  enum ErrorType {
    INVALID_FILE,      // Cannot open input file
    READ_ERROR,        // Error reading from file
    WRONG_ARG_NUM,     // 2 arguments per line
    INVALID_USERID,    // User ID is a positive integer
    INVALID_ACTION,    // Action is ">" (enter) or "<" (leave)
    // User has left or entered the room twice in a row
    DUPLICATE_ACTION("USERS WITH DUPLICATE ACTIONS"),
    // User still is in room at the end of the log/day
    LEFT_IN_ROOM("USERS REMAINING IN ROOM"),
    // User tries to leave before they have entered
    EXIT_BEFORE_ENTRY("USERS LEAVING BEFORE ENTERING");

    private String _description;
    ErrorType() {}
    ErrorType(String description) { _description = description; }
    public String getDescription() { return _description; }
  }

  private Map<String, List<String>> _formatErrors = new HashMap<String, List<String>>();  // Track formatting errors
  private Map<String, List<String>> _history = new HashMap<String, List<String>>(); // Track entrances and exits
  private Map<String, List<String>> _errors = new HashMap<String, List<String>>();  // Track all errors in one map
  // Make _inRoom a Set?
  private List<String> _inRoom = new ArrayList<String>();   // Track who is currently in the room

  public BinaExercise2() {}

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
          // Need to test comments, too
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

          if (!hasErrors(userID)) {
            if (!_history.containsKey(userID) && action.equals("<")) {
              addUserError(ErrorType.EXIT_BEFORE_ENTRY, userID);
            }
            else if (duplicateAction(userID, action)) {
              addUserError(ErrorType.DUPLICATE_ACTION, userID);
              if (_inRoom.contains(userID)) {
                _inRoom.remove(userID); // Don't track invalid users
              }
            }
          }
          addHistory(userID, action);
          if (!hasErrors(userID)) {
            if (action.equals(">")) {
              _inRoom.add(userID);
            } else {
              _inRoom.remove(userID);
            }
          }
          // System.out.println("Users currently in room: " + Arrays.toString(_inRoom.toArray()));
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
    if ( _inRoom.size() > 0 ) {
      _errors.put(ErrorType.LEFT_IN_ROOM.toString(), _inRoom);
    }
    return _formatErrors.size() == 0 && _inRoom.size() == 0;
  }

  void displayResults() {
    // Add a boolean to displayErrors() to skip users with errors (and another boolean to print on separate lines?)
    // Need Java 8 to use stream() to filter a map!
    // http://www.mkyong.com/java8/java-8-filter-a-map-examples
    // Map<String, List<String>> validUsers = _history.entrySet().stream().filter
    // Can't use displayMapData(_history) here?
    System.out.println("USERS WITH NO ERRORS (" + (_history.size() - totalUserErrors()) + "):");
    for (String userID : _history.keySet()) {
      if (!hasErrors(userID)) {
        displayUserHistory(userID);
      }
    }
    displayErrors(ErrorType.LEFT_IN_ROOM);
    displayErrors(ErrorType.DUPLICATE_ACTION);
    displayErrors(ErrorType.EXIT_BEFORE_ENTRY);

    System.out.println("FORMAT OR READ ERRORS (" + _formatErrors.size() + "):");
    for (String errorKey : _formatErrors.keySet()) {
      System.out.print(errorKey + ":");
      for (String entry : _formatErrors.get(errorKey)) {
        // Should put a comma or some other separator between lines
        System.out.print(" " + entry);
      }
      System.out.println();
    }
  }

  void clearValidator() { // To reuse the same validator on a new log file
    _formatErrors.clear();
    _history.clear();
    _errors.clear();
    _inRoom.clear();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////
  private static final String[] userErrorKeys = new String[]{
      ErrorType.LEFT_IN_ROOM.toString(),
      ErrorType.DUPLICATE_ACTION.toString(),
      ErrorType.EXIT_BEFORE_ENTRY.toString()
  };

  private boolean hasErrors(String userID) {
    for (String errorKey : userErrorKeys) {
      if (_errors.containsKey(errorKey) && _errors.get(errorKey).contains(userID)) {
        // System.out.println("Found error " + errorKey + " for user " + userID);
        return true;
      }
    }
    return false;
  }

  private int totalUserErrors() {
    int total = 0;
    for (String errorKey : userErrorKeys) {
      if (_errors.containsKey(errorKey)) {
        total += _errors.get(errorKey).size();
      }
    }
    return total;
  }

  private boolean duplicateAction(String userID, String action) {
    if (_history.containsKey(userID)) {
      List<String> userHistory = _history.get(userID);
      return userHistory.get(userHistory.size()-1).equals(action);
    }
    return false;
  }

  private void addFormatOrReadError(String key, String value) { addEntry(_formatErrors, key, value); }

  private void addUserError(ErrorType errorKey, String userID) {
    addEntry(_errors, errorKey.toString(), userID);
  }

  private void addHistory(String key, String value) { addEntry(_history, key, value); }

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

  private void displayUserHistory(String userID) {
    System.out.print(userID + ":");
    for (String action : _history.get(userID)) {
      System.out.print(" " + action);
    }
    System.out.println();
  }

  private void displayErrors(ErrorType errorKey) {
    if (!_errors.containsKey(errorKey.toString())) {
      return;
    }
    System.out.println(errorKey.getDescription() + " (" + _errors.get(errorKey.toString()).size() + "):");
    for (String userID : _errors.get(errorKey.toString())) {
      displayUserHistory(userID);
    }
  }

  public static void main(String[] args) {
    String[] entranceLogs = {
        "C:/Users/Kevin/Documents/GitHub/JavaTestClasses/input/bina_entrance_log.01.txt",
        "C:/Users/Kevin/Documents/GitHub/JavaTestClasses/input/bina_format_errors.txt",
        "C:/Users/Kevin/Documents/GitHub/JavaTestClasses/input/bina_user_errors.txt"
    };
    BinaExercise2 processor1 = new BinaExercise2();
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

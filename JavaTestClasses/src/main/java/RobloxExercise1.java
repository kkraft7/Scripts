/**
 * Coding problem for an interview with Roblox, done by a 3rd-party test administrator (2019-02-09).
 *
 * You are a developer for a university. Your current project is to develop a system for students to find courses
 * they share with friends. The university has a system for querying courses students are enrolled in, returned
 * as a list of (ID, course) pairs.
 *
 * Write a function that takes in a list of (student ID number, course name) pairs and returns, for every pair of
 * students, a list of all courses they share.
 *
 * For the hard-coded input in the main class, below, the expected output (in pseudo-code) is:

   find_pairs(student_course_pairs) =>
   {
       [58, 17]: ["Software Design", "Linear Algebra"]
       [58, 94]: ["Economics"]
       [17, 94]: []
   }
 */

 import java.util.*;

 public class RobloxExercise1 {
  public static void main(String[] args) {
    String[][] studentCoursePairs = {
        {"58", "Software Design"},
        {"58", "Linear Algebra"},
        {"94", "Art History"},
        {"94", "Operating Systems"},
        {"17", "Software Design"},
        {"58", "Mechanics"},
        {"58", "Economics"},
        {"17", "Linear Algebra"},
        {"17", "Political Science"},
        {"94", "Economics"}
    };
    Map<String, List<String>> sharedCourses = getSharedCourses(studentCoursePairs);
    for (String studentPair : sharedCourses.keySet()) {
      System.out.println("Courses shared by " + studentPair + ": " + sharedCourses.get(studentPair));
    }
  }

  static Map<String, List<String>> buildEnrollmentMap(String[][] studentCoursePairs) {
    Map<String, List<String>> enrollmentMap = new HashMap<>();
    for (String[] pair : studentCoursePairs) {
      if (!enrollmentMap.containsKey(pair[1])) {
        List<String> course = new ArrayList<>();
        course.add(pair[0]);
        enrollmentMap.put(pair[1], course);
      }
      else {
        enrollmentMap.get(pair[1]).add(pair[0]);
      }
    }
    return enrollmentMap;
  }

  static List<String> buildStudentList(String[][] studentCoursePairs) {
    List<String> studentList = new ArrayList<>();
    for (String[] pair : studentCoursePairs) {
      if (!studentList.contains(pair[0])) {
        studentList.add(pair[0]);
      }
    }
    return studentList;
  }

  static Map<String, List<String>> getSharedCourses(String[][] studentCoursePairs) {
    List<String> studentList = buildStudentList(studentCoursePairs);
    Map<String, List<String>> enrollmentMap = buildEnrollmentMap(studentCoursePairs);
    Map<String, List<String>> sharedCourses = new HashMap<>();
    for (int i = 0; i < studentList.size(); i++) {
      for (int j = i; j < studentList.size(); j++) {
        String key = studentList.get(i) + "+" + studentList.get(j);
        if (!sharedCourses.containsKey(key) && !studentList.get(i).equals(studentList.get(j))) {
          List<String> courses = new ArrayList<>();
          sharedCourses.put(key, courses);
        }
      }
    }
    for (String studentKey : sharedCourses.keySet()) {
      String[] studentPair = studentKey.split("\\+");
      for (String course : enrollmentMap.keySet()) {
        if (enrollmentMap.get(course).contains(studentPair[0]) &&
            enrollmentMap.get(course).contains(studentPair[1])) {
          sharedCourses.get(studentKey).add(course);
        }
      }
    }
    return sharedCourses;
  }
}

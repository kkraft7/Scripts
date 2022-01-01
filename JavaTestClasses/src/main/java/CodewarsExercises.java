
import helper.ExerciseBaseClass;
import java.util.Arrays;

public class CodewarsExercises extends ExerciseBaseClass {

  /*
  The first century spans from the year 1 up to and including the year 100,
  The second - from the year 101 up to and including the year 200, etc.
  Task: Given a year (AD), return the century it is in.
  */
  public static int centuryFromYear(int year) {
    // Better solution is (year + 99)/100
   return (int)(year - 1)/100 + 1;
  }

  /*
  You will be given a number and you will need to return it as a string in Expanded Form. For example:
  Kata.expandedForm(12); # Should return "10 + 2"
  Kata.expandedForm(42); # Should return "40 + 2"
  Kata.expandedForm(70304); # Should return "70000 + 300 + 4"
  NOTE: All numbers will be whole numbers greater than 0.
  */
  public static String expandedForm(int num) {
    debugLevel(1, "Input number is " + num);
    String result = "";
    for (int power = 10; power < num; power *= 10) {
      int remainder = num % power;
      if (remainder != 0) {
        result = remainder + (result.equals("") ? "" : " + " + result);
        num -= remainder;
        debugLevel(2, "Found digit " + remainder + ", result string is currently " + result);
      }
    }
    result = num + (result.equals("") ? "" : " + " + result);
    debugLevel(1, "Final result is " + result);
    return result;
  }

  /*
  Is this a triangle?
  Implement a method that accepts 3 integer values a, b, c. The method should
  return true if a triangle can be built with the sides of given length and
  false in any other case. (In this case, all triangles must have surface
  greater than 0 to be accepted).

  I think the trick is that the total length of the two shorter sides must be
  greater than the longer side.
  */
  public static boolean isThisATriangle(int side1, int side2, int side3) {
    int[] sides = new int[3];
    sides[0] = side1;
    sides[1] = side2;
    sides[2] = side3;
    Arrays.sort(sides);
    return sides[0] + sides[1] > sides[2];
  }

  /*
  Create a function that returns the name of the winner in a fight between two fighters.
  Each fighter takes turns attacking the other and whoever kills the other first is victorious.
  Death is defined as having health <= 0. Each fighter will be a Fighter object/instance.
  See the Fighter class below in your chosen language. Both health and damagePerAttack will
  be integers larger than 0. You can mutate the Fighter objects.
  */
  public static String declareWinner(Fighter fighter1, Fighter fighter2, String firstAttacker) {
    if (!firstAttacker.equals(fighter1.name) && !firstAttacker.equals(fighter2.name)) {
      return null;
    }
    String currentAttacker = firstAttacker;
    while (fighter1.health > 0 && fighter2.health > 0) {
      if (currentAttacker.equals(fighter1.name)) {
        fighter2.health -= fighter1.damagePerAttack;
        currentAttacker = fighter2.name;
      }
      else {
        fighter1.health -= fighter2.damagePerAttack;
        currentAttacker = fighter1.name;
      }
    }
    return fighter1.health > 0 ? fighter1.name : fighter2.name;
  }

  class Fighter {
    public String name;
    public int health, damagePerAttack;
    public Fighter(String name, int health, int damagePerAttack) {
      this.name = name;
      this.health = health;
      this.damagePerAttack = damagePerAttack;
    }
  }
}

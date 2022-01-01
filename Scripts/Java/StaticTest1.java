
class ClassID {
  static int nextID = 1;
}

public class StaticTest1 {

  public static int ID = ClassID.nextID++;
 
  StaticTest1() {
//  ID = nextID++;
  }

  public static void main(String[] args) {
    System.out.println("StaticTest1.ID = " + StaticTest1.ID);
    StaticTest1 s1A = new StaticTest1();
    System.out.println("StaticTest1.ID = " + StaticTest1.ID);
    StaticTest1 s1B = new StaticTest1();
    System.out.println("StaticTest1.ID = " + StaticTest1.ID);
  }
}


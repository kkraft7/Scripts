
public class StaticTest2 extends StaticTest1 {

  public static int ID;
  static { ID = ClassID.nextID++; }
 
  StaticTest2() {
//  ID = StaticTest1.nextID++;
  }

  public static void main(String[] args) {
    System.out.println("StaticTest2.ID = " + StaticTest2.ID);
    StaticTest1 s1A = new StaticTest1();
    System.out.println("StaticTest2.ID = " + StaticTest2.ID);
    StaticTest1 s1B = new StaticTest1();
    System.out.println("StaticTest2.ID = " + StaticTest2.ID);
  }
}


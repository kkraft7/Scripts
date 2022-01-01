
public class StaticTest4 extends StaticTest1 {

  public static int ID;
  static { ID = ClassID.nextID++; }
 
  StaticTest4() {
//  ID = StaticTest1.nextID++;
  }

  public static void main(String[] args) {
    System.out.println("StaticTest4.ID = " + StaticTest4.ID);
    StaticTest1 s1A = new StaticTest1();
    System.out.println("StaticTest4.ID = " + StaticTest4.ID);
    StaticTest1 s1B = new StaticTest1();
    System.out.println("StaticTest4.ID = " + StaticTest4.ID);
  }
}


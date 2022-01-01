import org.testng.Assert;
import org.testng.annotations.Test;

public class CodeWars1Test {
  @Test
  public void testGreet() {
    CodeWars1 person = new CodeWars1( "Bob" );
    Assert.assertEquals( person.greet( "Fred" ), "Hi Fred, my name is Bob" );
  }

  @Test
  public void testCompoundArray1(){
    Assert.assertEquals(
        CodeWars1.compoundArray(new int[] {1,2,3,4,5,6}, new int[] {9,8,7,6}),
        new int[]{1,9,2,8,3,7,4,6,5,6});
  }

  @Test
  public void testCompoundArray2(){
    Assert.assertEquals(
        CodeWars1.compoundArray(new int[] {0,1,2}, new int[] {9,8,7,6,5,4,3,2,1,0}),
        new int[]{0,9,1,8,2,7,6,5,4,3,2,1,0});
  }
}

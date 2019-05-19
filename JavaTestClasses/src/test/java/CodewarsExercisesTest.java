
import org.testng.Assert;
import org.testng.annotations.Test;

public class CodewarsExercisesTest {

  @Test
  public void testCenturyFromYear() {
    int result = CodewarsExercises.centuryFromYear(1705);
    Assert.assertEquals(result, 18);
    result = CodewarsExercises.centuryFromYear(1900);
    Assert.assertEquals(result, 19);
    result = CodewarsExercises.centuryFromYear(1601);
    Assert.assertEquals(result, 17);
    result = CodewarsExercises.centuryFromYear(2000);
    Assert.assertEquals(result, 20);
  }

  @Test
  public void testExpandedNumericForm() {
    CodewarsExercises.DEBUG_LEVEL = 1;
    String result = CodewarsExercises.expandedForm(1);
    Assert.assertEquals(result, "1");
    result = CodewarsExercises.expandedForm(10);
    Assert.assertEquals(result, "10");
    result = CodewarsExercises.expandedForm(12);
    Assert.assertEquals(result, "10 + 2");
    result = CodewarsExercises.expandedForm(42);
    Assert.assertEquals(result, "40 + 2");
    result = CodewarsExercises.expandedForm(70304);
    Assert.assertEquals(result, "70000 + 300 + 4");
  }
}

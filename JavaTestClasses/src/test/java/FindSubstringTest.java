
import org.testng.Assert;
import org.testng.annotations.Test;

// I am assuming that null is not a valid substring, and that the empty
// string is not valid, either (at least in this case), because there
// is no legal index to return.
public class FindSubstringTest {

  @Test
  public void testSimplePositiveCase() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "abcdefg", "cde" ), 2 );
  }

  @Test
  public void testSubstringMatchAtFrontOfString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "abcdefg", "abc" ), 0 );
  }

  @Test
  public void testSubstringMatchAtEndOfString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "abcdefg", "efg" ), 4 );
  }

  @Test
  public void testOneCharacterMatchAtFrontOfString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "abcdefg", "a" ), 0 );
  }

  @Test
  public void testOneCharacterMatchAtEndOfString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "abcdefg", "g" ), 6 );
  }

  @Test
  public void testMatchingSubstringEqualToMainString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "abcde", "abcde" ), 0 );
  }

  @Test
  public void testSimpleNegativeCase() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "a", "b" ), -1 );
  }

  @Test
  public void testSimpleNegativeCaseWithMulticharacterString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "ab", "c" ), -1 );
  }

  @Test
  public void testSubstringLongerThanMainString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "ab", "abc" ), -1 );
  }

  @Test
  public void testEmptySubstring() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "ab", "" ), -1 );
  }

  @Test
  public void testEmptyMainString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "", "a" ), -1 );
  }

  @Test
  public void testEmptyMainStringAndSubstring() {
    // This fails with StringIndexOutOfBoundsException (0)
    Assert.assertEquals( FindSubstring.indexOfSubstring( "", "" ), -1 );
  }

  @Test
  public void testNullSubstring() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( "", null ), -1 );
  }

  @Test
  public void testNullMainString() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( null, "" ), -1 );
  }

  @Test
  public void testNullMainStringAndSubstring() {
    Assert.assertEquals( FindSubstring.indexOfSubstring( null, null ), -1 );
  }

  // Additional test cases:
  // More than one match in the string
}

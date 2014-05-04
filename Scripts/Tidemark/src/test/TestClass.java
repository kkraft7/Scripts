package test;

import java.util.*;
import com.*;

// A few unit tests, which could be re-written in JUnit form
public class TestClass {
    public static void main( String[] args ) {
        // Specify path relative to project root
        final String filePath = "src/data/";
        final HashMap<String, List<TextNode>> expectedResults = getExpectedResults();
        for (String fileName : expectedResults.keySet() ) {
            ArrayList<TextNode> results = Tidemark1.processText(filePath + fileName);
            System.out.println( "Text processing results for " + fileName + ":" );
            boolean pass = compareResults( results, expectedResults.get( fileName ));
            System.out.println( "TEST " + ( pass ? "PASSED" : "FAILED" ));
            Tidemark1.printReport(results);
        }
    }

    // This defines the expected results from processing the text files
    private static HashMap<String, List<TextNode>> getExpectedResults() {
        HashMap<String, List<TextNode>> expectedResults = new HashMap<String, List<TextNode>>();
        final String[] inputFiles = new String[]{ "data1.txt", "data2.txt", "data_empty.txt","data_one_word.txt",
                "data_not_found.txt", "data_whitespace.txt" };
        final TextNode[][] expectedResultLists = new TextNode[][] {
                { new TextNode( "The", 1 ), new TextNode( "fox", 1 ), new TextNode( "the", 1 ), new TextNode( "back", 1 ),
                        new TextNode( "lazy", 1 ), new TextNode( "over", 1 ), new TextNode( "brown", 2 ),
                        new TextNode( "dog's", 1 ), new TextNode( "quick", 1 ), new TextNode( "jumped", 1 ) },
                { new TextNode( "is", 1 ), new TextNode( "on", 1 ), new TextNode( "end", 4 ), new TextNode( "the", 1 ),
                        new TextNode( "Here", 1 ), new TextNode( "some", 1 ), new TextNode( "text", 1 ),
                        new TextNode( "with", 1 ), new TextNode( "lines", 1 ), new TextNode( "words", 2 ),
                        new TextNode( "multiple", 1 ), new TextNode( "repeated", 3 ) },
                {},
                { new TextNode( "word", 1 ) },
                {},
                { new TextNode( "word1", 1 ), new TextNode( "word2", 1 ), new TextNode( "word3", 1 ),
                        new TextNode( "word4", 1 ), new TextNode( "word5", 1 ), new TextNode( "word6", 1 ),
                        new TextNode( "word7", 1 ), new TextNode( "word8", 1 ) }
        };
        for ( int i = 0; i < inputFiles.length; i++ ) {
            expectedResults.put( inputFiles[i], Arrays.asList(expectedResultLists[i]));
        }
        return expectedResults;
    }

    // Note that using equals() on List<TextNode> results in an object ID comparison
    private static boolean compareResults ( List<TextNode> actual, List<TextNode> expected ) {
        if ( actual.size() != expected.size() ) {
            return false;
        }
        for ( int i = 0; i < actual.size(); i++ ) {
            if ( actual.get(i).equals( expected.get(i) )) {
                System.out.println( "Failed match: EXPECTED " + expected.get(i) + "; ACTUAL " + actual.get(i) );
                return false;
            }
        }
        return true;
    }
}

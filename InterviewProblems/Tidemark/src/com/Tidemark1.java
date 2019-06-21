package com;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;

public class Tidemark1 {
    private final static Logger LOGGER = Logger.getLogger( Tidemark1.class.getName() );

    public static ArrayList<TextNode> processText( String fileName ) {
        ArrayList<TextNode> wordReport = new ArrayList<TextNode>();
        HashMap<String, AtomicInteger> _wordCount = new HashMap<String, AtomicInteger>();
        try {
            BufferedReader reader = Files.newBufferedReader( Paths.get( fileName ), StandardCharsets.US_ASCII );
            String nextLine;
            while (( nextLine = reader.readLine()) != null ) {
                for ( String word : nextLine.split( "\\s+" )) {
                    if ( _wordCount.containsKey(word)) {
                        _wordCount.get( word ).getAndIncrement();
                    }
                    else {
                        _wordCount.put( word, new AtomicInteger( 1 ));
                    }
                }
            }
        }
        catch ( IOException fnf ) {
            LOGGER.log( Level.SEVERE, "I/O error opening input file to Tidemark1.processText() + " + fileName, fnf );
        }
        catch ( Exception ex ) {
            LOGGER.log( Level.SEVERE, "Error opening input file to Tidemark1.processText() + " + fileName, ex );
        }

        for ( String key : _wordCount.keySet() ) {
            wordReport.add( new TextNode( key, _wordCount.get( key ).intValue() ));
        }
        Collections.sort( wordReport );
        return wordReport;
    }

    public static void printReport( ArrayList<TextNode> wordReport ) {
        for ( TextNode node : wordReport ) {
            System.out.println( node.getCount() + " " + node.getWord() );
        }
        System.out.println();
    }

    // To run from the command line (src directory) do: java com.Tidemark1 data/FILE
    public static void main( String[] args ) {
        for ( String inputFile : args ) {
            System.out.println( "Text processing results for " + inputFile + ":" );
            ArrayList<TextNode> results = Tidemark1.processText(inputFile);
            Tidemark1.printReport(results);
        }
    }
}

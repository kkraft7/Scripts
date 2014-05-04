import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.io.*;
 
/*
*
* Copyright 2005 Google Inc.
* All Rights Reserved.
*
* Date (Original): February 9, 2006
* Java Class: Combine
*
* Given an input file of terms appearing per line, 
* this program combines each line with an operator 
* using either "=" or ">". Duplicates are filtered.
*
* Usage: java Combine infile outfile [-gt] [-s Integer]
* Option -gt is to indicate that ">" should be used.
* Otherwise the operator defaults to "=".
*
* Option -s is to indicate the approximate size in bytes
* of the output file.  The size is approximate because
* the final file size may exceed the given limit by a small
* amount.
*
* author = "Mitch Lee <mitchl@google.com>"
* author = "Kevin Kraft <kkraft@google.com>"
*
*/

class Global {
  static int debugLevel = 0;
}

public class Combine {

    /* Note: this is not backward compatible due to new "maxSize" parameter */
    static void combine(Collection c, String op, BufferedWriter bw, int maxSize)
            throws IOException {
        String word1, word2;
        int opLength = op.length();
        int numChars = 0;

        if ( Global.debugLevel > 0 ) {
            System.out.println("MAX SIZE: " + maxSize);
            System.out.println("OPERATOR: " + op);
        }

        for (Iterator i = c.iterator(); i.hasNext(); ) {
            word1 = (String) i.next();
            i.remove();
            for (Iterator j = c.iterator(); j.hasNext(); ) {
                word2 = (String) j.next();
                bw.write(word1 + op + word2);
                bw.newLine();
                if ( maxSize > 0 ) {
                    /* one char = one byte on OS */
                    numChars += ( word1.length() + word2.length() + opLength + 1 );
                    if ( numChars >= maxSize ) {
                      return;
                    } 
                }
            }
        }
    }

    static void usage() {
        System.out.println();
        System.out.println("USAGE: java Combine infile outfile [-gt] [-s Integer]");
        System.out.println("    Option -gt indicates that '>' should be used.");
        System.out.println("        Otherwise the operator defaults to '='.");
        System.out.println("    Option -s indicates the approximate size in bytes");
        System.out.println("        of the output file (the final file size may");
        System.out.println("        exceed the given limit by a small amount.)");
        System.out.println();
        System.exit(1);
    }

    static void usage(String message) {
        System.out.println("\nERROR: " + message);
        usage();
    }

    public static void main(String[] args) {
        Set<String> uniques = new HashSet<String>();
        String word = null;
        String inFileName  = null;
        String outFileName = null;
        boolean GTOP = false;
        String OP = "=";
        int maxSize = 0;
        Global.debugLevel = 0;

        if (args.length < 2) {
            usage();
        } else {
            inFileName  = args[0];
            outFileName = args[1];
                 
            for (int argNo = 2; argNo < args.length; argNo++) {

                if (args[argNo].startsWith("-gt")) {            
                    GTOP = true;
                } else if ( args[argNo].startsWith("-s")) {

                    ++argNo;

		    try {
			maxSize = Integer.parseInt(args[argNo]);
		    } catch (NumberFormatException exception) {
                        usage("non-integer value specified for size (-s) option: " +
                               args[argNo]);
		    } catch (ArrayIndexOutOfBoundsException exception) {
			usage("no value specified for size (-s) option");
                    }
		    
		} else {
		    usage("unrecognized option or parameter: " + args[argNo]);
                }
            }
        }

        try {
            BufferedReader inFile = new BufferedReader(new FileReader(inFileName));
            BufferedWriter outFile = new BufferedWriter(new FileWriter(outFileName));

            do {
    	        word = inFile.readLine();
    	        if (word == null) break; 
    	        word = word.trim();
                uniques.add(word);
            } while (true);

            if (GTOP) {
                OP = ">";
            }

   	    combine(uniques, OP, outFile, maxSize);
            inFile.close();
            outFile.close();

        /* Should try/catch only apply to BufferedReader initialization? */
        } catch (IOException exception) {
            System.out.println("Cannot open " + inFileName);
        }
    }
}



import java.io.*;
import java.net.*;
import java.util.*;

// Check that protocol = http if I get MalformedURLException
// Use Java Channels or java.nio to implement interruptable I/O?
// ReadingWebPagesWithNonBlockingChannels.htm
// I added readerID for Thread debug messages

/**
*** Download contents from a URL, in string or array form.
**/
// Extend the HttpUrlConnection class to get rid of all the accessors?
public class UrlReader {
    private URL url;
    private String stringContents = "";
    private ArrayList<String> arrayContents = new ArrayList<String>();
    private HttpURLConnection connection = null;
    private Boolean completed = false;
    private Double elapsedTime = 0.0;
    private static int nextReaderID = 0;
    protected static int debugLevel = 2;
    protected final int timeout;
    protected final int readerID;

    public UrlReader(String urlString, int timeout)
            throws MalformedURLException {
        url = new URL(urlString);
        readerID = nextReaderID++;
        this.timeout = timeout;
    }

    public UrlReader(String urlString)
            throws MalformedURLException, IOException {
        this(urlString, 0);
    }

    /**
    *** Open connection to URL and set timeouts.
    **/
    public BufferedReader openConnection(int connectTimeout)
            throws IOException {
        long currentTime = System.currentTimeMillis();

        if ( connection == null )
            connection = (HttpURLConnection)url.openConnection();
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));

// READ TIMEOUT DOESN'T SEEM TO WORK?
        connection.setReadTimeout(timeout*1000);
        connection.setConnectTimeout(connectTimeout*1000);

        debug(1, "Connection time: %.2f",
            (System.currentTimeMillis() - currentTime)/1000.0);
        return reader;
    }

    public BufferedReader openConnection() throws IOException {
        return openConnection(0);
    }

// FACTOR OUT COMMON CODE BETWEEN get*Contents() METHODS?
    /**
    *** Return URL contents in String form.
    **/
    public String getStringContents() throws IOException {
        debug(1, "Getting contents for URL: " + getURL());
        int nextChar = -1;

        if ( stringContents.length() == 0 ) {
            debug(3, "Reading new contents");
            BufferedReader reader = openConnection();
// CURRENTLY NOT INCLUDING CONNECTION TIME IN TIMEOUT CALCULATION
            long startTime = System.currentTimeMillis();

            while (( nextChar = reader.read()) != -1 &&
                    ( timeout == 0 || elapsedTime < timeout )) {
                stringContents += (char)nextChar;
                elapsedTime = (System.currentTimeMillis() - startTime)/1000.0;
                debug(3, "Elapsed read time: %.2f", elapsedTime);
            }
            reader.close();
            debug(2, bytesRead() + " byte(s) downloaded");
            debug(1, "Total read time: %.2f", elapsedTime);
        }

        handleCompletion( nextChar == -1 );
        return stringContents;
    }

    /**
    *** Return URL contents in List form.
    **/
    public List<String> getArrayContents() throws IOException {
        debug(1, "Getting contents for URL: " + getURL());
        String line = null;

        if ( arrayContents.size() == 0 ) {
            debug(3, "Reading new contents");
            BufferedReader reader = openConnection();
// CURRENTLY NOT INCLUDING CONNECTION TIME IN TIMEOUT CALCULATION
            long startTime = System.currentTimeMillis();
// NO WAY TO INTERRUPT THIS IN "TIMEOUT" SCHEME!

            while ((line = reader.readLine()) != null &&
                    ( timeout == 0 || elapsedTime < timeout )) {
                arrayContents.add(line);
                elapsedTime = (System.currentTimeMillis() - startTime)/1000.0;
                debug(3, "Elapsed read time: %.2f", elapsedTime);
            }
            reader.close();
            debug(2, linesRead() + " line(s) downloaded");
            debug(1, "Total read time: %.2f", elapsedTime);
        }

        handleCompletion( line == null );
        return arrayContents;
    }

    /**
    *** Partial factoring of the get*Contents() methods.
    **/
    private void handleCompletion(boolean completed) {
        this.completed = completed;
        debug(1, "Downloaded completed: " + completed);
        if ( ! completed ) {
            error("Read timed out after %.2f seconds (timeout %d s)",
                elapsedTime, timeout);
            // Discard partial results
            arrayContents.clear();
            stringContents = "";
        }
    }

    // Setters and getters
    public int getID() { return readerID; }
    public String getURL() { return url.toString(); }
    public int linesRead() { return arrayContents.size(); }
    public int bytesRead() { return 2*stringContents.length(); }
    public boolean completed() { return completed; }
    public double elapsedTime() { return elapsedTime; }
    public static int debugLevel() { return debugLevel; }
    public static void setDebugLevel(int level) { debugLevel = level; }

// MAKE CONNECTION "PROTECTED" IF I HAVE TOO MANY METHODS LIKE THIS?
// VIOLATES ENCAPSULATION?
    public int responseCode() throws IOException {
        return connection.getResponseCode();
    }

    public String responseMessage() throws IOException {
        return connection.getResponseMessage();
    }

    public String response() throws IOException {
        return responseCode() + " (" + responseMessage() + ")";
    }

//  This includes the readerID and is used for thread debugging
    public void debug(int level, String format, Object ... args) {
        message(
            level, String.format("READER %02d: ", readerID) + format, args);
    }

    public void error(String format, Object ... args) {
        debug(0, "ERROR: " + format, args);
    }

    public static void message(int level, String format, Object ... args) {
        if ( debugLevel >= level )
            System.out.format(format + "\n", args);
    }

    public static void message(String format, Object ... args) {
        message(0, format, args);
    }

    public static void main(String[] args) {
        String[] testURLs = args.length > 0 ? args : new String[] {
            "http://www.google.com",
            "http://www.google.bad",
            "http://www.google.com/unknown_page",
            "http://unknown.domain",
            "hijk://unknown.protocol",
            "http:malformed.url",
            "ftp://incorrect.protocol"
        };

        for ( String url : testURLs ) {
            message("DOWNLOADING URL: " + url);
            try {
                UrlReader r = new UrlReader(url);
                message("     LINES READ: " + r.linesRead());
                if ( r.responseCode() >= 400 )
                    message("  ERROR MESSAGE: " + r.response());
            }
            catch (Exception ex) {
                message("DOWNLOAD FAILED: " + ex);
                if ( debugLevel > 1 )
                    ex.printStackTrace();
            }
            System.out.println();
        }
    }
}


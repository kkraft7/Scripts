
import java.io.*;
import java.net.*;
import java.util.*;

// Check that protocol = http if I get MalformedURLException
// Can I use setReadTimeout()?!
// Add protected debug() method

public class UrlReader {
    private URL url;
    private ArrayList<String> contents = new ArrayList<String>();
    private HttpURLConnection connection;
    private boolean completed = false;
    protected int timeout;
    protected static int debugLevel = 2;

    public UrlReader(String urlString, int timeout)
//  public UrlReader(String urlString)
            throws MalformedURLException, IOException {
        url = new URL(urlString);
        connection = (HttpURLConnection)url.openConnection();
        this.timeout = timeout;
    }

    public UrlReader(String urlString)
            throws MalformedURLException, IOException {
        this(urlString, 0);
    }

    public String getURL() { return url.toString(); }

    public ArrayList<String> getContents() throws IOException {
        if ( debugLevel > 0 )
            System.out.println("Getting contents for " + getURL());

        String line = null;
        if ( contents.size() == 0 ) {
            long startTime = System.currentTimeMillis();
            double elapsed = -1.0;

            if ( debugLevel > 1 ) System.out.println("Reading new contents");
// NO WAY TO INTERRUPT THIS IN "TIMEOUT" SCHEME!
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(url.openStream()));

// NEED TO HANDLE 0 TIMEOUT...
            while ((line = reader.readLine()) != null && elapsed < timeout ) {
                contents.add(line);
                if ( timeout > 0 )
                    elapsed = (System.currentTimeMillis() - startTime)/1000.0;
                System.out.println(String.format("Elapsed Time %.2f", elapsed));
            }
            System.out.println(String.format("Elapsed Time %.2f", elapsed));
        }
        completed = ( line == null );

        if ( debugLevel > 0 )
            System.out.println(contents.size() + " lines downloaded");
        return contents;
    }

    public int responseCode() throws IOException {
        return connection.getResponseCode();
    }

    public String responseMessage() throws IOException {
        return connection.getResponseMessage();
    }

    public String response() throws IOException {
        return responseCode() + " (" + responseMessage() + ")";
    }

    public boolean completed() { return completed; }

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
        //  System.out.println("CONNECTING TO  : " + url);
            System.out.println("DOWNLOADING URL: " + url);
            try {
                UrlReader r = new UrlReader(url);
                int linesRead = r.getContents().size();
            //  System.out.println("CONTENTS OF URL:");
                System.out.println("     LINES READ: " + linesRead);
                if ( r.responseCode() >= 400 )
                    System.out.println("  ERROR MESSAGE: " + r.response());
            //  for ( String line : r.getContents() )
            //      System.out.println("  " + line);
            }
            catch (Exception ex) {
                System.out.println("DOWNLOAD FAILED: " + ex);
            }
            System.out.println();
        }
    }
}


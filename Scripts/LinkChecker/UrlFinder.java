
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

/**
*** Class for parsing and returning URLs from a dowloaded web page.
**/
public class UrlFinder extends UrlReader {
    private final Pattern urlPattern = Pattern.compile(
        "<a href=\"(http[s]?://.+?)\"",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public UrlFinder(String url) throws MalformedURLException, IOException {
        super(url);
    }

    /**
    *** Download web page, parse URLs, and return them in a list
    **/
    public List<String> getURLs() throws IOException {
        ArrayList<String> URLs = new ArrayList<String>();
        Matcher matcher = urlPattern.matcher(getStringContents());

        debug(1, "UrlFinder: Finding URLs on page: " + getURL());
// URLDecoder failed with "Illegal hex character" exception for raw HTML
//      debug(3, "URL contents: " + URLDecoder.decode(contents, "UTF-8"));
        if ( debugLevel > 2 )
            System.out.println("URL contents: " + getStringContents());

        while ( matcher.find() ) {
            URLs.add(matcher.group(1));
            debug(1, "Found URL: "
                + URLDecoder.decode(matcher.group(1), "UTF-8"));
        }
        debug(1, "UrlFinder: Found " + URLs.size() + " URLs");
        return URLs;
    }

    public static void main(String[] args) {
        debugLevel = 1;
        try {
// Google page as downloaded differs from "view source"!
            UrlFinder finder = new UrlFinder("http://www.google.com");
            for ( String url : finder.getURLs() )
                System.out.println("Found URL: " + url);
        }
        catch (Exception ex) {
            System.out.println("UrlFinder failed with exception: " + ex);
            if ( debugLevel > 1 )
                ex.printStackTrace();
        }
    }
}


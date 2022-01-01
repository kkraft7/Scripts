
import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.util.concurrent.*;

/**
*** Event representing web page download status. Consists of the URL and
*** it's status, represented as a Color object (green for a successful
*** download, red for failure, and yellow for still-running).
**/
class ReaderStatusEvent extends EventObject {
    final String url;
    final Color status;

    public ReaderStatusEvent(Object source, String url, Color status) {
        super(source);
        this.url = url;
        this.status = status;
    }

    public String getURL() { return url; }
    public Color getStatus() { return status; }
}

/**
*** Interface to register for download status events.
**/
interface ReaderStatusListener {
    public void statusChanged(ReaderStatusEvent event);
}

// SHOULD THE TIMEOUT BE FOR THE READ ONLY OR FOR THE READ + CONNECT?!
/**
*** Thread for downloading web pages in parallel, and firing off a
*** download status event when finished.
**/
public class UrlReaderTask extends UrlReader implements Runnable {
    private Color status = Color.yellow;
    private ArrayList<ReaderStatusListener> listeners
        = new ArrayList<ReaderStatusListener>();

    public UrlReaderTask(String url, int timeout)
            throws InterruptedException, MalformedURLException, IOException {
        super(url, timeout);
    }

    public UrlReaderTask(String urlString)
            throws InterruptedException, MalformedURLException, IOException {
        this(urlString, 0);
    }

// INCLUDE CONNECTION TIME IN TIMEOUT?
    /**
    *** Downloads web page and reports status via an event sent to listeners.
    **/
    public void run() {
        try {
            getStringContents();
// SET THIS IN UrlReader SUPERCLASS?
            status = ( responseCode() < 400 && completed() ?
                Color.green : Color.red );
            debug(3, "Response code is " + responseCode());
        }
        catch (Exception ex) {
            status = Color.red;
            error("EXCEPTION DURING DOWNLOAD: " + ex);
            if ( debugLevel > 1 )
                ex.printStackTrace();
        }
        finally {
            fireStatusEvent();
        }
    }

    /**
    *** Adds a new download status event listener.
    **/
    public synchronized void addStatusListener(ReaderStatusListener l) {
        listeners.add(l);
    }

    /**
    *** Sends a new download status event to the registered listeners.
    **/
    private synchronized void fireStatusEvent() {
        ReaderStatusEvent event
            = new ReaderStatusEvent(this, getURL(), status);
        for ( ReaderStatusListener l : listeners )
            l.statusChanged(event);
    }

    public Color status() { return status; }

    public static void main(String[] args) {
        String[] testURLs = args.length > 0 ? args : new String[] {
            "http://www.google.com",
            "https://login.yahoo.com",
            "http://mail.yahoo.com",
            "http://bad.domain.com"
        };
// CLASHES WITH PASSING URLS ON COMMAND LINE
/*
        if ( args.length > 0 ) {
            try {
                debugLevel = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException ex) {
                System.out.println(ex + ": Invalid debug level: " + args[0]);
            }
        }
*/
        JFrame gui = new JFrame("URL Reader Task Test");
        gui.setSize(450, 450); 	// Size is bigger to account for title bar
        Container frontEnd = gui.getContentPane();

        for ( int timeout : new int[] { 1, 10, 30, 0 } ) {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            final PieChart pie = new PieChart(400, testURLs.length);
            frontEnd.add(pie);
            gui.setVisible(true);
            message("TIMEOUT (SECS) : " + timeout);
            for ( String url : testURLs ) {
                message("DOWNLOADING URL: " + url);
                try {
                    UrlReaderTask task = new UrlReaderTask(url, timeout);
                    task.addStatusListener( new ReaderStatusListener() {
                        public synchronized void statusChanged(
                                ReaderStatusEvent event) {
                            pie.incrementStatus(event.getStatus());
                        }
                    });
                    executor.execute(task);
                    message(2, "DOWNLOAD STATUS: " + task.status());
                }
                catch (Exception ex) {
                    message("DOWNLOAD FAILED: " + ex);
                    if ( debugLevel > 1 )
                        ex.printStackTrace();
                }
            }
            System.out.print("Press Enter to continue -->");
            try { System.in.read(); } catch (IOException ex) { }
        }
        System.exit(0);
    }
}


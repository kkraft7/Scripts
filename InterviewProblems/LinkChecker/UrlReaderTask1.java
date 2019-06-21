
import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.util.concurrent.*;

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

interface ReaderStatusListener {
    public void statusChanged(ReaderStatusEvent event);
}

// SHOULD THE TIMEOUT BE FOR THE READ ONLY OR FOR THE READ+CONNECT?!
public class UrlReaderTask extends UrlReader implements Runnable {
    private Color status = Color.yellow;
    private static int runningTasks = 0;
    private ArrayList<ReaderStatusListener> listeners
        = new ArrayList<ReaderStatusListener>();

    public UrlReaderTask(String url, int timeout)
            throws InterruptedException, MalformedURLException, IOException {
        super(url, timeout);
        long startTime = System.currentTimeMillis();
//      Thread task = new Thread(this);
// I am using a ExecutorService to run the Threads
//      task.start();
//      debug(3, "Thread %02d is %s", task.getId(), task.getState());
    }

    public UrlReaderTask(String urlString)
            throws InterruptedException, MalformedURLException, IOException {
        this(urlString, 0);
    }

// INCLUDE CONNECTION TIME IN TIMEOUT?
    public void run() {
        debug(3, "Incrementing running tasks");
        runningTasks++;
        debug(2, runningTasks + " thread(s) running");
//      fireStatusEvent();
        try {
            getStringContents();
// SET THIS IN UrlReader SUPERCLASS?
            status = ( responseCode() < 400 && completed() ?
                Color.green : Color.red );
            debug(3, "Response code is " + responseCode());
        }
        catch (Exception ex) {
            error("EXCEPTION DURING DOWNLOAD: " + ex);
            if ( debugLevel > 1 )
                ex.printStackTrace();
            status = Color.red;
        }
        finally {
        //  chart.incrementStatus(status);
            fireStatusEvent();
            debug(3, "Decrementing running tasks");
            runningTasks--;
        }
    }

    public synchronized void addStatusListener(ReaderStatusListener l) {
        listeners.add(l);
    }

    private synchronized void fireStatusEvent() {
        ReaderStatusEvent event
            = new ReaderStatusEvent(this, getURL(), status);
        for ( ReaderStatusListener l : listeners )
            l.statusChanged(event);
    }

    public static int runningTasks() { return runningTasks; }

// REPLACE WITH CYCLIC BARRIER?
    public static void waitForRunningTasks(int timeout) {
        // Give threads time to start...
        try { Thread.sleep(100); } catch (Exception ex) { }
        long startTime = System.currentTimeMillis();
        double elapsed = 0.0;

        message(1, "WAITING FOR " + runningTasks
            + " RUNNING TASK(S) TO COMPLETE (TIMEOUT " + timeout + " S)");
        while ( runningTasks > 0 && ( timeout == 0 || elapsed < timeout ))
            elapsed = (System.currentTimeMillis() - startTime)/1000.0;
        if ( runningTasks > 0 )
            message("Wait timed out after %.2f seconds (timeout %d s)",
                elapsed, timeout);
        else if ( debugLevel > 0 )
            message(1, "ALL TASKS COMPLETED");
    }

    public static void waitForRunningTasks() {
        waitForRunningTasks(0);
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
                    UrlReaderTask task= new UrlReaderTask(url, timeout);
                    task.addStatusListener( new ReaderStatusListener() {
                        public synchronized void statusChanged(
                                ReaderStatusEvent event) {
                            pie.incrementStatus(event.getStatus());
                        }
                    });
                    executor.execute(task);
//                  message("DOWNLOAD STATUS: " + task.status());
                }
                catch (Exception ex) {
                    message("DOWNLOAD FAILED: " + ex);
                    if ( debugLevel > 1 )
                        ex.printStackTrace();
                }
//              System.out.println();
            }
//          message("Before read()");
            UrlReaderTask.waitForRunningTasks();
            try { System.in.read(); } catch (IOException ex) { }
        }
        System.exit(0);
    }
}


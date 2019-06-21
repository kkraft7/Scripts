
import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

// Add ID field? PieChart? Error message?
public class UrlReaderTask extends UrlReader implements Runnable {
//  private int timeout;
    private Thread task;
    private Color status;
    private PieChart chart;
    private static int runningTasks = 0;

    public UrlReaderTask(String url, int timeout, PieChart chart)
            throws InterruptedException, MalformedURLException, IOException {
        super(url, timeout);
        status = Color.yellow;
        task = new Thread(this);
        task.start();
        this.chart = chart;
        this.chart.setSliceColor(0, status);
        long startTime = System.currentTimeMillis();
        task.join();
// CAN I MOVE SOME OR ALL OF THIS INTO RUN()? DON'T THINK SO
/*
        if ( timeout > 0 ) {
            for ( int i = 0; i < timeout && task.isAlive(); i++ )
                Thread.sleep(1000);
            if ( task.isAlive() ) {
                if ( debugLevel > 1 )
                    System.out.println("Attempting to interrupt download");
                task.interrupt();  // Bug - can fail for IO/IOStream?
                if ( debugLevel > 1 )
                    System.out.println("Decrementing running tasks");
                runningTasks--;
                status = Color.red;
                this.chart.setSliceColor(0, status);
            }
        }
        else
            task.join();
*/

//      if ( debugLevel > 0 )
//          System.out.println(String.format("Task ran for %5.2f seconds",
//              (System.currentTimeMillis() - startTime)/1000.0));
    }

    public UrlReaderTask(String urlString, PieChart chart)
            throws InterruptedException, MalformedURLException, IOException {
        this(urlString, 0, chart);
    }

    public void run() {
        if ( debugLevel > 1 )
            System.out.println("Incrementing running tasks");
        runningTasks++;
        if ( debugLevel > 1 )
            System.out.println(runningTasks + " thread(s) running");
        //  System.out.println("Running thread #" + runningTasks);
        try {
            getContents();
            status = ( responseCode() < 400 && completed() ?
                Color.green : Color.red );
            if ( debugLevel > 1 )
                System.out.println("Response code is " + responseCode());
        }
        catch (Exception ex) {
            System.out.println("EXCEPTION DURING DOWNLOAD: " + ex);
            status = Color.red;
        }
        finally {
            chart.setSliceColor(0, status);
            if ( debugLevel > 1 )
                System.out.println("Decrementing running tasks");
            runningTasks--;
        }
    }

    public static int runningTasks() { return runningTasks; }

    public Color status() { return status; }

    public static void main(String[] args) {
        String[] testURLs = args.length > 0 ? args : new String[] {
            "http://www.google.com"
//          "https://login.yahoo.com"
//          "http://mail.yahoo.com"
        };
        JFrame gui = new JFrame("URL Reader Task Test");
        gui.setSize(450, 450); 	// Size is bigger to account for title bar
        Container frontEnd = gui.getContentPane();

        for ( int timeout : new int[] { 1, 10, 30, 0 } ) {
            PieChart pie = new PieChart(400, testURLs.length);
            frontEnd.add(pie);
            gui.setVisible(true);
            for ( String url : testURLs ) {
                System.out.println("DOWNLOADING URL: " + url);
                System.out.println("    TIMEOUT (S): " + timeout);
                try {
                    UrlReaderTask task= new UrlReaderTask(url, timeout, pie);
                    System.out.println("DOWNLOAD STATUS: " + task.status());
                }
                catch (Exception ex) {
                    System.out.println("DOWNLOAD FAILED: " + ex);
                }
                System.out.println();
            }
            try { System.in.read(); } catch (IOException ex) { }
            // gui.setVisible(false);
            // frontEnd.remove(pie);
        }
    }
}



import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.util.concurrent.*;

// READ IN UI COMPONENT SIZES FOR GREATER FLEXIBILITY?
// SHOULD BE ABLE TO ENTER START URL ON UI
// COULD THIS BE A SINGLETON CLASS?

/**
*** Application that downloads the contents of a URL, crawls it looking
*** for sub-URLs, downloads the sub-URLs, and displays the progress and
*** results on a UI.
**/
public class LinkChecker {
    private DefaultListModel successModel = new DefaultListModel();
    private DefaultListModel failureModel = new DefaultListModel();
    private PieChart chart;
    private UrlFinder finder;
    private final int timeout = 10;  // Make this a constructor parameter
    private final int maxThreads;
    public static final int DEFAULT_TIMEOUT = 10;
    public static final int DEFAULT_MAX_THREADS = 5;

    public LinkChecker(String url, int maxThreads)
            throws MalformedURLException, IOException {
        finder = new UrlFinder(url);
        this.maxThreads = maxThreads;
        setupUI(url);
    }

    public LinkChecker(String url) throws MalformedURLException, IOException {
        this(url, DEFAULT_MAX_THREADS);
    }

    /**
    *** This downloads content from the main URL, downloads the sub URLs,
    *** and updates the status on the UI.
    **/
    public void crawl() {
        try {
            java.util.List<String> URLs = finder.getURLs();
            chart.setSlices(URLs.size());
            final CountDownLatch count = new CountDownLatch(URLs.size());
            ExecutorService exec = Executors.newFixedThreadPool(maxThreads);

            for ( String url : URLs ) {
                chart.incrementStatus(Color.yellow);
                UrlReaderTask reader = new UrlReaderTask(url, timeout);

                reader.addStatusListener( new ReaderStatusListener() {
                    public synchronized void statusChanged(
                            ReaderStatusEvent event) {
                        chart.incrementStatus(event.getStatus());
                        if ( event.getStatus().equals(Color.green) )
                            successModel.addElement(event.getURL());
                        if ( event.getStatus().equals(Color.red) )
                            failureModel.addElement(event.getURL());
                        count.countDown();
                    }
                });
                exec.execute(reader);
            }
            UrlReader.message(1, "WAITING AT COUNTDOWN LATCH");
//          count.await(5, TimeUnit.MINUTES);  // Doesn't compile?!
            count.await(300, TimeUnit.SECONDS);
            UrlReader.message(1, "COUNTDOWN LATCH PASSED");
        }
        catch (Exception ex) {
            UrlReader.message("Exception crawling %s: %s",
                finder.getURL(), ex);
        }
    }

// Should the UI be a separate class?
    /**
    *** Set up the LinkChecker UI.
    **/
    private void setupUI(String url) {  // Add height, width parameters?
        JFrame gui = new JFrame("Link Checker Test");
        Box frontEnd = Box.createVerticalBox();
//      JLabel header;
        JLabel header = new JLabel("DOWNLOADING URL: " + url);
        JPanel labelPanel = new JPanel();
        JPanel progressPanel = new JPanel();
        JList successList = new JList(successModel);
        JList failureList = new JList(failureModel);
        final int HORIZONTAL = 900;
        final int VERTICAL = 425;

// Use setFont() for larger, bold font
        gui.setContentPane(frontEnd);
//      header = new JLabel("DOWNLOADING URL: " + url);
        setComponentLayout(header, HORIZONTAL/3, 25);
        setComponentLayout(labelPanel, HORIZONTAL, 25);
        labelPanel.setLayout(new GridLayout(1, 3));
        labelPanel.add(new JLabel("Successful Downloads"));
        labelPanel.add(new JLabel("Download Progress"));
        labelPanel.add(new JLabel("Failed Downloads"));
        chart = new PieChart(HORIZONTAL/3 - 5, 0);
        setComponentLayout(progressPanel, HORIZONTAL, HORIZONTAL/3);
        progressPanel.setLayout(new GridLayout(1, 3));
        progressPanel.add(new JScrollPane(successList));
        progressPanel.add(chart);
        progressPanel.add(new JScrollPane(failureList));
        frontEnd.add(header);
        frontEnd.add(labelPanel);
        frontEnd.add(progressPanel);
        gui.setPreferredSize(new Dimension(HORIZONTAL, VERTICAL));
        gui.pack(); // THIS MAKES THE SCREEN TOO SMALL FOR SOME REASON
        gui.setVisible(true);
    }

    /**
    *** Set size and alignment for UI components.
    **/
    private void setComponentLayout(JComponent c, int width, int height) {
//  private void setComponentOrientation(JComponent c, int width, int height) {
        c.setMinimumSize(new Dimension(width, height));
//      c.setMaximumSize(new Dimension(width, height));
        c.setPreferredSize(new Dimension(width, height));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public static void main(String[] args) {
        if ( args.length > 0 )
            try { UrlReader.setDebugLevel(Integer.parseInt(args[0])); }
            catch (NumberFormatException ex) {
                UrlReader.message(ex + ": Invalid debug level: " + args[0]); }

// Apparently you are supposed to run Swing applications using invokeLater(),
// but the commented-out version below doesn't work.
//      SwingUtilities.invokeLater( new Runnable() {
//          public void run() {
        try {
            LinkChecker checker = new LinkChecker("http://www.google.com");
// FOR UI LAYOUT DEBUGGING
//          try { System.in.read(); } catch (IOException ex) { }
//          System.exit(0);
            checker.crawl();
        }
        catch (Exception ex) {
            UrlReader.message("LINK CHECKER FAILED: " + ex);
            if ( UrlReader.debugLevel() > 1 )
                ex.printStackTrace();
        }
//          }
//      });
        // Keep UI up until user presses Enter
        System.out.print("Press Enter key to exit program -->");
        try { System.in.read(); } catch (IOException ex) { }
        System.exit(0);
    }
}


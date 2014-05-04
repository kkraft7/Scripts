
import java.awt.*;
import java.util.*;
import javax.swing.*;

// Define a separate (inner) slice class?
// Have increment mode (grouping success and failure) and individual mode
//   (where an individual Thread is assigned to a particular PieChart slice)

/**
*** Class representing a progress bar as a pie chart.
**/
public class PieChart extends JPanel {
    private final int size;	// Configure pie chart to fit in panel
// NEXT 4 SHOULD IDEALLY BE FINAL, BUT I MOVED THEM OUT OF THE CONSTRUCTOR
    private int slices;
    private int intArcLength;
    private float arcLength;
    private float slop;
    private int successes = 0;
    private int failures = 0;
    private int notDone = 0;
    private ArrayList<Color> sliceColors = new ArrayList<Color>();

    // HAVE VERSION WITH DEFAULT SIZE?
    public PieChart(int size, int slices) {
        this.size   = size;
        setSize(size, size); // plus some fudge factor?
        setSlices(slices);
        UrlReader.message(2, "Arc length: %.2f", arcLength);
        UrlReader.message(3, "Total slop: %.2f", slop*slices);
    }

    public PieChart(int size) { this(size, 0); }

//  Not sure how to avoid redrawing the whole pie each time.
//  Drawing slices one at a time wipes out the previous slices.
    /**
    *** Draw the pie chart based on colors set in a list.
    **/
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Apparently this is standard
        Color previous = g.getColor();
        float slopSum = slop*2;  // Not sure why slop*2 works...
        if ( slices == 0 ) {  // Set initial PieChart to all white
            g.setColor(Color.white);
            g.fillOval(0, 0, size, size);
        }
        for ( int start = 0, i = 0; i < slices; i++ ) {
            g.setColor(sliceColors.get(i));
            g.fillArc(0, 0, size, size, start, intArcLength + (int)slopSum);
            start += intArcLength + (int)slopSum;
            slopSum += ( slopSum > 1 ? slop - 1 : slop );
            UrlReader.message(3, "START = %03d; SLOP = %.2f", start, slopSum);
        }
        g.setColor(previous);
    }

    /**
    *** Set the number of slices in the pie chart.
    ***
    *** I separated this out so I could lay out the pie chart on the UI
    *** before I knew the actual number of slices.
    **/
    public void setSlices(int slices) {
        this.slices  = slices;
        arcLength    = ( slices > 0 ? 360/(float)slices : 0 );
        intArcLength = (int)arcLength;
        slop = arcLength - intArcLength;
        for ( int i = 0; i < slices; i++ )
            sliceColors.add(i, Color.white);
    }

    public int getSlices() { return slices; }

// NOTE THAT STARTED + SUCCESSES + FAILURES SHOULD EQUAL SLICES!
// RESET TO 0 MAKES PIE CHART CYCLIC
// Have one method for each increment mode?
// 1. Green and Red update in opposite directions
// 2. Green and Red update in same direction (with green first)
// 3. Each slice tracks its own status
    /**
    *** Increment the progress chart based on a new status.
    *** This implements increment mode #2, above.
    **/
    public synchronized void incrementStatus(Color status) {
        if ( status.equals(Color.green) ) {
            successes++;
            notDone--;
            if ( failures > 0 ) // WHY DO I NEED THIS?!
                sliceColors.set(slices - successes - failures, Color.red);
            sliceColors.set(slices - successes, status);
            if ( successes == slices )
                successes = 0;
        }
        if ( status.equals(Color.red) ) {
            failures++;
            sliceColors.set(slices - successes - failures, Color.red);
            notDone--;
            if ( failures == slices )
                failures = 0;
        }
        if ( status.equals(Color.yellow) ) {
            notDone++;
            sliceColors.set(slices - notDone, status);
            if ( notDone == slices )
                notDone = 0;
        }

/*
// This is increment mode #1
        if ( status.equals(Color.green) ) {
            successes++;
            notDone--;
            sliceColors.set(getSlices() - successes, status);
            if ( successes == getSlices() )
                successes = 0
        }
        if ( status.equals(Color.red) ) {
            sliceColors.set(failures++, status);
            notDone--;
            if ( failures == getSlices() )
                failures = 0
        }
        if ( status.equals(Color.yellow) ) {
            notDone++;
            sliceColors.set(getSlices() - notDone, status);
            if ( notDone == getSlices() )
                notDone = 0
        }
*/
        repaint();
    }

// HAVE PAUSE TIME BE A FUNCTION OF NUMBER OF SLICES (SO TOTAL STAYS CONSTANT)
    public static void main(String[] args) throws InterruptedException {
        JFrame gui = new JFrame("Pie Chart Test");
        gui.setSize(450, 450); 	// Size is bigger to account for title bar
        PieChart pieChart = new PieChart(400, 17);
        Container frontEnd = gui.getContentPane();
        frontEnd.add(pieChart);
        gui.setVisible(true);
        final int pause = 500;	// Pause time, in milliseconds

        for ( int i = 0; i < pieChart.getSlices(); i++ ) {
            pieChart.incrementStatus(Color.yellow);
            Thread.sleep(pause);
        }

        for ( int i = 0; i < pieChart.getSlices(); i++ ) {
            pieChart.incrementStatus(Color.green);
            Thread.sleep(pause);
        }

        for ( int i = 0; i < pieChart.getSlices(); i++ ) {
            pieChart.incrementStatus(Color.red);
            Thread.sleep(pause);
        }
        System.exit(0);
    }
}


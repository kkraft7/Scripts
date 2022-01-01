
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Hashtable;

// Change class name to BMI Calc, BMIViewer?
// Experiment with laying out BMI Display horizontally
class BMI extends JFrame {
    public static void main(String[] args) {
        BMI window = new BMI();
        window.setVisible(true);
    }

// HARD-CODE LOCATION OF BMI RANGE LABELS?
//  USE LABELS FOR READ-ONLY TEXT?
// ADD A CLOSE/CANCEL BUTTON TO THE UI (BUTTON LISTENER)
// CONVERT ALL FIELDS WHEN SWITCH-UNITS BUTTON IS PRESSED?
// DEFINE A COMPONENT COMBINING LABEL, TEXTFIELD, VALUE, DEFAULT VALUE?
// - LabeledField - Extend Panel and specify orientation?
// MAKE MAX SLIDER VALUE A CONSTANT?
// final?
    private static final int DEFAULT_HEIGHT  = 200;    // Height in pixels
    private static final int DEFAULT_MIN_BMI = 10;
    private static final int DEFAULT_MAX_BMI = 35;
    private static final int TICK_INTERVAL   = 5;
    private static final double LOW_NORMAL   = 18.5;
    private static final int LOW_OVERWEIGHT  = 25;
    private static final int LOW_OBESE       = 30;
    private JSlider _bmiDisplay;  // CALL THIS _bmiSlider? _bmiBar?
    private ColorBar _bmiColors;
    private JTextField _heightMajorUnits = new JTextField(10);
    private JTextField _heightMinorUnits = new JTextField(10);
    private JTextField _weightMajorUnits = new JTextField(10);
//  USE LABELS FOR READ-ONLY TEXT?
    private JTextField _targetWeight     = new JTextField(5);
    private JTextField _bmiResult        = new JTextField(5);
    private Font bmiFont = new Font("Ariel", Font.BOLD, 12);
    private JLabel _heightLabel = new JLabel("Height");
    private JLabel _weightLabel = new JLabel("Weight");
    private JLabel _heightMajorLabel = new JLabel();
    private JLabel _heightMinorLabel = new JLabel();
    private JLabel _weightMajorLabel = new JLabel();
    private JLabel _bmiResultLabel = new JLabel("Your BMI is:");
    private JLabel _targetWeightLabel = new JLabel("Your Target Weight:");
    private JButton _bmiButton = new JButton("Calculate BMI");
    // LOOKS LIKE I NEED SOME KIND OF UNIT CLASS?!
    // Set to English units by default
    private JLabel _unitLabel = new JLabel();
    private JButton _unitButton = new JButton();
    private UnitType units = UnitType.ENGLISH;

//  NEED TO COMMENT - CONVERSION FROM/TO WHAT?
    private static int HEIGHT_CONVERSION_FACTOR = 12;
    private static int WEIGHT_CONVERSION_FACTOR = 703;
//  WILL BE FINAL EVENTUALLY...
//  private static final Hashtable<Integer, JLabel> displayLabels
    private static Hashtable<Integer, JLabel> displayLabels
        = new Hashtable<Integer, JLabel>();

    public BMI() {
        _bmiButton.addActionListener(new BMIListener());
        _unitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                units = ( units == UnitType.ENGLISH ?
                    UnitType.METRIC : UnitType.ENGLISH );
                setUnits(units);
            }
        });
        setUnits(units);  // This must come before UI layout?
        // This allows BMI button to be triggered by the Enter key
        getRootPane().setDefaultButton(_bmiButton);
//      displayLabels.put(0,    new JLabel("Underweight"));
//      displayLabels.put(1815, new JLabel("Normal"));
//      displayLabels.put(2500, new JLabel("Overweight"));
//      displayLabels.put(3000, new JLabel("Obese"));
        for ( Integer tick = DEFAULT_MIN_BMI; tick <= DEFAULT_MAX_BMI;
                tick += TICK_INTERVAL )
            displayLabels.put( tick*100, new JLabel( tick.toString() ));
        displayLabels.put((int)(LOW_NORMAL + DEFAULT_MIN_BMI)*50,
            new JLabel("Underweight"));
        displayLabels.put((int)(LOW_OVERWEIGHT + LOW_NORMAL)*50,
            new JLabel("Normal"));
        displayLabels.put((LOW_OBESE + LOW_OVERWEIGHT)*50,
            new JLabel("Overweight"));
        displayLabels.put((DEFAULT_MAX_BMI + LOW_OBESE)*50,
            new JLabel("Obese"));
        System.out.println("Manually-created display labels for JSlider:");
        System.out.println("Size of displayLables Hashtable: " +
            displayLabels.size());
        for ( Integer i : displayLabels.keySet() )
            System.out.print(String.format("%2d: %s\n",
                i, displayLabels.get(i).getText()));
//      displayLabels.clear();
// WOULD WORK HORIZONTALLY AS WELL? POSITION UNDER INPUT FIELDS?
        _bmiDisplay = new JSlider(
            SwingConstants.VERTICAL, DEFAULT_MIN_BMI*100, DEFAULT_MAX_BMI*100, (int)((LOW_OVERWEIGHT + LOW_NORMAL)/2)*100);
//          SwingConstants.VERTICAL, DEFAULT_MIN_BMI*100, DEFAULT_MAX_BMI*100, 2200);
/*
        displayLabels = _bmiDisplay.createStandardLabels(500);
        System.out.println("Automatically-created display labels for JSlider:");
        System.out.println("Size of displayLables Hashtable: " +
            displayLabels.size());
        for ( Integer i : displayLabels.keySet() )
            System.out.print(String.format("%2d: %s\n",
                i, displayLabels.get(i).getText()));
*/
        _bmiDisplay.setPaintLabels(true);
        _bmiDisplay.setLabelTable(displayLabels);
        _bmiDisplay.setPaintTicks(true);
        _bmiDisplay.setMajorTickSpacing(500);  // OR (int)(max/10)
        _bmiDisplay.setMinorTickSpacing(100);
        _bmiDisplay.setEnabled(false);  // No setEditable() for JSlider
//      JPanel _bmiPanel = new JPanel();
//      _bmiPanel.setSize(_bmiDisplay.getSize());
//      Graphics g = _bmiPanel.getComponentGraphics();

        int inset = 2;
        double sliderRange = DEFAULT_MAX_BMI - DEFAULT_MIN_BMI + 2*inset;
        ColorBlock[] colorList = new ColorBlock[] {
            new ColorBlock(Color.white, inset/sliderRange),
            new ColorBlock(Color.red,
                (DEFAULT_MAX_BMI - LOW_OBESE)/sliderRange),
            new ColorBlock(Color.orange,
                (LOW_OBESE - LOW_OVERWEIGHT)/sliderRange),
            new ColorBlock(Color.green,
                (LOW_OVERWEIGHT - LOW_NORMAL)/sliderRange),
            new ColorBlock(Color.yellow,
                (LOW_NORMAL - DEFAULT_MIN_BMI)/sliderRange),
            new ColorBlock(Color.white, inset/sliderRange)
        };
    //  _bmiColors = new ColorBar(colorList, _bmiDisplay.getSize());
        _bmiColors = new ColorBar(colorList, 100, 500);

        _bmiResult.setEditable(false);
        _bmiResult.setFont(bmiFont); // DO I NEED THIS?
        _targetWeight.setEditable(false);
        _targetWeight.setFont(bmiFont); // DO I NEED THIS?
        JPanel content = new JPanel();
    //  setUpFlowLayout1(content);
    //  setUpBorderLayout1(content);
    //  setUpBorderLayout2(content);
        setUpGroupLayout1(content);
        _bmiColors.repaint();
    //  _bmiColors.setSize(_bmiDisplay.getWidth(), getHeight());
        setContentPane(content);
        setTitle("Body Mass Index Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);    // Center window
    }

    public BMI(int height, int width) {
        this();
//      _bmiDisplay.setPreferredSize(new Dimension(20, height));
        _bmiDisplay.setSize(new Dimension(20, height));
        setSize(new Dimension(width, height));
    }

    public BMI(int height) { this(height, height*3); }

    private void setUpGroupLayout1(JPanel content) {
        GroupLayout layout = new GroupLayout(content);
        content.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(_unitLabel)
                .addComponent(_heightLabel, GroupLayout.Alignment.TRAILING)
                .addComponent(_weightLabel, GroupLayout.Alignment.TRAILING))
            .addGroup(layout.createParallelGroup()
                .addComponent(_unitButton)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(_heightMajorUnits)
                    .addComponent(_heightMajorLabel))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(_heightMinorUnits)
                    .addComponent(_heightMinorLabel))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(_weightMajorUnits)
                    .addComponent(_weightMajorLabel))
                .addComponent(_bmiButton))
        //  .addComponent(_bmiDisplay)
            .addComponent(_bmiColors)
            .addGroup(layout.createParallelGroup()
                .addComponent(_bmiResultLabel)
                .addComponent(_bmiResult)
                .addComponent(_targetWeightLabel)
                .addComponent(_targetWeight))
        );
        // Use GroupLayout.Alignment.TRAILING to align UI at bottom
        layout.setVerticalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                    .addComponent(_unitLabel)
                    .addComponent(_unitButton))
                .addGroup(layout.createParallelGroup()
                    .addComponent(_heightLabel)
                    .addComponent(_heightMajorUnits)
                    .addComponent(_heightMajorLabel))
                .addGroup(layout.createParallelGroup()
                    .addComponent(_heightMinorUnits)
                    .addComponent(_heightMinorLabel))
                .addGroup(layout.createParallelGroup()
                    .addComponent(_weightLabel)
                    .addComponent(_weightMajorUnits)
                    .addComponent(_weightMajorLabel))
                .addComponent(_bmiButton))
        //  .addComponent(_bmiDisplay)
            .addComponent(_bmiColors)
            .addGroup(layout.createSequentialGroup()
                .addComponent(_bmiResultLabel)
                .addComponent(_bmiResult)
                .addComponent(_targetWeightLabel)
                .addComponent(_targetWeight))
        );
        // Link widget sizes
        layout.linkSize(
            SwingConstants.HORIZONTAL, _unitButton, _heightMajorUnits);
        layout.linkSize(
            SwingConstants.HORIZONTAL, _unitButton, _heightMinorUnits);
        layout.linkSize(
            SwingConstants.HORIZONTAL, _unitButton, _weightMajorUnits);
        layout.linkSize(SwingConstants.HORIZONTAL, _unitButton, _bmiButton);
        layout.linkSize(SwingConstants.VERTICAL, _bmiButton, _heightMajorUnits);
        layout.linkSize(SwingConstants.VERTICAL, _bmiButton, _heightMinorUnits);
        layout.linkSize(SwingConstants.VERTICAL, _bmiButton, _weightMajorUnits);
        layout.linkSize(SwingConstants.VERTICAL, _bmiButton, _bmiResult);
        layout.linkSize(SwingConstants.VERTICAL, _bmiButton, _targetWeight);
    }

    private class BMIListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
// DEFINE A COMPONENT COMBINING LABEL, TEXTFIELD, VALUE, DEFAULT VALUE?
            double height1 = getDoubleFromTextfield(_heightMajorUnits);
            double height2 = getDoubleFromTextfield(_heightMinorUnits);
            double weight  = getDoubleFromTextfield(_weightMajorUnits);

            BmiModel bmi = new BmiModel(height1, height2, weight, units);
            double bmiValue = bmi.value();
            _bmiResult.setText(String.format("%.2f", bmiValue));
            _bmiDisplay.setValue((int)bmiValue*100);  // 2 SIGNIFICANT DIGITS
            _targetWeight.setText(String.format("%.2f", bmi.target()));
        }
    }

    private void setUnits(UnitType units) {
        if ( units == UnitType.ENGLISH ) {
            System.out.println("Setting units to English");
            _unitLabel.setText("English Units");
            _unitButton.setText("Switch to Metric");
            _heightMajorLabel.setText("Feet");
            _heightMinorLabel.setText("Inches");
            _weightMajorLabel.setText("Pounds");
            this.units = UnitType.ENGLISH;
        }
        else {
            System.out.println("Setting units to Metric");
            _unitLabel.setText("Metric Units");
            _unitButton.setText("Switch to English");
            _heightMajorLabel.setText("Meters");
            _heightMinorLabel.setText("Centimeters");
            _weightMajorLabel.setText("Kilograms");
            this.units = UnitType.METRIC;
        }
    }

    private double getDoubleFromTextfield(JTextField field) {
        String text = field.getText();
        double value;
        try {
            value = Double.parseDouble(text);
        }
        catch(NumberFormatException e) {
            // DISPLAY ERROR ON UI?
            System.out.format(
                "Setting invalid numeric value '%s' to 0\n", text);
            value = 0.0;
        }
        return value;
    }

    private enum UnitType { METRIC, ENGLISH };

// INCLUDE A setUnits() METHOD?
    private class BmiModel {
        private double _height;
        private double _weight;
        private double _targetWeight;
        private double _bmi;
        // Units default to English
        private int _heightConversionFactor = 12;
        private int _weightConversionFactor = 703;
//      private static final double LOW_NORMAL  = 18.5;
//      private static final int LOW_OVERWEIGHT = 25;
//      private static final int LOW_OBESE      = 30;

        public BmiModel(double heightMajorUnits, double heightMinorUnits,
                double weightMajorUnits, UnitType units ) {

            if ( units == UnitType.METRIC ) {
                _heightConversionFactor = 100;
                _weightConversionFactor = 10000;
            }
            _height = heightMajorUnits*_heightConversionFactor
                + heightMinorUnits;
            _weight = weightMajorUnits;

            // DISPLAY ERRORS ON UI?
            if ( _height == 0 )
                System.out.println("Please set height");
            if ( _weight == 0 )
                System.out.println("Please set weight");
            // Apparently division by 0 returns "Infinity"
            _bmi = _weight*_weightConversionFactor/(_height*_height);
            setTargetWeight(_bmi, _height);
        }

        public BmiModel(double heightMajorUnits, double heightMinorUnits,
                double weightMajorUnits ) {
            this(heightMajorUnits, heightMinorUnits, weightMajorUnits,
                UnitType.ENGLISH);
        }

        public double value() { return _bmi; }
        public double target() { return _targetWeight; }

        private void setTargetWeight(double bmi, double height) {
            double targetBMI =
                ( bmi >= LOW_OVERWEIGHT ? LOW_OVERWEIGHT :
                  bmi <  LOW_NORMAL     ? LOW_NORMAL : bmi );
            _targetWeight = height*height*targetBMI/_weightConversionFactor;
        }

        public boolean normal() {
            return ( _bmi >= LOW_NORMAL && _bmi < LOW_OVERWEIGHT );
        }
    }

    // NEED TO TRY AND LEFT-JUSTIFY SLIDER ON PANEL
    private class ColorBar extends JPanel {
        private ColorBlock[] colors;

        public ColorBar(ColorBlock[] colors, Dimension size) {
            setSize(size);
            this.colors = colors;
            ((FlowLayout)getLayout()).setAlignment(FlowLayout.LEFT);
            _bmiDisplay.setOpaque(false);
            add(_bmiDisplay);
        }

        public ColorBar(ColorBlock[] colors, int height, int width) {
            this(colors, new Dimension(width, height));
        }

        public void paintComponent(Graphics g) {
            double sum = 0.0;
        //  double lastY = 0.0;
            int lastY = 0;
        //  int height = _bmiDisplay.getHeight();
            System.out.println();
            System.out.println("In paint()");
            System.out.println("Height of JPanel: " + getHeight());
            System.out.println("Height of JSlider: " + _bmiDisplay.getHeight());
            System.out.println("Extent of JSlider: " + _bmiDisplay.getExtent());
/*
// INSETS ARE 0 AND BORDER IS NULL
            Insets ins = getInsets();
            System.out.println("JPanel inset top   : " + ins.top);
            System.out.println("JPanel inset bottom: " + ins.bottom);
            System.out.println("JPanel inset left  : " + ins.left);
            System.out.println("JPanel inset right : " + ins.right);
            // Border bord = getBorder();
            System.out.println("Border class: " + getBorder().getClass().getSimpleName());
*/
            for ( int i = 0; i < colors.length && lastY < getHeight(); i++ ) {
        //  for ( int i = 0; i < colors.length && lastY < height; i++ ) {
                g.setColor(colors[i].c);
                int height = (int)(getHeight()*colors[i].size);
                System.out.println("Color of segment : ( " +
                    colors[i].c.getRed() + ", " + colors[i].c.getGreen() +
                    ", " + colors[i].c.getBlue() + " )");
                System.out.println("Segment size (%) : " +
                    String.format("%.2f", colors[i].size));
                System.out.println("Height of segment: " + height);
            //  double height = getHeight()*colors[i].size;
            //  double h = height*colors[i].size;
                g.fillRect(0, (int)lastY, getWidth(), (int)height);
            //  g.fillRect(0, (int)lastY, getWidth(), (int)h);
                lastY += height;
            //  lastY += h;
            }
        //  _bmiDisplay.setSize(_bmiDisplay.getWidth(), getHeight());
        }
    }

    // SIZE SHOULD BE LESS THAN ONE
    private class ColorBlock {
        public final Color c;
        public final double size;

        public ColorBlock(Color c, double size) {
            this.c = c;
            this.size = size;
        }
    }

    ////////////////////////////////////
    // Previous UI layout experiments //
    ////////////////////////////////////
    private void setUpFlowLayout1(JPanel content) {
        content.setLayout(new FlowLayout());
        content.add(_unitLabel);
        content.add(_unitButton);
        content.add(_heightMajorLabel);
        content.add(_heightMajorUnits);
        content.add(_heightMinorLabel);
        content.add(_heightMinorUnits);
        content.add(_weightMajorLabel);
        content.add(_weightMajorUnits);
        content.add(_bmiButton);
        content.add(new JLabel("Your BMI is:"));
        content.add(_bmiResult);
        content.add(new JLabel("Your Target Weight:"));
        content.add(_targetWeight);
        content.add(_bmiDisplay);
    }

    // The BoxLayout makes the TextFields ridiculously large
    private void setUpBorderLayout1(JPanel content) {
        content.setLayout(new BorderLayout());
        Box unitFields = Box.createHorizontalBox();
// NEED TO CENTER THIS!
        unitFields.add(Box.createVerticalGlue());
        unitFields.add(_unitLabel);
        unitFields.add(_unitButton);
        unitFields.add(Box.createVerticalGlue());
        content.add(unitFields, BorderLayout.SOUTH);
//      Panel inputFields = new Panel();
        Box inputFields = Box.createVerticalBox();
// ADD "FILLER" TO TAKE UP SPACE? INPUT FIELDS ARE TOO LARGE
        inputFields.add(Box.createVerticalGlue());
        inputFields.add(_heightMajorLabel);
        inputFields.add(_heightMajorUnits);
        inputFields.add(_heightMinorLabel);
        inputFields.add(_heightMinorUnits);
        inputFields.add(_weightMajorLabel);
        inputFields.add(_weightMajorUnits);
        inputFields.add(Box.createVerticalGlue());
        inputFields.add(_bmiButton);
        content.add(inputFields, BorderLayout.WEST);
        Box bmiFields = Box.createHorizontalBox();
        bmiFields.add(Box.createVerticalGlue());
        bmiFields.add(new JLabel("Your BMI is:"));
        bmiFields.add(_bmiResult);
        bmiFields.add(new JLabel("Your Target Weight:"));
        bmiFields.add(_targetWeight);
        bmiFields.add(Box.createVerticalGlue());
        content.add(bmiFields, BorderLayout.NORTH);
        content.add(_bmiDisplay, BorderLayout.CENTER);
    }

    private void setUpBorderLayout2(JPanel content) {
        content.add(_unitLabel, BorderLayout.SOUTH);
        content.add(_unitButton, BorderLayout.SOUTH);
        content.add(_heightMajorLabel, BorderLayout.EAST);
        content.add(_heightMajorUnits, BorderLayout.EAST);
        content.add(_heightMinorLabel, BorderLayout.EAST);
        content.add(_heightMinorUnits, BorderLayout.EAST);
        content.add(_weightMajorLabel, BorderLayout.EAST);
        content.add(_weightMajorUnits, BorderLayout.EAST);
        content.add(_bmiButton, BorderLayout.EAST);
        content.add(_bmiDisplay, BorderLayout.CENTER);
        content.add(new JLabel("Your BMI is:"), BorderLayout.PAGE_START);
        content.add(_bmiResult, BorderLayout.PAGE_START);
        content.add(new JLabel("Your Target Weight:"), BorderLayout.PAGE_START);
        content.add(_targetWeight, BorderLayout.PAGE_START);
        content.add(_unitLabel, BorderLayout.PAGE_END);
        content.add(_unitButton, BorderLayout.PAGE_END);
        content.add(_heightMajorLabel, BorderLayout.EAST);
        content.add(_heightMajorUnits, BorderLayout.EAST);
        content.add(_heightMinorLabel, BorderLayout.EAST);
        content.add(_heightMinorUnits, BorderLayout.EAST);
        content.add(_weightMajorLabel, BorderLayout.EAST);
        content.add(_weightMajorUnits, BorderLayout.EAST);
        content.add(_bmiButton, BorderLayout.EAST);
        content.add(new JLabel("Your BMI is:"), BorderLayout.NORTH);
        content.add(_bmiResult, BorderLayout.NORTH);
        content.add(new JLabel("Your Target Weight:"), BorderLayout.NORTH);
        content.add(_targetWeight, BorderLayout.NORTH);
        content.add(_bmiDisplay, BorderLayout.CENTER);
    }
}


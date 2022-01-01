#!c:/Perl/bin/perl.exe
#
# pi_calc_mc.pl: Calculate PI using a Monte Carlo simulation.
#
#   DERIVATION OF EQUATION:
#
#   (1) Create a unit square with a quarter circle inscribed within
#       (i.e. the radius of the circle equals 1 side of the square)
#   (2) The area of the unit square is: r*r = 1*1 = 1
#   (3) The area of 1/4 of a circle is: (PI*r^2)/4 = PI/4
#   (4) The ratio between the areas is: (PI/4)/r*r = PI/4
#   (5) Therefore: PI = 4*ratio
#   (6) If we randomly pick points in the square the ratio will
#       equal the number of "hits" inside the circle divided by
#       the total number of "throws" or tries
#   (7) Therefore the algorithm for determining PI is:
#       (a) Pick a random x and y between 0 and 1
#       (b) If x^2 + y^2 < 1 then it is a "hit"
#       (c) Compute hits over throws and multiply by 4
#   (8) Some helpful source URLs:
#       http://www.eveandersson.com/pi/monte-carlo-circle
#       http://www.chem.unl.edu/zeng/joy/mclab/mcintro.html
#       http://polymer.bu.edu/java/java/montepi/MontePi.html
#       http://math.fullerton.edu/mathews/n2003/MonteCarloPiMod.html
#
# NOTE (01) An alternate probabilistic derivation is given at:
#           http://www.angelfire.com/wa/hurben/buff.html

# COULD HAVE OPTIONAL COMMAND LINE ARGUMENTS FOR PARAMETERS

use Math::Trig;		# Includes definition of pi
use File::Basename;
$C = basename($0);

$OUTPUT_DIR  = "c:/temp";
$OUTPUT_FILE = "pi_calc.dat";
$DEF_THROWS  = 10000;		# Number of random "dart throws"
$DEF_AVERAGE = 100;		# Number of pi calculations to average
$DEBUG_LEVEL = 0;

################################################################################
# numeric:                                                                     #
# Checks if the argument is numeric -- returns number if so, 0 otherwise.      #
# Note that this may fail if the number passed in is 0!                        #
################################################################################
sub numeric { defined($_[0]) && $_[0] =~ /^(\d+).*/ && $1 || 0; }

################################################################################
# usage:                                                                       #
# Print out a Help message detailing script usage.                             #
################################################################################
sub usage {
  print("\nUSAGE: $C [ -h|-d|-n <num_throws>|-f <output_file> ]\n\n");
  print("\t-h        ==> display this help/usage information\n");
  print("\t-d <num>  ==> level of debug tracing to implement\n");
  print("\t-n <num>  ==> number of throws for calculating PI\n");
  print("\t-a <num>  ==> number of loops to average the data\n");
  print("\t-f <file> ==> output data file for PI calculation\n\n");
  print("\tDEFAULT VALUES:\n");
  print("\tNumber of throws  (-n): $DEF_THROWS\n");
  print("\tNumber to average (-a): $DEF_AVERAGE\n");
  print("\t\n");
  exit(($_[0] =~ /[01]/) ? $_[0] : 0);
}

################################################################################
# error:                                                                       #
# Print out an error message.                                                  #
################################################################################
sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C: $_[0] (line #", __LINE__, ")\n");
  usage(1);
}

################################################################################
# Process command-line arguments.                                              #
################################################################################
for ($_ = shift(); /-[A-z]/; $_ = shift()) {
  /-h/ && (&usage(0),              next);	# display help/syntax message
  /-d/ && ($DEBUG_LEVEL = shift(), next);	# turn on debug; skip command
  /-n/ && ($NUM_THROWS  = shift(), next);	# number of throws in PI calc
  /-a/ && ($NUM_AVERAGE = shift(), next);	# number of loops to average
  /-f/ && ($OUTPUT_FILE = shift(), next);	# name of an output data file
  error("unknown option '$_'");
}

$NUM_THROWS  = numeric($NUM_THROWS)  || $DEF_THROWS;
$NUM_AVERAGE = numeric($NUM_AVERAGE) || $DEF_AVERAGE;

open(OUTPUT_FILE, ">>$OUTPUT_DIR/$OUTPUT_FILE") ||
  die("Unable to open '$OUTPUT_DIR/$OUTPUT_FILE' for writing\n");

if ( $DEBUG_LEVEL > 0 ) {
  print("Num. Throws: $NUM_THROWS\n");
  print("Output File: $OUTPUT_FILE/$OUTPUT_FILE\n");
  print("\n");
# exit(0);
}

################################################################################
#                                                                              #
################################################################################

$start_time = time();

printf("\nCALCULATING PI OVER %d LOOPS AND AVERAGING RESULTS %d TIMES:\n",
  $NUM_THROWS, $NUM_AVERAGE);

for ( $i = $sum_digit = $sum_pi = 0; $i < $NUM_AVERAGE; $i++ ) {

  for ( $j = 0, $hits = 0; $j < $NUM_THROWS; $j++ ) {

    $x = rand(1);	# produce a random number between 1 and 0
    $y = rand(1);

    if ( $DEBUG_LEVEL > 2 ) { printf("X = %9.7f; Y = %9.7f\n", $x, $y); }

    if ( $x**2 + $y**2 <= 1 ) { $hits++; }
  }

  $PI   = 4*$hits/$NUM_THROWS;
  $diff = abs(pi - $PI);
  for ( $k = $l = 1; $l > $diff; $l /= 10 ) { $k++; }

  if ( $DEBUG_LEVEL > 1 ) {
    printf("  Differs from PI in digit #%d (%f)\n", $k, $diff);
  }

  $sum_digit += $k;
  $sum_pi    += $PI;
}

printf("EXPECTED ANSWER: %f\n", pi);
printf("COMPUTED ANSWER: %f\n", $sum_pi/$NUM_AVERAGE);
printf("The average calculation differs in digit %5.3f (elapsed time %d seconds)\n",
  $sum_digit/$NUM_AVERAGE, time() - $start_time);
# print(OUTPUT_FILE);
close(OUTPUT_FILE);


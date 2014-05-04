#!c:/MKS/mksnt/perl.exe
#
# line_fit.pl: Perform a linear regression on a set of data points to
#              determine the best-fit line and r-squared correlation
#              coefficient for that line. Provides a weighting option
#              to repeat specified data points multiple times.

$data_dir = $ENV{'HOME'} . "/NCAA";
$WEIGHTED = FALSE;
chop($C = `basename "$0"`);

sub usage {
  print("USAGE: $C [ -h|-d|-w|-r <data_dir>] <data_file>\n\n");
  print("\t-h         ==> display this help message\n");
  print("\t-d         ==> do debug tracing and skip command\n");
  print("\t-w         ==> set all of the weights equal to one\n");
  print("\t-r <dir>   ==> the directory containing the data file\n");
  print("\t-f <file>  ==> a data file containing seed and year data\n\n");
  exit( ($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  print("\nERROR: $C\n\n\t$_[0]\n");
  usage(1);
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
  /-h/ && (&usage(0),               next);	# display help/syntax message
  /-d/ && ($DEBUG     = "TRUE",     next);	# turn on debug; skip command
  /-w/ && ($WEIGHTED  = "TRUE",     next);	# calculate weighted line fit
# /-w/ && ($WEIGHT1   = "TRUE",     next);	# set all of the weights to 1
  /-r/ && ($data_dir  = shift(),    next);      # directory for the data file
  error("unknown option '$_'");
}

if ( defined($_) ) {
  $data_file = $_;
}
else {
  error("Need to specify data file on command line\n");
}

$file_name = "${data_dir}/${data_file}";
open(DATA_FILE, "${file_name}") || die("Can't open '${file_name}' for reading\n");

$i = $sum_x = $sum_y = 0;

while (<DATA_FILE>) {

  if ( /^\s*#/ || /^\s*$/ ) { next; }		# skip comments & blank lines
  
  if ( /(\d+\.*\d*)\s+(\d+\.*\d*)\s+(\d+\.*\d*)*/ ) {
    
    ( $x_val, $percent, $weight ) = split();
    if ( "$WEIGHTED" ne "TRUE" ) { $weight = 1; }	# default weight is 1
    
    $sum_x += ( ${weight}*${x_val} );
    $sum_y += ( ${weight}*${percent} );
    
    for ($i0 = $i; ($i - $i0) < $weight; $i++) {
      $x_array[$i] = $x_val;
      $y_array[$i] = $percent;
    }
    if ( "$DEBUG" eq "TRUE" ) {
      print("X_VAL: $x_val; PERCENT: $percent; WEIGHT: $weight\n");
    }
  } else { print("Skipping line with non-numeric data\n"); }
  
}

$sum_xy = $sum_x_sqr = $diff_xy = $diff_x_sqr = $diff_y_sqr = 0;
$num_points = scalar(@x_array);
$x_avg = $sum_x/$num_points;
$y_avg = $sum_y/$num_points;

for ( $i = 0; $i < $num_points; $i++ ) {
    
  $sum_xy     += ( $x_array[$i] * $y_array[$i] );
  $sum_x_sqr  += ( $x_array[$i] * $x_array[$i] );
  $diff_xy    += (($x_array[$i] - $x_avg)*($y_array[$i] - $y_avg));
  $diff_x_sqr += (($x_array[$i] - $x_avg)*($x_array[$i] - $x_avg));
  $diff_y_sqr += (($y_array[$i] - $y_avg)*($y_array[$i] - $y_avg));
}

$delta = ${num_points}*${sum_x_sqr} - ${sum_x}*${sum_x};
$slope = (${num_points}*${sum_xy} - ${sum_x} *${sum_y})/$delta;
$y_int = (${sum_x_sqr} *${sum_y}  - ${sum_xy}*${sum_x})/$delta;
$r_sqr = ${diff_xy}*${diff_xy}/(${diff_x_sqr}*${diff_y_sqr});

print ("-----------------\n");
printf("EQUATION FOR LINE: Y = [%6.4f]*X + %6.4f\n", $slope, $y_int);
printf("CORRELATION (R^2): %6.4f\n", $r_sqr);
print ("TOTAL DATA POINTS: ${num_points}\n");
print ("-----------------\n");

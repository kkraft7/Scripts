#!c:/MKS/mksnt/perl.exe
#
# bracket.pl: Print out the bracket (seeds only) for a given NCAA tournament year.
#
# There is no round(a) function in Perl so I am using round(a) = int(a + 0.5)
#
# ADAPT FOR AND TEST WITH PARTIAL YEAR RESULTS (e.g. up to the 5th round)
# Is there a tranform for rotating the solution 90 or 180 degrees?!
# I am not using the "seed_list" variable initialized by the "-s" option!
# Print results to a file in c:/temp!

$data_file = "ncaa.dat";
$data_dir  = "c:/Documents and Settings/kkraft/My Documents/Scripts";

chop($C = `basename "$0"`);

sub usage {
  print("USAGE: $C [ -h|-d|-s <seed_totals>|-y <year>|-r <input_dir>|-f <input_file> ]\n\n");
  print("\t-h         ==> display this help message\n");
  print("\t-d         ==> do debug tracing and skip command\n");
  print("\t-s <seeds> ==> comma-separated list of seeds (round 2-7)\n");
  print("\t-y <year>  ==> the NCAA tournament year for the seed data\n");
  print("\t-f <file>  ==> a data file containing seed and year data\n\n");
  exit( ($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C\n\n\t$_[0]\n\tLine # ", __LINE__, "\n");
  usage(1);
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
# ( $_ eq "-d" || $DEBUG eq "TRUE" ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),               next);	# display help/syntax message
  /-d/ && ($DEBUG = TRUE,           next);	# turn on debug; skip command
  /-s/ && ($seed_list = shift(),    next);	# list of seed sums per round
  /-y/ && ($year      = shift(),    next);	# year of the NCAA tournament
  /-r/ && ($data_dir  = shift(),    next);      # directory for the data file
  /-f/ && ($data_file = shift(),    next);	# file holding seed/year data
  error("unknown option '$_'");
}

# CAN'T FIGURE OUT 2-D ARRAYS!
$file_name = "${data_dir}/${data_file}";
open(DATA_FILE, "${file_name}") || die("Can't open '${file_name}' for reading");

for (<DATA_FILE>) {
  if( ! /^\s*#/ && ! /^\s*$/ ) {	# TEST FOR COMMENTS OR BLANK LINES...
    ($y, $s) = split(/ /);    		# Year, Seed
    if ( $year eq $y ) {
      $seed_data{$y} = $s;
      last;
    }
  }
}

# Initialize index counters for each round:
$i2 = 0;
$i3 = 32;
$i4 = 48;
$i5 = 56;
$i6 = 60;
$i7 = 62;
@seeds = split(/,/, $seed_data{$year});
$games = scalar(@seeds);
# Warn if there are more than 63 games!
  
if ($DEBUG eq "TRUE") {
  print("File  : ${data_file}\n");
  print("Year  : ${year}\n");
  print("Games : ${games}\n");
  print("Seeds : @seeds\n");
# exit(0);
}

print("\nNCAA TOURNAMENT YEAR: ${year}\n");
# This currenly assumes 7 rounds and 63 games -- need to handle less!
for ( $i = 0; $i < $games; $i++ ) {

  if    ( ($i +  2) %   2 == 0 ) { $s =  0; $j = $i2++; }
  elsif ( ($i +  3) %   4 == 0 ) { $s =  2; $j = $i3++; }
  elsif ( ($i +  5) %   8 == 0 ) { $s =  4; $j = $i4++; }
  elsif ( ($i +  9) %  16 == 0 ) { $s =  6; $j = $i5++; }
  elsif ( ($i + 17) %  32 == 0 ) { $s =  8; $j = $i6++; }
  elsif ( ($i + 33) %  64 == 0 ) { $s = 10; $j = $i7++; }
  else  { print("ERROR: illegal index number: ${i}\n"); }
    
  $r = $s + 2;
  # print("Index1: ${i}; Round: ${r}; Spaces: ${s}; Index2: ${j}\n");
  printf("%s%2d\n", ' ' x $s, $seeds[$j]);
}

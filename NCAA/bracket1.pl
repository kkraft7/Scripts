#!c:/Perl/bin/perl.exe -w
#
# bracket.pl: Print out the bracket (seeds only) for a given NCAA tournament year.
#
# There is no round(a) function in Perl so I am using round(a) = int(a + 0.5)
#
# YEAR (-y) IS EFFECTIVELY MANDATORY (REMOVE AS AN OPTION OR DO MULTIPLE YEARS?)
# ADAPT FOR AND TEST WITH PARTIAL YEAR RESULTS (e.g. up to the 5th round)
# Is there a tranform for rotating the solution 90 or 180 degrees?!
# I am not using the "seed_list" variable initialized by the "-s" option!
# Print results to a file in c:/temp!

use File::Basename;
$C         = basename($0);
$data_file = "ncaa.dat";
$data_dir  = "c:/Documents and Settings/Kevin/My Documents/NCAA";

# AT THE MOMENT YEAR IS REQUIRED! (if ( $year eq $y ))
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

# CAN'T FIGURE OUT 2-D ARRAYS! (WHY DO I NEED THEM?)
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

@seeds = split(/,/, $seed_data{$year});
$games = scalar(@seeds);
chop($seeds[$games - 1]);
$r0 = int(log($games + 1)/log(2)) + 1;	# last round number (usually 7)
# Warn if there are more than 63 games!?
  
if ($DEBUG eq "TRUE") {
  print("File  : ${data_file}\n");
  print("Year  : ${year}\n");
  print("Rounds: ${rounds}\n");
  print("Games : ${games}\n");
  print("Seeds : @seeds\n");
# exit(0);
}

print("\nNCAA TOURNAMENT YEAR: ${year}\n");
# This currenly assumes 7 rounds and 63 games -- need to handle less!

$num_teams = 2**(7 - $r0);
$s0 = 47;		# initial number of spaces
$i  = $games - 1;
$iMAX = $games - $num_teams - 1;
for ( $r = $r0; $r > 1; $r-- ) {

    print(' ' x $s0 );
  
  if    ( $r == 7 ) { $s = 0; }
  elsif ( $r == 2 ) { $s = 1; }
  else  { $s = ${s0}*2; }
  
  while ( $i > $iMAX ) {
    # print("r = $r; iMAX = $iMAX; s0 = $s0; i = $i; seeds[i] = $seeds[$i--]; s = $s\n");
      printf("%s%2d%s", "", $seeds[$i--], ( ( $i == $iMAX ) ? "" : ' ' x $s ));
  }
  print("\n");
  $num_teams *= 2;
  $iMAX = $i - $num_teams;
  $s0 = ($s0 - 1)/2;
  # $num_teams += ${num_teams}*2;
}

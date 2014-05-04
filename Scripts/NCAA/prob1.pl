#!c:/Perl/bin/perl.exe -w
#
# prob.pl: calculate empirical and theoretical probablilities of one seed
#          beating another.

# Need to add in 32-team tournament data (pre-1985)? Only goes to 1979?!
# Add message like "$NO_DATA indicates no data available"
# 1/30.5 =~ 0.032787

$WEIGHT    = 0;		# Minimum # of games to calculate actual probabilities
$NO_DATA   = "  X ";	# The string to print when data is missing (or "  0 ")
$MAX_SEED  = 16;	# The maximum seed for a 64-team NCAA tournament field
$PROB_FACT = 1/30.5;	# Factor for assigning seed differential probabilities
$data_file = "ncaa.dat";
$data_dir  = "c:/Documents and Settings/kevin/My Documents/NCAA";

chop($C = `basename "$0"`);

sub usage {
  print("USAGE: $C [ -h|-d|-p <prob>|-w <int>|-r <data_dir>|-f <data_file> ]\n\n");
  print("\t-h         ==> print help message describing script usage\n");
  print("\t-d         ==> print out seed data to help with debugging\n");
  print("\t-p <prob>  ==> specify the theoretical probability factor\n");
  print("\t-w <int>   ==> weight factor for probability calculations\n");
  print("\t-r <dir>   ==> directory that contains the seed data file\n");
  print("\t-f <file>  ==> data file that contains seed and year data\n\n");
  exit( ($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  print("\nERROR: $C\n\n\t$_[0]\n\tLine # ", __LINE__, "\n");
  usage(1);
}

sub print_prob {
  
  local(@prob_array) = @_;	# THIS ASSUMES AN ARRAY LENGTH OF 256!
  print(  "    01  02  03  04  05  06  07  08  09  10  11  12  13  14  15  16\n");
  print(  "  +---------------------------------------------------------------\n");

  for ($i = 0; $i < $MAX_SEED; $i++) {
    printf("%s%d|", (( $i < 9 ) ? "0" : ""), $i + 1);
    for ($j = 0; $j < $MAX_SEED; $j++) {
      if ( ! defined($prob_array[$i*$MAX_SEED + $j]) ) {
        print($NO_DATA);
      }
      elsif ( $prob_array[$i*$MAX_SEED + $j] !~ /\d+/ ) {	# Non-numeric value
        print("$prob_array[$i*$MAX_SEED + $j]");
      }
      else { printf("%3d ", $prob_array[$i*$MAX_SEED + $j]); }
    }
    print("\n");
  }
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
# ( $_ eq "-d" || $DEBUG eq "TRUE" ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),                  next);	# display help/syntax message
  /-d/ && ($DEBUG     = "TRUE",        next);	# turn on debug; skip command
  /-p/ && ($PROB_FACT = eval(shift()), next);	# define a probability factor
  /-w/ && ($WEIGHT    = shift(),       next);	# min # games for probability
  /-r/ && ($data_dir  = shift(),       next);	# directory for the data file
  /-f/ && ($data_file = shift(),       next);	# file holding seed/year data
  error("unknown option '$_'");
}

$file_name = "${data_dir}/${data_file}";
open(DATA_FILE, "${file_name}") || die("Can't open '${file_name}' for reading\n");

for (<DATA_FILE>) {
  if( ! /^\s*#/ && ! /^\s*$/ && ! /^\s*sM\w\w/ ) {  # TEST FOR COMMENTS, BLANKS, SMIN, SMAX
    ($y, $s) = split(/ /);                                # Year, Seed
    if ( ! defined($year) || $year eq $y ) { $seed_data{$y} = $s; }
  }
}

foreach $year (sort(keys(%seed_data))) {
  
  @seeds = split(/,/, $seed_data{$year});
  $games = scalar(@seeds);
  chop($seeds[$games - 1]);
  if ( "$DEBUG" eq TRUE ) { print("YEAR: $year\n"); }
  
  for ($i = 0; $i < $games; $i++) {
  
    $winner = $seeds[$i];
    
    if ( $i < 32 ) {
      $loser = 17 - $winner;
    }
    else {
      $k = 2*($i - 32);
      $loser = ( $winner == $seeds[$k] ) ? $seeds[$k + 1] : $seeds[$k];
    }
    
    if ( "$DEBUG" eq TRUE ) {
      printf("INDEX: %2d; WINNER: %2d; LOSER: %2d\n", $i, $winner, $loser);
    }
    $prob_totals[($loser  - 1)*$MAX_SEED + $winner - 1]++;  # total games for winner
    $prob_totals[($winner - 1)*$MAX_SEED + $loser  - 1]++;  # total games for loser
    $wins_actual[($winner - 1)*$MAX_SEED + $loser  - 1]++;  # total wins for winner

    $diff_totals[$winner - $loser  + $MAX_SEED - 1]++;	# total games for winner
    $diff_totals[$loser  - $winner + $MAX_SEED - 1]++;	# total games for loser
    $diff_result[$winner - $loser  + $MAX_SEED - 1]++;	# total wins for winner
    
    $seed_totals[$winner - 1]++;	# total games for winning seed
    $seed_totals[$loser  - 1]++;	# total games for losing seed
    $seed_winner[$winner - 1]++;	# total wins for winning seed
    
  }
}

for ($i = 0; $i < $MAX_SEED*$MAX_SEED; $i++) {
  if ( defined($prob_totals[$i]) && $prob_totals[$i] > $WEIGHT ) {
    $prob_actual[$i] = int(100*$wins_actual[$i]/$prob_totals[$i] + 0.5);
  }
}

for ($i = 0; $i < $MAX_SEED; $i++) {
  for ($j = 0; $j < $MAX_SEED; $j++) {
    $prob_theory1[$i*$MAX_SEED + $j] = int(100*(0.5 - $PROB_FACT*($i - $j)) + 0.5);
  }
}

for ($i = 0; $i < $MAX_SEED; $i++) {
  for ($j = 0; $j < $MAX_SEED; $j++) {
    $prob_theory2[$i*$MAX_SEED + $j] = int(100*(($j + 1)/($i + $j + 2)));
  }
}

print("\n    TOTAL GAMES (HISTORICAL):\n");
print_prob(@prob_totals);

print("\n    WINNING SEEDS (HISTORICAL):\n");
print_prob(@wins_actual);

print("\n    ACTUAL PROBABILITIES (HISTORICAL):\n");
print_prob(@prob_actual);

print("\n    THEORETICAL PROBABILITIES (NORMALIZED DIFFERENTIAL):\n");
print_prob(@prob_theory2);

print("\n    THEORETICAL PROBABILITIES (SEED DIFFERENTIAL):\n");
print_prob(@prob_theory1);

print("\nABS SEED DIFF  FAVORITE WINS  UNDERDOG WINS  TOTAL GAMES  PERCENT\n");
print(  "-------------  -------------  -------------  -----------  -------\n");
for ($i = 0, $j = 2*$MAX_SEED - 2; $i < $MAX_SEED; $i++, $j--) {

  printf("           %2d            %3d            %3d          %3d  ",
    ($j + 1 - $MAX_SEED), $diff_result[$i], $diff_result[$j], $diff_totals[$i]);

  if ( defined($diff_totals[$i]) && $diff_totals[$i] > $WEIGHT*5 ) {
    printf(" %6.2f\n", (100*$diff_result[$i]/$diff_totals[$i])); 
  }
  else { print("      X\n"); }    
}

print("\nSEED  SEED WINS  TOTAL GAMES  PERCENT\n");
print(  "----  ---------  -----------  -------\n");
for ($i = 0; $i < $MAX_SEED; $i++) {

  printf("  %2d        %3d          %3d  ", ($i + 1), $seed_winner[$i],
    $seed_totals[$i]);

# if ( defined($seed_totals[$i]) && $seed_totals[$i] > $WEIGHT*5 ) {
  if ( defined($seed_totals[$i]) ) {
    printf(" %6.2f\n", (100*$seed_winner[$i]/$seed_totals[$i])); 
  }
  else { print("      X\n"); }    
}

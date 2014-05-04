#!c:/MKS/mksnt/perl.exe
#
# metric5.pl: Calculate the per-round diff metric for estimating the
#             level of upsets for a given NCAA tournament.
#
# CAN I GET RID OF MIN_INC SINCE IT IS EQUAL TO MIN_DIFF_TOT?!
# CAN'T USE THE FORMULA: $min_inc = int($min_inc/2); (0 FOR ROUNDS 6 AND 7)
# MIN_INC COULD BE 0 FOR ROUNDS 6 AND 7 IF I USE AN ARITHMATIC INCREMENT?!

# NOTE THAT MIN_AVG_SEED ISN'T NEGATIVE BECAUSE IT STARTS AT 8.5
# Change "$games" to "$games_played" or "$total_games"?
# A major mystery is why does $avg_diff_tot keep adding up to an odd integer?!
# Explain why the original metric is bogus: min/max diff per round based on
#   nMIN, nMAX is not the actual maximum theoretical diff!

# TABLE OF DIFF INCREMENTS:
#              +-----------------------------------+
# ROUND NUMBER |  2  |  3  |  4  |  5  |  6  |  7  |
#              |-----|-----|-----|-----|-----|-----|
# MIN DIFF PER | -8  | -8  |-12  | -14 | -15 | -15 |
# MIN DIFF TOT | -8  |-12  |-14  | -15 | -15 | -15 |
# MIN AVG SEED |  4.5|  2.5|  1.5|   1 |   1 |   1 |
# MIN INCRMENT | -8  | -4  | -2  |  -1 | 1/2 | 1/4 |
#              +-----------------------------------+

use File::Basename;
$C         = basename($0);
$DEBUG     = "FALSE";
$per_round = "FALSE";
$data_file = "ncaa.dat";
# $data_file = "metric5.dat";
$data_dir  = "c:/Documents and Settings/Kevin/My Documents/NCAA";

sub sort_key_by_value {
  local(%assoc_array) = @_;
  local(@list);		# MUST DEFINE THIS AS LOCAL FOR THIS SUBROUTINE TO WORK!
  $size = scalar(@_)/2;
  for ($i = 0; $i < $size; $i++) {
    # THIS FAILS FOR NEGATIVE VALUES (BECAUSE IT ASSUMES ONLY POSITIVE VALUES)!!
    $max_val = -10000000000000000;
    $max_key = -1;
    while ( ($key, $value) = each(%assoc_array) ) {
      if ( $value > $max_val || ($value == $max_val && $key gt $max_key) ) {
        $max_val = $value;
        $max_key = $key;
      }
    }
    @list = ( $max_key, @list );
    delete($assoc_array{$max_key});
  }
  @list;
}

sub usage {
  print("USAGE: $C [ -h|-d|-p|-y <year>|-r <input_dir>|-f <input_file> ]\n\n");
  print("\t-h         ==> display this help message\n");
  print("\t-d         ==> do debug tracing and skip command\n");
  print("\t-p         ==> display the results for each round\n");
  print("\t-y <year>  ==> the NCAA tournament year for the seed data\n");
  print("\t-r <dir>   ==> the directory containing the data file\n");
  print("\t-f <file>  ==> a data file containing seed and year data\n\n");
  exit( ($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  print("\nERROR: $C\n\n\t$_[0]\n\tLine # ", __LINE__, "\n");
  usage(1);
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
# ( $_ eq "-d" || $DEBUG eq "TRUE" ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),               next);	# display help/syntax message
  /-d/ && ($DEBUG     = "TRUE",     next);	# turn on debug; skip command
  /-p/ && ($per_round = "TRUE",     next);	# display per-round results
  /-y/ && ($Y         = shift(),    next);	# year of the NCAA tournament
  /-r/ && ($data_dir  = shift(),    next);      # directory for the data file
  /-f/ && ($data_file = shift(),    next);	# file holding seed/year data
  error("unknown option '$_'");
}

$file_name = "${data_dir}/${data_file}";
open(DATA_FILE, "${file_name}") || die("Can't open '${file_name}' for reading\n");

while (<DATA_FILE>) {

  ($year, $seed_data) = split(/ /);
  
  if (( /^\s*#/ || /^\s*$/ ) || (defined($Y) && $Y ne $year)) { next; }

  $min_avg_seed =  8.5;
  $min_diff_per = $min_diff_tot = $min_inc = -8;
# $min_avg_incr = $min_incr_per = 4;
  $avg_diff_tot =  $tot_diff_pct = $tot_diff_new = 0;
  
  @seeds  = split(/,/, $seed_data);
  $games  = scalar(@seeds);
  # This counts rounds processed (usually 6) not total (7)
  $rounds = int(log($games + 1)/log(2));
  $zero_diff = 0;
  
  if ($DEBUG eq "TRUE" ) {
    print("\nFile  : ${data_file}\nYear  : ${year}\nRounds: ${rounds}\n");
    print(  "Games : ${games}\n");
  }

  for ($r = 2, $i = 0; $r <= $rounds + 1; $r++) {	# Loop over rounds
  
    $seed_diff = 0;
    $num_teams = 2**(7 - $r);	# Could probably decrement this
    
    for ( ; $i < 64 - $num_teams; $i++ ) {	# Loop over all teams per round
    
      if ( $r > 2 ) {
        $k = 2*($i - 32);
        $loser = ( $seeds[$i] == $seeds[$k] ) ? $seeds[$k + 1] : $seeds[$k];
      }
      else { $loser = 17 - $seeds[$i]; }
      
      # Can get rid of $diff
      $diff = $seeds[$i] - $loser;
      $seed_diff += $diff;		# sum over seed diffs
    }
    
    $min_avg_seed += ($r < 6) ? int(${min_inc}/2) : 0;
    $min_diff_per += ($r > 3) ? int(${min_inc}*2) : 0;
    $min_diff_tot += ($r > 2) ? int($min_inc)     : 0;
    $avg_diff_tot += $seed_diff/$num_teams;
    
    if ( "$per_round" eq "TRUE" ) {
      printf("  %d       %8.4f   %3d        %8.4f   %4d       %2d\n", $r,
        $seed_diff/$num_teams, $min_diff_per, $avg_diff_tot, $seed_diff, $num_teams);
    }
    if ($DEBUG eq "TRUE" ) {
      printf("  %d: MIN INC: %5.2f\n", $r, $min_inc);
      printf("  %d: MIN DIFF PER: %5.1f; MIN DIFF TOT: %5.1f; MIN AVG SEED: %5.1f\n",
        $r, $min_diff_per, $min_diff_tot, $min_avg_seed);
    }
    
    if ( int($min_inc) != 0 ) {
      $tot_diff_pct += 100*($seed_diff/$num_teams - $min_inc)/(-2*$min_inc);
    }
    else { $zero_diff++; }
    
    $tot_diff_new += 100*($seed_diff/$num_teams - $min_diff_per)/(-2*$min_diff_per);
    $min_inc /= 2;
  }
  if ( "$DEBUG" eq TRUE ) {
    printf("YEAR %s: TOT_DIFF_PCT = %6.2f; TOT_DIFF_NEW = %6.2f\n\n",
      $year, $tot_diff_pct, $tot_diff_new);
  }
  $seed_diff_new{$year} = $tot_diff_new/$rounds;
  $seed_diff_per{$year} = $tot_diff_pct/($rounds - $zero_diff);
  $seed_diff_pr1{$year} = 100*($avg_diff_tot - $min_diff_tot)/(-2*$min_diff_tot);
}

@key5a = reverse(sort_key_by_value(%seed_diff_new));
@key5b = reverse(sort_key_by_value(%seed_diff_per));
@key5c = reverse(sort_key_by_value(%seed_diff_pr1));

print("\nSEED DIFF NEW   SEED DIFF PER   SEED DIFF PR1\n");
print(  "=============   =============   =============\n");
for ($i = 0; $i < scalar(@key5a); $i++) {
  printf("%s: %7.2f   %s: %7.2f   %s: %7.2f\n",
    $key5a[$i], $seed_diff_new{$key5a[$i]},
    $key5b[$i], $seed_diff_per{$key5b[$i]},
    $key5c[$i], $seed_diff_pr1{$key5c[$i]});
}

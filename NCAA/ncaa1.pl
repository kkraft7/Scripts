#!c:/MKS/mksnt/perl.exe
#
# ncaa.pl: Calculate a metric designed to estimate the level of
#          upsets for a given NCAA tournament.
#
# THIS IS A BACKUP FROM BEFORE I STARTED USING ARRAYS FOR THE MAIN CONSTANTS
#
# NOTES: (01) The metrics used in this scipt are:
#             Include a short description of each metric
#        (02) I start with the 2nd round since the seed matchups in the first
#             round remain constant from year to year

# I recalculate the constants for every year - put them in arrays, instead?!
# Update script to handle historical 32-team NCAA tournament data! (only 6 years?)
#   (I currently assume a 64-team tournament)
# Add the average remaining seed for each year to the output
# Make year-by-year notes on the upsets for each year
# Change "$games" to "$games_played", "$total_games", or $num_games?
# Start min_avg_seed with its 2nd round value?
# Warn if there are more than 7 rounds (i.e. $rounds > 6)!?
# Change all "$r < 6" tests to "$r < $rounds - 1"?
# TRY AVERAGING tot_norm_dif EACH ROUND?!
# Add a "-m <method>" flag to select a particular upset metric method?

$PROB_FACT = 1/30.5;	# Factor for assigning probabilities to seed differentials
$data_file = "ncaa.dat";
$data_dir  = $ENV{'HOME'} . "/Scripts";

chop($C = `basename "$0"`);

# This is fine if you only want to operate on globally defined associative arrays:
sub by_prob { ( $prob_array{$a} <=> $prob_array{$b} ) || ( $a <=> $b ); }
sub sort_key_by_value {
  local(%assoc_array) = @_;
  local(@list);		# MUST DEFINE THIS AS LOCAL FOR THIS SUBROUTINE TO WORK!
  $size = scalar(@_)/2;
  for ($i = 0; $i < $size; $i++) {
    $max_val = -1;	# MAKE THIS -10000000000000000 TO HANDLE NEGATIVE VALUES
    $max_key = -1;
    while ( ($key, $value) = each(%assoc_array) ) {
      if ( $value > $max_val || ($value == $max_val && $key > $max_key) ) {
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
  /-y/ && ($year      = shift(),    next);	# year of the NCAA tournament
  /-r/ && ($data_dir  = shift(),    next);      # directory for the data file
  /-f/ && ($data_file = shift(),    next);	# file holding seed/year data
  error("unknown option '$_'");
}

$file_name = "${data_dir}/${data_file}";
open(DATA_FILE, "${file_name}") || die("Can't open '${file_name}' for reading\n");

while (<DATA_FILE>) {

  ($y, $s) = split(/ /);	# Year and Seed Data
  if (( /^\s*#/ || /^\s*$/ ) || (defined($year) && $year ne $y)) { next; }

  $tot_seed = 0;	# total of all seeds for all rounds
  $tot_pcnt = 0;	# total of all percentage differentials
  $min_avg_seed =  8.5;	# minimum average seed for the first round
  $min_diff_per = -8;	# minimum average seed difference for first 2 rounds
  $min_avg_incr =  $min_incr_per = 4;
  $min_seed_tot =  $tot_seed_dif = $tot_norm_dif = 0;
  $min_norm_dif =  $max_norm_dif = $tot_diff_per = 0;
  
  @seeds  = split(/,/, $s);
  $games  = scalar(@seeds);	# total number of games played (64 or 32)
  # This counts rounds processed (usually 6 - or 5 for 32 teams) not total (7)
  $rounds = int(log($games + 1)/log(2));  # call this "R" instead of "rounds"?
  
  if ( "$DEBUG" eq "TRUE" || "$per_round" eq "TRUE" ) {
    print("File  : ${data_file}\nYear  : ${y}\nRounds: ${rounds}\n");
    print("Games : ${games}\n");
  # exit(0);
  }
  
  if ( "$per_round" eq "TRUE" ) {
    print("\nNCAA TOURNAMENT YEAR: ${y}\n");
    print(  "==========================\n");
    print(  "ROUND   AVG SEED   MIN SEED   DIFFERENCE   % DIFF\n");
    print(  "=====   ========   ========   ==========   ======\n");
  }
  
  for ($r = 2, $i = 0; $r <= $rounds + 1; $r++) {	# Loop over rounds
    $seed_sum = 0;
    $seed_dif = 0;
    $norm_dif = 0;
    $num_teams = 2**(7 - $r);	# Could probably decrement this
    
    for ( ; $i < 64 - $num_teams; $i++ ) {	# Loop over all teams per round
    
      if ( $r > 2 ) {				# seed i is winner of last round
        $k = 2*($i - 32);			# matchup between seeds k, k + 1
        $loser = ( $seeds[$i] == $seeds[$k] ) ? $seeds[$k + 1] : $seeds[$k];
      }
      else { $loser = 17 - $seeds[$i]; }	# 1st-round matchups are constant
      
      $norm_dif += ( $seeds[$i]/($seeds[$i] + $loser) );  # normalized seed factor
      $diff = $seeds[$i] - $loser;			  # seed differential
      $seed_dif += $diff;	# sum over seed diffs (CAN GET RID OF $DIFF!)
      $seed_sum += $seeds[$i];	# sum over winning seeds
      # print("SEED DIFF: $diff ($seeds[$i] seed beat $loser seed)\n");
    }
    
    $min_avg_seed -= ($r < 6) ? $min_avg_incr : 0;	# increment min_avg_seed
    $min_avg_incr /= 2;					# recalculate incrememnt
    
    $min_diff_per -= ($r > 3) ? int($min_incr_per) : 0;
    $min_incr_per /= ($r > 3) ? 2 : 1;
    
    $min_seed_tot += $min_avg_seed*$num_teams;
    $tot_seed_dif += $seed_dif;
    $tot_norm_dif += $norm_dif;
    $min_norm_dif += $num_teams*(($r < 6) ? $min_avg_seed/(4*$min_avg_seed - 1) : 0.5 );
    $max_norm_dif += $num_teams*(($r < 6) ?
      (17 - $min_avg_seed)/(35 - 4*$min_avg_seed) : 0.5);
    $tot_diff_per += 100*($seed_dif/$num_teams - $min_diff_per)/(-2*$min_diff_per);
    
    $tot_seed += $seed_sum;
    $tot_pcnt += (100*($seed_sum/$num_teams - $min_avg_seed)/(17 - 2*${min_avg_seed}));
    
    if ( "$per_round" eq "TRUE" ) {
      printf("%d       %8.5f   %3.1f         %9f\n", $r, $seed_sum/$num_teams,
        $min_avg_seed, $seed_sum/$num_teams - $min_avg_seed);
    }
  }
  
  $min_seed_dif = $games - 2*$min_seed_tot + ( ($rounds < 5) ? 0 : $rounds - 3 );
  
  # (1) Winning seed averaged each round, and over all rounds
  $avg_seed_per{$y} = $tot_pcnt/$rounds;
  # (2) Winning seed averaged over all games
  $avg_seed_tot{$y} = 100*($tot_seed     - $min_seed_tot)/(17*$games  -  2*$min_seed_tot);
  # (3) Differential between winning and losing seed averaged over all games
  $avg_seed_dif{$y} = 100*($tot_seed_dif - $min_seed_dif)/(-2*$min_seed_dif);
  # (4) Winning seed normalization factor averaged over all games
  $avg_norm_dif{$y} = 100*($tot_norm_dif - $min_norm_dif)/($max_norm_dif - $min_norm_dif);
  # (5) Seed differentials averaged each round, and over all rounds
  $seed_dif_per{$y} = $tot_diff_per/$rounds;
}

@key1 = reverse(sort_key_by_value(%avg_seed_per));
@key2 = reverse(sort_key_by_value(%avg_seed_tot));
@key3 = reverse(sort_key_by_value(%avg_seed_dif));
@key4 = reverse(sort_key_by_value(%avg_norm_dif));
@key5 = reverse(sort_key_by_value(%seed_dif_per));

print("\nAVG PER RND    AVG PER GAME   AVG SEED DIF   NRM SEED FCT   SEED DIF PER\n");
print(  "===========    ============   ============   ============   ============\n");

for ($i = 0; $i < scalar(@key1); $i++) {
  printf("%s: %6.2f   %s: %6.2f   %s: %6.2f   %s: %6.2f   %s: %6.2f\n",
    $key1[$i], $avg_seed_per{$key1[$i]}, $key2[$i], $avg_seed_tot{$key2[$i]},
    $key3[$i], $avg_seed_dif{$key3[$i]}, $key4[$i], $avg_norm_dif{$key4[$i]},
    $key5[$i], $seed_dif_per{$key5[$i]});
}

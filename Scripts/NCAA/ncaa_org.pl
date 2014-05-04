#!c:/Perl/bin/perl.exe -w
#
# ncaa.pl: Calculate a metric designed to estimate the level of
#          upsets for a given NCAA tournament.
#
# NOTES: (01) The metrics used in this scipt are:
#             [Include a short description of each metric]
#        (02) I start with the 2nd round since the seed matchups in the first
#             round remain constant from year to year

# NEED BETTER COMMENTING!
# List result columns by number and have descriptions (by number) above!?
# Update script to handle historical 32-team NCAA tournament data!
#   (I currently assume a 64-team tournament)
# Add the average remaining seed for each year to the output
# Make year-by-year notes on the upsets for each year
# Change "$games" to "$games_played", "$total_games", or $num_games?
# Warn if there are more than 7 rounds (i.e. $rounds > 6)!?
# Change all "$r < 6" tests to "$r < $rounds - 1"?
# TRY AVERAGING tot_norm_dif EACH ROUND?!
# Add a "-m <method>" flag to select a particular upset metric method?

use File::Basename;
$C         = basename($0);
$per_round = "FALSE";
$DEBUG     = "FALSE";
$PROB_FACT = 1/30.75;	# Factor for assigning probabilities to seed differentials
# $PROB_FACT = 1/25;
$data_file = "ncaa.dat";
$data_dir  = "c:/Documents and Settings/Kevin/My Documents/NCAA";

# This is fine if you only want to operate on globally defined associative arrays:
sub by_prob { ( $prob_array{$a} <=> $prob_array{$b} ) || ( $a <=> $b ); }

################################################################################
# sort_key_by_value:                                                           #
#                                                                              #
################################################################################
sub sort_key_by_value {

  my(%assoc_array) = @_;
  my($size)        = scalar(@_)/2;
  my(@list);       # Must define @list as my() or local()!

  for ( my($i) = 0; $i < $size; $i++ ) {

    # $max_val = -1;	# MAKE THIS -10000000000000000 TO HANDLE NEGATIVE VALUES
    my($max_val) = -10000000000000000;
    my($max_key) = -1;

    while ( my($key, $value) = each(%assoc_array) ) {

      if ( $value > $max_val || ($value == $max_val && $key > $max_key) ) {
        $max_val = $value;
        $max_key = $key;
      }
    }
    unshift(@list, $max_key);
    delete($assoc_array{$max_key});
  }
  @list;
}

################################################################################
# count_perm:                                                                  #
#                                                                              #
################################################################################
sub count_perm {	# Assumes 2 equal-sized lists as input

  my($N) = scalar(@_)/2;
  my(@A) = @_[ 0 .. $N-1];	# Split 1st half of command line into array A
  my(@B) = @_[$N..2*$N-1];	# Split 2nd half of command line into array B
  my($P, $M, $i, $j);		# Defining these in the for-loop didn't work!

  for ( $P = 0, $M = 0, $i = 0; $i < $N; $i++ ) {

    for ( $j = $i; $j < $N && $A[$i] ne $B[$j]; $j++ ) { ; }
    
    if ( $i < $j && $j < $N ) {
      for ($M++; $j > $i; $P++, $j--) { $B[$j] = $B[$j - 1]; }
      $B[$j] = $A[$i];
    }
    elsif ( $j == $N ) { print("ERROR: Element $A[$i] not in list (@B)\n"); }
  }
  ($M, $P);	# Return total number of elements moved and permutations
}

################################################################################
# usage:                                                                       #
# Print out a Help message detailing script usage.                             #
################################################################################
sub usage {
  print("USAGE: $C [ -h|-d|-p|-y <year>|-r <data_dir>|-f <data_file> ]\n\n");
  print("\t-h         ==> display this help message\n");
  print("\t-d         ==> do debug tracing and skip command\n");
  print("\t-p         ==> display the results for each round\n");
  print("\t-y <year>  ==> the NCAA tournament year for the seed data\n");
  print("\t-r <dir>   ==> the directory containing the data file\n");
  print("\t-f <file>  ==> a data file containing seed and year data\n\n");
  exit( ($_[0] =~ /[01]/) ? $_[0] : 0);
}

################################################################################
# error:                                                                       #
# Print out an error message.                                                  #
################################################################################
sub error {
  print("\nERROR: $C\n\n\t$_[0]\n\tLine # ", __LINE__, "\n");
  usage(1);
}

################################################################################
# Process command-line arguments.                                              #
################################################################################
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

# @min_prob_list   = ( 15, 1, 7, 9, 5, 11, 3, 13, 7, 1, 3, 5, 3, 1, 1 );
  @min_prob_list   = ( 15, 1, 7, 9, 5, 11, 3, 13, 8, 8, 8, 8, 12, 12, 14 );
$rMAX            =  7;		# number of rounds for a 32-team tournament
$iMAX            =  0;		# number of teams in one bracket for round $r
$min_avg_seed[1] =  8.5;	# minimum average seed for the first round
$min_diff_per[1] = -8;		# minimum average seed dif for first round
$min_norm_fct = $max_norm_fct = 0;

# CALCULATE MAIN PER-ROUND CONSTANTS ONLY ONCE BY PUTTING THEM IN ARRAYS:
for ($r = 2, $i = 0; $r <= $rMAX; $r++) {
  
  $num_teams = 2**(7 - $r);
  $mult  =  ($r < 6) ? 4 : 1;
  $iMAX += (($r < 6) ? ${num_teams}/${mult} : $num_teams);
  
  $min_avg_seed[$r] = ($r < 6) ? ($min_avg_seed[$r - 1] + 0.5)/2 : $min_avg_seed[$r - 1];
  
  # $min_diff_per[$r] = $min_diff_per[$r - 1] - 
  #   ((3 < $r && $r < 7) ? (2*$min_avg_seed[$r - 1] - 1) : 0);
  $min_diff_per[$r] = $min_diff_per[$r - 1];
  if (3 < $r && $r < 7) { $min_diff_per[$r] -= (2*$min_avg_seed[$r - 1] - 1); }
  $min_norm_fct += ${num_teams}*$min_avg_seed[$r]/(2*$min_avg_seed[$r - 1]);
  $max_norm_fct += ${num_teams}*(17 - $min_avg_seed[$r])/(2*(17 - $min_avg_seed[$r - 1]));
  
  for ($min_prob_sum = $max_prob_sum = 0; $i < $iMAX; $i++) {
    
    $new_prob = 0.5 - ${PROB_FACT}*(($r < 6) ? $min_prob_list[$i] : 15);
#   $new_prob = 0.5 - (($r < 6) ? ${PROB_FACT}*$min_prob_list[$i] : 0);
    # print("R = $r; i = $i; NEW_PROB = $new_prob; MIN_PROB_LIST[$i] = $min_prob_list[$i]\n");
    
    if ($r == 2) {
      $old_min_prob = $old_max_prob = 1.0;
    }
    elsif ($r >= 6) {
      $old_min_prob = $min_prob_list[$r + 8];
      $old_max_prob = $max_prob_list[$r + 8];
    }
    else {
      $k = 2*($i - 8);
      if ( $min_prob_list[$k] < $min_prob_list[$k + 1] ) {
        $old_min_prob = $min_prob_list[$k];
        $old_max_prob = $max_prob_list[$k];
      }
      else {
        # $max_prob1 = 0.5 + ${PROB_FACT}*{$max_diff_list[$k - 1]};
        $old_min_prob = $min_prob_list[$k + 1];
        $old_max_prob = $max_prob_list[$k + 1];
      }
    }
    
    $min_prob_list[$i] = ${old_min_prob}*${new_prob};
    $max_prob_list[$i] = ${old_max_prob}*(1 - $new_prob);
    $min_prob_sum += ${old_min_prob}*${new_prob};
    $max_prob_sum += ${old_max_prob}*(1 - $new_prob);
  }
  $min_diff_prob[$r] = ${min_prob_sum}*${mult}/$num_teams;
  $max_diff_prob[$r] = ${max_prob_sum}*${mult}/$num_teams;
  
  # printf("MAX_DIFF_PROB[%d] = %7.4f; MIN_DIFF_PROB[%d] = %7.4f\n",
  #   $r, $max_diff_prob[$r], $r, $min_diff_prob[$r]);
  # print("OLD_MIN_PROB = $old_min_prob; OLD_MAX_PROB = $old_max_prob\n\n");
  
  if ( "$DEBUG" eq "TRUE" ) {
    if ( $r == 2 ) {
      print("ROUND  MIN_AVG_SEED  MIN_DIFF_PER  MIN_NORM_FCT  MAX_NORM_FCT\n");
      print("-----  ------------  ------------  ------------  ------------\n");
    }
    printf("%5d           %3.1f  %12d     %4.1f/%4.1f     %4.1f/%4.1f\n", $r,
      $min_avg_seed[$r], $min_diff_per[$r], $min_avg_seed[$r], 2*$min_avg_seed[$r - 1],
      17 - $min_avg_seed[$r], 2*(17 - $min_avg_seed[$r - 1]));
  }
}

# $file_name = "ncaa.dat";
# print("DATA_DIR: $data_dir; DATA_FILE: $data_file\n");
$file_name = "${data_dir}/${data_file}";
open(DATA_FILE, "${file_name}") || die("Can't open '${file_name}' for reading\n");

while (<DATA_FILE>) {

  ($y, $s) = split(/ /);	# Year and Seed Data
  if (( /^\s*#/ || /^\s*$/ ) || (defined($year) && $year ne $y)) { next; }

  $tot_seed = 0;	# total of all seeds for all rounds
  $tot_pcnt = 0;	# total of all percentage differentials
  $min_avg_incr = $min_incr_per = 4;
  $min_seed_tot = $tot_seed_dif = $tot_norm_dif = $tot_diff_per = 0;
  $tot_prob_mlt = 0;
  
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
    $seed_sum  = $seed_dif = $norm_dif = $prob_mlt = 0;
    $num_teams = 2**(7 - $r);	# Could probably decrement this
    
    for ( ; $i < 64 - $num_teams; $i++ ) {	# Loop over all teams per round
    
      if ( $r > 2 ) {				# seed i is winner of last round
        $k = 2*($i - 32);			# matchup between seeds k, k + 1
        $loser = ( $seeds[$i] == $seeds[$k] ) ? $seeds[$k + 1] : $seeds[$k];
        $k_win = ( $seeds[$i] == $seeds[$k] ) ? $k : $k + 1;
      }
      else { $loser = 17 - $seeds[$i]; }	# 1st-round matchups are constant
        
      $prob = 0.5 - ${PROB_FACT}*($seeds[$i] - $loser);
      $diff_prob_mult[$i] = ${prob}*( $r == 2 ? 1 : $diff_prob_mult[$k_win] );
      $prob_mlt += $diff_prob_mult[$i];
      $norm_dif += ($seeds[$i]/($seeds[$i] + $loser));    # normalized seed factor
      $seed_dif += ($seeds[$i] - $loser);	# sum over seed diffs
      $seed_sum += $seeds[$i];			# sum over winning seeds
      # printf("SEED DIFF: %d (%d seed beat %d seed)\n",
      #  ($seeds[$i] - $loser), $seeds[$i], $loser);
    }
    
    # NORMALIZE PROBABILITY MULTIPLICATION PER ROUND?! (MUST CALCULATE MIN/MAX)
    $min_seed_tot += $min_avg_seed[$r]*$num_teams;
    $tot_seed_dif += $seed_dif;
    $tot_norm_dif += $norm_dif;
    $tot_diff_per += 100*($seed_dif/$num_teams - $min_diff_per[$r])/(-2*$min_diff_per[$r]);
    
    $tot_seed += $seed_sum;
    $tot_pcnt += (100*($seed_sum/$num_teams - $min_avg_seed[$r])/(17 - 2*$min_avg_seed[$r]));
    
    if ( $prob_mlt/$num_teams < $min_diff_prob[$r] || $prob_mlt/$num_teams > $max_diff_prob[$r] ) {
#     print("ERROR: PROB_MLT INVALID (YEAR = $y)\n");
      $INVALID_VAL = "TRUE";
    }
    # if ($y =~ /^sM\w\w/) {
    if (defined($INVALID_VAL)) {
#     printf("MAX_DIFF_PROB[%d] = %7.4f; MIN_DIFF_PROB[%d] = %7.4f; PROB_MLT/N = %7.4f\n",
#       $r, $max_diff_prob[$r], $r, $min_diff_prob[$r], $prob_mlt/$num_teams);
      undef($INVALID_VAL);
    }
    $tot_prob_mlt += ($prob_mlt/$num_teams - $min_diff_prob[$r])/($max_diff_prob[$r] - $min_diff_prob[$r]);
    
    if ( "$per_round" eq "TRUE" ) {
      printf("%d       %8.5f   %3.1f         %9f\n", $r, $seed_sum/$num_teams,
        $min_avg_seed[$r], $seed_sum/$num_teams - $min_avg_seed[$r]);
    }
  }
  
  $min_seed_dif = $games - 2*$min_seed_tot + ( ($rounds < 5) ? 0 : $rounds - 3 );
  
  # (1) Winning seed averaged each round, and over all rounds
  $avg_seed_per{$y} = ${tot_pcnt}/${rounds};
  # (2) Winning seed averaged over all games
  $avg_seed_tot{$y} = 100*($tot_seed     - $min_seed_tot)/(17*$games  -  2*$min_seed_tot);
  # (3) Differential between winning and losing seed averaged over all games
  $avg_seed_dif{$y} = 100*($tot_seed_dif - $min_seed_dif)/(-2*$min_seed_dif);
  # (4) Winning seed normalization factor averaged over all games
  $avg_norm_dif{$y} = 100*($tot_norm_dif - $min_norm_fct)/($max_norm_fct - $min_norm_fct);
  # (5) Seed differentials averaged each round, and over all rounds
  $seed_dif_per{$y} = ${tot_diff_per}/${rounds};
  # (6) 
  # $dif_prob_mlt{$y} = 1 - pop(@diff_prob_mult);
  # $dif_prob_mlt{$y} = ${prob_mlt}/$games;
  $dif_prob_mlt{$y} = 100*(1 - ${tot_prob_mlt}/${rounds});
  # if ($y =~ /^sM\w\w/) { print("Multiplied probability for $y: $dif_prob_mlt{$y}\n"); }
  # print("Multiplied probability for $y: $dif_prob_mlt{$y}\n");
}

@key1 = reverse(sort_key_by_value(%avg_seed_per));
@key2 = reverse(sort_key_by_value(%avg_seed_tot));
@key3 = reverse(sort_key_by_value(%avg_seed_dif));
@key4 = reverse(sort_key_by_value(%avg_norm_dif));
@key5 = reverse(sort_key_by_value(%seed_dif_per));
@key6 = reverse(sort_key_by_value(%dif_prob_mlt));

# print("\nAVG PER RND    AVG PER GAME   AVG SEED DIF   NRM SEED FCT   SEED DIF PER\n");
print("\nAVG PER RND    AVG PER GAME   AVG SEED DIF   NRM SEED FCT   PROB MLT DIF\n");
print(  "===========    ============   ============   ============   ============\n");

for ($i = 0; $i < scalar(@key1); $i++) {
  printf("%s: %6.2f   %s: %6.2f   %s: %6.2f   %s: %6.2f   %s: %6.2f\n",
    $key1[$i], $avg_seed_per{$key1[$i]}, $key2[$i], $avg_seed_tot{$key2[$i]},
    $key3[$i], $avg_seed_dif{$key3[$i]}, $key4[$i], $avg_norm_dif{$key4[$i]},
#   $key5[$i], $seed_dif_per{$key5[$i]},
    $key6[$i], $dif_prob_mlt{$key6[$i]});
}

print("\n");
@key_list = ( key1, key2, key3, key5, key6 );
# @key_list = ( key2, key3 );
foreach $K1 (@key_list) {
  foreach $K2 (@key_list) {
    if ( $K1 lt $K2 ) {
      @totals = count_perm(eval("\@${K1}"), eval("\@${K2}"));
      printf("%s VS %s: %2d MOVES AND %3d PERMUTATIONS\n",
        $K1, $K2, $totals[0], $totals[1]);
    }
  }
}

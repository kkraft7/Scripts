#!c:/Perl/bin/perl.exe -w
#
# metric6.pl: Calculate the multiplied probability metric for estimating
#             the level of upsets for a given NCAA tournament.
#
#        (02) I start with the 2nd round since the seed matchups in the first
#             round remain constant from year to year

use File::Basename;
$C         = basename($0);
$per_round = "FALSE";
$DEBUG     = "FALSE";
$PROB_FACT = 1/30.75;	# Factor for assigning probabilities to seed differentials
# $PROB_FACT = 1/25;
  $data_file = "ncaa.dat";
# $data_file = "metric5.dat";
$data_dir  = "c:/Documents and Settings/Kevin/My Documents/NCAA";

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

# $PROB_FACT = 1;
# @min_prob_list   = ( 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 );
# @min_prob_lst2   = ( 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 );
# @min_prob_list   = ( 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 );
# @min_prob_lst2   = ( 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 );
  @min_prob_list   = ( 15, 1, 7, 9, 5, 11, 3, 13, 8, 8, 8, 8, 12, 12, 14 );
# @min_prob_list   = ( 15, 1, 7, 9, 5, 11, 3, 13, 7, 1, 3, 5, 03, 01, 01 );
  @min_prob_lst2   = ( 15, 1, 7, 9, 5, 11, 3, 13, 8, 8, 8, 8, 12, 12, 14 );
$rMAX            =  7;		# number of rounds for a 32-team tournament
$iMAX            =  0;		# number of teams in one bracket for round $r

# CALCULATE MAIN PER-ROUND CONSTANTS ONLY ONCE BY PUTTING THEM IN ARRAYS:
for ($r = 2, $i = 0; $r <= $rMAX; $r++) {
  
  $num_teams = 2**(7 - $r);
  $mult  =  ($r < 6) ? 4 : 1;
  $iMAX += (($r < 6) ? ${num_teams}/${mult} : $num_teams);
  
  for ($min_prob_sum = $max_prob_sum = $min_prb_sum2 = $max_prb_sum2 = 0; $i < $iMAX; $i++) {
    
#   $new_prob = 0.5 - ${PROB_FACT}*(($r < 6) ? $min_prob_list[$i] : 15);
    $new_prb2 = 0.5 - ${PROB_FACT}*(($r < 6) ? $min_prob_lst2[$i] : 15);
    $new_prob = 0.5 - (($r < 6) ? ${PROB_FACT}*$min_prob_list[$i] : 0);
#   $new_prb2 = 0.5 - (($r < 6) ? ${PROB_FACT}*$min_prob_lst2[$i] : 0);
#   $new_prob = 0.0 - (($r < 6) ? ${PROB_FACT}*$min_prob_list[$i] : 0);
#   $new_prb2 = 0.0 - (($r < 6) ? ${PROB_FACT}*$min_prob_lst2[$i] : 0);
    # print("R = $r; i = $i; NEW_PROB = $new_prob; MIN_PROB_LIST[$i] = $min_prob_list[$i]\n");
    
    if ($r == 2) {
      $old_min_prob = $old_max_prob = 1.0;
      $old_min_prb2 = $old_max_prb2 = 1.0;
    }
    elsif ($r >= 6) {
      $old_min_prob = $min_prob_list[$r + 8];
      $old_max_prob = $max_prob_list[$r + 8];

      $old_min_prb2 = $min_prob_lst2[$r + 8];
      $old_max_prb2 = $max_prob_lst2[$r + 8];
    }
    else {
      $k = 2*($i - 8);
      if ( $min_prob_list[$k] < $min_prob_list[$k + 1] ) {
        $old_min_prob = $min_prob_list[$k];
        $old_max_prob = $max_prob_list[$k];
      }
      else {
        $old_min_prob = $min_prob_list[$k + 1];
        $old_max_prob = $max_prob_list[$k + 1];
      }

      if ( $min_prob_lst2[$k] < $min_prob_lst2[$k + 1] ) {
        $old_min_prb2 = $min_prob_lst2[$k];
        $old_max_prb2 = $max_prob_lst2[$k];
      }
      else {
        $old_min_prb2 = $min_prob_lst2[$k + 1];
        $old_max_prb2 = $max_prob_lst2[$k + 1];
      }
    }
    
    $min_prob_list[$i] = ${old_min_prob}*${new_prob};
    $max_prob_list[$i] = ${old_max_prob}*(1 - $new_prob);
    $min_prob_sum += ${old_min_prob}*${new_prob};
    $max_prob_sum += ${old_max_prob}*(1 - $new_prob);

    $min_prob_lst2[$i] = ${old_min_prb2}*${new_prb2};
    $max_prob_lst2[$i] = ${old_max_prb2}*(1 - $new_prb2);
    $min_prb_sum2 += ${old_min_prb2}*${new_prb2};
    $max_prb_sum2 += ${old_max_prb2}*(1 - $new_prb2);
  }
  $min_diff_prob[$r] = ${min_prob_sum}*${mult}/$num_teams;
  $max_diff_prob[$r] = ${max_prob_sum}*${mult}/$num_teams;

  $min_diff_prb2[$r] = ${min_prb_sum2}*${mult}/$num_teams;
  $max_diff_prb2[$r] = ${max_prb_sum2}*${mult}/$num_teams;
  
  # printf("MAX_DIFF_PROB[%d] = %7.4f; MIN_DIFF_PROB[%d] = %7.4f\n",
  #   $r, $max_diff_prob[$r], $r, $min_diff_prob[$r]);
  # print("OLD_MIN_PROB = $old_min_prob; OLD_MAX_PROB = $old_max_prob\n\n");
}

if ( "$DEBUG" eq "TRUE" ) {
  for (my $i = 2; $i < scalar(@max_diff_prb2); $i++) {
    printf("min_diff_prob[%d] = %5.2f; max_diff_prob[%d] = %5.2f\n",
      $i, $min_diff_prob[$i], $i, $max_diff_prob[$i]);
    printf("min_diff_prob[%d] = %5.2f; max_diff_prob[%d] = %5.2f\n\n",
      $i, $min_diff_prb2[$i], $i, $max_diff_prb2[$i]);
  }
  exit(0);
}



$file_name = "${data_dir}/${data_file}";
open(DATA_FILE, "${file_name}") || die("Can't open '${file_name}' for reading\n");

while (<DATA_FILE>) {

  ($y, $s) = split(/ /);	# Year and Seed Data
  if (( /^\s*#/ || /^\s*$/ ) || (defined($year) && $year ne $y)) { next; }

  $tot_prob_mlt = 0;
  $tot_prb_mlt2 = 0;
  
  @seeds  = split(/,/, $s);
  $games  = scalar(@seeds);	# total number of games played (64 or 32)
  # This counts rounds processed (usually 6 - or 5 for 32 teams) not total (7)
  $rounds = int(log($games + 1)/log(2));  # call this "R" instead of "rounds"?
  
  if ( "$DEBUG" eq "TRUE" || "$per_round" eq "TRUE" ) {
    print("File  : ${data_file}\nYear  : ${y}\nRounds: ${rounds}\n");
    print("Games : ${games}\n");
  # exit(0);
  }
 
  for ($r = 2, $i = 0; $r <= $rounds + 1; $r++) {	# Loop over rounds
    $prob_mlt = 0;
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
      # printf("SEED DIFF: %d (%d seed beat %d seed)\n",
      #  ($seeds[$i] - $loser), $seeds[$i], $loser);
    }

    if ( $prob_mlt/$num_teams < $min_diff_prb2[$r] || $prob_mlt/$num_teams > $max_diff_prb2[$r] ) {
      print("ERROR: PROB_MLT INVALID (YEAR = $y)\n");
      $INVALID_VAL = "TRUE";
    }

    if (defined($INVALID_VAL)) {
      printf("MAX_DIFF_PRB2[%d] = %7.4f; MIN_DIFF_PRB2[%d] = %7.4f; PROB_MLT/N = %7.4f\n",
       $r, $max_diff_prb2[$r], $r, $min_diff_prb2[$r], $prob_mlt/$num_teams);
       undef($INVALID_VAL);
    }

#   if ( $prob_mlt/$num_teams < $min_diff_prob[$r] || $prob_mlt/$num_teams > $max_diff_prob[$r] ) {
#     print("ERROR: PROB_MLT INVALID (YEAR = $y)\n");
#     $INVALID_VAL = "TRUE";
#   }

#   if (defined($INVALID_VAL)) {
#     printf("MAX_DIFF_PROB[%d] = %7.4f; MIN_DIFF_PROB[%d] = %7.4f; PROB_MLT/N = %7.4f\n",
#      $r, $max_diff_prob[$r], $r, $min_diff_prob[$r], $prob_mlt/$num_teams);
#      undef($INVALID_VAL);
#   }

    $tot_prob_mlt += ($prob_mlt/$num_teams - $min_diff_prob[$r])/($max_diff_prob[$r] - $min_diff_prob[$r]);
    $tot_prb_mlt2 += ($prob_mlt/$num_teams - $min_diff_prb2[$r])/($max_diff_prb2[$r] - $min_diff_prb2[$r]);
  }
  
  # (6) 
  # $dif_prob_mlt{$y} = 1 - pop(@diff_prob_mult);
  # $dif_prob_mlt{$y} = ${prob_mlt}/$games;
  $dif_prob_mlt{$y} = 100*(1 - ${tot_prob_mlt}/${rounds});
  $dif_prb_mlt2{$y} = 100*(1 - ${tot_prb_mlt2}/${rounds});
  # if ($y =~ /^sM\w\w/) { print("Multiplied probability for $y: $dif_prob_mlt{$y}\n"); }
  # print("Multiplied probability for $y: $dif_prob_mlt{$y}\n");
}

@key6a = reverse(sort_key_by_value(%dif_prob_mlt));
@key6b = reverse(sort_key_by_value(%dif_prb_mlt2));

print("\nPRB MLT DIF    PRB MLT DIF2\n");
print(  "===========    =============\n");

for ($i = 0; $i < scalar(@key6a); $i++) {

# printf("%s: %6.2f   %s: %6.2f   %s: %6.2f   %s: %6.2f   %s: %6.2f\n",
  printf("%s: %6.2f   %s: %6.2f\n",
    $key6a[$i], $dif_prob_mlt{$key6a[$i]}, $key6b[$i], $dif_prb_mlt2{$key6b[$i]});
}

# for ($i = 0; $i < scalar(@key1); $i++) {
#   printf("%s: %6.2f   %s: %6.2f   %s: %6.2f   %s: %6.2f   %s: %6.2f\n",
#     $key1[$i], $avg_seed_per{$key1[$i]}, $key2[$i], $avg_seed_tot{$key2[$i]},
#     $key3[$i], $avg_seed_dif{$key3[$i]}, $key4[$i], $avg_norm_dif{$key4[$i]},
#     $key5[$i], $seed_dif_per{$key5[$i]});
#     $key5[$i], $dif_prob_mlt{$key5[$i]});
# }

# print("\n");
# @key_list = ( key1, key2, key3, key4, key5 );
# @key_list = ( key2, key3 );
# foreach $K1 (@key_list) {
#   foreach $K2 (@key_list) {
#     if ( $K1 lt $K2 ) {
#       @totals = count_perm(eval("\@${K1}"), eval("\@${K2}"));
#       printf("%s VS %s: %2d MOVES AND %3d PERMUTATIONS\n",
#         $K1, $K2, $totals[0], $totals[1]);
#    }
#   }
# }

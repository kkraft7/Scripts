#!/usr/bin/perl
# c:/Perl/bin/perl.exe -w
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
#   Make everything dependent on "N" (currently assume 64-team tournament)
# Add the average remaining seed for each year to the output
# Make year-by-year notes on the upsets for each year
# Change "$games" to "$games_played", "$total_games", or $num_games?
# Warn if there are more than 7 rounds (i.e. $rounds > 6)!?
# Change all "$r < 6" tests to "$r < $rounds - 1"?
# TRY AVERAGING tot_norm_dif EACH ROUND?!
# Add a "-m <method>" flag to select a particular upset metric method?

use File::Basename;
$C         = basename($0);
$DEBUG     = "FALSE";
# $data_file = "ncaaMW.dat";
# $data_dir  = "c:/Documents and Settings/Kevin/My Documents/Scripts/NCAA";
$data_file = "ncaa.dat";
$data_dir  = "/home/Kevin/Scripts/NCAA";

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

      if ( $value > $max_val || ($value == $max_val && $key gt $max_key) ) {
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
  print("USAGE: $C [ -h|-d|-y <year>|-r <data_dir>|-f <data_file> ]\n\n");
  print("\t-h         ==> display this help message\n");
  print("\t-d         ==> do debug tracing and skip command\n");
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
  /-y/ && ($year      = shift(),    next);	# year of the NCAA tournament
  /-r/ && ($data_dir  = shift(),    next);      # directory for the data file
  /-f/ && ($data_file = shift(),    next);	# file holding seed/year data
  error("unknown option '$_'");
}

$min_seed_tot = 203; # THIS ONLY WORKS FOR A 64-GAME TOURNAMENT!

$file_name = "${data_dir}/${data_file}";
open(DATA_FILE, "${file_name}") || die("Can't open '${file_name}' for reading\n");

while (<DATA_FILE>) {

  ($y, $s) = split(/ /);	# Year and Seed Data
  if (( /^\s*#/ || /^\s*$/ ) || (defined($year) && $year ne $y)) { next; }

  $tot_seed = $tot_seed_dif = 0;
  
  @seeds  = split(/,/, $s);
  $games  = scalar(@seeds);	# total number of games played (64 or 32)
  # This counts rounds processed (usually 6 - or 5 for 32 teams) not total (7)
  $rounds = int(log($games + 1)/log(2));  # call this "R" instead of "rounds"?
  
  if ( "$DEBUG" eq "TRUE" ) {
    print("File  : ${data_file}\nYear  : ${y}\nRounds: ${rounds}\n");
    print("Games : ${games}\n");
  # exit(0);
  }
  
  for ($r = 2, $i = 0; $r <= $rounds + 1; $r++) {	# Loop over rounds
    $seed_sum  = $seed_dif = 0;
    $num_teams = 2**(7 - $r);	# Could decrement this ($num_teams /= 2;)
    
    for ( ; $i < 64 - $num_teams; $i++ ) {	# Loop over all teams per round
    
      if ( $r > 2 ) {				# seed i is winner of last round
        $k = 2*($i - 32);			# matchup between seeds k, k + 1
        $loser = ( $seeds[$i] == $seeds[$k] ) ? $seeds[$k + 1] : $seeds[$k];
      }
      else { $loser = 17 - $seeds[$i]; }	# 1st-round matchups are constant

      $seed_dif += ($seeds[$i] - $loser);	# sum over seed diffs
      $seed_sum += $seeds[$i];			# sum over winning seeds
    }

    $tot_seed_dif += $seed_dif;    
    $tot_seed     += $seed_sum;
  }  
  $min_seed_dif = $games - 2*$min_seed_tot + 3;             # only works for 64 games

  # (01) WINNING SEED AVERAGED OVER ALL GAMES:
  $avg_seed_tot{$y} = 100*($tot_seed     - $min_seed_tot)/(17*$games - 2*$min_seed_tot);
  # (02) WINNING SEED MINUS LOSING SEED AVERAGED OVER ALL GAMES:
  $avg_seed_dif{$y} = 100*($tot_seed_dif - $min_seed_dif)/(-2*$min_seed_dif);
}

@key1 = reverse(sort_key_by_value(%avg_seed_tot));
@key2 = reverse(sort_key_by_value(%avg_seed_dif));

print("\nAVG. WIN SEED   AVG. SEED DIF\n");
print(  "=============   =============\n");

for ($i = 0; $i < scalar(@key1); $i++) {
  printf("%-5s: %6.2f   %-5s: %6.2f\n",
    $key1[$i], $avg_seed_tot{$key1[$i]}, $key2[$i], $avg_seed_dif{$key2[$i]});
}

print("\n");
@key_list = ( key1, key2 );
foreach $K1 (@key_list) {
  foreach $K2 (@key_list) {
    if ( $K1 lt $K2 ) {
      @totals = count_perm(eval("\@${K1}"), eval("\@${K2}"));
      printf("%s VS %s: %d MOVES AND %d PERMUTATIONS\n",
        $K1, $K2, $totals[0], $totals[1]);
    }
  }
}


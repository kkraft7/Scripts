#!c:/MKS/mksnt/perl.exe
#
# permute.pl: Calculate the permutation difference between two lists assumed
#    to contain the same set of elements. I define this quantity as the total
#    number of element swaps required to turn one list into the other.
#
# NOTE (01) 

chop($C = `basename "$0"`);

# @LIST1 = (0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
@LIST1 = (0, 1, 2, 3, 4);

sub usage {
  print("USAGE: $C [ -h|-d ]\n\n");
  print("\t-h        ==> display this help/usage information\n");
  print("\t-d        ==> do debug tracing & skip the command\n");
  print("\t-f <file> ==> the input data file to be processed\n\n");
  print("\tDEFAULT VALUES:\n");
  print("\tInput file (-f): ${INPUT_FILE}\n");
  print("\n\n");
  exit(($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C: $_[0] (line #", __LINE__, ")\n");
  usage(1);
}

sub count_perm {	# Assumes 2 equal-sized lists

  $N = scalar(@_);
  local(@A) = @_[ 0..$N/2-1];	# Split 1st half of command line into array A
  local(@B) = @_[$N/2..$N-1];	# Split 2nd half of command line into array B
  
  for ($P = 0, $M = 0, $i = 0; $i < $N; $i++) {
  
    for ($j = $i; $j < $N && $A[$i] != $B[$j]; $j++) { ; }
    
    if ( $i < $j && $j < $N ) {
      ( "$DEBUG" ) && ( print("MOVING ELEMENT $B[$j] TO INDEX $i\n") );
      for ($M++; $j > $i; $P++, $j--) {
        ( "$DEBUG" ) && ( print("J = $j; SWAPPING $A[$i] & $B[$j - 1]\n") );
        $B[$j] = $B[$j - 1];
      }
      $B[$j] = $A[$i];
      ( "$DEBUG" ) && ( print("J = $j; NEW LIST IS: (@B)\n" ) );
    }
    elsif ( $j == $N ) { print("ERROR: Element $A[$i] not in list (@B)\n"); }
  }
  ($M, $P);	# Return total number of elements moved and permutations
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
  ( $_ eq "-d" || $DEBUG eq "TRUE" ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),             next);	# display help/syntax message
  /-d/ && ($DEBUG      = TRUE,    next);	# turn on debug; skip command
  /-f/ && ($INPUT_FILE = shift(), next);	# a data file to be processed
  error("unknown option '$_'");
}

unshift(@ARGV, $_);	# put shifted command line argument back into ARGV
@LIST2 = @ARGV;
print("COMPARING LISTS (@LIST1) AND (@LIST2)\n");
@totals = count_perm(@LIST1, @LIST2);

print("$totals[1] PERMUTATIONS AND $totals[0] ELEMENTS MOVED\n");

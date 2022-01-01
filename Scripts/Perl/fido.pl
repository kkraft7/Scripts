#
# fido.pl: this script is my attempt to implement the "Fido" number puzzle
#   found at http://www.digicc.com/fido/. It seems to be based on the idea
#   that ... (sum to 9)
#
# NOTE (01) 

# Put the whole thing in a loop (with a parameter for specifying iterations)

# gen_rand_num() (return an array with the number and its individual digits)
# scramble_num()
$NUM_LOOPS      = 10;
$DEF_NUM_DIGITS = 4;

chop($C = `basename "$0"`);

sub usage {
  print("USAGE: $C [ -h|-d|-n <#loops> ] <#digits>\n\n");
  print("\t-h          ==> display this help/usage information\n");
  print("\t-d          ==> include some debug tracing messages\n");
  print("\t-n <#loops> ==> number of loops for the Fido puzzle\n");
  print("\t<#digits>   ==> REQUIRED: # of digits in random num\n\n");
  print("\tDEFAULT VALUES:\n");
  print("\tNumber of loops (-n): ${NUM_LOOPS}\n");
  print("\tNumber of digits    : ${DEF_NUM_DIGITS}\n\n");
  exit(($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C: $_[0] (line #", __LINE__, ")\n");
  usage(1);
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
  /-h/ && (&usage(0),            next);		# display help/syntax message
  /-d/ && ($DEBUG     = TRUE,    next);		# turn on debug; skip command
  /-n/ && ($NUM_LOOPS = shift(), next);		# number of Fido puzzle loops
  error("unknown option '$_'");
}

$NUM_DIGITS = ( "$_" || $DEF_NUM_DIGITS );

for ($k = 0; $k < $NUM_LOOPS; $k++) {

  $scramble = $number = 0;
  @num_array1 = @num_array2 = @num_array3 = ();
  
  # (01) Create a random number with $NUM_DIGITS number of digits:
  # Need to prevent number from starting with 0...?

  # Can I do this via int(rand(10**${NUM_DIGITS} - 10**($NUM_DIGITS - 1)) + 10**($NUM_DIGITS - 1))?
  # And then do mod 10 div 10 to get the digits...

  for ($i = 0, $magnitude = 1; $i < $NUM_DIGITS; $i++, $magnitude *= 10) {
     $digit = int(rand(10));
     $number += ${magnitude}*${digit};
     unshift(@num_array1, $digit);
  }

  if ( "$DEBUG" eq "TRUE" ) {
    print("NUMBER     = $number\n");
    print("NUM_ARRAY1 = @{num_array1}\n");
  }

  # (02) Scramble 1st number to create a 2nd number with digits rearranged:

  # (2a) Create an array of indexes (0, 1, 2 .. $NUM_DIGITS):
  for ($i = 0; $i < $NUM_DIGITS; $i++) { push(@{index_array}, $i); }

  # (2b) Scramble the first number:
  for ($i = 0, $magnitude = 1; $i < $NUM_DIGITS; $i++, $magnitude *= 10) {
     $index = splice(@index_array, int(rand($NUM_DIGITS - $i)), 1); # equivalent of delete
     $scramble += $magnitude * $num_array1[$index];
     unshift(@num_array2, $num_array1[$index]);
  }

  if ( "$DEBUG" eq "TRUE" ) {
    print("SCRAMBLE   = $scramble\n");
    print("NUM_ARRAY2 = @{num_array2}\n");
  }

  # (03) Take numbers in Step 1 and Step 2 and subtract larger from smaller:

  # set flag ($num_1_bigger?) indicating which number is bigger
  $new_number = ( $number > $scramble ) ? $number - $scramble : $scramble - $number;

  # (3b) Do digit-wise subtraction using borrowing on the two number arrays:

  if ($number >= $scramble) { @a = @num_array1; @b = @num_array2; }
  if ($scramble > $number ) { @a = @num_array2; @b = @num_array1; }

  for ($i = $NUM_DIGITS - 1; $i >= 0; $i--) {
    if ($a[$i] < $b[$i]) {
      $a[$i] += 10;
      for ($j = $i - 1; $a[$j] == 0; $j--) { $a[$j] = 9; }
      $a[$j]--;
    }
    unshift(@num_array3, $a[$i] - $b[$i]);
  }

  if ( "$DEBUG" eq "TRUE" ) {
    print("NEW_NUMBER = ${new_number}\n");
    print("NUM_ARRAY3 = @{num_array3}\n");
  }

  # (04) Select and remove one digit from the number from Step 3:

  if ( $new_number != 0 ) {
  
    $i = int(rand($NUM_DIGITS));
    while ( $num_array3[$i] == 0 ) { $i = int(rand($NUM_DIGITS)); }
    $selected = splice(@num_array3, $i, 1);

    if ( "$DEBUG" eq "TRUE" ) { print("SELECTED   = ${selected}\n"); }

    # (05) Sum the remaining digits:

    $sum1 = 0;
    for ($i = 0; $i < scalar(@num_array3); $i++) { $sum1 += $num_array3[$i]; }

    # (06) Find first multiple of 9 greater or equal to the sum from Step 4:

    $sum2 = 0;
    while ( $sum1 >= $sum2 ) { $sum2 += 9; }

    # (07) Subtract number in Step 4 from number in Step 5

    $answer = $sum2 - $sum1;

    # (08) The result from Step 6 should equal the number from Step 3

    if ($selected == $answer) {
      if ( "$DEBUG" eq "TRUE" ) { print("ANSWER     = ${answer}\n\n"); }
    }
    else { print("\nERROR: ANSWER = ${answer}; SELECTED = ${selected}\n\n"); }
  }
  else { print("\nERROR: NEW_NUMBER = ${new_number} (SCRAMBLE FAILED)\n\n"); }
}

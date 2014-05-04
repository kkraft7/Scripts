#!c:/Perl/bin/perl.exe
#
# str_combos.pl: Return all ordered combinations of a string
#   and its substrings. I define an "ordered combination" as
#   a string with it characters arranged in alphabetical order.

# ASSUME THE INPUT IS ALREADY IN ALPHABETICAL ORDER?

die "\n1ST ARGUMENT MUST BE A STRING TO PERMUTE\n" if ! $ARGV[0];

sub ordered_combo {
  print "$_[0]\n";
  @a = split '', $_[0];
  if ( @a > 1 ) {
    while ( @a > 1 ) {
      $c = shift @a;
      return ( $c . ordered_perm(@a) );
    }
  }
  else { return "$[0]\n"; }
}

ordered_combo $ARGV[0];


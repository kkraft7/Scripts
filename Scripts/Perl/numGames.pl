#!c:/Perl/bin/perl.exe
#
#

$debug = 0;

sub lhs1 {

  ( $N_max ) = @_;
  $total = 0;
  print "CALCULATING LHS1 FOR N = $N_max:\n" if $debug;

  for ( my $i = 1; $i <= $N_max; $i++ ) {
    $ith_term = $i*10**($N_max - $i);
    $total += $ith_term;
    print "  TERM $i: $ith_term\n" if $debug;
  }
  print "($total * 8) + $N_max\n" if $debug;
  return ($total * 8) + $N_max;
}

sub rhs1 {

  ( $N_max ) = @_;
  $total = 0;
  print "CALCULATING RHS1 FOR N = $N_max:\n" if $debug;

  for ( my $i = 1; $i <= $N_max; $i++ ) {
    $ith_term = (10 - $i)*10**($N_max - $i);
    $total += $ith_term;
    print "  TERM $i: $ith_term\n" if $debug;
  }
  return $total;
}

for ( $i = 1; $i <= 5; $i++ ) {
  print "LHS1($i) = " . lhs1($i) . "\n";
  print "RHS1($i) = " . rhs1($i) . "\n\n";
}


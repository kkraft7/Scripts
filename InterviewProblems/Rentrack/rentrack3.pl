#!c:/Perl/bin/perl.exe
#
# rentrack3.pl: Explain, in English, what this code does, how you might
#     use it, and how you might improve it.
# Toy Problem #3 from Rentrack

sub pr($$) {
    my ( $a, $s ) = @_;
    print "pr( $a, $s )\n";
    return "pr($a, $s)";
}
 
sub foo($$@) {

    my ( $pr, $s, @vect ) = @_;
    my $v0 = shift @vect;
    printf "foo(): V0 = $v0, VECT = [ %s ]\n", join ', ', @vect;

    if ( @vect == 1 ) {
        return $pr->( $v0, $s );
    }
    else {
        return foo( $pr, $pr->( $v0, $s ), @vect );
    }
}

printf "FINAL VALUE IS: %s\n", foo( \&pr, 's', ( a, b, c, d ));


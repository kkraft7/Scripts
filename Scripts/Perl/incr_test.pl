#!c:/Perl/bin/perl.exe -w
#
# test_incr.pl: Test whether there is any difference between using the
#     pre-increment or the post-increment operator in a for-loop. This
#     is based on an question I got at Yahoo! -- the interviewer claimed
#     there was a difference.

# Put results in two lists and compare to create a unit test.

use strict;
my $MAX = 10;
my ( @list1, @list2 );

print "For-loop using post-increment operator:\n";
for ( my $i = 0; $i < $MAX; $i++ ) {
    print " $i";
    push @list1, $i
}
print "\n";

print "For-loop using pre-increment operator:\n";
for ( my $i = 0; $i < $MAX; ++$i ) {
    print " $i";
    push @list2, $i
}
print "\n";
# $list1[0] = 10;

printf "LISTS ARE %sEQUAL\n", ( lists_equal() ? "" : "NOT " );

# Assumes existence of @list1 and @list2
sub lists_equal {
    return 0 if ( @list1 != @list2 );
    for ( my $i = 0; $i < @list1; $i++ ) {
        return 0 if ( $list1[$i] != $list2[$i] );
    }
    return 1;
}


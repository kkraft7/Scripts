#!c:/Perl/bin/perl.exe
#
# sorter.pl: Write a function to return a sorted copy of a list.
#     The function's caller should be able to choose the comparison.
#     How would you test your sort function to convince yourself it
#     works correctly?  Feel free to include more than one solution
#     to this problem.
# Toy Problem #2 from Rentrack

# my $defaultSortFunction = sub { my ( $a, $b ) = @_; return $a <=> $b; }
# my $defaultSortFunction = do { $a <=> $b }
sub defaultSortFunction($$) {
    my ( $a, $b ) = @_;
    return $a <=> $b;
}

sub customSort($@) {
    my ( $sortFunction, @list ) = @_;
    return sort $sortFunction @list;
}

my @list1 = ( 1, 5, 3, 8, 3, 6 );

printf "Original list: %s\n", ( join ', ', @list1 );
# @list1 = sort { $a <=> $b } @list1;
# @list1 = sort defaultSortFunction @list1;
@list1 = customSort(&defaultSortFunction, @list1);
printf "After sorting: %s\n", ( join ', ', @list1 );


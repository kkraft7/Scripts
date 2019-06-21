#!c:/Perl/bin/perl.exe
#
# count.pl: Write a program that, given a file name, returns the
#     number of times the work 'Rentrack' appears in the named file.
# Toy Problem #1 from Rentrack

# Note case-sensitivity issues...

my $filename = ( $ARGV[0] || 'rentrack1.txt' );
my $pattern = 'Rentrack';
my $count = 0;

print "Opening file: $filename\n";
open FILE, $filename;

while ( <FILE> ) {
    print if ( $debugLevel > 0 );
    while ( $_ =~ /$pattern/gc ) { $count++; }
}

printf "The pattern %s appeared in the file %s %d times\n",
    $pattern, $filename, $count;


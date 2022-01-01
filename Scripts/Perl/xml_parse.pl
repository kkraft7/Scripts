#!C:/perl/bin/perl.exe -w
#
# xml_parse.pl: test a simple expression for parsing XML/HTML documents.

die "First argument must be an XML/HTML file to parse" if ( ! -r $ARGV[0] );
open XML_FILE, "$ARGV[0]";

# while ( <XML_FILE> ) {
#     print;
# }

# exit 0;

my $content = join '', <XML_FILE>;
# $content =~ s/\n//;
$content = join '', ( split /\n/, $content );
# print "CONTENTS OF FILE: $ARGV[0]:\n";
# print $content;
 
# my %tags = ( $content =~ /<([^>]+)>+([^<]*)/gc );
my %tags;
# pos($content) = 0;
while ( $content =~ /<([^>]+)>([^<]*)/gc ) {
    my ( $key, $val ) = ( $1, $2 );
    print "KEY = $key; VAL = $val; POS = " . ( pos $content ) . "\n";
    $tags{$key} = $val;
}

for ( my ( $key, $val ) = each %tags ) {
    print "TAG: $key; VAL: $val\n";
}


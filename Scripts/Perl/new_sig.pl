#!c:/mksnt/perl
#
# new_sig.pl: generates new email signature file with randomly-chosen quote
#
# NOTE: 1) Must use escape characters (which are different for both the UNIX
#          and Perl shells) to search for the asterisk divider line
#       2) Here is an example of how to specify a path to a remote file:
#          $sig_file = "//mspacman/kkraft/Humor/sig_quotes.txt";
#          Note that the file must be shared from the remote machine!
#       3) Microsoft Outlook requires (for some unknown reason) that all
#          signature files be in the Rich Text Format (extension ".rtf") 

chop($C = `basename "$0"`);

$DEBUG_LEVEL=0;
$divider    = " +" . ("*" x 72) . "+\n";
$sig_dir    =
  "c:/WinNT/Profiles/kkraft/Application Data/Microsoft/Shared/Signatures";
$sig_name   = "sig_file";
$sig_file   = "${sig_dir}/${sig_name}.rtf";
$sig_quotes = "c:/kkraft/Humor/sig_quotes.txt";

# Use Perl "grep" here??!!
chop($max_quotes = `egrep -c '^ \\+\\*+\\+' ${sig_quotes}`);
srand();				# seed the random number generator
$rand_quote = int(rand($max_quotes));	# generate a random number for a quote
$sig_header =

  "${divider}" .
  " | Kevin Kraft              " . (" " x 25) . "Work: (510) 595-5104 |\n" .
  " | kkraft\@extensity.com    " . (" " x 26) . "Home: (650) 234-1134 |\n" .
  " | Quality Assurance Testing" . (" " x 25) . " Fax: (510) 596-2676 |\n" .
  " | Extensity                " . (" " x 25) . "                     |\n" .
  " |                          " . (" " x 25) . "                     |\n" ;

sub usage {
  print("USAGE: $C [ -h|-d ]\n\n");
  print("\t-h   ==> display this help message\n");
  print("\t-d   ==> do debug tracing and skip command\n\n");
  exit( ($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C\n\n\t$_[0]\n\tLine # ", __LINE__, "\n");
  usage(1);
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
# ( $_ eq "-d" || $DEBUG_LEVEL > 1 ) && ( print("Argument is '$_'\n") );
# Rewrite this with GoTo so we go to bottom of case rather than next loop?!
  /-h/ && (&usage(0),              next);	# display help/syntax message
  /-d/ && ($DEBUG_LEVEL = shift(), next);	# turn on debug; skip command
  error("unknown option '$_'");
  ( $DEBUG_LEVEL > 1 ) && ( print("Argument is '$_'\n") );
}

if ($DEBUG_LEVEL > 0) {
  print("\n");
  print("Dir  is '${sig_dir}'\n");
  print("Max  is '${max_quotes}'\n");
  print("Rand is '${rand_quote}'\n");
  if ($DEBUG_LEVEL > 1) {
    print("Sig  is:\n${sig_header}${divider}\n");
  }
  print("\n");
}

$i = 0;
open (SIG_QUOTES, $sig_quotes) || &error("Failed to open '${sig_quotes}'\n");

while (<SIG_QUOTES>) {
  # could add comment capability with "/^#/ && ( next );"
  if ( $i == $rand_quote ) {
    $quote .= "$_";
    ( $_ eq ${divider} ) && ( last );
  }
  ( $_ eq ${divider} ) && ( $i++ );	# increment i for each divider line
}

# Save the old signature file to a backup:
system("c:/mksnt/cp", "${sig_file}", "${sig_dir}/old_sig.rtf");
open (SIG_FILE, ">${sig_file}") || &error("Failed to open '${sig_file}'\n");
print(SIG_FILE "${sig_header}${quote}");
close(SIG_FILE);
system("c:/mksnt/cat", "${sig_file}");


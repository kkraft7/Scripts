#!c:/MKS/mksnt/perl.exe
#
# rename.pl: Rename files with 2-digit padded numeric suffixes to
#            use 3-digit padded numeric suffixes
#
# NOTE (01) 

use File::Basename;
$C        = basename($0);
$HOME_DIR = "c:/Documents and Settings/Kevin/My Documents";
$PATH_DIR = "$HOME_DIR/Humor";

sub usage {
  print("\nUSAGE: $C [ -h|-d|-r <dir> ]\n\n");
  print("\t-h        ==> display a help/usage message for user\n");
  print("\t-d        ==> do debug tracing and skip the command\n");
  print("\t-r <dir>  ==> directory containing files to process\n\n");
  print("\tDEFAULT VALUES:\n");
  print("\tInput dir (-r): ${PATH_DIR}\n");
  printf("\n");
  exit(($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C: $_[0]\n");
  usage(1);
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
  ( $_ eq "-d" || $DEBUG eq "TRUE" ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),           next);	# display help/syntax message
  /-d/ && ($DEBUG    = TRUE,    next);	# turn on debug; skip command
  /-r/ && ($PATH_DIR = shift(), next);	# dir having files to process
  error("unknown option '$_'");
}

if ( "$DEBUG" eq "TRUE" ) {
  print("File  : ${INPUT_FILE}\n");
  print("\n");
# exit(0);
}

chdir("$PATH_DIR")        || die("Unable to do chdir() for '$PATH_DIR'\n");
opendir(DIR, "$PATH_DIR") || die("Unable to open directory '$PATH_DIR'\n");

while ( $file = readdir(DIR) ) {

  if ( $file =~ /(.*)(\d\d\.(jpg|gif))/ ) {

    print("Moving file '$file' to '${1}0${2}'\n");
    rename("$file", "${1}0${2}");
  }
}

closedir(DIR);

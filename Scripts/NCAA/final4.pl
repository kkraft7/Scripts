#!/usr/bin/perl
# c:/Perl/bin/perl.exe -w
#
# final4.pl: 

use File::Basename;
$C         = basename($0);
$DEBUG     = "FALSE";
$START_IDX = 56;	# index of first final four seed
# $data_file = "ncaaMW.dat";
$data_file = "ncaa.dat";
$data_dir  = "/home/Kevin/Scripts/NCAA";

################################################################################
# error:                                                                       #
# Print out an error message.                                                  #
################################################################################
sub error {
  print("\nERROR: $C\n\n\t$_[0]\n\tLine # ", __LINE__, "\n");
  usage(1);
}

################################################################################
# Process command-line arguments.                                              #
################################################################################
for ($_ = shift(); /-[A-z]/; $_ = shift()) {
# ( $_ eq "-d" || $DEBUG eq "TRUE" ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),               next);	# display help/syntax message
  /-d/ && ($DEBUG     = "TRUE",     next);	# turn on debug; skip command
  /-r/ && ($data_dir  = shift(),    next);      # directory for the data file
  /-f/ && ($data_file = shift(),    next);	# file holding seed/year data
  error("unknown option '$_'");
}

$file_name = "${data_dir}/${data_file}";
open DATA_FILE, "${file_name}" || die "Failed to open file '${file_name}'\n";

while ( <DATA_FILE> ) {

  if ( /^\s*#/ || /^\s*$/ || /^\s*sM\w\w/ ) { next; }
  ( $year, $seeds ) = split;
  @final4 = ( split /,/, $seeds )[ ${START_IDX}..($START_IDX + 3) ];
  printf "%d: %s\n", $year, join " ", @final4;
}


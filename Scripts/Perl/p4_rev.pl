#!c:/MKS/mksnt/perl.exe
#
# p4_rev.pl: Given a Perforce 6.x build label and Perforce file from the
#            label, determine the revision number of the file in the label.
#
# NOTE (01) 

# UPDATE THIS TO TAKE IN A CHANGELIST NUMBER?! (-c)
# 1) Determine the build label for the build you are using
#    * 6.1 build labels should be: Build_Rel6_x_<build#>
#    * SP6 build labels should be: Build_Rel6_x_SP_<build#>
# 2) Example file: install/V0/data/qa/wf_activity_permission.txt
# 3) p4 -n sync //depot/<path>/<file>@<label_name>

chop($C = `basename "$0"`);

$label_root = "Build_Rel6_x";

sub usage {
  print("USAGE: $C [ -h|-d ]\n\n");
  print("\t-h         ==> display this help/usage information\n");
  print("\t-d         ==> do debug tracing & skip the command\n");
  print("\t-s         ==> indicates that this is an SP build\n");
  print("\t-b <build> ==> the build number for the 6.x label\n");
  print("\t-f <files> ==> comma-separated list of source files\n\n");
  exit( ($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C: $_[0]\n");
  usage(1);
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
  ( $_ eq "-d" || $DEBUG eq "TRUE" ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),                          next);	# display help/syntax message
  /-d/ && ($DEBUG       = TRUE,                next);	# turn on debug; skip command
  /-s/ && ($label_root .= "_SP",               next);	# build is for a service pack
  /-b/ && ($build_num   = shift(),             next);	# build number for this label
  /-f/ && (@file_list   = split(/,/, shift()), next);	# a comma-separated file list
  error("unknown option '$_'");
}

if (!defined($build_num) || !defined(@file_list)) {
  error("MUST DEFINE BUILD NUMBER AND FILE LIST");
}

if ( $label_root =~ /Build_(.*)/ ) { $source_root = "//depot/$1/largesoft"; }

if ($DEBUG eq "TRUE") {
  print("Source Root: $source_root\n");
  print("Label Root : $label_root\n");
  print("Build Num  : $build_num\n");
  print("File List  : @file_list\n");
# exit(0);
}

print("THE BUILD LABEL IS: ${label_root}_${build_num}\n");
print("THE SOURCE ROOT IS: ${source_root}\n");

foreach $file (@file_list) {
  print("FILE IS: ${file}\n");
  system("p4 sync -n ${source_root}/${file}@${label_root}_${build_num}");
  # NEED TO JUST GRAB PART OF P4 OUTPUT!
}

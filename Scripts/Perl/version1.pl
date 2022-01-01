#!/usr/bin/perl -w
#
# version.pl: Compare product version numbers.

# NOTES:
# 01) The construct '@_ = split(/\./, $v2)' is a way to get around issues
#     with treating the output of split() as an array

# ADD MATH OPERATORS (> >= < <= ==)?
# COULD CHANGE MANDATORY ARGUMENTS TO V1, V2, AND OP, FOR BREVITY

use strict;
use File::Basename;		# Includes Unix-like basename() function

my ( $version1, $version2, $operator, $v1Length, $status );
my ( @v1Array, @v2Array, @versionList );
my $valid_ops = "gt|ge|lt|le|eq|in";

my $C = basename($0);
my ( $DebugLevel, $ExitScript ) = ( 0, 0 );

##############################################################################
# num:                                                                       #
# Checks if the argument is numeric -- returns number if so, 0 otherwise.    #
# Note that this may fail in a boolean test if the number passed in is 0!    #
##############################################################################
sub num { defined($_[0]) && $_[0] =~ /^(-?\d+(\.\d+)?)$/ && $1 || 0; }

##############################################################################
# usage:                                                                     #
# Print out a Help message detailing script usage.                           #
##############################################################################
sub usage {
  print("\nDESCR: $C: Compare product version numbers.\n");
  print("\nUSAGE: $C [ -h|-x|-d <num> ] <v1> <op> <v2> [ <v3> .. <vN> ]\n\n");
  print("\t-h        ==> display this help/usage information\n");
  print("\t-x        ==> exit script without running command\n");
  print("\t-d <num>  ==> specify the debug information level\n");
  print("\n\tCURRENT VALUES:\n");
  print("\tExit Script (-x): $ExitScript\n");
  print("\tDebug Level (-d): $DebugLevel\n");
  print("\n\tNOTES:\n");
  print("\t01. The <v#> arguments indicate version numbers (e.g. 3.0.1)\n");
  print("\t02. The <op> argument indicates a math operator ($valid_ops)\n");
  print("\n");
  exit(($_[0] =~ /[01]/) ? $_[0] : 0);
}

##############################################################################
# error:                                                                     #
# Print out an error message.                                                #
##############################################################################
sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C: $_[0] (line #", __LINE__, ")\n");
  usage(1);
}

##############################################################################
# Process command-line options.                                              #
##############################################################################
while ( defined($ARGV[0]) && $ARGV[0] =~ /-[a-zA-Z]/ ) {

# if ( $_ eq "-d" || $DebugLevel > 0 ) { print("Argument is '$_'\n"); }
  $_ = shift;

  /-h/ && (&usage(0),                next);     # display help/syntax message
  /-x/ && ($ExitScript = 1,          next);     # exit before running command
  /-d/ && ($DebugLevel = num(shift), next);     # specify level of debug info
  error("unknown option '$_'");
}

##############################################################################
# Process positional command-line arguments.                                 #
##############################################################################
if ( defined($ARGV[0]) && $ARGV[0] =~ /^\d+(\.\d+)*$/ ) {
  $version1 = $ARGV[0];
  $v1Length = scalar( @_ = split(/\./, $version1) );
} else { error("1ST ARGUMENT MUST BE A VERSION NUMBER"); }

if ( defined($ARGV[1]) && $ARGV[1] =~ /^($valid_ops)$/ ) {
  $operator = $ARGV[1];
} else { error("2ND ARGUMENT MUST BE AN OPERATOR ($valid_ops)"); }

if ( defined($ARGV[2]) && $ARGV[2] =~ /^\d+(\.\d+)*$/ ) {
  $version2 = $ARGV[2];
} else { error("3RD ARGUMENT MUST BE A VERSION NUMBER"); }

if ( defined($ARGV[3]) ) {

  if ( $operator ne "in" ) { error("EXTRA ARGUMENTS AT END-OF-LINE"); }
  @versionList = ( $version2 );

  for ( my $i = 3; defined($ARGV[$i]); $i++ ) {
    if ( $ARGV[$i] =~ /^\d+(\.\d+)*$/ ) { push(@versionList, $ARGV[$i]); }
    else { error("${i}TH ARGUMENT MUST BE A VERSION NUMBER"); }
  }
}
  
if ( $DebugLevel || $ExitScript ) {
  print("\n#debug: Exit Script: $ExitScript\n");
  print(  "#debug: Debug Level: $DebugLevel\n");
  print(  "#debug: Version #1 : $version1\n");
  print(  "#debug: Version #2 : $version2\n");
  print(  "#debug: Operator   : $operator\n");
  if ( @versionList ) { print("#debug: IN List    : @versionList\n"); }
  if ( $ExitScript  ) { print("DEBUG MODE (EXIT=1): exiting $C\n"); exit(0); }
  print("\n");
}

##############################################################################
#                                                                            #
##############################################################################
if ( $operator eq "in" ) {

  foreach my $v2 ( @versionList ) {
    my $v1 = $version1;
    my $v2Length = ( @_ = split(/\./, $v2) );
    if ( $v1Length < $v2Length ) {
      $v1 .= '.0' x ( $v2Length - $v1Length );
    }
    elsif ( $v2Length < $v1Length ) {
      $v2 .= '.0' x ( $v1Length - $v2Length );
    }
    if ( $DebugLevel > 1 ) { print("COMPARING $v1 TO $v2\n"); }
    if ( $v1 eq $v2 ) { $status = 0; last; }
  }
  if ( ! defined($status) ) { $status = 1; }
}
else {

  my $v2Length = ( @_ = split(/\./, $version2) );
  if ( $v1Length < $v2Length ) {
    $version1 .= '.0' x ( $v2Length - $v1Length );
  }
  elsif ( $v2Length < $v1Length ) {
    $version2 .= '.0' x ( $v1Length - $v2Length );
  }
  if ( $DebugLevel > 1 ) { print("COMPARING $version1 TO $version2\n"); }
  $status = eval("$version1 $operator $version2") ? 0 : 1;
}

if ( $DebugLevel ) {
  printf("CONDITION %s (EXIT %d): $version1 $operator %s\n",
    ( $status == 0 ? "TRUE" : "FALSE" ), $status,
    ( @versionList ? join(' ', @versionList) : $version2 ));
}

exit $status;


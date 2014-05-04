#!/usr/bin/perl -w
#
# NAME.pl: DESCRIPTION.

# NOTES:
# 01) Can "fake" a 2-D array by doing a double for-loop over $i and $j
#     with a 1-D array and referencing "array($i*$MAX_VAL + $j)"
# 02) Can "fake" a round() function by doing: round(a) = int(a + 0.5)
# 03) Note that undef($var) is NOT equivalent to $var = ""!
# 04) All Perl variables are global unless explicitly declared
#     otherwise (e.g., with "my" or "local")
# 05) For some reason "pop(split())" fails to compile!
# 06) Looks like confess() is preferable to die(), for longer programs
# 07) Can create a Perl quasi-MAXINT by using $MAXINT = 1 << 32;
# 08) How to force split() into an array context: @{[split(/\./, $string)]}
# 09) How to use tr to count fields in a string: ( $string =~ tr/\.// + 1 )

use strict;
require 5.000;			# Perl version must be greater than this
require "ctime.pl";		# Includes time format function: ctime()
use File::Basename;		# Includes Unix-like basename() function
use Carp;			# Includes croak(), confess() and carp()
use Math::Trig;			# Include trig functions & pi definition
use File::Copy;			# Include system-independent copy routine
# use Time::Format qw(%time %strftime %manip);	# Time formatting

# $C = (split(/\\/, $0))[-1];	# indexes wrap around: -1 is last element!
my $C          = basename($0);
my ( $InputFile, $OutputFile ) = ( "Fred", "Barney" );
my $HomeDir    = "/home/kkraft";
my $FirstArg   = "default_value";
my ( $DebugLevel, $ExitScript ) = ( 0, 0 );

##############################################################################
# num:                                                                       #
# Checks if the argument is numeric -- returns number if so, 0 otherwise.    #
# Note that this may fail in a boolean test if the number passed in is 0!    #
##############################################################################
sub num { defined($_[0]) && $_[0] =~ /^(-?\d+(\.\d+)?)$/ && $1 || 0; }

##############################################################################
# ts:                                                                        #
# Use the ctime() function to format a timestamp.                            #
# The argument is a Unix-style timestamp from the time() function.           #
##############################################################################
sub ts {
  # Is there some way to do error-checking to ensure that $_[0] is a timestamp?
  my ($day, $mon, $dd, $tt, $yy) = split(/\s+/, ctime($_[0]));
  return "$tt \U$day \U$mon $dd $yy";
}

##############################################################################
# usage:                                                                     #
# Print out a Help message detailing script usage.                           #
##############################################################################
sub usage {
  print("\nDESCR: $C: DESCRIPTION.\n");
  print("\nUSAGE: $C [ -h|-x|-d <num>|-f <file> ] <cluster>\n\n");
  print("\t-h        ==> display this help/usage information\n");
  print("\t-x        ==> exit script without running command\n");
  print("\t-d <num>  ==> specify the debug information level\n");
  print("\t-f <file> ==> the input data file to be processed\n");
  print("\n\tCURRENT VALUES:\n");
  print("\tExit Script (-x): $ExitScript\n");
  print("\tDebug Level (-d): $DebugLevel\n");
  print("\tInput File  (-f): $InputFile\n");
  print("\n\tNOTES:\n");
  print("\t01. The <cluster> is specified as '<node>:<port>'\n");
  print("\t02. Describe all required parameters\n");
  print("\n");
  exit(($_[0] =~ /[01]/) ? $_[0] : 0);
}

##############################################################################
# error:                                                                     #
# Print out an error message.                                                #
##############################################################################
# Could loop over multiple input arguments printing out a list of errors:
sub error { print("\nERROR: $C: $_[0] (line #", __LINE__, ")\n"); usage(1); }

##############################################################################
# Process command-line options.                                              #
##############################################################################
while ( defined($ARGV[0]) && $ARGV[0] =~ /^[ ]*-[a-zA-Z]/ ) {

# if ( $_ eq "-d" || $DebugLevel > 0 ) { print("Argument is '$_'\n"); }
  $_ = shift;

  /-h/ && (&usage(0),                next);	# display help/syntax message
  /-x/ && ($ExitScript = 1,          next);	# exit before running command
  /-d/ && ($DebugLevel = num(shift), next);	# specify level of debug info
  /-f/ && ($InputFile  = shift,      next);	# a data file to be processed
  error("UNKNOWN OPTION '$_'");
}

##############################################################################
# Process positional command-line arguments.                                 #
##############################################################################
# CLUSTER/NODE PATTERN IS ^[\w\-\.]+
if ( defined($ARGV[0]) && $ARGV[0] =~ /^\w+$/ ) {
  $FirstArg = $ARGV[0];
} else { error("INVALID FIRST ARGUMENT: '" . ( $ARGV[0] || "" ) . "'"); }

if ( $DebugLevel || $ExitScript ) {
  print("\n#debug: Exit Script: $ExitScript\n");
  print(  "#debug: Debug Level: $DebugLevel\n");
  print(  "#debug: Input File : $InputFile\n");
  if ( $ExitScript ) { print("DEBUG MODE (EXIT=1): exiting $C\n"); exit(0); }
  print("\n");
}

open(INPUT_FILE,  "$InputFile")   || confess("CANNOT OPEN FILE '$InputFile'\n");
open(OUTPUT_FILE, ">$OutputFile") || confess("CANNOT OPEN FILE '$OutputFile'\n");

##############################################################################
#                                                                            #
##############################################################################
while ( <INPUT_FILE> ) {
  # Manipulate input file line and print it to the output file
  s/aaa/bbb/;
  print(OUTPUT_FILE);
}

close(INPUT_FILE);
close(OUTPUT_FILE);


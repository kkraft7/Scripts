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

require 5.000;			# Version must greater than specified number
require "ctime.pl";		# Includes ctime() time format function
use Math::Trig;			# Includes definition of pi
use Carp;			# Include croak(), confess(), and carp()
use File::Copy;			# # System-independent copy routine
use File::Basename;		# Includes Unix-like basename() function
use Time::Format qw(%time %strftime %manip);	# Time formatting
use strict;

# $C = (split(/\\/, $0))[-1];	# indexes wrap around: -1 is last element!
my $C          = basename($0);
my $HOME_DIR   = "/home/kkraft";
my $DebugLevel = 0;
my $FirstArg   = "default_value";

##############################################################################
# numeric:                                                                   #
# Checks if the argument is numeric -- returns number if so, 0 otherwise.    #
# Note that this may fail if the number passed in is 0!                      #
##############################################################################
sub numeric { defined($_[0]) && $_[0] =~ /^(\d+)$/ && $1 || 0; }

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
  print("\n$C: DESCRIPTION.\n\n");
  print("\nUSAGE: $C [ -h|-d <num>|-f <file> ]\n\n");
  print("\t-h        ==> display this help/usage information\n");
  print("\t-d <num>  ==> specify the debug information level\n");
  print("\t-f <file> ==> the input data file to be processed\n");
  print("\n\tCURRENT VALUES:\n");
  print("\tDebug level (-d): $DebugLevel\n");
  print("\tInput file  (-f): $INPUT_FILE \n");
  print("\n\tNOTES:\n");
  print("\t01. If debug level = 1 program exits without running commands\n");
  print("\t\n");
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
  $_ = shift();

  /-h/ && (&usage(0),             next);	# display help/syntax message
  /-d/ && ($DebugLevel = shift(), next);	# specify level of debug info
  /-f/ && ($INPUT_FILE = shift(), next);	# a data file to be processed
  error("unknown option '$_'");
}

##############################################################################
# Process $FIRST_ARG from the command-line                                   #
##############################################################################
if ( defined($ARGV[0]) && $ARGV[0] =~ /^[\w\.]+:\d{4}$/ ) {
  $FirstArg = $ARGV[0];
  shift;
} else { error ("INVALID FIRST ARGUMENT: '$ARGV[0]'"); }

open(INPUT_FILE, "$INPUT_FILE") ||
  confess ("Unable to open '$INPUT_FILE' for reading\n");
open(OUTPUT_FILE, ">$OUTPUT_FILE") ||
  confess ("Unable to open '$OUTPUT_FILE' for reading\n");

if ( "$DebugLevel" > 0 ) {
  print("Debug Level: $DEBUG_LEVEL\n");
  print("File Name  : $INPUT_FILE\n");
  if ( $DebugLevel == 1 ) { print("DEBUG RUN: quitting program\n"); exit(0); }
  print("\n");
}

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

#!c:/Perl/bin/perl.exe -w
#
# backup.pl: Save backup copies of important files to a central location
#
# NOTE 
#      (02) Only origin files that have been updated (i.e. where the origin
#           timestamp is more recent than the target timestamp) are copied
#      (03) Use the pound sign character (#) for comments (although I have
#           set up the pattern match to simply ignore unrecognized lines)
#      (04) Specify the origin_dir and target_dir directories relative to
#           their respective origin_root and target_root directories
#      (05) Note that setting $var = "" is not the same as undef($var)!
#      (06) TO COPY FROM CORP-FSR1 TO MY ZIP DISK USE BACKUP_HOME.TXT

# CHANGING OR AT END OF SCRIPT IS FAILING (?)
# HAVE TO EXIT WHEN UNABLE TO FIND ORIGIN DIR, BECAUSE THE WRONG DIR
# BECOMES THE ORIGIN AND THE WRONG FILES GET COPIED!

# USE READDIR() FOR '*' AND GLOBBING FOR *.SQL ETC? OR READDIR() + GREP?
# * STOP PRINTING OUT FILE NAMES (FN), OR MAKE IT AN OPTION (-n) OR DEBUG LEVEL
# * DON'T NEED TO PRINT OUT FULL PATH WITH FILE NAME? (ALREADY DISPLAYED ABOVE)
# * Have an option to print out only copied files? (make this the default?)
# * ADD FLAGS TO SPECIFY CERTAIN DATA FILES (E.G. -b FOR "BACKUP_HOME.TXT")?
# * Can we change the origin_root and target_root in the MIDDLE of a run?
# * Note that a missing origin directory causes a bug (next origin dir is copied)
# * Report errors and total number of files copied at the end of the run?
# * Need a debug setting to print config info and exit before copying
# * Use the "I" flag (BEFORE "*"!) to construct an "ignore" list?
# * Create a flag to copy all files after a given date?

# * Implement the log file and the "-l" flag for the log file directory
# * Figure out what to do with subdirectories (right now I get an error) --
#   Can I automatically copy them and their contents? Or create them?
# * globbing didn't work on '*' with no directory path (i.e. $file_name == '*')
#     chdir($origin);
#     while ( $file = <$file_name> ) { ; }
# * If target directory missing look for origin directory on target file system?
# * Measure total size of files slated for transfer and actually transferred
# * Create target directory if it does not exist?

require 5.000;
# use strict;
# use warnings;

use File::Copy;		# System-independent copy routine
use File::Basename;
$C = basename($0);
$| = 1;			# Flush I/O buffers

my $DEBUG_LEVEL   = 0;
# WHAT IS THE VALUE OF $ENV{'HOME'}?
my $HOME_DIR      = "c:/Documents and Settings/kevin/My Documents";
my $start_time = time();
$origin_root   = "$HOME_DIR";
$target_root   = "g:/Kevin";
$data_file     = "backup_home.txt";
$data_dir      = "$HOME_DIR/Scripts/backup";
$log_file      = "";
# $log_dir     = ""; # NOT USED, CURRENTLY

sub usage {
  print("USAGE: $C [ -h|-d <level>|-f <file>|-r <dir>|-o <origin>|-t <target>\n");
  print("		     |-l <log_dir> ]\n\n");
  print("\t-h             ==> display this help/usage information\n");
  print("\t-d <dbg_level> ==> do debug tracing & skip the command\n");
  print("\t-f <data_file> ==> the input data file to be processed\n");
  print("\t-r <data_dir>  ==> the directory holding the data file\n");
  print("\t-o <origin>    ==> the origin root directory for files\n");
  print("\t-t <target>    ==> the target root directory for files\n");
  print("\t-l <log_dir>   ==> the directory holding the log files\n");
  print("\n");
  exit( ($_[0] =~ /[01]/) ? $_[0] : 0);
}

sub error {
  # Could loop over multiple input arguments printing out a list of errors:
  print("\nERROR: $C: $_[0] (line #", __LINE__, ")\n");
  usage(1);
}

for ($_ = shift(); /-[A-z]/; $_ = shift()) {
# ( $_ eq "-d" || $DEBUG_LEVEL > 0 ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),              next);	# display help/syntax message
  /-d/ && ($DEBUG_LEVEL = shift(), next);	# specify debug message level
  /-f/ && ($data_file   = shift(), next);	# data file to be processed
  /-r/ && ($data_dir    = shift(), next);	# data file directory
  /-o/ && ($origin_root = shift(), next);	# origin file root directory
  /-t/ && ($target_root = shift(), next);	# target file root directory
  /-l/ && ($log_file    = shift(), next);	# log file directory
  error("unknown option '$_'");
}

# Test validity of $origin_root and $target_root!
print("\nORIGIN_ROOT: $origin_root\nTARGET_ROOT: $target_root\n");
print(  "DEBUG_LEVEL: $DEBUG_LEVEL\nDATA_FILE  : $data_dir/$data_file\n");
open(DATA_FILE, "$data_dir/$data_file") ||
  die("\nERROR: UNABLE TO OPEN '$data_dir/$data_file' FOR READING.\n\n");
# open(OUTPUT_FILE, ">${OUTPUT_FILE}") ||
#   die("Unable to open '${OUTPUT_FILE}' for reading");

while ( <DATA_FILE> ) {
  
# if ( /^\s*(\w\w)\s+([^\r\n])/ && ! /^\s*#/ ) {
  chomp();
  if ( /^\s*(\w\w)\s+(.*)/ && ! /^\s*#/ ) {
  
    if    ( $1 eq "OD" ) { print("\n"); }
    
    if    ( $1 eq "FN" ) { $file_names  = "$2"; print( "FILE_NAMES: $file_names\n" ); }
    elsif ( $1 eq "OD" ) { $origin_dir  = "$2"; print( "ORIGIN_DIR: $origin_dir\n" ); }
    elsif ( $1 eq 'TD' ) { $target_dir  = "$2"; print( "TARGET_DIR: $target_dir\n" ); }
    elsif ( $1 eq 'OR' ) { $origin_root = "$2"; print("ORIGIN_ROOT: $origin_root\n"); }
    elsif ( $1 eq 'TR' ) { $target_root = "$2"; print("TARGET_ROOT: $target_root\n"); }
  }
  
  if ( defined($origin_dir) && defined($target_dir) && defined($file_names) ) {
    
    $origin = "$origin_root/$origin_dir";
    $target = "$target_root/$target_dir";
    if ( ! -d $origin || ! -d $target ) {
      if ( ! -d $origin ) { print("     ERROR: Can't find origin directory: $origin\n"); }
      if ( ! -d $target ) { print("     ERROR: Can't find target directory: $target\n"); }
      print("\n");
      undef($origin_dir);
      undef($target_dir);
      undef($file_names);
      next;
    }

    # TRY USING "ls" INSTEAD OF GLOBBING TO GENERATE FILE LIST (e.g. mssql/binn/*.sql)?
    # DOESN'T WORK. TRY GLOBBING AND GRABBING FILE NAME FOR TARGET?
    chdir($origin);
    # @file_list = `ls $file_names`;
    # USE READDIR() FOR '*' AND GLOBBING FOR *.SQL ETC? OR READDIR() + GREP?

    opendir(ORIGIN, $origin) || ( print("Unable to open directory: $origin\n") );
    @file_list = ( $file_names eq '*' || $file_names eq 'ALL' ) ? readdir(ORIGIN) : ($file_names);
    foreach $file (@file_list) {
      
      if (   -d "$origin/$file" ) { next; }	# SKIP DIRECTORIES
      if ( ! -f "$origin/$file" ) { print("     ERROR: Can't find file: $origin/$file\n"); next; }
      if ( ! -d "$target"       ) { print("     ERROR: Can't find directory: $target\n" ); next; }

      if ( ! -f "$target/$file" || -M "$target/$file" > -M "$origin/$file" ) {
        if ( "$DEBUG_LEVEL" >= 0 ) { print("   COPYING: $file to $target\n"); }
#       if ( "$DEBUG_LEVEL" == 0 ) { copy( "$origin/$file", $target ) || print("FAILED TO COPY '$origin/$file' to '$target'\n"); }
#       if ( "$DEBUG_LEVEL" == 0 ) { copy( "$origin/$file", $target ) || print("$EXTENDED_OS_ERROR"); }
        if ( "$DEBUG_LEVEL" == 0 ) { copy( "$file", $target ) || print("$EXTENDED_OS_ERROR"); }
#       if ( "$DEBUG_LEVEL" == 0 ) { system("copy '$origin/$file' '$target'") || print("ERROR: $!\n"); }
#       if ( "$DEBUG_LEVEL" == 0 ) { system("cp '$origin/$file' '$target'") || print("ERROR: $!\n"); }
#       if ( "$DEBUG_LEVEL" == 0 ) { system("cp '$file' $target") || print("ERROR: $!\n"); }
        if ( "$DEBUG_LEVEL" >= 2 ) {
          print("      FILE: $file\n");
          if ( -f "$target/$file" ) {
            print("            Days since origin modification: " . int( -M "$origin/$file" ) . "\n");
            print("            Days since target modification: " . int( -M "$target/$file" ) . "\n");
          }
        }
      }
    }
    $file_names = "";
  }
  # print(OUTPUT_FILE);
}
close(DATA_FILE);
# close(OUTPUT_FILE);

$elapsed_time = time() - $start_time;
$hrs = $elapsed_time / 3600;
$min = ( $elapsed_time % 3600 ) / 60;
$sec = ( $elapsed_time % 3600 ) % 60;

# This is a numeric pad function that adds leading zeros to a number:
sub pad {
  $int_part = int($_[0] + 0.5);
  $pad_size = ( defined($_[1]) ) ? $_[1] : 2;
  $act_size = length($int_part);
  $num_pads = ( $pad_size > $act_size ) ? $pad_size - $act_size : 0;
  (( '0' x $num_pads ) . ( $int_part ));
}

if ( "$DEBUG_LEVEL" <= 2 ) {
  printf("\nELAPSED TIME: %s:%s:%s (HH:MM:SS)\n\n", pad($hrs), pad($min), pad($sec));
}


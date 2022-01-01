#!c:/MKS/mksnt/perl.exe
#
# backup.pl: Save backup copies of important files to a central location
#
# NOTE (01) Only origin files that have been updated (i.e. where the origin
#           timestamp is more recent than the target timestamp) are copied
#      (02) Use the pound sign character (#) for comments (although I have
#           set up the pattern match to simply ignore unrecognized lines)
#      (03) Specify the origin_dir and target_dir directories relative to
#           their respective origin_root and target_root directories

# * Use the "I" flag (BEFORE "*"!) to construct an "ignore" list?
# * Implement the log file and the "-l" flag for the log file directory
# * Figure out what to do with subdirectories (right now I get an error) --
#   Can I automatically copy them and their contents?
# * globbing didn't work on '*' with no directory path (i.e. $file_name == '*')
#     chdir($origin);
#     while ( $file = <$file_name> ) { ; }
# * If target directory missing look for origin directory on target file system?
# * Create target directory if it does not exist?

$start_time = time();

chop($C = `basename "$0"`);

$origin_root = "c:";
$target_root = "//Corp-fsr1/kkraft/Backup";
$data_file   = "backup_work.txt";
$data_dir    = "c:/kkraft";
$log_file    = "";
$log_dir     = "";

sub usage {
  print("USAGE: $C [ -h|-d|-f <data_file>|-r <data_dir>|-o <origin>|-t <target>\n");
  print("		     |-l <log_dir> ]\n\n");
  print("\t-h             ==> display this help/usage information\n");
  print("\t-d             ==> do debug tracing & skip the command\n");
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
  ( $_ eq "-d" || $DEBUG eq "TRUE" ) && ( print("Argument is '$_'\n") );
  /-h/ && (&usage(0),              next);	# display help/syntax message
  /-d/ && ($DEBUG       = TRUE,    next);	# turn on debug messages
  /-f/ && ($data_file   = shift(), next);	# data file to be processed
  /-r/ && ($data_dir    = shift(), next);	# data file directory
  /-o/ && ($origin_root = shift(), next);	# origin file root directory
  /-t/ && ($target_root = shift(), next);	# target file root directory
  /-l/ && ($log_file    = shift(), next);	# log file directory
  error("unknown option '$_'");
}

# Test validity of $origin_root and $target_root!
print("\nORIGIN_ROOT: $origin_root\nTARGET_ROOT: $target_root\n");
open(DATA_FILE, "${data_dir}/${data_file}") ||
  die("Unable to open '${data_dir}/${data_file}' for reading");
# open(OUTPUT_FILE, ">${OUTPUT_FILE}") ||
#   die("Unable to open '${OUTPUT_FILE}' for reading");

while ( <DATA_FILE> ) {
  
  if ( /^\s*(\w\w)\s+(.*)/ && ! /^\s*#/ ) {
  
    if    ( $1 eq "OD" ) { print("\n"); }
    
    if    ( $1 eq "FN" ) { $file_names  = "$2"; print("FILE_NAMES: $file_names\n"  ); }
    elsif ( $1 eq "OD" ) { $origin_dir  = "$2"; print("ORIGIN_DIR: $origin_dir\n"  ); }
    elsif ( $1 eq 'TD' ) { $target_dir  = "$2"; print("TARGET_DIR: $target_dir\n"  ); }
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
    # chdir($origin);
    # @file_list = `ls $file_names`;
    # USE READDIR() FOR '*' AND GLOBBING FOR *.SQL ETC?

    opendir(ORIGIN, $origin) || ( print("Unable to open directory: $origin\n") );
    @file_list = ( $file_names eq '*' || $file_names eq 'ALL' ) ? readdir(ORIGIN) : ($file_names);
    foreach $file (@file_list) {
    # while ( $file = <$origin/$file_names> ) {		# Globbing
      
      # @path_list = split('/', $file);
      # print("PATH LIST = @path_list\nPATH SIZE = $#path_list\n");
      # $f = $path_list[$#path_list];
      
      if (   -d "$origin/$file" ) { next; }	# SKIP DIRECTORIES
      if ( ! -f "$origin/$file" ) { print("     ERROR: Can't find file: $origin/$file\n"); next; }
      
      # if (   -d "$file" ) { next; }	# SKIP DIRECTORIES
      # if ( ! -f "$file" ) { print("     ERROR: Can't find file: $origin/$file\n"); next; }

      if ( ! -f "$target/$file" || -M "$target/$file" > -M "$origin/$file" ) {
      # if ( ! -f "$target/$f" || -M "$target/$f" > -M "$file" ) {
        print("   COPYING: $origin_dir/$file\n");
        # system("cp '$origin/$file' '$target'");
        # print("   COPYING: $file\n");
        # HAVE TO USE "system()" TO DO COPYING?
      }
      
      if ( "$DEBUG" ) {
        print("      FILE: $file\n");
        if ( -f "$target/$file" ) {
          print("            Days since origin modification: " . int( -M "$origin/$file" ) . "\n");
          print("            Days since target modification: " . int( -M "$target/$file" ) . "\n");
        }
      }
    }
    $file_names = "";
    # print("\n");
  }
  # print(OUTPUT_FILE);
}
close(DATA_FILE);
# close(OUTPUT_FILE);

print("\nELAPSED TIME: " . ( time() - $start_time ) . " seconds\n\n");

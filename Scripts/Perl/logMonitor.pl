#!c:/Perl/bin/perl.exe
#
# logMonitor.pl: Monitor the specified log for the specified error.

# Parameterize script to monitor other process logs
# Note that contentfilter.INFO links to the latest content filter log
# Write this in Python!

$debugLevel = 1;
$maxLoops   = 0;	# Set to 0 or less for infinite loop
$sleepVal   = 30;
$process    = "contentfilter";

for ( $i = 1; 1; ) {

  if ( ! -e $process.INFO ) { sleep($sleepVal); }
  chomp($result = `grep 'Signal ' $process.INFO`);
  if ( "$result" ) {

    $logFile = sprintf("snapshot%04d.log", $i);
    printf("LOGFILE: %s\n", $logFile);
    printf("  ERROR: %s\n", $result);

    open(FILEOUT, "> $logFile");

    # GET MOST RECENT CONTENT FILTER LOG (LINKED TO BY CONTENTFILTER.INFO):
    # CAN I DO "LS -L CONTENTFILTER.INFO" AND FOLLOW LINK?
    # SOME WAY TO FORCE ARRAY CONTEXT HERE?
    @errFiles = <$process.*.INFO.*>;
    printf(FILEOUT "\nGOT ERROR FROM LOG FILE:\n%s\n", pop(@errFiles));
    printf(FILEOUT "\nSTACK TRACE:\n============\n");
    # MORE CONCISE WAY TO PIPE FROM STDIN HERE? DO WHOLE THING IN UNIX?
    open(STDIN, "tail -50 $process.INFO|");
    while (<>) { print(FILEOUT); }
    printf(FILEOUT "\nFILES WITH ERRORS:\n==================\n");
    open(STDIN, "ls -lrt `grep -l 'Signal ' *.INFO.*`|");
    while (<>) { print(FILEOUT); }
    
    close(FILEOUT);
    sleep($sleepVal);
    if ( $i eq $maxLoops ) { last; } else { $i++; }
  }
}


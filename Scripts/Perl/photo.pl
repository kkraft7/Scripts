#!c:/Perl/bin/perl.exe
#
# photo.pl: Create a an HTML page of family photo JPEG files
#
# NOTES (1) The directory format is expected to be the following:
#           $BASE_DIR/<type>/<date>/<file>, where the path elements are:
#             $BASE_DIR    The main directory containing all sub-directories
#             <type>       A directory containing JPEG files of a given type
#             <date>       ...
#	  (2) The thumbnail files have the format "*_tm.jpg"
#       (3) I am unable to glob paths containing directory names with spaces
#       (4) Chdir() seems to work now! (which is how I get around problem 3)

# REDO SCRIPT TO USE @CATEGORY VARIABLE!
# ADD "USE STRICT" AND USE "MY" FOR VARIABLE DECLARATIONS

chop($C = `basename "$0"`);

# $SUFFIX         = "_tst";
$BASE_DIR       = "c:/Documents and Settings/Kevin/My Documents/Pictures";
# SELECT THUMBNAILS FOR EACH MAIN DIRECTORY!
@CATEGORY       = ("Andrew", "Grace", "Family");
$FILE_NAME      = "index${SUFFIX}";
$MAX_HORZ_CELLS = 9;		# Number of cells across page
$MAX_VERT_CELLS = 5;		# Number of cells down page
$MAX_CELLS      = $MAX_HORZ_CELLS * $MAX_VERT_CELLS;
$CELL_WIDTH     = 100;
@MONTH_NAMES    = ("JANUARY", "FEBRUARY", "MARCH"    , "APRIL"  , "MAY"     , "JUNE",
                   "JULY"   , "AUGUST"  , "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");

# (1) SAVE THE PREVIOUS VERSION OF $FILE_NAME BEFORE OVERWRITING IT: 
# rename("${FILE_NAME}.html", "${FILE_NAME}_bk.html");
open(INDEX_FILE, ">${BASE_DIR}/${FILE_NAME}.html") ||
  die ("Failed to open file '${BASE_DIR}/${FILE_NAME}.html'");

print(INDEX_FILE "<HTML>\n<BODY>\n<BASE HREF='file:///${BASE_DIR}/'>\n");
chdir("${BASE_DIR}");

# (2) LOOP OVER EACH DIRECTORY TYPE:
foreach my $main_dir ( @CATEGORY ) {
# while ($main_dir = <*>) {

  if ( ! -d "$main_dir" || "$main_dir" eq "New" ) { next; }
  $sub_file = "${main_dir}${SUFFIX}.html";
  print(INDEX_FILE "  <A HREF='${sub_file}'>\U${main_dir}\E</A></DL>\n");
  
  open(SUB_FILE, ">${BASE_DIR}/${sub_file}");
  print(SUB_FILE "<HTML>\n<BODY BGCOLOR='#00FF88'>\n<BASE HREF='file:///${BASE_DIR}/${main_dir}/'>\n\n");

  chdir("${main_dir}");
  print("CURRENT DIRECTORY: ${main_dir}\n");
  
  # (3) LOOP OVER EACH SUB-DIRECTORY:
  while ($sub_dir = <*>) {
  
    if ( ! -d "$sub_dir" || "$sub_dir" !~ /\d{8}/ ) { next; }
    print("SUB_DIR = $sub_dir\n");

    $MON = $MONTH_NAMES[substr($sub_dir, 4, 2) - 1];
    $DAY = ( substr($sub_dir, 6, 2) == 0 ) ? "" : substr($sub_dir, 6, 2) . " ";
    $date = "$MON $DAY" . substr($sub_dir, 0, 4);
    
    print(SUB_FILE "<H3>$date</H3>\n<TABLE border>\n  <TR>\n");
  
    # (4) LOOP OVER EACH THUMBNAIL FILE IN THE DIRECTORY:
    for ($i=1; $thumb_file = <${sub_dir}/*_tm.jpg>; $i++) {

    # PUT UNDER A DEBUG SWITCH:
    # print("Section = ${main_dir}/${sub_dir}; i = $i; " .
    #      "i % ( MAX_HORZ_CELLS + 1 ) = " . ($i % ( $MAX_HORZ_CELLS + 1 )) . "\n");

      $file_name = $thumb_file;
      $file_name =~ s/_tm//; 
      # Print an error if $file_name doesn't exist!

      if ( $i % ( $MAX_HORZ_CELLS + 1 ) == 0) { print(SUB_FILE "  </TR>\n  <TR>\n"); }
      print(SUB_FILE "    <TD align=center valign=middle width=$CELL_WIDTH>\n");
      print(SUB_FILE "      <A HREF='$file_name'><IMG SRC='$thumb_file'></A>\n");
      print(SUB_FILE "    </TD>\n");
    }
    print(SUB_FILE "  </TR>\n</TABLE>\n");
  }
  print(SUB_FILE "</BODY>\n");
  close(SUB_FILE);
  chdir("../");
}

print(INDEX_FILE "</BODY>\n");
close(INDEX_FILE);

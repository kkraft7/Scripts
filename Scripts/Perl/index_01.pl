#!c:/MKS/mksnt/perl.exe
#
# index.pl: Create a visual HTML page index to a given set of JPEG files
#
# NOTES (1) The directory format is expected to be the following:
#           $BASE_DIR/<type>/thumbs, where the path elements are defined as:
#
#             $BASE_DIR    The main directory containing all sub-directories
#             <type>       A directory containing JPEG files of a given type
#             thumbs       A directory containing thumbnail versions of JPEGs
#
#	(2) The thumbnail files have the format "*_tm.jpg"
#       (3) Chdir() doesn't seem to work!
#
# TO DO (1) Try drawing thicker borders around the cells
#	(2) Divide large pages into sets of sub-pages

chop($C = `basename "$0"`);

$BASE_DIR      = "e:/Main";
$FILE_NAME     = "master";
$CELL_WIDTH    = 100;
$SCREEN_WIDTH  = 900;
$MAX_CELLS     = ${SCREEN_WIDTH}/${CELL_WIDTH};
%DIR_TYPE_LIST = (A, "Category A",
                  B, "Category B",
                  C, "Category C",
                  D, "Category D",
                  E, "Category E",
                  F, "Category F",
                  G, "Category G");

# (1) Save the previous version of $FILE_NAME before overwriting it! 
rename("${FILE_NAME}.html", "${FILE_NAME}_bk.html");
open(INDEX_FILE, ">${BASE_DIR}/${FILE_NAME}.html") ||
  die ("Failed to open file '${BASE_DIR}/${FILE_NAME}.html'");

print(INDEX_FILE "<HTML>\n<BODY>\n<BASE HREF='file:///${BASE_DIR}/'>\n");

# (2) Loop over each directory type:
foreach $dir_type (sort(keys(%DIR_TYPE_LIST))) {

  $sub_file = "index${dir_type}.html";
  print(INDEX_FILE "  <A HREF='${sub_file}'>" .
    "${dir_type}: $DIR_TYPE_LIST{$dir_type}</A></DL>\n");

  open(SUB_FILE, ">${BASE_DIR}/index${dir_type}.html");
  print(SUB_FILE "<HTML>\n<BODY>\n<BASE HREF='file:///${BASE_DIR}/'>\n<TABLE>\n");

  # (3) Loop over each thumbnail file in the directory:
  for ($i=1; $thumb_file = <${BASE_DIR}/${dir_type}*/thumbs/*_tm.jpg>; $i++) {

#   print("Section = ${dir_type}; i = $i; MAX_CELLS = $MAX_CELLS; " .
#        "i % MAX_CELLS = " . $i % $MAX_CELLS . "\n");

    @path = split(/\//, $thumb_file);
    # (4) Specify the path for the thumb files:
    $file_spec = join("/", ($path[2], $path[3], $path[4]));
    # (5) Specify the path for the target files (and remove the "_tm"):
    $file_name = join("/", ($path[2], $path[4]));
    $file_name =~ s/_tm//; 

    if ($i % $MAX_CELLS == 1) { print(SUB_FILE "  <TR>\n"); }
    print(SUB_FILE "    <TD align=center valign=middle width=$CELL_WIDTH>\n");
    print(SUB_FILE "      <A HREF='$file_name'><IMG SRC='$file_spec'></A>\n");
    print(SUB_FILE "    </TD>\n");
    if ($i % $MAX_CELLS == 0) { print(SUB_FILE "  </TR>\n"); }
  }

  if ($i % $MAX_CELLS != 0) { print(SUB_FILE "  </TR>\n"); }
  print(SUB_FILE "</TABLE>\n</BODY>\n");
  close(SUB_FILE);
}

print(INDEX_FILE "</BODY>\n");
close(INDEX_FILE);


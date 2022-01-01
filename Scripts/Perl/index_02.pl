#!c:/MKS/mksnt/perl.exe
#
# index.pl: Create a visual HTML page index of all my thumb files
#
# NOTES (1) Be sure and save a backup copy of the HTML file
#	(2) Chdir() didn't seem to work!
#
# TO DO (1) Experiment with different borders around the cells?
#	(2) Put index pages in a frame with navigational links to sub-pages
#	(3) Add parameters for MAX_HORZ_CELLS and MAX_VERT_CELLS?
#	(4) Verify that file (and thumb file?) exist or print warning?
#       (5) FLAG ERROR IF $file_name OR $file_spec DON'T EXIST?
#       (6) Will get an empty page if the number of picures is an exact
#           multiple of $MAX_HORZ_CELLS * $MAX_VERT_CELLS

chop($C = `basename "$0"`);

$BASE_DIR       = "f:/Main";
$FILE_NAME      = "master";
$MAX_HORZ_CELLS = 9;		# Number of cells across page
$MAX_VERT_CELLS = 5;		# Number of cells down page
$MAX_CELLS      = $MAX_HORZ_CELLS * $MAX_VERT_CELLS;
$CELL_WIDTH     = 100;		# Cell width in pixels
%DIR_TYPE_LIST = (A, "Category A",
                  B, "Category B",
                  C, "Category C",
                  D, "Category D",
                  E, "Category E",
                  F, "Category F",
                  G, "Category G");

# Don't need to save the previous version unless also saving the sub-files! 
# rename("${FILE_NAME}.html", "${FILE_NAME}_bk.html");
open(INDEX_FILE, ">${BASE_DIR}/${FILE_NAME}.html") ||
  die ("Failed to open file '${BASE_DIR}/${FILE_NAME}.html'");

print(INDEX_FILE "<HTML>\n<BODY>\n<BASE HREF = 'file:///${BASE_DIR}/'>\n  <TABLE>\n");
print("MAX_CELLS = ${MAX_CELLS}\n");

# Loop over each directory type:
foreach $dir_type (sort(keys(%DIR_TYPE_LIST))) {

  $i = 0;
  @thumb_list = <${BASE_DIR}/${dir_type}*/thumbs/*_tm.jpg>;
  $num_thumbs = $#{thumb_list} + 1;
  $num_pages  = int($num_thumbs / $MAX_CELLS) + 1;
  print("\nNumber of files in directory ${dir_type}: ${num_thumbs}\n");
  print("Number of pages in directory ${dir_type}: ${num_pages}\n");
  
  # Print section header indicating type
  print(INDEX_FILE "    <TR>\n      <TD>\n");
  print(INDEX_FILE "        (${dir_type}) $DIR_TYPE_LIST{$dir_type}  \n");
  print(INDEX_FILE "      </TD>\n      <TD>\n");
  
  for ($page_num = 1; $page_num <= $num_pages; $page_num++) {
    
    $sub_file = "master/index${dir_type}${page_num}.html";
    # Print reference to $sub_file in INDEX_FILE (and HTML new-line if last page):
    print(INDEX_FILE "        <A HREF='${sub_file}'>${dir_type}${page_num}</A>" .
      ( ($page_num == $num_pages)? "\n      </TD>\n    </TR>\n" : "\n") );
    printf("OPENING PAGE '%s' (PAGE %d)\n", ${sub_file}, $page_num);
    # Open $sub_file and write header:
    open(SUB_FILE, ">${BASE_DIR}/${sub_file}");
    print(SUB_FILE "<HTML>\n<BODY>\n<BASE HREF = 'file:///${BASE_DIR}/'>\n" .
      "  <TABLE border>\n");
    
    for ($row = 0; $row < $MAX_VERT_CELLS && $i < $num_thumbs; $row++) {
    
      printf("ROW = %d; PRINTING HTML ROW TAG: <TR>\n", $row + 1);
      print(SUB_FILE "    <TR>\n");
      
      for ($col = 0; $col < $MAX_HORZ_CELLS && $i < $num_thumbs; $col++, $i++) {
      
        @path = split(/\//, $thumb_list[$i]);
        # @path = (e: main $dir_type thumbs $file_name)
	# Specify the path for the thumb files:
	$file_spec = join("/", ($path[2], $path[3], $path[4]));
	# Specify the path for the target files (and remove the "_tm"):
	$file_name = join("/", ($path[2], $path[4]));
	$file_name =~ s/_tm//;
	
	# print("path[2] = $path[2]; path[3] = $path[3]; path[4] = $path[4]\n");
        printf("COL = %d; printing HTML table cell for picture %s\n",
          $col + 1, $file_name);
        
        print(SUB_FILE "      <TD align=center valign=middle width=$CELL_WIDTH>\n");
	print(SUB_FILE "        <A HREF='$file_name'><IMG SRC='$file_spec'></A>\n");
	print(SUB_FILE "      </TD>\n");

      }
      print(SUB_FILE "    </TR>\n");
    }
    
    print(SUB_FILE "    <TR>\n      <TD align=center colspan=$MAX_HORZ_CELLS>\n");
    print(SUB_FILE "        <A HREF='${FILE_NAME}.html'>INDEX</A>\n");
    $href = ($page_num > 1) ? "master/index${dir_type}".($page_num - 1).".html" : "${FILE_NAME}.html";
    print(SUB_FILE "        <A HREF='${href}'>&lt;&lt;PREV</A>\n");

    for ($p = 1; $p <= $num_pages; $p++) {
      if ($p == $page_num) {
        print(SUB_FILE "        PAGE $p\n");
      }
      else {
        print(SUB_FILE "        <A HREF='master/index${dir_type}${p}.html'>PAGE $p</A>\n");
      }
    }
    
    $href = ($page_num < $num_pages) ? "master/index${dir_type}".($page_num + 1).".html" : "${FILE_NAME}.html";
    print(SUB_FILE "        <A HREF='${href}'>NEXT&gt;&gt;</A>\n");

    print(SUB_FILE "      </TD>\n    </TR>\n  </TABLE>\n</BODY>\n");
    close(SUB_FILE);
  }
}

print(INDEX_FILE "  </TABLE>\n</BODY>\n");
close(INDEX_FILE);

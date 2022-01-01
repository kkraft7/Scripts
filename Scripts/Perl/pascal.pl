#!c:/Perl/bin/perl.exe -w
#
# pascal: write out the first N rows of Pascal's triangle (default 10).
#         This script originated at Oracle.
#
# Should describe properties of Pascal's triangle.

sub numeric { defined($_[0]) && $_[0] =~ /^(\d+).*/ && $1 || 0; }

$rows  = numeric($ARGV[0]) || 10;
$width = $rows < 6 ? 1 : $rows < 10 ? 2 : $rows < 14 ? 3 : $rows < 17 ? 4 : 5;

print("\nCalculating the first $rows row(s) of Pascal's triangle:\n\n");

for ($i = 0; $i < $rows; $i++) {

  for ($j = 0; $j < $rows; $j++) {

    $a[$i][$j] = ($j == 0)? 1 : ($i == 0)? 0 : $a[$i-1][$j-1] + $a[$i-1][$j];
    printf("%${width}d ", $a[$i][$j]);
  }

  print("\n");
}

# is it possible to figure out max(pascal(N))?

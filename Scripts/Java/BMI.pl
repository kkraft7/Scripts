#!c:/Perl/bin/perl.exe
#
# BMI.pl: this script calulates Body Mass Index (BMI) based on an input of
#         weight in pounds (argument #1) and height in inches (argument #2).
#
# NOTE: Must set environment variable "HASHBANG=1" in order for the "#!"
#       construct to force a Perl shell to run inside the script.


chop($C = `basename "$0"`);

$BMI_CONST=704.5;
$BMI_IDEAL=25;

if (($ARGV[0] =~ /\d+/) && ($ARGV[1] =~ /\d+/)) {
  $weight = $ARGV[0];
  $height = $ARGV[1]; }
else {
  die("\nUSAGE: $C <weight_pounds> <height_inches>\n\n");
}

$BMI    = ${weight} * $BMI_CONST / ${height}**2;
$RATING = ( $BMI < 25 ) ? "GOOD" : ( $BMI < 30 ) ? "OVERWEIGHT" : "OBESE";

printf("\n");
printf("==================================================\n");
printf("If you weigh %.2f pounds & are %.2f inches tall\n", $weight, $height);
printf("your body mass index (BMI) is:\n");
printf("\n");
printf("\t\t%.2f (%s)\n", $BMI, $RATING);
printf("\n");
printf("A BMI of 25 or greater is considered overweight.\n");
printf("A BMI of 30 or greater is considered obese.\n");
printf("==================================================\n");
printf("\n");


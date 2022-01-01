#
# BMI.ksh: this script calulates Boby Mass Index (BMI) based on an input of
#          weight in pounds (argument #1) and height in inches (argument #2).
#
# May want to check that both input arguments are numeric.


[ "$1" ] || { echo "$0: 1st argument must be weight in pounds"; exit 1; }
[ "$2" ] || { echo "$0: 2nd argument must be height in inches"; exit 1; }

echo ""
echo "The Body Mass Index (BMI) for"
echo "a person weighing $1 pounds"
echo "who is $2 inches tall is \c"

bc << INPUT
  scale=2
  ${1} * 704.5 / ${2}^2
INPUT

echo "A desirable BMI is under 25"
echo ""


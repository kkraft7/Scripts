#!c:/Python2.4.1/python.exe
#
# div_prob.py: This script implements division without using the
#   division operator. Taken from Google interview questions.

import sys

debugLevel = 1
exitScript = 0

try:
    number = int(sys.argv[1])
except (IndexError, ValueError, TypeError):
    print ("\n%s: 1ST ARG MUST BE THE NUMBER TO BE DIVIDED\n"
           % (sys.argv[0]))
    sys.exit(1)

try:
    divisor = int(sys.argv[2])
except (IndexError, ValueError, TypeError):
    print ("\n%s: 2ND ARG MUST BE THE NUMBER TO DIVIDE BY\n"
           % (sys.argv[0]))
    sys.exit(1)

if divisor == 0:
    print '\nCANNOT DIVIDE BY 0. PLEASE SPECIFY A VALID DIVISOR\n'
    sys.exit(1)

remainder = number % divisor
number -= remainder

# FIND MAGNITUDE OF NUMBER IN POWERS OF 10
power = 0
while 10**(power + 1) <= number:
    power += 1

if debugLevel > 0:
    print 'Number has magnitude 10^%d (%d digits)' % (power, power + 1)

answer = 0
sum = 0

for p in range(power, -1, -1):
    digit = 0
    while sum + (digit + 1) * divisor * 10**p <= number:
        digit += 1
    if debugLevel > 0:
        print 'Digit for the %ds place is %d' % (10**p, digit)
    sum += digit * divisor * 10**p
    answer += digit * 10**p

print 'QUOTIENT : ' + str(answer)
print 'REMAINDER: ' + str(remainder)


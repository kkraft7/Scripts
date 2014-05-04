#!c:/Python2.4.1/python.exe
# 
# reverse_string.py: Reverse an input string

import sys

try:
    inputString = sys.argv[1]
except (IndexError):
    print ("\n%s: 1ST ARG MUST BE A STRING TO REVERSE\n" % (sys.argv[0]))
    sys.exit(1)

try:
    debugLevel = int(sys.argv[2])
except (IndexError, ValueError, TypeError):
    debugLevel = 0

print '\nORIGINAL STRING IS: "%s"' % (inputString)

if len(inputString) < 2:
  print 'REVERSAL IS A NO-OP FOR THIS SIZE STRING\n'
  sys.exit(0)

stringList = list(inputString)

if debugLevel > 0: print 'STRING LIST IS: ' + str(stringList)

# Integer division gives the correct result here:
for i in range(len(stringList)/2):
  if debugLevel > 1: print 'i = ' + str(i)
  temp = stringList[i]
  stringList[i] = stringList[len(stringList) - 1 - i]
  stringList[len(stringList) - 1 - i] = temp

print 'REVERSED STRING IS: "%s"\n' % (''.join(stringList))
  

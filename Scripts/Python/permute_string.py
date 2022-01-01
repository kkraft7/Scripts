#!/usr/bin/python
# c:/Python2.4.1/python.exe
# 
# permute_string.pl: Print out all the permutations of an input string

import sys

try:
    inputString = sys.argv[1]
except (IndexError):
    print ("\n%s: 1ST ARG MUST BE A STRING TO PERMUTE\n" % (sys.argv[0]))
    sys.exit(1)

try:
    debugLevel = int(sys.argv[2])
except (IndexError, ValueError, TypeError):
    debugLevel = 0

print '\nORIGINAL STRING IS: "%s"' % (inputString)

if len(inputString) < 2:
  print 'PERMUTATION IS A NO-OP FOR THIS SIZE STRING\n'
  sys.exit(0)

stringList = list(inputString)

def swap(inputList, i, j):
  temp = inputList[i]
  inputList[i] = inputList[j]
  inputList[j] = temp

def permute(prefixStr, inputList):
  if len(inputList) > 1:
    for i in range(len(inputList)):
      newList = list(inputList)
      if i > 0:
        swap(newList, 0, i)
      newPrefix = prefixStr + newList.pop(0)
      permute(newPrefix, newList)
  else:
    print prefixStr + inputList[0]

permute('', stringList)


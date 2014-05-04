#!/usr/bin/python2.2
#
# phone_words.pl: Given a string of digits, compute all possible words
#   based on the mapping between digits and letters on a phone keypad.
#
# NOTES:
# (01) I used "aspell -l" to implement a dictionary in Linux
# (02) Treating input digits as chars since for use as dict keys
# (03) Comment on run-time of this algorithm

import os, sys

debugLevel = 2
spellCheck = 1
inputList  = (len(sys.argv) == 2 and sys.argv[1].split() or sys.argv[1:])
digitList  = []
phoneChars = { '2': [ 'a', 'b', 'c' ], '3': [ 'd', 'e', 'f' ],
               '4': [ 'g', 'h', 'i' ], '5': [ 'j', 'k', 'l' ],
               '6': [ 'm', 'n', 'o' ], '7': [ 'p', 'q', 'r', 's' ],
               '8': [ 't', 'u', 'v' ], '9': [ 'w', 'x', 'y', 'z' ] }

def usage():
  print '\nUSAGE: %s <list-of-digits>\n' % (sys.argv[0])
  print 'THE EXPECTED INPUT IS A LIST OF INTEGERS\n'
  sys.exit(0)

def printPhoneChars(digits):
  "Print phone keypad characters based on digits (used for debugging.)"

  print 'CONVERTING DIGITS TO LETTERS ON A PHONE KEYPAD:'
  for ch in digits:
    print '    %s: %s' % (ch, phoneChars[ch])
  print

def checkWord(word):
  # Should probably go in a try/except block (expected exceptions?)
  # Per Huyen this is superior to attempting to leave the pipe open
  # aspell returns an empty string if the word is spelled correctly
  if not spellCheck:
    print 'ERROR: CANNOT CHECK WORD - SPELL CHECKING IS TURNED OFF'
    return 0

  spellResults = os.popen('echo %s | aspell -l' % (word))
  result = spellResults.readlines()
  spellResults.close()
  return (not result and 1 or 0)

if debugLevel > 1: print '\nTHE INPUT LIST IS: ' + str(inputList)

if len(inputList) == 0: usage()

# Filter out invalid phone keypad entries
for ch in inputList:
  if not phoneChars.has_key(ch):
    print "WARNING: IGNORING INVALID PHONE KEYPAD ENTRY: %s" % (ch)
    continue
  digitList.append(ch)

print ('\nPRINTING WORDS FROM A PHONE KEYPAD BASED ON DIGITS:\n    %s\n'
       % (str(digitList)))

if debugLevel > 1: printPhoneChars(digitList)

def phoneWords(prefixStr, digits):
  if len(digits) > 0:
    for c in phoneChars[digits.pop(0)]:
      newList   = list(digits)
      newPrefix = prefixStr + c
      phoneWords(newPrefix, newList)
  else:
    if not spellCheck or checkWord(prefixStr):
      print prefixStr

phoneWords('', digitList)

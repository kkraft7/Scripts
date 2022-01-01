#!c:/Python2.4.1/python.exe
#
# reverse_words.py: Reverse the words in an input string

import sys

debugLevel = 0

inputString = ''
for i in range(1, len(sys.argv)):
  if inputString != '':
    inputString += ' '
  inputString += sys.argv[i]

print '\nORIGINAL STRING IS: "%s"' % (inputString)

if len(inputString.split()) < 2:
  print 'WORD REVERSAL IS A NO-OP FOR THIS STRING\n'
  sys.exit(0)

newString = []
i = 0

for ch in inputString:
  if ch == ' ': i = 0
  newString.insert(i, ch)
  if ch != ' ': i += 1

print 'REVERSED STRING #0: "%s"' % (''.join(newString))

newString = []
i = 0

for ch in inputString:
  if ch == ' ':
    newString.insert(0, ch)
    i = 0
  else:
    newString.insert(i, ch)
    i += 1

print 'REVERSED STRING #1: "%s"' % (''.join(newString))

newString = ''
newWord   = ''

for ch in inputString:
  if ch != ' ':
    newWord += ch
  else:
    newString = ' ' + newWord + newString
    newWord = ''

newString = newWord + newString

print 'REVERSED STRING #2: "%s"\n' % (newString)


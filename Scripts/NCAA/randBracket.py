#!/usr/bin/python
# c/Python2.4.1/python.exe
#
# randBracket.py: generate randomized NCAA Basketball Tournament picks
#   based probability (as derived from seeding.)

from random import random
import math, sys
# Had to comment out my old scriptOptions package
# import math, sys, scriptOptions

# Use standard try/except with "debugLevel = int(sys.argv[1])"?
# Track brackets with probability and single-seed methods
# scriptOptions.cmdLineArgParser.add_argument(0, 'debugLevel', 'int', 0,
#     help='Set the level for printing debug information')

# cmdLineArgs = scriptOptions.cmdLineArgParser.parse_args(sys.argv[1:])

CONTINUE_PROMPT    = 'PRESS ENTER TO CONTINUE -->'
# PUT THESE IN THE GAME CLASS?
TEAMS_PER_REGION   = 16
NUMBER_OF_REGIONS  = 4

initialBracket = [ 2, 15, 7, 10, 3, 14, 6, 11, 4, 13, 5, 12, 8, 9, 1, 16 ]
bracketList = initialBracket * 4
interactive = False  ## Pass this on command line?!
methodNum   = 1

def convertRegion(regionNum):

    return (regionNum < 5) and 'REGIONAL %d' % (regionNum) or 'FINAL FOUR'

def regionHeader(regionNum):

    borderChar = '#'
    regionName = convertRegion(regionNum)
    regionLine = (borderChar + ' ' + regionName + ' ' + borderChar)
    borderLine = ((borderChar * len(regionLine)) + '\n')
    return ('\n' + borderLine + regionLine + '\n' + borderLine)

class game:

    PROBABILITY_FACTOR = 1/30.5
    
    def __init__(self, seed1, seed2, roundNum=None, regionNum=None):

        self.diff        = abs(seed1 - seed2)
        self.region      = (regionNum and convertRegion(regionNum) or None)
        self.outcome     = 100 * random()
        self.roundNum    = roundNum
        self.probability = 100 * (game.PROBABILITY_FACTOR * self.diff + 0.5)

        if ((seed1 < seed2 and self.outcome < self.probability) or
            (seed1 > seed2 and self.outcome > self.probability)):
            (self.winner, self.loser) = (seed1, seed2)
        else:
            (self.winner, self.loser) = (seed2, seed1)

    def __repr__(self):

        returnStr = ''

        if self.region:
            returnStr += (self.region + ': ')

        if self.roundNum:
            returnStr += 'ROUND %d: ' % (self.roundNum)
        
        # HOW TO SET DEBUG-LEVEL? STATIC CLASS MEMBER?
        returnStr += 'SEED %02d BEAT SEED %02d' % (self.winner, self.loser)
       
#       if cmdLineArgs.debugLevel > 2:
#           returnStr += ('\n  DIFF = %02d\n  PROB = %05.2f\n  RAND = %05.2f'
#               % (self.diff, self.probability, self.outcome))

        return returnStr

# Added this because raw_input() wasn't working on MINGW commmand line:
def wait_prompt(prompt=CONTINUE_PROMPT):
    print prompt,
    sys.stdout.flush()
    sys.stdin.readline()

for regionNum in range(5):
    maxExp = (regionNum < 4) and 3 or 1
    index2 = 16*regionNum + 2**(maxExp + 1)
    bracketList.insert(16*regionNum, regionHeader(regionNum + 1))
    print bracketList[16*regionNum]
    for exp in range(maxExp, -1, -1):
        startNum = 16*regionNum + 2**(maxExp + 1) - 2**(exp + 1) + 1
        for gameID in range(startNum, startNum + 2**exp):
            result = game(bracketList.pop(gameID),
                          bracketList.pop(gameID), 7 - exp - maxExp)
            bracketList.insert(gameID, result)
            if exp > 0: bracketList.insert(index2, result.winner)
            print result
    if regionNum < 4:
        bracketList.append(bracketList[index2 - 1].winner)
        if interactive: wait_prompt()


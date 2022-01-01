#
# randBracket.py: generate randomized NCAA Basketball Tournament picks
#   based probability (as derived from seeding.)

from random import random
import math, sys, scriptOptions

# Track brackets with probability and single-seed methods
scriptOptions.cmdLineArgParser.add_argument(0, 'method', 'int', 1,
    help='Choose the method for generating a random bracket')
scriptOptions.cmdLineArgParser.add_argument(1, 'debugLevel', 'int', 0,
    help='Set the level for printing debug information')

cmdLineArgs = scriptOptions.cmdLineArgParser.parse_args(sys.argv[1:])

CONTINUE_PROMPT    = 'PRESS ENTER TO CONTINUE -->'
# PUT THESE IN THE GAME CLASS?
TEAMS_PER_REGION   = 16
NUMBER_OF_REGIONS  = 4

initialBracket = [ 2, 15, 7, 10, 3, 14, 6, 11, 4, 13, 5, 12, 8, 9, 1, 16 ]
bracketList = initialBracket * 4
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
       
        if cmdLineArgs.debugLevel > 2:
            returnStr += ('\n  DIFF = %02d\n  PROB = %05.2f\n  RAND = %05.2f'
                % (self.diff, self.probability, self.outcome))

        return returnStr

# Added this because raw_input() wasn't working on MINGW commmand line:
def wait_prompt(prompt=CONTINUE_PROMPT):
    print prompt,
    sys.stdout.flush()
    sys.stdin.readline()

if cmdLineArgs.method == methodNum:
    print "\nCONSTRUCTING NCAA BRACKET USING METHOD #%d" % (methodNum)
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
            wait_prompt()

methodNum += 1

if cmdLineArgs.method == methodNum:
    print "\nCONSTRUCTING NCAA BRACKET USING METHOD #%d" % (methodNum)
    for regionNum in range(5):
        maxExp = (regionNum < 4) and 3 or 1
        index2 = 16*regionNum + 2**(maxExp + 1) - 1
        print regionHeader(regionNum + 1)
        for exp in range(maxExp, -1, -1):
            for i in range(2**exp):
# index = (maxExp- 1 )*(8 - 2**exp) + (3 - maxExp)*(2 - 2**exp) + 16*region + i
                index1 = 6*maxExp - 2 - 2*2**exp + 16*regionNum + i
                result = game(bracketList.pop(index1),
                              bracketList.pop(index1), 7 - exp - maxExp)
                bracketList.insert(index1, result)
                if exp > 0: bracketList.insert(index2, result.winner)
                print result
        if regionNum < 4:
            wait_prompt()
            bracketList.insert(index2, '')
    #       print bracketList[index2]
            bracketList.append(bracketList[index2 - 1].winner)

methodNum += 1

if cmdLineArgs.method == methodNum:
    print "\nCONSTRUCTING NCAA BRACKET USING METHOD #%d" % (methodNum)
    i = 0
    while i < len(bracketList) - 1:
        bracketList.insert(i, game(bracketList.pop(i), bracketList.pop(i)))
        bracketList.append(bracketList[i].winner)
        i += 1

    for region in range(5):
        print
        maxExp = (region < 4) and 3 or 1
        for ix in [ i + 64 + (2*maxExp - 6 + region - 2**maxExp)*2**exp
                for exp in range(maxExp, -1, -1) for i in range(2**exp) ]:
                    print bracketList[ix]
        if region < 4: wait_prompt()

methodNum += 1

if cmdLineArgs.method == methodNum:
    print "\nCONSTRUCTING NCAA BRACKET USING METHOD #%d" % (methodNum)
    i=0
    while i < len(bracketList) - 1:
        result = game(bracketList[i], bracketList[i + 1])
        bracketList.append(result.winner)
        i += 2

    for region in range(5):
        print
        maxExp = (region < 4) and 3 or 1
        for ix in [ i + 128 + (2*maxExp - 6 + region - 2**maxExp)*2**exp
                for exp in range(maxExp, -1, -1) for i in range(2**exp) ]:

            if bracketList[ix] == bracketList[2*(ix - 64)]:
                loser = bracketList[2*(ix - 64) + 1]
            else:
                loser = bracketList[2*(ix - 64)]

            if cmdLineArgs.debugLevel > 0:
                print 'SEED %02d BEAT SEED %02d' % (bracketList[ix], loser)

bracketList = []
final4List  = []

# THERE MAY BE A WAY TO CREATE BRACKET-LIST WORKING BACKWARD FROM 1 VS 2
def buildBracketList():
    tmpList = []
    for i in range(8):
        tmpList.extend([ i + 1, TEAMS_PER_REGION - i ])
    return tmpList

methodNum += 1

if cmdLineArgs.method == methodNum:
    print "\nCONSTRUCTING NCAA BRACKET USING METHOD #%d" % (methodNum)
    # I DON'T GET HOW THIS (METHOD #5) WAS SUPPOSED TO WORK!
    for region in range(5):
#       if method != 4: break
        if region > 0:
            final4List.append(bracketList[0])
        if region < 4:
            bracketList = initialBracket[:]
            regionName  = 'REGION #%d' % (region + 1)
            roundList   = range(4)
        else:
            bracketList = final4List[:]
            regionName  = 'FINAL 4'
    #       roundList   = [ 4, 5 ]
            roundList   = [ 2, 3 ]
    #       roundList   = range(1)
        print regionName
        # Combine both for-loops (value of loop variable  doesn't matter)?
        for round in roundList:
            print 'ROUND %d' % (round + 1)
            for i in range(2**(3 - round)):
                print '%02d' % (i)

    ##            result = game(bracketList.pop(0), bracketList.pop(0))
    ##            bracketList.append(result.winner)
    ##
    ##            if cmdLineArgs.debugLevel > 1: print result

methodNum += 1

if cmdLineArgs.method == methodNum:
    print "\nCONSTRUCTING NCAA BRACKET USING METHOD #%d\n" % (methodNum)
    bracketList = initialBracket[:]
    (region, round, const) = (1, 1, 5)
    while True:
        if len(bracketList) <= 1:
            print
            region += 1
            if len(final4List) < NUMBER_OF_REGIONS:
                if bracketList:
                    final4List.append(bracketList[0])
                    wait_prompt()
                    print
                if len(final4List) == NUMBER_OF_REGIONS:
                    (round, const) = (5, 7)
                    regionName = 'FINAL FOUR'
                    bracketList = final4List[:]
                else:
                    (round, const) = (1, 5)
                    regionName = 'REGION #%02d' % (region)
                    bracketList = initialBracket[:]
            else:
                break

        if const - math.log(len(bracketList), 2) == round + 1:
            round += 1
        
        result = game(bracketList.pop(0), bracketList.pop(0), round, region)
        bracketList.append(result.winner)

        if cmdLineArgs.debugLevel > 0: print result

methodNum += 1

if cmdLineArgs.method == methodNum:
    print "\nCONSTRUCTING NCAA BRACKET USING METHOD #%d\n" % (methodNum)
    bracketList = initialBracket[:]
    winnerList  = []
    (region, round) = (1, 1)
    while True:
        if not bracketList:
            if not winnerList: pass
                # FIRST REGION, FIRST ROUND
            elif len(winnerList) > 1:
                # LATER ROUND
                bracketList = winnerList
            elif len(final4List) < NUMBER_OF_REGIONS:
                # END OF REGION
                region += 1
                round = 0
                print
                final4List.append(winnerList[0])
                if len(final4List) == NUMBER_OF_REGIONS:
                    bracketList = final4List[:]
                else:
                    bracketList = initialBracket[:]
                wait_prompt()
                print
            else:
                break
            round += 1
            winnerList = []

        result = game(bracketList.pop(0), bracketList.pop(0), round, region)
        winnerList.append(result.winner)

        if cmdLineArgs.debugLevel > 0: print result

methodNum += 1

if cmdLineArgs.method == methodNum:
    print "\nCONSTRUCTING NCAA BRACKET USING METHOD #%d\n" % (methodNum)
    for region in range(NUMBER_OF_REGIONS):
        bracketList = initialBracket[:]
        round = 1
        tempList = []
        
        while bracketList:
            result = game(bracketList.pop(0), bracketList.pop(0),
                          round, region + 1)
            tempList.append(result.winner)

            if cmdLineArgs.debugLevel > 0: print result

            if not bracketList:
                if len(tempList) > 1:
                    bracketList = tempList
                elif len(final4List) < NUMBER_OF_REGIONS:
                    final4List.append(tempList[0])
                    wait_prompt()
                    print
                    if len(final4List) == NUMBER_OF_REGIONS:
                        bracketList = final4List[:]
                        region += 1
                round += 1
                tempList = []

if cmdLineArgs.method > methodNum:
    print "\nINVALID METHOD NUMBER: %d" % (cmdLineArgs.method)

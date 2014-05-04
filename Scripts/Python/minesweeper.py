#!c:/python2.4.1/python.exe
#
# minesweeper.py: Calculate number of clicks before mine explodes

import random, sys

debugLev = 0
fakeProb = True     ## 1st Minesweeper click never fails
numTries = 1000000  ## Number of Minesweeper repetitions 
gridSize = 16**2    ## Square grid containing mines
numMines = 40       ## Number of mines in the grid
probList = []       ## Cached list of mine-hit probabilities
meanList = []       ## List for calculating mean # of clicks
totalPrb = 1        ## Probability of mine-hit for each step

## Calculate ...
for i in range(gridSize - numMines + 1):
  probList.append(1.0 * numMines / (gridSize - i))
  totalPrb *= (1 - probList[i])

  if debugLev > 0:
    print "PROBABILITY %03d: %.5f" % (i + 1, probList[i])
    print "CUMMULATIVE %03d: %.5f" % (i + 1, totalPrb)

if fakeProb: probList[0] = 0.0

print "\nInitial probability of hitting a mine is %.5f" % (probList[0])
sys.stdout.flush()  ## Force output of all lines before for-loop

numClicks = 0
for i in range(numTries):
  click = 0
  while random.random() > probList[click]: click += 1
  numClicks += click
  for j in range(len(meanList), click + 1):
    meanList.append(0)
  meanList[click] += 1
# print "Hit mine on click #%d" % (click)

print ("Average number of clicks for %d tries: %.2f"
       % (numTries, 1.0 * numClicks / numTries))

if debugLev > 0:
  for i in range(len(meanList)):
    print "%02d CLICKS: %d" % (i, meanList[i])

median = 0
total  = meanList[0]
while total < numTries / 2:
  median += 1
  total  += meanList[median]

print "Median number of clicks for %d tries: %d" % (numTries, median)


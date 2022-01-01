#!c:/python2.4.1/python.exe
#
# sort.py: Practice coding different sorting algorithms.
#   1. 
#
# NOTES:
# 01) Some internet sources about sorting:
#     http://en.wikipedia.org/wiki/Sorting_algorithm

# Other algorithms to look at:
# 1. Binary tree sort 

# Look at big-O run times for the various sorting algorithms
import sys

#################
# Process input #
#################
try:
    method = int(sys.argv.pop(1))
except (IndexError, ValueError, TypeError):
    print 'The first argument must be the sort method ID'
    sys.exit(1)

numberList = []
for n in sys.argv:
    try:
        numberList.append(int(n))
    except (IndexError, ValueError, TypeError):
        print 'Ignoring non-numeric element %s\n' % (n)

print '\nThe original list is %s\n' % (numberList)

def swap(array, i1, i2):
    temp = array[i1]
    array[i1] = array[i2]
    array[i2] = temp

####################################
# Try different sorting algorithms #
####################################
if method == 1:

    print '\nUsing sorting method #1: Selection sort'
    for i in range(len(numberList)):
        minIndex = i
        for j in range(i + 1, len(numberList)):
            if numberList[j] < numberList[minIndex]:
                minIndex = j
        if minIndex != i:
            swap(numberList, i, minIndex)

elif method == 2:

    print '\nUsing sorting method #2: Bubble sort'
    # Keep iterating through list swapping elements until list is sorted
    swapped = None
    while swapped == None or swapped > 0:
        break

elif method == 3:

  print '\nUsing sorting method #3: Shell sort'

elif method == 4:

  print '\nUsing sorting method #4: Quick sort' # (?)

elif method == 5:

  print '\nUsing sorting method #5: Merge sort'

else:

  print '\nUnknown sorting method (%d). Valid values are 1-5.\n' % (method)
  sys.exit(0)

print '\nThe sorted list is %s\n' % (numberList)


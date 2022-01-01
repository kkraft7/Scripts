#!c:/python2.4.1/python.exe
#
# fibonacci.py: Calculate the Fibonacci sequence using 4 different methods:
#   1. The traditional recursive method
#   2. An iterative method starting at 0
#   3. An iterative method starting at 2
#   4. Memoization version using pre-sized array
#   5. Memoization version building array dynamically
#
# NOTES:
# 01) Some internet sources about the Fibonacci sequence:
#     http://en.wikipedia.org/wiki/Fibonacci_sequence
#     http://en.wikipedia.org/wiki/Memoization
# 02) The Fibonacci sequence is defined as:
#     f(n) = f(n-1) + f(n-2) [f(0) = 0; f(1) = 1]

# WHAT IS THE RUNNING TIME FOR COMPUTING THE FIBONACCI SEQUENCE?

import sys

#################
# Process input #
#################
try:
    number = int(sys.argv[1])
    if number < 0:
      raise ValueError
except (IndexError, ValueError, TypeError):
    print ("\n%s: 1ST ARG MUST BE A POSITIVE INTEGER\n"
           % (sys.argv[0]))
    sys.exit(1)

try:
    method = int(sys.argv[2])
except (IndexError, ValueError, TypeError):
    method = 1

try:
    debugLevel = int(sys.argv[3])
except (IndexError, ValueError, TypeError):
    debugLevel = 1

# Had to define method which is not part of the list API:
def has_index(array, index):
    try:
        array[index]
        return True
    except IndexError:
        return False

##################################
# Compute the Fibonacci sequence #
##################################
fibonacciList = []
fibonacciResult = None

if method == 1:

    print '\nUsing Fibonacci method #1: Basic recursion.'

    def fibonacci(num):
        return ((num > 1) and (fibonacci(num-1) + fibonacci(num-2)) or num)

    fibonacciResult = fibonacci(number)
    for n in range(number + 1):
        fibonacciList.append(fibonacci(n))

elif method == 2:

    print '\nUsing Fibonacci method #2: Iteration starting at 0'

    fib_2 = 0	# F(n - 2)
    fib_1 = 1	# F(n - 1)
    fibonacciList.append(0)

    for n in range(number):
        (fib_2, fib_1) = (fib_1, fib_1 + fib_2)
        fibonacciList.append(fib_2)

    fibonacciResult = fib_2

elif method == 3:

    print '\nUsing Fibonacci method #3: Iteration starting at 2'

    fibonacciList = [ 0, 1 ]
    fib_2   = 0	# F(n - 2)
    fib_sum = (number > 0 and 1 or 0)

    for n in range(2, number + 1):
        (fib_2, fib_sum) = (fib_sum, fib_sum + fib_2)
        fibonacciList.append(fib_sum)

    fibonacciResult = fib_sum

elif method == 4:

    print '\nUsing Fibonacci method #4: Memoization (pre-sized array)'

    fibonacciList = [ 0, 1 ]
    fibonacciList.extend([ None ] * (number - 1))

    def memoizedFib1(num):
        if fibonacciList[num] is not None:
            return fibonacciList[num]
        fibonacciList[num] = memoizedFib1(num - 1) + memoizedFib1(num - 2)
        return fibonacciList[num]

    fibonacciResult = memoizedFib1(number)

elif method == 5:

    print '\nUsing Fibonacci method #5: Memoization (dynamic array)'

    fibonacciList = [ 0, 1 ]

    def memoizedFib2(num):
#       if len(fibonacciList) < num + 1:
#       if not len(fibonacciList) > num:
        if not has_index(fibonacciList, num):
            fibonacciList.append(memoizedFib2(num - 1) + memoizedFib2(num - 2))
        return fibonacciList[num]

    fibonacciResult = memoizedFib2(number)

else:

    print '\nUnknown Fibonacci method: %d (valid values are 1-5)\n' % (method)
    sys.exit(0)

print '\nThe Fibonacci number for %d is %d\n' % (number, fibonacciResult)

print 'Fibonacci sequence for %d is:\n  ' % (number),
for n in fibonacciList:
    print '%d ' % (n),
print


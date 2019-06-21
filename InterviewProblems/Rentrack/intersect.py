#!c:/python2.4.1/python.ex
#
# intersect.py:
# 1. What does the code below do? What is its big-O performance?
# 2. Re-write the code. What is the big-O performance of your solution?
# Toy Problem #4 from Rentrack
#
# 1. This code returns the intersection of lists a and b.
#    It's big-O performance is O(n^2)

def who_knows(a, b):
   """ a and b are lists """
   c = []
   for i in a:
       if is_member(b, i):
           c.append(i)
   return uniqify(c)

def is_member(a, elem):
   for i in a:
       if i == elem:
           return True
   return False

def uniqify(array):
   b = {}
   for i in array:
       b[i] = 1
   return b.keys()

def intersect(a, b):
   """ a and b are lists """
   a.extend(b);
   c = {}
   d = []
   for i in a:
       if not c.has_key(i):
           c[i] = 0
       c[i] += 1
       if c[i] == 2:
           d.append(i)
   return d

l1 = [ 'a', 'b', 'c', 'd' ]
l2 = [ 'c', 'd', 'e', 'f' ]

print 'Result of who_knows() is:\n  '
for s in who_knows( l1, l2 ):
   print '%s ' % (s),
print

print 'Result of intersect() is:\n  '
for s in intersect( l1, l2 ):
   print '%s ' % (s),
print


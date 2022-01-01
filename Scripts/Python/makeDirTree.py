#!c:/python2.4.1/python.exe
#!/usr/bin/python
#
# makeDirTree.py: Create a directory tree of the specified depth
#   (number of levels) and degree (children per parent directory.)
#   The default degree is 2 (a binary tree.)

# Replace variables with underscores for consistency?
# Potential Parameters/Options:
# -f [FILE PREFIX - DEFAULT "file"]
# -r [DIR. PREFIX - DEFAULT "dir"]
# -s [START DIR.  - DEFAULT "." ]

import os, sys, math, scriptOptions

# maxLevel = (len(sys.argv) > 1 and sys.argv[1].isdigit() and
#   int(sys.argv[1])) or 10

scriptOptions.cmdLineOptParser.add_option('-r', '--dirPrefix',
    dest='dirPrefix', default='dir',
    help='specify the prefix for the directory name')
scriptOptions.cmdLineOptParser.add_option('-f', '--filePrefix',
    dest='filePrefix', default='file',
    help='specify the prefix for the file name')
scriptOptions.cmdLineArgParser.add_argument(0, 'maxLevel', 'int',
    help='specify the depth of the directory tree')
scriptOptions.cmdLineArgParser.add_argument(1, 'degree', 'int', 2,
    help='specify the number of subdirectories per directory')

(options, args) = scriptOptions.cmdLineOptParser.parse_args()
scriptOptions.cmdLineArgParser.parse_args(args)

# WHY CAN'T I USE MY COMMAND PARSER TO GET maxLevel AND degree?
try:
    maxLevel = int(args[0])
except (IndexError, ValueError):
    print "\n%s: 1ST ARG MUST BE INT (DEPTH OF TREE)" % (sys.argv[0])
    sys.exit(1)

# DEGREE: NUMBER OF CHILD NODES PER PARENT (DEFAULT 2)
try:
    degree = int(args[1])
except IndexError:
    degree = 2
except ValueError:
    print "\n%s: 2ND ARG MUST BE INT (CHILDREN/NODE)" % (sys.argv[0])
    sys.exit(1)

# dir_prefix  = 'dir'
# file_prefix = 'file'
dir_prefix  = 'd'
file_prefix = 'f'
# options.debugLevel = 1
# options.exitScript = 1
# THIS AVOIDS MOST TESTS OF EXITSCRIPT:
if options.exitScript and not options.debugLevel:
    options.debugLevel = 1

max_dir_id = max(degree**(maxLevel - 1), maxLevel)
num_fmt    = str(int(math.log10(max_dir_id) + 1))

if options.debugLevel:
    print 'MAX LEVEL  :', maxLevel
    print 'MAX CHILD  :', degree
    print 'MAX DIR ID :', max_dir_id
    print 'DIR PREFIX :', dir_prefix
    print 'FILE PREFIX:', file_prefix
    print 'DEBUG LEVEL:', options.debugLevel
    print 'EXIT SCRIPT:', options.exitScript
    if options.exitScript: print 'EXIT = TRUE: SKIPPING COMMANDS\n'

def nestedDirs(level=0, dirnum=0):

    # FORMAT LEVEL AND DIRNUM FOR PRETTY OUTPUT:
    l_fmt = eval("'%0' + num_fmt + 'd'") % (level  + 1)
    d_fmt = eval("'%0' + num_fmt + 'd'") % (dirnum + 1)
    
    dir_name  = dir_prefix  + l_fmt + '_' + d_fmt
    file_name = file_prefix + l_fmt + '_' + d_fmt + '.txt'
    unique_id = ('LEV' + str(maxLevel) + '_DEG' + str(degree) + '_' +
                 l_fmt + '_' + d_fmt)

    message = 'LEVEL %s DIRECTORY %s' % (l_fmt, d_fmt)
    if options.debugLevel > 1: message += (' ID ' + unique_id)
    if options.debugLevel:
        print '%s (%s/%s)' % (message, dir_name, file_name)
    
    if not options.exitScript:

        os.mkdir(dir_name)
        os.chdir(dir_name)

        fl = open(file_name, 'w')
        fl.write('FILE FOR ' + message + '\n')
        fl.close()

    if level < maxLevel - 1:
        for i in range(degree):
            nestedDirs(level + 1, (dirnum * degree) + i)

    if not options.exitScript: os.chdir(os.pardir)

# Call nestedDirs() if running file as a script:
if __name__ == '__main__': nestedDirs()


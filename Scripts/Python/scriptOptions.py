#
# scriptOptions.py: Standard command-line options for scripts.

import sys, optparse, argparse

cmdLineOptParser = optparse.OptionParser()
cmdLineArgParser = argparse.ArgumentParser()

cmdLineOptParser.add_option('-d', '--debugLevel', dest='debugLevel',
        type='int', default=0, help='specify the debug information level')
cmdLineOptParser.add_option('-x', '--exitScript', dest='exitScript',
        action='store_true', default=False,
        help='exit script without running command')

def error(script, message):
    print '\nERROR: %s: %s' % (script, message)
    sys.exit(1)

if __name__ == '__main__':
    (options, args) = cmdLineOptParser.parse_args(['-d 2', '-x', 'arg1'])
    print 'OPTIONS:', options
    print 'ARGS   :', args
    print 'debugLevel has been set to %d' % (options.debugLevel)
    print 'exitScript has been set to %s' % (options.exitScript)
    error('basicOptions.py', 'TESTING ERROR FUNCTION')

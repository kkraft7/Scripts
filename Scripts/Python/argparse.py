#
# argparse.py: Define and parse positional command line arguments.
#   This is intended to complement the standard Python optparse module.
#   I attempt to follow the same interface and naming conventions used
#   in optparse. I make 2 assumptions about the command line arguments:
#   1. All positional arguments come after all command line options
#   2. All optional arguments come after all required arguments
#   3. I allow for only optional arguments to be defined

# optparse.py source can be found under: C:\Python2.4.1\Lib
# optparse project page: http://sourceforge.net/projects/optik

import sys
debugLevel = 1

# * Add "string" type, since optparse supports it?
# * OptionParser() constructor takes a "usage" parameter:
#   http://www.python.org/doc/2.3/lib/optparse-generating-help.html
# * Add Pydoc strings for module, classes, and methods
# * Turn error flags into exception objects?
# Specify optional (dash-separated) range for number/integer?
# Specify custom type for input files (AP_INFILE)?
# Put this in python_info.txt:
# Functions may not have the same name as data fields in classes,
#   each member need a unique name, or you end up with the error:
#   'str' object is not callable

class genericObject: pass

class cmdLineArg:

    # No "value" field required -- can only come from command line
    def __init__(self, index, dest, argType=None, default=None, help=''):
        self.index    = index
        self.dest     = dest
        self.help     = help
        self.type     = argType
        self.default  = default

        if not self.type:
            if self.default:
                self.type = type(self.default).__name__
            else:
                self.type = 'str'

    def __repr__(self):
        return ('\nPOSITION: %d'
                '\nNAME    : %s'
                '\nTYPE    : %s'
                '\nHELP    : %s'
                '\nDEFAULT : %s' %
                (self.index + 1, self.dest, self.type, self.help,
                str(self.default)))

class ArgumentParser(list):

    def __init__(self, usage=None):
        self.maxRequiredArg = None
        self.argDefError    = False
        self.parseError     = False
        # DEFINE SOME KIND OF HEADER FOR HELP-STR?
        self.helpStr        = (usage and (usage + '\n') or '')

    def _checkArgDefs(self):

        if len(self) == 0:
            print "ERROR: No command line arguments defined by the parser"
            self.argDefError = True
        
        minOptionalArg = None
        for i in range(len(self)):
            if not self[i]:
                self.argDefError = True
                print ("ERROR: No command line argument defined for"
                       " position %d" % (i + 1))
            elif self[i].default is None:
                if self.maxRequiredArg is None or i > self.maxRequiredArg:
                    self.maxRequiredArg = i
                if minOptionalArg is not None and i > minOptionalArg:
                    self.argDefError = True
                    print ("ERROR: Required argument at position %d after\n"
                           "       optional argument at position %d"
                           % (i + 1, minOptionalArg + 1))
            elif minOptionalArg is None or i < minOptionalArg:
                minOptionalArg = i

    def _buildHelpStr(self):

        for arg in self:
            # NEED TO FORMAT ARG # (2 DIGITS)
            self.helpStr += ('ARG #%d: %s\n' % (arg.index + 1, arg.help))

    def add_argument(self, index, dest, argType=None, default=None, help=''):

        # Fill in any missing list indices:
        self.extend([None]*(index - len(self) + 1))
        
        if self[index]:
            self.argDefError = True
            print ("ERROR: Duplicate definition for command line "
                   "argument #%d" % (index + 1))
            return
        
        self[index] = cmdLineArg(index, dest, argType, default, help)

    def parse_args(self, cmdLineArgs):

        # Validate the command line argument definitions first:
        self._checkArgDefs()

        if (self.maxRequiredArg is not None and
            len(cmdLineArgs) < self.maxRequiredArg + 1):
            self.parseError = True
            for i in range(len(cmdLineArgs), self.maxRequiredArg + 1):
                # PRINT OUT NAME OF ARG AND HELP STRING?
                print ("ERROR: Missing required command line argument #%d"
                       % (i + 1))

        if len(cmdLineArgs) > len(self):
            self.parseError = True
            for i in range(len(self), len(cmdLineArgs)):
                print ("ERROR: Undefined command line argument at position"
                       " %d" % (i + 1))

        if self.argDefError or self.parseError:
            print "\nPLEASE FIX ABOVE ERRORS AND TRY AGAIN\n"
            # Do sys.exit(1) and run tests in try/except?
            self.argDefError = self.parseError = False
            return

        argObject = genericObject()
        for i in range(len(cmdLineArgs)):
            if type(cmdLineArgs[i]).__name__ == self[i].type:
                argObject.__dict__[self[i].dest] = cmdLineArgs[i]
            else:
                try:
                    # CAST COMMAND LINE ARG TO CORRECT TYPE:
                    argObject.__dict__[self[i].dest] = eval(self[i].type +
                        "(" + str(cmdLineArgs[i]) + ")")
                except:
                    self.parseError = True
                    print ("ERROR: Failed to convert value '%s' to type '%s'"
                           % (str(cmdLineArgs[i]), self[i].type))
                    if debugLevel > 1:
                        print 'EXCEPTION TYPE :', sys.exc_type
                        print 'EXCEPTION VALUE:', sys.exc_value

        for i in range(len(cmdLineArgs), len(self)):
#           print 'SETTING %s TO %s' % (self[i].dest, str(self[i].default))
            argObject.__dict__[self[i].dest] = self[i].default

        # NEED TO CHECK FOR PARSE ERROR HERE, TOO?
        self._buildHelpStr()

        return argObject

    def __repr__(self):
        stringRepr = ''
        for i in range(len(self)):
            if self[i]:
                stringRepr += ( str(self[i]) + '\n' )
            else:
                stringRepr += ("\nCOMMAND LINE ARGUMENT %d IS UNDEFINED\n" %
                               (i + 1))
        return stringRepr

if __name__ == '__main__':

    # RUN TEST SUITE:
    import argparseTest
    argparseTest.testMissingArgDefs()
    argparseTest.testDuplicateArgDefs()
    argparseTest.testOutOfOrderArgDefs1()
    argparseTest.testOutOfOrderArgDefs2()
    argparseTest.testOutOfOrderArgDefs3()
    argparseTest.testMultipleArgDefErrors()
    argparseTest.testMissingRequiredArgs()
    argparseTest.testTooManyCmdLineArgs()
    argparseTest.testInvalidArgTypes()
    argparseTest.testBackwardsArgDefs()
    argparseTest.testOnlyOptionalArgs()


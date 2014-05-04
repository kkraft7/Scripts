#
# testArgparse.py: Test suite for argparse.py.
#
import argparse
showHelp = False
prompt   = False
argparse.debugLevel = 1

# Print out test name (figure out how to print function name)
# * Test default values that evaluate to False (0, 0.0, False, None, "")
# * Test defining only optional command line arguments

def testMissingArgDefs():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg', help="1ST ARGUMENT")
    cmdLineArgParser.add_argument(1, '2ndArg', None, 'Hi!')
    cmdLineArgParser.add_argument(2, '3rdArg', 'bool', True)
    cmdLineArgParser.add_argument(4, '5thArg', 'float', 3.14)
    cmdLineArgParser.add_argument(8, '9thArg', 'Str', 'A')
    cmdLineArgParser.parse_args([ "1" ])
    if argparse.debugLevel > 1:
        print cmdLineArgParser
        raw_input("PRESS RETURN TO CONTINUE -->")

def testDuplicateArgDefs():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg')
    cmdLineArgParser.add_argument(1, '2ndArg', 'int', 5)
    cmdLineArgParser.add_argument(0, '3rdArg', 'str')
    cmdLineArgParser.add_argument(1, '4thArg', 'bool', True)
    cmdLineArgParser.parse_args([ "1" ])
    if argparse.debugLevel > 1: print cmdLineArgParser
    if prompt: raw_input("PRESS RETURN TO CONTINUE -->")

# CAN I LOSE ONE OF THE OUT-OF-ORDER ARGUMENT TESTS?
def testOutOfOrderArgDefs1():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg', 'int', 5)
    cmdLineArgParser.add_argument(1, '2ndArg')
    cmdLineArgParser.add_argument(2, '3rdArg', 'float', 7.14)
    cmdLineArgParser.add_argument(3, '4thArg', 'bool')
    cmdLineArgParser.parse_args([ 1, "2", 3.0, 4 ])
    if argparse.debugLevel > 1: print cmdLineArgParser
    if prompt: raw_input("PRESS RETURN TO CONTINUE -->")

def testOutOfOrderArgDefs2():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg', 'int', 5)
    cmdLineArgParser.add_argument(1, '2ndArg', 'str', "A")
    cmdLineArgParser.add_argument(2, '3rdArg', 'float', 7.14)
    cmdLineArgParser.add_argument(3, '4thArg', 'bool')
    cmdLineArgParser.parse_args([ 1, "2", 3.0, 4 ])
    if argparse.debugLevel > 1: print cmdLineArgParser
    if prompt: raw_input("PRESS RETURN TO CONTINUE -->")

def testOutOfOrderArgDefs3():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(2, '3rdArg', 'float')
    cmdLineArgParser.add_argument(3, '4thArg', 'bool')
    cmdLineArgParser.add_argument(0, '1stArg', 'int', 5)
    cmdLineArgParser.add_argument(1, '2ndArg', 'str', "A")
    cmdLineArgParser.parse_args([ 1, "2", 3.0, 4 ])
    if argparse.debugLevel > 1: print cmdLineArgParser
    if prompt: raw_input("PRESS RETURN TO CONTINUE -->")

def testMultipleArgDefErrors():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg', None, 'Hi!')
    cmdLineArgParser.add_argument(1, '2ndArg')
    cmdLineArgParser.add_argument(2, '3rdArg', 'bool', True)
    cmdLineArgParser.add_argument(2, '3rdArg', 'int', 3)
    cmdLineArgParser.add_argument(4, '5thArg')
    cmdLineArgParser.add_argument(4, '5thArg', 'float', 3.14)
    cmdLineArgParser.add_argument(6, '7thArg', 'Str', 'A')
    cmdLineArgParser.parse_args([ "1", "2",  False, None, 1.0, None, 'B' ])
    if argparse.debugLevel > 1: print cmdLineArgParser
    if prompt: raw_input("PRESS RETURN TO CONTINUE -->")

def testMissingRequiredArgs():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg', 'int')
    cmdLineArgParser.add_argument(1, '2ndArg')
    cmdLineArgParser.add_argument(2, '3rdArg', 'float', 1.0)
    cmdLineArgParser.add_argument(3, '4thArg', 'bool', False)
    if argparse.debugLevel > 1: print cmdLineArgParser
    cmdLineArgParser.parse_args([])
    cmdLineArgParser.parse_args([ 1 ])
    if prompt: raw_input("PRESS RETURN TO CONTINUE -->")

def testTooManyCmdLineArgs():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg')
    cmdLineArgParser.add_argument(1, '2ndArg', 'bool', True)
    if argparse.debugLevel > 1: print cmdLineArgParser
    cmdLineArgParser.parse_args([ "1", False, 2, "Hi!" ])
    if prompt: raw_input("PRESS RETURN TO CONTINUE -->")

def testInvalidArgTypes():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg', 'bool')
    cmdLineArgParser.add_argument(1, '2ndArg')
    cmdLineArgParser.add_argument(2, '3rdArg', 'float')
    cmdLineArgParser.add_argument(3, '4thArg', 'int')
    if argparse.debugLevel > 1: print cmdLineArgParser
    cmdLineArgParser.parse_args([ "Hi",  None, "Pi", "Three" ])
    if prompt: raw_input("PRESS RETURN TO CONTINUE -->")

def testValidCmdLineArgs():
    pass

def testBackwardsArgDefs():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(3, '4thArg', 'bool', True,
                                  help="4TH ARGUMENT")
    cmdLineArgParser.add_argument(2, '3rdArg', 'float', 7.14,
                                  help="3RD ARGUMENT")
    cmdLineArgParser.add_argument(1, '2ndArg', 'str', "A",
                                  help="2ND ARGUMENT")
    cmdLineArgParser.add_argument(0, '1stArg', 'int', 5,
                                  help="1ST ARGUMENT")
    print cmdLineArgParser.parse_args([ 1, "2", 3.0, 4 ]).__dict__
    if argparse.debugLevel > 1:
        print cmdLineArgParser
        print cmdLineArgParser.helpStr
        raw_input("PRESS RETURN TO CONTINUE -->")

def testOnlyOptionalArgs():

    cmdLineArgParser = argparse.ArgumentParser()
    cmdLineArgParser.add_argument(0, '1stArg', 'int', 10)
    cmdLineArgParser.add_argument(1, '2ndArg', 'str', "Rufi")
    print cmdLineArgParser.parse_args([]).__dict__
    if argparse.debugLevel > 1:
        print cmdLineArgParser
        print cmdLineArgParser.helpStr
        raw_input("PRESS RETURN TO CONTINUE -->")

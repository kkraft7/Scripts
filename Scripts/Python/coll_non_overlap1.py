#!/usr/bin/python2.2
# 
# coll_non_overlap.py: Create non-overlapping collections from list of
#   crawled URLs.
#
# NOTES:
# 01) 20000 is the port for the Crawl Manager
# 02) The "crawled" command displays only crawled URLs
# 03) The "unknown" command displays all URLs (+ 7 other fields)
# 04) If no URLs are being returned using "crawled" then try "unknown"
# 05) This uses sockets instead of telnetlib to talk to Crawl Manager
# 06) This version ... (describe algorithm)

# Create documentation page for this script.
# 1. Description
# 2. Milestones/dates
# 3. Technical details
# 4. TO-DO

"""
Uses an adminrunner_client to create non-overlapping collections.

Connects to Crawl Manager to get the list of all crawled URLs which are
then used to define collections. Exludes all subsets of collection URLs
in order to eliminate overlap.

USAGE: coll_non_overlap.py <number_of_collections>
"""

__author__ = 'kkraft@google.com (Kevin Kraft)'

import adminrunner_client
import socket, time, sys, os

maxDepth     = 10
exitScript   = 0
crawlMgrHost = 'LOCALHOST'
crawlMrgPort = 20000

try:
    numCollections = int(sys.argv[1])
except (IndexError, ValueError, TypeError):
    print ("\n%s: 1ST ARG MUST BE THE NUMBER OF COLLECTIONS TO CREATE\n"
           % (sys.argv[0]))
    sys.exit(1)

# DEBUG LEVEL SHOULD BE AN OPTION - THIS IS TEMPORARY FOR FLEXIBILITY:
try:
    debugLevel = int(sys.argv[2])
except (IndexError, ValueError, TypeError):
    debugLevel = 1

class crawlMgrError(Exception):
  "Used to indicate failure of a Crawl Manager command"

def waitForFile(fileName, timeout=5):
  "Wait timeout seconds for fileName to exist and have non-zero size"

  t0 = time.time()

  while not os.path.exists(fileName) or os.path.getsize(fileName) == 0:
    time.sleep(0.1)
    if time.time() - t0 > timeout: break

  if not os.path.exists(fileName):
    print ('ERROR: FILE NOT CREATED WITHIN %d SECONDS: %s' %
           (timeout, fileName))
  elif os.path.getsize(fileName) == 0:
    print ('ERROR: FILE STILL 0 SIZE AFTER %d SECONDS: %s' %
           (timeout, fileName))
  elif debugLevel > 1:
    print 'CREATED FILE: ' + fileName
    print 'ELAPSED TIME: %.2f seconds' % (time.time() - t0)

def createCollection(collectionName, urlList):
  arc = adminrunner_client.AdminRunnerClient("localhost", 2100)
  if arc.IsAlive():
    if debugLevel > 0: print collectionName
    if (arc.CreateCollection(collectionName)):
      for url in urlList:
        if debugLevel > 0: print '  ' + url
        arc.SetCollectionFileParam(collectionName, 'GOODURLS', url)
    else:
      print 'CREATION FAILED FOR: ' + collectionName
  else:
    print "FAILED TO CREATE ADMINRUNNER CLIENT!"

message = '# CREATING %d COLLECTIONS #' % (numCollections)
border  = '\n' + ('#' * len(message)) + '\n'
print border + message + border
if debugLevel > 0: print 'DEBUG SET TO: ' + str(debugLevel)

try:
  # (1) Get all crawled URLs by connecting to crawlmanager.
  UDPSock = socket.socket() # UDP streaming socket (?)
  print 'SETUP SOCKET:\n   ADDR TYPE: AF_INET\n   SOCK TYPE: SOCK_STREAM'

  urlDumpFile  = '/tmp/url_dump.out'
  urlDumpState = 'crawled'
  crawlMgrCmd  = 'x dump %s %s' % (urlDumpState, urlDumpFile)

  if os.path.exists(urlDumpFile):
    os.rename(urlDumpFile, urlDumpFile + '.bk')

  UDPSock.connect((crawlMgrHost, crawlMrgPort))
  print ('CONNECTED TO: CRAWL MANAGER\n%sHOST: %s\n%sPORT: %d' %
         (' ' * 8, crawlMgrHost, ' ' * 8, crawlMrgPort))

  UDPSock.send(crawlMgrCmd + '\n')
  print 'SENT COMMAND: %s' % (crawlMgrCmd)

  response = ''
  buffSize = 1024

  while 1:
    data = UDPSock.recv(buffSize).strip()
    response += data

    if debugLevel > 2: print '        DATA: ' + data

    if response.count('ACKgoogle'):
      UDPSock.close()
      break

  if debugLevel > 0:
    print 'GOT RESPONSE: %s' % (response)

  if response.count('NACKgoogle'):
    raise crawlMgrError, "COMMAND FAILED (NACKgoogle)"

except socket.error, exceptionData:
  print 'SOCKET ERROR: ' + str(exceptionData)
except crawlMgrError, exceptionData:
  print 'CRAWLMANAGER: %s\n' % (str(exceptionData))
else:
  # This is necessary to ensure that urlDumpFile has been created:
  waitForFile(urlDumpFile)

  # (2) Process list of all crawled URLs to find non-overlapping URLs
  try:
    print 'OPENING FILE: %s' % (urlDumpFile)
    urlDumpFD = open(urlDumpFile, 'r')
  except IOError:
    print 'ERROR: UNABLE TO OPEN FILE: %s' % (urlDumpFile)
    sys.exit(1)

  overlappingURLs = []
  collectionURLs  = []

  for url in urlDumpFD:

    url      = url.strip()
    splitURL = url.split('/')

    if debugLevel > 1: print 'CHECKING URL: ' + url
    if debugLevel > 3: print 'SPLIT URL IS: ' + str(splitURL)

    # Ignore URLs that are blank, don't end in '/', or are too long:
    if not url or url[-1] != '/' or len(url.split('/')) > maxDepth: continue

    if not url in overlappingURLs:

      if debugLevel > 1: print '  ADDING URL: ' + url
      collectionURLs.append(url)

      # Strip off trailing URL directory and check for overlaps
      del splitURL[-2]
      while '/'.join(splitURL) not in overlappingURLs and url.count('/') > 3:
        url = '/'.join(splitURL)
        if debugLevel > 1: print '     OVERLAP: ' + url
        overlappingURLs.append(url)
        if url in collectionURLs:
          if debugLevel > 1: print '     REMOVED: ' + url
          collectionURLs.remove(url)
        del splitURL[-2]

      if len(collectionURLs) == numCollections: break

  urlDumpFD.close()

  if len(collectionURLs) < numCollections:
    print ('\nERROR: ONLY FOUND ENOUGH URLS FOR %d/%d COLLECTIONS\n' %
           (len(collectionURLs), numCollections))
  else:
    print '\nSUCCESSFULLY LOCATED %d COLLECTION URLS\n' % (numCollections)
    # (3) Create collections from non-overlapping URL list
    if not exitScript:
      for i in range(len(collectionURLs)):
        collectionName = 'collection%04d' % (i + 1)
        createCollection(collectionName, [ collectionURLs[i] ])
      print


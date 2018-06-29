import re 
import logging
from operator import attrgetter
from com.secureops.fieldextraction.regex import IRegexFieldExtractorItem

###
#
class RegexFieldExtractorItem(IRegexFieldExtractorItem):
    ###
    #
    def __init__(self):
            self.__priority = 10000000
            self.__tags = {}
            self.__matchDict = None
            self.__regexString = None
            self.__pattern = None
    #
    ###
    
    
    ###
    #
    def getRegexString(self):
        return self.__regexString
    #
    ###
    
        
    ###
    #
    def getPriority(self):
        return self.__priority
    #
    ###
    
    
    ###
    # Be careful, this returns a read/write version of the tag dictionary which might not
    # be what we want    
    def getTags(self):
        return self.__tags
    
    #
    ###
    
    
    ###
    #
    def getTag(self, tagName):
        return self.__tags[tagName]
    #
    ###
    
        
    ###
    #
    def setRegexString(self, value):
        try:
            self.__regexString = value
            self.__pattern = re.compile(value)
        except re.error:
            raise AttributeError("%s is not a valid regular expression" % value)
        except:
            raise Exception("An error happened in Jython. " + self.__class__.__name__ +".setRegexString()")
    #
    ###
    
    
    ###
    #
    def setPriority(self, value):
        self.__priority = value
    #
    ###
    
    ###
    #
    def addTag(self, tagName, tagValue, overwrite=True):
        if self.__tags.has_key(tagName) and not overwrite:
            raise ValueError("Tags already contains an item with key %s" % tagName)
        self.__tags[tagName] = tagValue
    #
    ###
    
    ###
    #    
    def matches(self, sourceString):
        matchDict = None
        if self.__pattern:
            matchObj = self.__pattern.match(sourceString)
            if matchObj:
                matchDict = matchObj.groupdict();
                logging.debug('Jython Regex Matched %s using %s: %s' %(sourceString, self.__regexString, str(matchDict)))
        return matchDict
    #
    ###
    
    ###
    #
    def extract(self, sourcesString):
       raise ValueError("extract() NOT SUPPORTED under Jython")
    ###
    #
    def compareTo(self, obj):
        ret = 1;
        if self.getPriority() < obj.getPriority():
            ret = -1
        elif self.getPriority() > obj.getPriority():
            ret = 1
        else:
            if str(self.__class__) == str(obj.__class__):
                if self.getRegexString() == obj.getRegexString():
                    ret = 0
        return ret
    #
    ###
    
    ###
    #
    def quickcheck(self, sourceString):
    	return true
    
    
    ###
    #    
    def __repr__(self):
        return self.__str__()
    #
    ###
    
    
    ###
    #
    def __str__(self):
        return "regexString:'" + str(self.__RegexString) + "'" + \
               " priority:" + str(self.__priority) + \
               " tags:'" + str(self.__tags) + "'"
    #
    ###
    
    
    priority = property(getPriority, setPriority, None, None)
    tags = property(getTags, None, None, None)
    regexString = property(getRegexString, None, None, None)


#
###

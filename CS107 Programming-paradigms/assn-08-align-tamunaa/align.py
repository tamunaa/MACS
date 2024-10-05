#!/usr/bin/env python

import random # for seed, random
import sys    # for stdout



################################### TEST PART ##################################
################################################################################

# Tests align strands and scores
# Parameters types:
#    score          =  int   example: -6
#    plusScores     = string example: "  1   1  1"
#    minusScores    = string example: "22 111 11 "
#    strandAligned1 = string example: "  CAAGTCGC"
#    strandAligned2 = string example: "ATCCCATTAC"
#
#   Note: all strings must have same length
def test(score, plusScores, minusScores, strandAligned1, strandAligned2):
    print("\n>>>>>>START TEST<<<<<<")

    if testStrands(score, plusScores, minusScores, strandAligned1, strandAligned2):
        sys.stdout.write(">>>>>>>Test SUCCESS:")
        sys.stdout.write("\n\t\t" + "Score: "+str(score))
        sys.stdout.write("\n\t\t+ " + plusScores)
        sys.stdout.write("\n\t\t  " + strandAligned1)
        sys.stdout.write("\n\t\t  " + strandAligned2)
        sys.stdout.write("\n\t\t- " + minusScores)
        sys.stdout.write("\n\n")
    else:
        sys.stdout.write("\t>>>>!!!Test FAILED\n\n")


# converts character score to int
def testScoreToInt(score):
    if score == ' ':
        return 0
    return int(score)


# computes sum of scores
def testSumScore(scores):
    result = 0
    for ch in scores:
        result += testScoreToInt(ch)
    return result


# test each characters and scores
def testValidateEach(ch1, ch2, plusScore, minusScore):
    if ch1 == ' ' or ch2 == ' ':
        return plusScore == 0 and minusScore == 2
    if ch1 == ch2:
        return plusScore == 1 and minusScore == 0
    return plusScore == 0 and minusScore == 1


# test and validates strands
def testStrands(score, plusScores, minusScores, strandAligned1, strandAligned2):
    if len(plusScores) != len(minusScores) or len(minusScores) != len(strandAligned1) or len(strandAligned1) != len(
            strandAligned2):
        sys.stdout.write("Length mismatch! \n")
        return False

    if len(plusScores) == 0:
        sys.stdout.write("Length is Zero! \n")
        return False

    if testSumScore(plusScores) - testSumScore(minusScores) != score:
        sys.stdout.write("Score mismatch to score strings! TEST FAILED!\n")
        return False
    for i in range(len(plusScores)):
        if not testValidateEach(strandAligned1[i], strandAligned2[i], testScoreToInt(plusScores[i]),
                                testScoreToInt(minusScores[i])):
            sys.stdout.write("Invalid scores for position " + str(i) + ":\n")
            sys.stdout.write("\t char1: " + strandAligned1[i] + " char2: " +
                             strandAligned2[i] + " +" + str(testScoreToInt(plusScores[i])) + " -" +
                             str(testScoreToInt(minusScores[i])) + "\n")
            return False

    return True

######################## END OF TEST PART ######################################
################################################################################


# Computes the score of the optimal alignment of two DNA strands.
def find_bestie(strand1, strand2, memo):

    len1, len2 = len(strand1), len(strand2)
    if(len1 * len2 == 0):
        res = ()
        if len1 == 0:
            res = (len2 * -2, ' ' * len2, strand2)
        if len2 == 0:
            res = (len1 * -2, strand1, ' ' * len1)
        memo[(strand1, strand2)] = res
        return res

    r = ()
    try:
        r = memo[(strand1[1:], strand2[1:])]
    except KeyError:
        r = find_bestie(strand1[1:], strand2[1:], memo)

    best_with = r[0]
    best1 = r[1]
    best2 = r[2]

    if strand1[0] == strand2[0]:
        result = (best_with + 1, strand1[0] + best1, strand2[0] + best2)
        memo[(strand1, strand2)] = result
        return result

    best = best_with - 1
    best_strand1 = strand1[0] + best1
    best_strand2 = strand2[0] + best2

    r = ()
    try:
        r = memo[(strand1, strand2[1:])]
    except KeyError:
        r = find_bestie(strand1, strand2[1:], memo)

    best_without = r[0]
    ver1 = r[1]
    ver2 = r[2]

    best_without -= 2

    if best_without > best:
        best_strand1 = ' ' + ver1
        best_strand2 = strand2[0] + ver2
        best = best_without

    f = ()
    try:
        f = memo[(strand1[1:], strand2)]
    except KeyError:
        f = find_bestie(strand1[1:], strand2, memo)

    best_without = f[0]
    ver1 = f[1]
    ver2 = f[2]

    best_without -= 2
    if best_without > best:
        best_strand1 = strand1[0] + ver1
        best_strand2 = ' ' + ver2
        best = best_without

    result = (best, best_strand1, best_strand2)

    memo[(strand1, strand2)] = result
    return result
def findOptimalAlignment(dna1, dna2):
    memo = {}
    return find_bestie(dna1, dna2, memo)


def generateRandomDNAStrand(minlength, maxlength):
	assert minlength > 0, \
	       "Minimum length passed to generateRandomDNAStrand" \
	       "must be a positive number" # these \'s allow mult-line statements
	assert maxlength >= minlength, \
	       "Maximum length passed to generateRandomDNAStrand must be at " \
	       "as large as the specified minimum length"
	strand = ""
	length = random.choice(range(minlength, maxlength + 1))
	bases = ['A', 'T', 'G', 'C']
	for i in range(0, length):
		strand += random.choice(bases)
	return strand

# Method that just prints out the supplied alignment score.
# This is more of a placeholder for what will ultimately
# print out not only the score but the alignment as well.
def printPositiveScore(strand1, strand2):
    res = "+ "
    for i in range(0,len(strand1)):
        if(strand1[i] == strand2[i] and strand1[i] != " "):
            res += "1"
        else:
            res += " "


def printNegScore(strand1, strand2):
    res = "- "
    for i in range(0,len(strand1)):
        if(strand1[i] != strand2[i] and strand1[i] != " " and strand2[i] != " "):
            res += "1"
        elif(strand1[i] != strand2[i]):
            res += "2"

def printAlignment(score, out = sys.stdout):
	out.write("Optimal alignment score is " + str(score[0]) + "\n")


# Unit test main in place to do little more than
# exercise the above algorithm.  As written, it
# generates two fairly short DNA strands and
# determines the optimal alignment score.
#
# As you change the implementation of findOptimalAlignment
# to use memoization, you should change the 8s to 40s and
# the 10s to 60s and still see everything execute very
# quickly.

def printResTxt(str1, str2):
    res1 = "+ "
    for i in range(0, len(str1)):
        if(str1[i] == str2[i] and str1[i] != " "):
            res1 += "1"
        else: res1 += " "

    res2 = "- "
    for i in range(0, len(str1)):
        if(str1[i] != str2[i] and str1[i] != " " and str2[i] != " "):
            res2 += "2"
        elif (str1[i] != str2[i] and (str1[i] == " " or str2[i] == " ")):
            res2 += "1"
        else: res2 += " "

    print(res1)
    print("  "+ str1)
    print("  " + str2)
    print(res2)
    
def main():
    test(-4,
             "  11 1 1 11 ",
             "12  2 2 1  2",
             "G ATCG GCAT ",
             "CAAT GTGAATC")
    while (True):
        sys.stdout.write("Generate random DNA strands? ")
        answer = sys.stdin.readline()
        if answer == "no\n":
            break
        strand1 = generateRandomDNAStrand(8, 10)
        strand2 = generateRandomDNAStrand(8, 10)
        strand1 = "TGTCGAGAATATCTTTCCTGTGGCTCAGACGCAGCGGTCCCCGTAGTCAA"
        strand2 = "AGCCGGGGTAATGCGCACGGGAGCCTGCATTTACAATAGCCAGGTGCCCATTTTCAAC"
        sys.stdout.write("Aligning these two strands: " + strand1 + "\n")
        sys.stdout.write("                            " + strand2 + "\n")
        alignment = findOptimalAlignment(strand1, strand2)
        printAlignment(alignment)
        str1 = alignment[1]
        str2 = alignment[2]
        printResTxt(str1, str2)

if __name__ == "__main__":
    main()

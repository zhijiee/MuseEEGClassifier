#!/usr/bin/python

import sys, getopt
import csv
import Constants

# Input validation
def main(argv):
    inputfile = ''
    outputfile = ''
    try:
        opts, args = getopt.getopt(argv,"hi:o:",["ifile=","ofile="])
    except getopt.GetoptError:
        print ('format_csv.py -i <inputfile> -o <outputfile>')
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print ('format_csv.py -i <inputfile> -o <outputfile>')
            sys.exit()
        elif opt in ("-i", "--ifile"):
            inputfile = arg
        elif opt in ("-o", "--ofile"):
            outputfile = arg

    if not inputfile or not outputfile:
        print ('format_csv.py -i <inputfile> -o <outputfile>')
        sys.exit(2)

    print ('Input file is',inputfile)
    print ('Output file is',outputfile)
    format_csv(inputfile, outputfile)


# Filter out important details on
def format_csv(inputfile,outputfile):

    with open(inputfile, 'r') as csvfile:
        reader = csv.reader(csvfile, delimiter=',', quoting=csv.QUOTE_NONE)

        eeg = []
        EEG_STATE = Constants.STATE_NOT_IMPT

        for row in reader:
            if row[1] == Constants.MUSE_ANNO:
                print(row[2])
                if row[2] == Constants.START_MEDITATION:
                    EEG_STATE = Constants.STATE_MEDITATION

                elif row[2] == Constants.START_ARITH_TEST:
                    EEG_STATE = Constants.STATE_STRESSOR

                else:
                    EEG_STATE = Constants.STATE_NOT_IMPT

            # if row[1] == Constants.MUSE_EEG:
            if row[1] == Constants.MUSE_EEG and EEG_STATE != -1:
                # print(row[2], row[3], row[4], row[5])
                try:
                    temp = [row[2], row[3], row[4], row[5], EEG_STATE]
                    eeg.append(temp)

                # Some EEG Rows do not have values which cause this
                except IndexError:
                    pass

        # print(eeg)

    with open(outputfile, 'w') as outputcsv:
        writer = csv.writer(outputcsv)
        for row in eeg:
            writer.writerow(row)

        print("Operation successfully completed!")



if __name__ == "__main__":
   main(sys.argv[1:])

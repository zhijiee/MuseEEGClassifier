#!/usr/local/bin/python3
import Constants as C
import format_csv
import sys, getopt, subprocess
import os
from pathlib import Path

'''
Converts .Muse to .CSV using Muse
'''

def main(argv):
    input_folder = ''
    output_folder = ''
    try:
        opts, args = getopt.getopt(argv,"hi:o:",["ifile=","ofile="])
    except getopt.GetoptError:
        print ('main_batch_convert.py -i <input folder> -o <output folder>')
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-h':
            print ('main_batch_convert.py -i <input_folder> -o <output_folder>')
            sys.exit()
        elif opt in ("-i", "--ifolder"):
            input_folder = arg
        elif opt in ("-o", "--ofolder"):
            output_folder = arg

    if not input_folder or not output_folder:
	    print ('main_batch_convert.py -i <input folder> -o <output folder>')
	    sys.exit(2)

    if not os.path.isdir(input_folder):
        print(input_folder, "not a directory!")
        sys.exit(2)

    if not os.path.isdir(output_folder):
        os.makedirs(output_folder)

    if not os.path.isdir(output_folder + C.ORIGINAL_CSV_DIR):
        os.makedirs(output_folder + C.ORIGINAL_CSV_DIR)


    print ('Input folder is',input_folder)
    print ('Output folder is',output_folder)
    print()
    batch_convert(input_folder, output_folder)

def batch_convert(input_folder, output_folder):
    files = get_files(input_folder)
    count = 0;
    for inputfile in files:
        # Convert Muse to CSV
        original_csv = output_folder + C.ORIGINAL_CSV_DIR + inputfile[:-5] + ".csv"
        if (not os.path.isfile(original_csv)):
            subprocess.call(["muse-player", "-f", input_folder + "/" + inputfile, "-C", original_csv])

            # Process Original CSV to required format
            final_csv = output_folder + inputfile[:-5] + ".csv"
            format_csv.format_csv(original_csv, final_csv)

        else:
            print(original_csv," file exists. Skipping..")
        count+=1
        print( "Completed " + str(count) + " of " + str(len(files)))
        print()
        print()


def get_files(input_folder):
    items = os.listdir(input_folder)
    files = []
    for i in items:
        if i[-5:] == ".muse":
            files.append(i)
    # print(files)
    return files


if __name__ == "__main__":
   main(sys.argv[1:])

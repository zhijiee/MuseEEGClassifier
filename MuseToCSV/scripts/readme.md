# Convert .Muse to .CSV

Convert EEG data file from .Muse to .CSV file and format for use in SVM.

The current implementation only work on Linux. Only tested on Mac OS.

## Prerequisites

Requires MusePlayer to work.

# Running the program

## main_batch_convert
Converts all \*.Muse file in folder to output_folder/original_csv/\*.csv

Then format output_folder/original_csv/\*.csv to output_folder/\*.csv .
```
./main_batch_convert.py -i <input folder> -o <output folder>
```



## main_muse_to_csv
Convert a single \*.muse file to formatted \*.csv.
```
./main_muse_to_csv.py -i <inputfile> -o <outputfile>
```

## format_csv.py
Format \*.CSV file removing redundant EEG data and append labels for meditation and stess.

```
./format_csv.py -i <inputfile> -o <outputfile>
```

# Author
Oh Zhi Jie

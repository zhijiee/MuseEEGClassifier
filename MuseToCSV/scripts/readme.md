# Convert .Muse to .CSV

Convert EEG data file from .Muse to .CSV file and format for use in SVM.

The current implementation only work on Linux. Only tested on Mac OS.

## Prerequisites

Requires MusePlayer to work.

# Running the program

## main_batch_convert
The first operation is to convert all \*.Muse file in folder to output_folder/original_csv/\*.csv

The second operation is to format the original_csv into labelled CSV that can be used for training in SVM. It formats files from output_folder/original_csv/\*.csv to output_folder/\*.csv .
```
./main_batch_convert.py -i <input folder> -o <output folder>
```

## main_muse_to_csv
Convert a \*.muse file  into CSV, then format and label the CSV file.
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

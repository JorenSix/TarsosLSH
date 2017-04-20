#!/bin/bash
#
#pipe ascii struct into parser
h5dump lsh_64_sift_1M.mat | ruby parse_hdf5_struct.rb > 64bit_sift.txt
# data_set is all but last 10k
head -n -10000 64bit_sift.txt > 64bit_sift_data_set.txt
# queries
tail -n 10000 64bit_sift.txt > 64bit_sift_queries.txt
#remove the initial file:
rm 64bit_sift.txt


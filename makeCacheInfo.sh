#!/bin/bash

# this script takes dir, assuming a log file dir in it,
# and creates a ASTR-cache-info.csv based on calls/hits/invalids

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <result-directory>" >&2
    exit 1
fi

DIR=$1

LOG_DIR=$DIR/*.logfiles

OUTPUT_FILE="$DIR/ASTR-cache-info.csv"

echo "bench,calls,hits,invalids,subsets,supersets" > "$OUTPUT_FILE"

calls=$(grep -c Calling $LOG_DIR/String-MAS.* | sed -E 's/^.*String-MAS\.(\S+)\.yml\.log:/\1,/')
hits=$(grep -c CACHE $LOG_DIR/String-MAS.* | sed 's/^.*://')
invalids=$(grep -c Invalid $LOG_DIR/String-MAS.* | sed 's/^.*://')
subsets=$(grep -c SUBSET $LOG_DIR/String-MAS.* | sed 's/^.*://')
supersets=$(grep -c SUPERSET $LOG_DIR/String-MAS.* | sed 's/^.*://')

paste -d',' <(echo "$calls") <(echo "$hits") <(echo "$invalids") <(echo "$subsets") <(echo "$supersets") >> "$OUTPUT_FILE"
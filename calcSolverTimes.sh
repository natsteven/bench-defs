#!/bin/bash

# this script takes dir, assuming a log file dir in it,
# and calculates solver times based on printouts

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <result-directory>" >&2
    exit 1
fi

DIR=$1

LOG_DIR=$DIR/*.logfiles

OUTPUT_FILE="$DIR/solver-times.csv"

echo "bench,astr,z3" > "$OUTPUT_FILE"

astr_times=$(grep Time $LOG_DIR/String-MAS.* | sed 's/:.*:/:/' | \
  awk -F':' '{sum[$1]+=$NF} END{for(f in sum) print f "," sum[f]}' | \
  sed 's/^.*String-MAS.//; s/.yml.log//' | sort -t, -k1,1)

z3_times=$(grep Time $LOG_DIR/String-z3.* | sed 's/:.*:/:/' | \
  awk -F':' '{sum[$1]+=$NF} END{for(f in sum) print f "," sum[f]}' | \
  sed 's/^.*String-z3.//; s/.yml.log//' | sort -t, -k1,1)

# need to make sure they are aligned (no missing benches)
join -t, -a1 -a2 -e0 -o 0,1.2,2.2 <(echo "$astr_times") <(echo "$z3_times") >> "$OUTPUT_FILE"

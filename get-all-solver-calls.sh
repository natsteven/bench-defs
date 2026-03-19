#! /bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <result-directory>" >&2
    exit 1
fi

DIR=$1
OUTPUT_FILE="$DIR/solver-call-times.csv"
# Z3_OUT="$DIR"/util/z3-calls.csv
# ASTR_OUT="$DIR"/util/astr-calls.csv

# echo "bench,astr-time (ms)" > "$ASTR_OUT"
# echo "bench,z3-time (ms)" > "$Z3_OUT"
echo "bench,astr-time (ms),z3-time (ms)" > "$OUTPUT_FILE"

for file in "$DIR"/spf.logfiles/String-MAS.*.yml.log; do
  bench=$(basename "$file" .yml.log)
  bench=${bench#String-MAS.}
  z3file=String-z3."$bench".yml.log
  z3file="$DIR"/spf.logfiles/"$z3file"

  # rg "Solver Time" "$file" | awk -F: -v b="$bench" '{print b "," $NF}' >> "$ASTR_OUT"
  # rg "Solver Time" "$z3file" | awk -F: -v b="$bench" '{print b "," $NF}' >> "$Z3_OUT"

  astr_times=($(rg "Solver Time" "$file" | sed 's/.*://'))
  z3_times=($(rg "Solver Time" "$z3file" | sed 's/.*://'))

  max=${#astr_times[@]}
  if [ ${#z3_times[@]} -gt $max ]; then
    max=${#z3_times[@]}
  fi

  for ((i=0; i<max; i++)); do
    astr_time=${astr_times[i]:-}
    z3_time=${z3_times[i]:-}
    echo "$bench,$astr_time,$z3_time" 
  done >> "$OUTPUT_FILE"

done

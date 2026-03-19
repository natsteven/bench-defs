#! /bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <result-directory>" >&2
    exit 1
fi

DIR=$1

if [[ $DIR == *z3* ]]; then
  SOLVER="Z3"
else
  SOLVER="ASTR"
fi

OUTPUT_FILE="$DIR/solver-info.csv"

echo -n "bench,$SOLVER-time (ms),$SOLVER-calls,$SOLVER-error" > "$OUTPUT_FILE"

if [[ $SOLVER == "ASTR" ]]; then
  echo ",SPF-error" >> "$OUTPUT_FILE"
else
  echo "" >> "$OUTPUT_FILE"
fi

 for file in "$DIR"/logfiles/*; do
   bench=$(basename "$file" .yml.log)
   bench=${bench#SV-COMP26_no-runtime-exception.}

   # accumalate final field after : and add 0 (if nomatches found)
   time=$(rg "Solver Time" "$file" | awk -F: '{sum+=$NF} END{print sum+0}')

   # counts
   calls=$(rg -c '^Calling' "$file" || echo 0)
   error=$(rg -c 'ERROR|Unhandled' "$file" || echo 0)
   if [[ $SOLVER == "ASTR" ]]; then
     spf_error=$(rg -c 'Exception: gov\.nasa|\[SEVERE\]|java.lang.NullPointerException' "$file" || echo 0)
   fi
   echo -n "$bench","$time","$calls","$error" >> "$OUTPUT_FILE"
   if [[ $SOLVER == "ASTR" ]]; then
     echo ",$spf_error" >> "$OUTPUT_FILE"
  else 
    echo "" >> "$OUTPUT_FILE"
   fi
 done


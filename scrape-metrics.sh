#! /bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <result-directory>" >&2
    exit 1
fi

DIR=$1

OUTPUT_FILE="$DIR/solver-metrics.csv"

echo "bench,astr-time (ms),astr-error,z3-time (ms),z3-error,spf-error" > "$OUTPUT_FILE"

for file in "$DIR"/spf.logfiles/String-MAS.*.yml.log; do
  bench=$(basename "$file" .yml.log)
  bench=${bench#String-MAS.}
  z3file=String-z3."$bench".yml.log
  z3file="$DIR"/spf.logfiles/"$z3file"

  # accumalate final field after : and add 0 (if nomatches found)
  astr_time=$(rg "Solver Time" "$file" | awk -F: '{sum+=$NF} END{print sum+0}')
  z3_time=$(rg "Solver Time" "$z3file" | awk -F: '{sum+=$NF} END{print sum+0}')

  astr_error=$(rg -c 'ERROR|Unhandled' "$file" || echo 0)
  z3_error=$(rg -c 'ERROR|Unhandled' "$z3file" || echo 0)
  spf_error=$(rg -c 'Exception: gov\.nasa|\[SEVERE\]|java.lang.NullPointerException' "$file" || echo 0)

  echo "$bench,$astr_time,$astr_error,$z3_time,$z3_error,$spf_error" >> "$OUTPUT_FILE"
done

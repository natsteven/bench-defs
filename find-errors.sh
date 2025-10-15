#!/bin/bash

#while IFS= read -r line; do
#  echo "$line"
#  grep -E 'Error|Unhandled|Exception|Warning|Numeric' 'results-verified/spf-results.2025-10-14_15-50-09/spf.2025-10-14_15-40-11.logfiles/'"$line" | grep -v 'RuntimeException'
#done < diff.txt

for file in results-verified/spf-results.2025-10-14_15-50-09/spf.2025-10-14_15-40-11.logfiles/String-MAS*.log; do
  output=$(grep Unhandled "$file")
#  output=$(grep -E 'Exception' "$file" | grep -vE 'NoUncaughtExceptionsProperty|RuntimeException|runtime-exception')
#  output=$(grep "Calling MAS" "$file")
  if [ -n "$output" ]; then
    echo $(basename "$file")
    echo "$output"
  fi
done
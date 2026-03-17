#! /bin/bash

mapfile -t patterns < good.txt

while IFS= read -r bench; do
  for pattern in "${patterns[@]}"; do
    if [[ $(dirname "$bench") == *"$pattern" ]]; then
      echo "$bench"
      break
    fi
  done
done < <(fd Main.java sv-benchmarks/java/argv/)

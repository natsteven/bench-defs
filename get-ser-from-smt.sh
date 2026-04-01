#!/bin/bash

dir="results/03-31_13-24.results"
log_dir="$dir/spf.logfiles"
ser_dir="$dir/util/spf.2026-03-31_13-18-12.files/graphs"

for file in argv-string-smt/*.smt2; do
  filename=$(basename "$file" .smt2)
  log_file="$log_dir/graphs.$filename.yml.log"
  flat_smt=$(tr -d ' \t\n\r' < "$file")
  match=false

  while read -r line; do
    rest="${line#*SMT QUERY:}"
    id="${rest%%:*}"
    raw_smt="${rest#*:}"
    smt=$(echo "$raw_smt" | sed 's/||//g' | tr -d ' \t\n\r')

    if [[ "$flat_smt" == "$smt" ]]; then
      # ser_name="query_$id.ser"
      # ser_path="$ser_dir/$filename.yml/$ser_name"
      # cp $ser_path argv-string-smt/$filename.ser
      # echo "$file: $ser_path"
      match=true
      break
    fi
  done< <(rg "^SMT QUERY" "$log_file")

  if [[ "$match" == false ]]; then
    echo "NO MATCH: $file"
  fi
done

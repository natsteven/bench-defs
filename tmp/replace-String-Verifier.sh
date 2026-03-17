#!/bin/bash

file=$1
out=$file.new

if [[ -z "$file" ]]; then
  echo "Usage: $0 <file>"
  exit 1
fi

main=0
while IFS= read -r line; do
  if [[ $line =~ public[[:space:]]+static[[:space:]]+void[[:space:]]+main.* ]];then
    main=1
  elif [[ $line =~ public.* ]]; then
    main=0
  fi
  if [[ $main -eq 1 && $line =~ .*String.* ]]; then
    newline=$(echo "$line" | sed -E 's/(String\s+\S+\s*=).*/\1 Verifier.nondetString();/')
    echo "$newline" >> "$out"
  else
    echo "$line" >> "$out"
  fi
done < "$file"
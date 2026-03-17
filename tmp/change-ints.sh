#!/bin/bash

FILES="tmp/symints.txt"
# COMMON="/home/nat/Repos/bench-defs/numerics/common/"
Z3_LOG_DIR="/home/nat/Repos/bench-defs/results-verified/z3/logfiles/"

mapfile -t files < "$FILES"

for file in "${files[@]}"; do
  echo "$file"
  parent="${file%/*}"
  z3Log=$(fd "exception.${parent##*/}.yml.log" "$Z3_LOG_DIR")
  rg define-fun.*Int "$z3Log" | sed -n 's/(define-fun \(int[0-9]\+\).*Int\s\+(\?\(-\?\s\?[0-9]\+\)))\?/\1,\2/p' | tr -d ' '
done

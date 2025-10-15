#!/bin/bash
while IFS= read -r line; do
  sed -i "/$line/d" Strings.set
#  cp -r sv-benchmarks/java/"$line" ../SPF/jpf-symbc/src/tests/edu/boisestate/cs/svcomp/
done < dont-call-string-proc.txt
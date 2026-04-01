#!/bin/bash
set -euo pipefail

# Define your local paths
LOCAL_SPF="$HOME/Repos/SPF"
RESULTS="results"
VERIFIER="spf"
BENCHDEF="$VERIFIER.xml"

# Set your Benchexec limits and options here explicitly
NUMTHREADS="-N 4"
LIMIT_CORES="-c 1"
LIMIT_TIME="-T 120"
LIMIT_MEMORY="-M 1GB"
BENCHEXECOPTIONS="--results-per-taskset --maxLogfileSize 2MB"

echo "($VERIFIER) Run started"

ABS_BENCHDEF=$(realpath "$BENCHDEF")
ABS_OUTPUT=$(realpath "$RESULTS")
ABS_BENCHEXEC=$(realpath "./benchexec/bin/benchexec")

pushd "$LOCAL_SPF" > /dev/null

"$ABS_BENCHEXEC" "$ABS_BENCHDEF" \
    --outputpath "$ABS_OUTPUT" \
    --tool-directory . \
    $BENCHEXECOPTIONS $NUMTHREADS $LIMIT_TIME $LIMIT_CORES $LIMIT_MEMORY

popd > /dev/null

TIMESTAMP=$(date +"%m-%d_%H-%M")

for file in "$RESULTS"/spf.*.results.*.xml.bz2; do
  ./benchexec/bin/table-generator "$file"
done

# Custom table that can grab info from logs (keep for future use)
# ./benchexec/bin/table-generator -x tmp/custom-table.xml

OUTPUT_DIR="$RESULTS/$TIMESTAMP".results
LOGS="$OUTPUT_DIR/spf.logfiles"
UTIL="$OUTPUT_DIR/util"
mkdir -p "$OUTPUT_DIR"
mkdir -p "$UTIL"

mv "$RESULTS"/$VERIFIER.* "$UTIL"

unzip -q "$UTIL"/$VERIFIER.*.logfiles.zip -d "$OUTPUT_DIR/"
mv "$OUTPUT_DIR"/spf.*.logfiles/ "$LOGS"

mv "$UTIL"/$VERIFIER.*.html "$OUTPUT_DIR/"
# mv "$OUTPUT_DIR"/$VERIFIER.*.results.String-MAS.*.html "$OUTPUT_DIR"/spf.results.MAS.html
# mv "$OUTPUT_DIR"/$VERIFIER.*.results.String-z3.*.html "$OUTPUT_DIR"/spf.results.z3.html
# mv "$UTIL"/$VERIFIER.*.results.String-MAS.*.csv "$OUTPUT_DIR"/spf.results.MAS.csv
# mv "$UTIL"/$VERIFIER.*.results.String-z3.*.csv "$OUTPUT_DIR"/spf.results.z3.csv

# ./makeCacheInfo.sh "$OUTPUT_DIR"
# ./calcSolverTimes.sh "$OUTPUT_DIR"
#
# awk -F',' 'NR>1 {astr+=$2; z3+=$3} END {print "Total Solving Time (ms):\nAstr: " astr "\nZ3: " z3}' "$OUTPUT_DIR/solver-times.csv" | tee "$OUTPUT_DIR/total-solver-times.txt"

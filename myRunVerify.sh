#!/bin/bash
# this is mostly a modified copy of scripts/execute_runs/mkRunVerify.sh

set -euo pipefail

source "scripts/configure.sh"

SOLVERS="mas z3"
# LIMIT_TIME=
# LIMIT_MEMORY=

export VERIFIER="spf";

for SOLVER in $SOLVERS; do
  BENCHDEF="$VERIFIER-$SOLVER.xml"
  ARCHIVE="archives/$VERIFIER-$SOLVER-verify.zip"

  echo "($VERIFIER) w/$SOLVER  Run started";

  # this could be simpler as we use the same container
  CONTAINER=$(yq --raw-output '(.competition_participations[]? | select( .competition=="'"$COMPETITIONNAME $YEAR"'" and .track == "'"$TRACK"'") | .tool_version ) as $version  | .versions[] | select(.version == $version  and (.full_container_images | length != 0)) | .full_container_images[0]' "fm-tools/data/$VERIFIER.yml")
  if [[ "$CONTAINER" != "" ]]; then
    echo "Using container $CONTAINER"
  fi
  "$SCRIPT_DIR"/execute_runs/execute-runcollection.sh \
  	  "benchexec/bin/benchexec" "$ARCHIVE" "$BENCHDEF" \
  	  "\"$WITNESSTARGET\"" "$(dirname "$0")/$RESULTSVERIFICATION/" \
  	  "--tool-directory benchexec/tools/spf.py" \
  	  "$OPTIONSVERIFY $BENCHEXECOPTIONS $LIMIT_TIME $LIMIT_CORES $LIMIT_MEMORY $TESTCOMPOPTION"

  date -Iseconds
done

echo "Getting HTML and CSV tables:"
OUT="$RESULTSVERIFICATION/results-table.xml"

# Helper: pick latest matching file for each verifier (reuse function if already sourced configure.sh)
latest() {
  ls -1t "$RESULTSVERIFICATION"/"$1".*.results.*.xml.bz2 | head -n1
}

MAS_RES=$(basename "$(latest spf-mas)")
Z3_RES=$(basename "$(latest spf-z3)")
echo "Using result files:"
echo "  $MAS_RES"
echo "  $Z3_RES"

cat > "$OUT" <<EOF
<?xml version="1.0"?>
<table>
  <column title="status"/>
  <column title="score" displayTitle="raw score"/>
  <column title="cputime" numberOfDigits="2" displayTitle="cpu"/>
  <column title="memory" numberOfDigits="2" displayTitle="mem" displayUnit="MB" sourceUnit="B"/>
  <result filename="$MAS_RES" title="spf-mas"/>
  <result filename="$Z3_RES" title="spf-z3"/>
</table>
EOF

# Generate HTML + CSV + TXT
./benchexec/bin/table-generator -x "$OUT"

echo "Done. Files: ${OUT%.xml}.html / .csv / .txt"

for SOLVER in $SOLVERS; do
  RESULT="$(ls $RESULTSVERIFICATION/$VERIFIER-$SOLVER.*.xml.bz2)"
  echo "$RESULT"
   if [[ "$RESULT" =~ $RESULTSVERIFICATION/$VERIFIER-$SOLVER\.([0-9]{4}-[0-9]{2}-[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2})\.results\.(.+)\.xml\.bz2$ ]]; then
     DATETIME="${BASH_REMATCH[1]}"
     RUN="${BASH_REMATCH[2]}"
   fi

  LOGDIR="$RESULTSVERIFICATION/$VERIFIER-$SOLVER.$DATETIME.$RUN"

  TXT="${RESULT%.xml.bz2}.txt"
  LOGS="$RESULTSVERIFICATION/$VERIFIER-$SOLVER.$DATETIME.logfiles.zip"

  mkdir "$LOGDIR"
  mkdir "$LOGDIR/witnesses"

  mv "$TXT" "$LOGDIR"
  mv "$RESULT" "$LOGDIR"
  mv $RESULTSVERIFICATION/$VERIFIER-$SOLVER.$DATETIME.files/String/* "$LOGDIR/witnesses"

  unzip "$LOGS" -d "$LOGDIR"
  rm "$LOGS"
  rm -rf "$RESULTSVERIFICATION/$VERIFIER-$SOLVER.$DATETIME.files"
  rm "$RESULTSVERIFICATION/$VERIFIER-$SOLVER.$DATETIME.fileHashes.json"
  rm -rf bin/$VERIFIER-$SOLVER*
done

LOGDIR="$RESULTSVERIFICATION/combined-results.$RUN.$(date +%F_%H-%M-%S)"
mkdir "$LOGDIR"
mv $RESULTSVERIFICATION/results-table* "$LOGDIR"
mv $RESULTSVERIFICATION/$VERIFIER-* "$LOGDIR"


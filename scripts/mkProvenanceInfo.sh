#!/bin/bash

# @title Write provenance information to config file
# @description Prepare Phase: write info about competition and components used to file
COMPETITIONNAME=`scripts/parseInitConfig.py --get-comp benchmark-defs/category-structure.yml`;
YEAR=`scripts/parseInitConfig.py --get-year benchmark-defs/category-structure.yml`;
TARGETSERVER=`echo ${COMPETITIONNAME} | tr [:upper:] [:lower:]`
export PROVENANCEFILE="`pwd`/provenance.txt";
rm -f "$PROVENANCEFILE"
touch "$PROVENANCEFILE"
echo "" >> "$PROVENANCEFILE"
echo "Provenance information:" >> "$PROVENANCEFILE"
echo "Benchmark executed" >> "$PROVENANCEFILE"
echo "for ${COMPETITIONNAME} ${YEAR}, https://${TARGETSERVER}.sosy-lab.org/${YEAR}/" >> "$PROVENANCEFILE"
echo "by ${USER}@${HOSTNAME}" >> "$PROVENANCEFILE"
echo "based on the components" >> "$PROVENANCEFILE"
for repo in "$@"; do
  (
  cd "$repo"
  echo "`git remote get-url origin`  `git describe --long --always`" >> "$PROVENANCEFILE"
  )
done

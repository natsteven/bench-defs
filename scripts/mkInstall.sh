#!/bin/bash

# @title Install Tool Archive
# @description Unzips and checks the structure of tool archive.

VERIFIER=$1;
ARCHIVE="`pwd`/archives/2021/${VERIFIER}.zip";

if [ -z $VERIFIER ]; then
  echo "Error: No verifier specified.";
  exit 1;
fi

# Prepare config info
PROVENANCEFILE="`pwd`/provenance.txt";
PROVENANCEVERIFIER="./results-logs/${VERIFIER}-config.txt";
cp -f ${PROVENANCEFILE} ${PROVENANCEVERIFIER};
echo "Archive: ${VERIFIER}.zip-sha1-"`shasum ${ARCHIVE} | sed "s/\(.\{10\}\).*/\1/"` >> ${PROVENANCEVERIFIER};
echo "on `date -Iminutes`" >> ${PROVENANCEVERIFIER};
echo "" >> ${PROVENANCEVERIFIER};
# Unzip
echo "Installing $ARCHIVE ...";
rm -rf ./bin/${VERIFIER};
mkdir -p ./bin/${VERIFIER};
cd ./bin/$VERIFIER;
unzip $ARCHIVE;
# Check structure
if [[ `find . -mindepth 1 -maxdepth 1 | wc -l` == 1 ]]; then
  echo "Info: One folder found in archive.";
  DIR="`find . -mindepth 1 -maxdepth 1`";
  mv "${DIR}" "${DIR}__COMP";
  mv "${DIR}__COMP"/* .
else
  echo "Error: Archive does not contain exactly one folder.";
  exit 1;
fi


# SV-COMP Benchmark Definitions

This directory contains the BenchExec benchmark definitions for SV-COMP.
Each XML file represents the run configuration for a verifier,
as it is used in the SV-COMP competition.

The files are automatically generated from:
* The reference benchmark definition `reference/reference-verifier-c.xml`
* The `category-structure.yml`.
* The [FM-tools](https://gitlab.com/sosy-lab/benchmarking/fm-tools) data files.

Example execution:
```
python3 ./create-benchmark-xmls.py \
    --xml-template reference/reference-verifier-c.xml \
    --category-structure category-structure.yml \
    --fm-data ../fm-tools/data/ \
    --output .
```

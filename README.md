# SV-COMP Reproducibility
This repository describes the configuration of the competition machines (below)
and the benchmark definition for each verifier (folder [benchmark-defs/](benchmark-defs/)),
in order to make results of the competition reproducible.


## Setup
The following steps set up the benchmarking environment:
- `git clone https://gitlab.com/sosy-lab/sv-comp/bench-defs.git ./`
- `make init` (takes a while: downloads several GB of data from repositories)
- `make update`

For reproducing results of a specific edition of the competition, please checkout the tag for that edition.

## Executing a Benchmark for a Particular Tool

Assume that we would like to reproduce results for the verifier `CPAchecker`.
We can execute the verification runs using the following command:

`scripts/execute-runs/execute-runcollection.sh "../../benchexec/bin/benchexec -t ReachSafety-ControlFlow" cpachecker cpachecker.xml witness.graphml .graphml ../../results-verified/`

In the following we explain some of the steps.

### Unpack a Tool

The following command unpacks the verifier `CPAchecker`:
- `mkdir bin/cpachecker-32KkXQ0CzM`
- `scripts/execute-runs/mkInstall.sh cpachecker bin/cpachecker-32KkXQ0CzM`

### Assemble Provenance Information for a Tool

The following command prints information about the repositories and their versions:
- `scripts/execute-runs/mkProvenanceInfo.sh cpachecker`

### Execute a Benchmark for a Tool

- `cd bin/cpachecker-32KkXQ0CzM`
- `../../benchexec/bin/benchexec -t ReachSafety-ControlFlow ../../benchmark-defs/cpachecker.xml -o ../../results-verified/`


## Computing Environment on Competition Machines

### Docker Image

SV-COMP provides a Docker image that tries to provide an environment
that has mostly the same packages installed as the competition machines.
The Docker image is described here:
https://gitlab.com/sosy-lab/benchmarking/competition-scripts/#docker-image

### Parameters of RunExec

The parameters that are passed to the [BenchExec](https://github.com/sosy-lab/benchexec) [1]
executor [runexec](https://github.com/sosy-lab/benchexec/blob/main/doc/runexec.md) on the competition machines
are described here:
https://gitlab.com/sosy-lab/benchmarking/competition-scripts/#parameters-of-runexec


## References

[1]: Dirk Beyer, Stefan Löwe, and Philipp Wendler.
     Reliable Benchmarking: Requirements and Solutions.
     International Journal on Software Tools for Technology Transfer (STTT), 21(1):1-29, 2019.
     https://doi.org/10.1007/s10009-017-0469-y



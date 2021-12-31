# SV-COMP Reproducibility - Overview

This repository describes the configuration of the competition machines (below)
and the benchmark definition for each verifier (folder [benchmark-defs/](benchmark-defs/)),
in order to make results of the competition reproducible.


# Components for Reproducing Competition Results

The competition uses several components to execute the benchmarks.
The components are described in the following table.

| Component              | Repository                                                      | Participants             |
| ---                    | ---                                                             | ---                      |
| Verification Tasks     | https://gitlab.com/sosy-lab/benchmarking/sv-benchmarks          | add, fix, review tasks   |
| Benchmark Definitions  | https://gitlab.com/sosy-lab/sv-comp/bench-defs                  | define their parameters  |
| Tool-Info Modules      | https://github.com/sosy-lab/benchexec/tree/main/benchexec/tools | define inferface         |
| Verifier Archives      | https://gitlab.com/sosy-lab/sv-comp/archives-2022               | submit to participate    |
| Benchmarking Framework | https://github.com/sosy-lab/benchexec                           | (use to test their tool) |
| Competition Scripts    | https://gitlab.com/sosy-lab/benchmarking/competition-scripts    | (use to reproduce)       |
| Witness Format         | https://github.com/sosy-lab/sv-witnesses                        | (know)                   |
| Task-Definition Format | https://gitlab.com/sosy-lab/benchmarking/task-definition-format | (know)                   |
| Remote Execution       | https://gitlab.com/sosy-lab/software/coveriteam                 | (use to test their tool) |


# Setup
The following steps set up the benchmarking environment:
- `git clone https://gitlab.com/sosy-lab/sv-comp/bench-defs.git ./`
- `make init` (takes a while: downloads several GB of data from repositories)
- `make update`

For reproducing results of a specific edition of the competition, please checkout the tag for that edition.

The following sections assume that the working directory is the same as used in the above commands.

## Executing a Benchmark for a Particular Tool

Assume that we would like to reproduce results for the tool `CPAchecker`,
including results validation.
This can be achieved using the following command:

`scripts/execute-runs/mkRunVerify.sh cpachecker`

The above command executes the verification runs with tool `CPAchecker`, and
afterwards all result validators that are declared in `benchmark-defs/category-structure.yml`.

## Executing Only Verification Runs

If we would like to execute only verification runs, then we can use the following command:

```
scripts/execute-runs/execute-runcollection.sh \
    benchexec/bin/benchexec \
    archives/2022/cpachecker.zip \
    benchmark-defs/cpachecker.xml \
    witness.graphml \
    .graphml \
    results-verified/
```

The parameters specify the:
- benchmarking utility (BenchExec) to be used to run the benchmark,
- tool archive,
- benchmark definition,
- name of the witness files, to which the unification script links the witness produced by the tool,
- pattern using which the unification script searches for produced witnesses,
- the directory in which the results shall be stored, and
- (optional) parameters to be passed to the benchmarking utility.

For quick tests and sanity checks, BenchExec can be told to restrict the execution to a certain test-set.
For example, to restrict the execution to the sub-category `ReachSafety-ControlFlow`,
you add an extra parameter `-t ReachSafety-ControlFlow` that is passed to the benchmarking utility.

Furthermore, BenchExec can be told to overwrite limit from the benchmark definitions (which should be used only for test executions).
To see if a tool generally works and produces outputs, you could use (assuming we use a machine with 8 cores and 30 GB of RAM)
the additional parameters `--timelimit 60 --memorylimit 3GB --limitCores 1 --numOfThreads 8` to
- limit the CPU time to `60 s`,
- limit the memory to `3 GB`,
- limit the number of cores to `1`, and
- set the number of runs executed in parallel to `8`.

It is important to execute the tools (when running experiments) in a container.
Since we use BenchExec, this is done automatically.
In order to protect our file system and to give proper write access to the tool inside the container,
we add the setup of the overlay filesystem using the parameters
- `--read-only-dir /` to make sure the tool we execute does not write at unexpected places,
- `--overlay-dir /home/` to let BenchExec setup a directory for the tool inside the container, and
- `--overlay-dir ./` to give permission to write to the working directory.

A complete command line would look as follows:

```
scripts/execute-runs/execute-runcollection.sh \
    benchexec/bin/benchexec \
    archives/2022/cpachecker.zip \
    benchmark-defs/cpachecker.xml \
    witness.graphml \
    .graphml \
    results-verified/ \
    -t ReachSafety-ControlFlow \
    --timelimit 60 --memorylimit 3GB --limitCores 1 --numOfThreads 8 \
    --read-only-dir / --overlay-dir /home/ --overlay-dir ./
```

**Note:** If you execute [CoVeriTeam](https://gitlab.com/sosy-lab/software/coveriteam/)-based tools, or other tools that use CGroups, then the following additional parameter is necessary:
`--full-access-dir /sys/fs/cgroup`.

### Executing Only Validation Runs (Incl. Witness Linter)

The above executions produce results (witnesses) in a results directory similar to `cpachecker.2021-12-03_10-39-40.files/`
inside the output directory `results-verified/`.

The benchmark definition for validation must be updated with this results directory:
The string `results-verified/LOGDIR/` must be replaced by the string `results-verified/cpachecker.2021-12-03_10-39-40.files/`

Suppose we would like to run result validation for violation results with CPAchecker.
We would make a copy of `cpachecker-validate-violation-witnesses.xml` to `cpachecker-validate-violation-witnesses-cpachecker.xml`
and replace the string as mentioned above there. The we can run:

```
scripts/execute-runs/execute-runcollection.sh \
    benchexec/bin/benchexec \
    archives/2022/val_cpachecker.zip \
    benchmark-defs/cpachecker-validate-violation-witnesses-cpachecker.xml \
    witness.graphml \
    .graphml \
    ../../results-validated/ \
    -t ReachSafety-ControlFlow \
    --memorylimit 3GB --limitCores 1 --numOfThreads 8 \
    --read-only-dir / --overlay-dir /home/ --overlay-dir ./
```

Suppose we would like to run the witness linter to check that the witnesses are syntactically valid.
We would make a copy of `witnesslint-validate-witnesses.xml` to `witnesslint-validate-witnesses-cpachecker.xml`
and replace the string as mentioned above there. Then we can run:

```
scripts/execute-runs/execute-runcollection.sh \
    benchexec/bin/benchexec \
    archives/2022/val_witnesslint.zip \
    benchmark-defs/witnesslint-validate-witnesses-cpachecker.xml \
    witness.graphml \
    .graphml \
    results-validated/ \
    -t ReachSafety-ControlFlow \
    --read-only-dir / --overlay-dir /home/ --overlay-dir ./
```


### Detailed Execution of Tools

In the following we explain some of the steps that the script `scripts/execute-runs/execute-runcollection.sh` normally performs for us.

### Unpack a Tool

The following command unpacks the tool `CPAchecker`:
- `mkdir bin/cpachecker-32KkXQ0CzM`
- `scripts/execute-runs/mkInstall.sh archives/2022/cpachecker.zip bin/cpachecker-32KkXQ0CzM`

### Assemble Provenance Information for a Tool

The following command prints information about the repositories and their versions:
- `scripts/execute-runs/mkProvenanceInfo.sh archives/2022/cpachecker.zip`

### Execute a Benchmark for a Tool

- `cd bin/cpachecker-32KkXQ0CzM`
- `../../benchexec/bin/benchexec ../../benchmark-defs/cpachecker.xml --outputpath ../../results-verified/ -t ReachSafety-ControlFlow`

### Initialize Result Files (for Validation Runs and Reproduction)

The script `scripts/execute-runs/execute-runcollection.sh` also performs some post-processing steps to:
- create a mapping from files to SHA hashes (for output files like witnesses, and for input files like programs, specifications, and task definitions) and
- create a symbolic link at a uniform location of the result files (in order to be able to feed the results as input to validation runs).


# Computing Environment on Competition Machines

## Docker Image

SV-COMP provides a Docker image that tries to provide an environment
that has mostly the same packages installed as the competition machines.
The Docker image is described here:
https://gitlab.com/sosy-lab/benchmarking/competition-scripts/#docker-image

## Parameters of RunExec

The parameters that are passed to the [BenchExec](https://github.com/sosy-lab/benchexec) [1]
executor [runexec](https://github.com/sosy-lab/benchexec/blob/main/doc/runexec.md) on the competition machines
are described here:
https://gitlab.com/sosy-lab/benchmarking/competition-scripts/#parameters-of-runexec


# References

[1]: Dirk Beyer, Stefan Löwe, and Philipp Wendler.
     Reliable Benchmarking: Requirements and Solutions.
     International Journal on Software Tools for Technology Transfer (STTT), 21(1):1-29, 2019.
     https://doi.org/10.1007/s10009-017-0469-y



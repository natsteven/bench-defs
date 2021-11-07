# SV-COMP Reproducibility
This repository describes the configuration of the competition machines (below)
and the benchmark definition for each verifier (folder benchmark-defs),
in order to make results of the competition reproducible.


## Setup
The following steps setup the benchmarking environment:
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


## Ubuntu Packages on Apollon Machines

### Docker Image
SV-COMP provides a Docker image that tries to provide an environment
that has mostly the same packages installed as the competition machines:
- Docker definition: https://gitlab.com/sosy-lab/benchmarking/competition-scripts/-/blob/main/test/Dockerfile.user.2022
- Docker image: `registry.gitlab.com/sosy-lab/benchmarking/competition-scripts/user:latest`
- Test if the tool works with the installation:
  - Unzip tool archive to temporary directory `<TOOL>` (**`<TOOL>` must be an absolute path!**)
  - `docker pull registry.gitlab.com/sosy-lab/benchmarking/competition-scripts/user:latest`
  - `docker run --rm -i -t --volume=<TOOL>:/tool --workdir=/tool registry.gitlab.com/sosy-lab/benchmarking/competition-scripts/user:latest bash`
  - Start tool


## Parameters of RunExec for SV-COMP
<!-- Fetch latest version from the Ansible configuration for the competition machines:
https://gitlab.com/sosy-lab/admin/sysadmin/ansible/-/blob/master/roles/vcloud-worker/templates/Config.j2
Last synchronized: 2020-12-05 from commit 670c4eb
-->

```
--container
--read-only-dir /
--hidden-dir /home
--hidden-dir /var/lib/cloudy # environment-specific
--set-cgroup-value pids.max=5000
--output-directory <work-dir>
--overlay-dir <run-dir>
--debug
--maxOutputSize 2MB
--dir <work-dir>
--output <logfile>
--timelimit <depends on benchmark XML>
--softtimelimit 900s # only if specified in benchmark XML
--memlimit 15GB
--memoryNodes 0 # hardware-specific
--cores 0-7 # hardware-specific
```



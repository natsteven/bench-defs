<?php require('../template.php');
pageHeader(); ?>

<!--
  Changes to this file should be commited also to
  https://gitlab.com/sosy-lab/sv-comp/bench-defs/-/blob/main/doc/rules.php
-->

<style type="text/css">
  table,
  td {
    border: 1px solid grey;
  }
</style>

<h2>Call for Participation &mdash; Procedure</h2>

<p>
  The competition will compare state-of-the-art tools for
  software verification with respect to effectiveness and efficiency.
  The competition consists of two phases:
  a training phase, in which benchmark programs are given to the tool developers,
  and an evaluation phase, in which all participating verifiers will be executed on
  benchmark verification tasks, and the number of solved instances as well as the runtime is measured.
  The competition is performed and presented by the TACAS Competition Chair.
</p>

<p>
  This page is the authoritative reference for the rules of the competition.
  Please also read the <a href="../faq.txt">FAQ with more descriptions</a>.
</p>

<!--
<h3>Quick Overview of Changes to Previous SV-COMP</h3>

We made some changes to the rules, compared to last year,
as a reaction to the community feedback after the first competition
(cf. the <a href="Minutes-2012.txt">minutes of the community meeting and jury meeting</a>).

<ul>
<li>  We have added several new categories and many new verification tasks
      that were contributed by the community (tops 1 and 12 in minutes).
</li>
<li>  New benchmark programs do not need to be pre-processed with CIL (top 5 in minutes).
      (If a verifier requires such a pre-processing, it needs to be done inside the verifier itself.)
</li>
<li>  The negative scores are doubled, i.e., the scoring schema is updated as follows: UNSAFE incorrect = -4 and SAFE incorrect = -8 (top 15 in minutes).
</li>
<li>  The participating tools are required to be publicly available on the internet (top B).
</li>
<li>  There is one global set of parameters with impact on the performance (top 18 in minutes).
</li>
<li>  The operating system on the competition machines is Ubuntu 12.04.
      (If a verifier is submitted as source code, it needs to build on that environment.)
</li>
<li>  There is a new convention '__VERIFIER_atomic_' for modeling atomic execution.
</li>
<li>  There is a newly introduced category 'MemorySafety' with new result types for the verification tasks.
</li>
<li>  We are moving from verification results SAFE and UNSAFE to TRUE and FALSE, respectively,
      in order to be prepared for properties that are not safety properties in the future.
</li>
</ul>
-->

<h3>Publication and Presentation of the Competition Candidates</h3>

<p>
  The jury selects qualified competition candidates to publish (in the LNCS proceedings of TACAS)
  a contribution paper that gives an overview of the competition candidate.
  An overview paper by the competition organizers will describe the competition procedure
  and present the results.
  The paper-submission deadline is specified on the <a href="dates.php">dates</a> page;
  submission requirements are explained on the <a href="submission.php">submission page</a>.
</p>

<p>
  In addition, every qualified competition candidate is granted a demonstration slot
  in the TACAS program to present the competition candidate to the TACAS audience.
</p>


<h3>Definition of Verification Task</h3>

<p>
  A <i>verification task</i> consists of a C program and a specification.
  A <i>verification run</i> is a non-interactive execution of a competition candidate on a single verification task,
  in order to check if the following statement is correct:
  <i>"The program satisfies the specification."</i>
</p>

<p>
  The <i>result</i> of a verification run is a triple (ANSWER, WITNESS, TIME).
  ANSWER is one of the following outcomes:
</p>
<table>
  <tbody>
    <tr>
      <td><span style="color:green">TRUE</span> + Witness</td>
      <td>The specification is satisfied
        (i.e., there is no path that violates the specification)
        and a correctness witness is produced.
      </td>
    </tr>
    <tr>
      <td><span style="color:green">FALSE</span> + Witness</td>
      <td>The specification is violated
        (i.e., there exists a path that violates the specification)
        and a violation witness is produced.
      </td>
    </tr>
    <tr>
      <td><span style="color:orange">UNKNOWN</span></td>
      <td>The tool cannot decide the problem or terminates
        by a tool crash, time-out, or out-of-memory
        (i.e., the competition candidate does not succeed in computing an
        answer <span style="color:green">TRUE</span> or <span style="color:green">FALSE</span>).
      </td>
    </tr>
  </tbody>
</table>


<p>
  TIME is the consumed CPU time until the verifier terminates.
  It includes the consumed CPU time of all processes that the verifier starts.
  If TIME is equal to or larger than the time limit, then the verifier
  is terminated and the ANSWER is set to "timeout" (and interpreted as UNKNOWN).
</p>

<p>
  The C programs are partitioned into categories,
  which are defined in category-set files.
  The categories and the contained programs
  are explained on the <a href="benchmarks.php">benchmark page</a>.
  The requirements for the C programs are described <a href="#programs">below in the next section</a>.
</p>


<h4>Witnesses</h4>
<p>
  There is a <a href="https://github.com/sosy-lab/sv-witnesses/">fixed exchange format for the witnesses</a>.
  The witness has to be written to a file
  <span style="font-family: monospace;">witness.graphml</span> or <span style="font-family: monospace;">witness.yml</span>,
  which is given to a witness validator to check validity.
  The result is counted as correct only if at least one validator successfully validated it.
  Verifiers may output both a GraphML (version 1) and a YAML (version 2.0) witness,
  in which case it suffices that one of these two witnesses is validated.
  The resource limits for the witness validators are 2 processing units, 7 GB memory, and
  10 % of the verification time (i.e., 90 s) for violation witnesses
  and 100 % (900 s) for correctness witnesses.
</p>

<p>
  The category *NoDataRace* is excluded from validation of violation witnesses.
  The following categories are excluded from validation of correctness witnesses:
  *-Arrays, *-Floats, *-Heap, *MemSafety*, ConcurrencySafety-*, *NoDataRace*, and Termination-*.
</p>

<h4>Properties</h4>

<p>
  The specification to be verified for a program "path/filename" is given either
  in a file with the name "path/filename.prp"
  or in a file "Category.prp".
</p>

<p>
  The definition 'init(main())' gives the initial states of the program by a call of function 'main' (with no parameters).
  The definition 'LTL(f)' specifies that formula 'f' holds at every initial state of the program.
  The LTL (linear-time temporal logic) operator 'G f' means that 'f' globally holds.
  The proposition 'label(L)' is true if the C label 'L' is reached.
  The proposition 'call(func())' is true if the function 'func()' is called.
</p>


<h5>Unreachability of Error Function:</h5>
<p>
<pre><?php echo "CHECK( init(main()), LTL(G ! call(func())) )"; ?>
    </pre>
</p>

<table>
  <thead>
    <tr>
      <td>Formula</td>
      <td>Definition</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="width: 150px;">
        <pre>G ! call(func())</pre>
      </td>
      <td>
        The function 'func' is not called in any finite execution of the program.
      </td>
    </tr>
  </tbody>
</table>


<h5>Memory Safety (only for category 'MemorySafety'):</h5>
<p>
<pre><?php echo trim(file_get_contents("https://sv-comp.sosy-lab.org/2023/results/sv-benchmarks/c/properties/valid-memsafety.prp")); ?>
    </pre>
</p>

<table>
  <thead>
    <tr>
      <td>Formula</td>
      <td>Definition</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="width: 150px;">G valid-free</td>
      <td>All memory deallocations are valid (counterexample: invalid free).
        More precisely: There exists no finite execution of the program
        on which an invalid memory deallocation occurs.
      </td>
    </tr>
    <tr>
      <td>G valid-deref</td>
      <td>All pointer dereferences are valid (counterexample: invalid dereference).
        More precisely: There exists no finite execution of the program
        on which an invalid pointer dereference occurs.
      </td>
    </tr>
    <tr>
      <td>G valid-memtrack</td>
      <td>All allocated memory is tracked, i.e., pointed to or deallocated (counterexample: memory leak).
        More precisely: There exists no finite execution of the program
        on which the program lost track of some previously allocated memory.
        (Comparison to Valgrind: This property is violated if Valgrind reports 'definitely lost'.)
      </td>
    </tr>
  </tbody>
</table>

<p>
  If a verification run detects that a property from {valid-free, valid-deref, valid-memtrack} is violated,
  then the violated (partial) property has to be given in the result:
</p>
<p>
  <span style="color:green">FALSE(<i>p</i>)</span>,
  with p in {valid-free, valid-deref, valid-memtrack}, means that the (partial) property p is violated.
</p>

<p>
  <i>Agreement:</i>
  All programs in category 'MemorySafety' either satisfy all (partial) properties
  or violate exactly one (partial) property p (p in {valid-free, valid-deref, valid-memtrack})
  that is reachable from the program entry.
  We ignore further property violations on a path after finding the first violation
  (because the behavior is undefined after the first violation).
</p>


<h5>Memory Cleanup (only for category 'MemorySafety'):</h5>
<p>
<pre><?php echo trim(file_get_contents("https://sv-comp.sosy-lab.org/2023/results/sv-benchmarks/c/properties/valid-memcleanup.prp")); ?>
    </pre>
</p>

<table>
  <thead>
    <tr>
      <td>Formula</td>
      <td>Definition</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="width: 150px;">
        <pre>G valid-memcleanup</pre>
      </td>
      <td>All allocated memory is deallocated before the program terminates.
        In addition to valid-memtrack: There exists no finite execution of the program on which the program terminates
        but still points to allocated memory.
        (Comparison to Valgrind: This property can be violated even if Valgrind reports 'still reachable'.)
      </td>
    </tr>
  </tbody>
</table>


<h5>No Overflow:</h5>
<p>
<pre><?php echo trim(file_get_contents("https://sv-comp.sosy-lab.org/2023/results/sv-benchmarks/c/properties/no-overflow.prp")); ?>
    </pre>
</p>

<table>
  <thead>
    <tr>
      <td>Formula</td>
      <td>Definition</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="width: 150px;">
        <pre> G ! overflow</pre>
      </td>
      <td>
        It can never happen that the resulting type of an operation
        is a signed integer type but the resulting value is not in the
        range of values that are representable by that type.
      </td>
    </tr>
  </tbody>
</table>



<h5>No Data Race (only for category 'ConcurrencySafety'):</h5>
<p>
<pre><?php echo trim(file_get_contents("https://sv-comp.sosy-lab.org/2023/results/sv-benchmarks/c/properties/no-data-race.prp")); ?>
    </pre>
</p>

<table>
  <thead>
    <tr>
      <td>Formula</td>
      <td>Definition</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="width: 150px;">
        <pre> G ! data-race</pre>
      </td>
      <td>
        If there exist two or more concurrent accesses to the same memory location and at least one is a write access,
        then all accesses must be atomic.
      </td>
    </tr>
  </tbody>
</table>



<h5>Termination (only for category 'Termination'):</h5>
<p>
<pre><?php echo trim(file_get_contents("https://sv-comp.sosy-lab.org/2023/results/sv-benchmarks/c/properties/termination.prp")); ?>
    </pre>
</p>

<table>
  <thead>
    <tr>
      <td>Formula</td>
      <td>Definition</td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="width: 150px;">
        <pre> F end</pre>
      </td>
      <td>
        Every path finally reaches the end of the program.
        The proposition "end" is true at the end of every finite program execution (exit, abort, return from the initial call of main, etc.).
        A counterexample for this property is an infinite program execution.
      </td>
    </tr>
  </tbody>
</table>

<p>
  Termination is a liveness property, and thus, counterexamples must have maximal execution length, i.e., are terminating or infinite; all terminating paths end with proposition "end".
</p>

<p>
  For the result "<span style="color:green">FALSE</span> + Error Path", the
  error path has to be a finite path that ends in a location that is repeated infinitely often in an infinite path.
  If there is a periodic infinite execution, a second path may be added.
  This second path has to lead from this infinitely repeated location
  back to this infinitely repeated location and this path has to correspond to the infinite execution.




<h3 id="programs">Benchmark Verification Tasks</h3>


<p>
  The training verification tasks will be provided by a <a href="dates.php">specified date</a> on <a href="benchmarks.php">this web page</a>.
  A subset of the training verification tasks will be used for the actual competition experiments.
  The jury agrees on a procedure to define the subset (if not all verification tasks are taken).
  We avoid using verification tasks without training, because the semantics of the language C is underspecified;
  the participants, jury, and community ensure that all involved parties agree on the intended meaning of the verification tasks.
  (This is in contrast to competitions over completely specified problems, such as SAT or SMT.)
</p>

<p>
  The programs are assumed to be written in GNU C (some of them adhere to ANSI C).
  Each program contains all code that is needed for the verification, i.e., no includes (cpp -P).
  Some programs are provided in CIL (C Intermediate Language) <a href="http://www.eecs.berkeley.edu/~necula/cil/">[1]</a>.
</p>

<p>
  Potential competition participants are invited to submit benchmark verification tasks until the specified date.
  Verification tasks have to fulfill two requirements, to be eligible for the competition:
  (1) the program has to be written in GNU C or ANSI C, and
  (2) the program has to come with a specification given by one of the properties stated above.
  Other specifications are possible, but need to be proposed and discussed.
</p>

<p>
  New proposed categories will be included if at least three different tools or teams participate in the category
  (i.e., not the same tool twice with a different configuration).
</p>

<p>
  For each category, we specify whether the programs are written for
  an <strong>ILP32 (32-bit)</strong> or an <strong>LP64 (64-bit)</strong> architecture
  (cf. <a href="http://www.unix.org/whitepapers/64bit.html">http://www.unix.org/whitepapers/64bit.html</a>).
</p>

<p>
  In the following, we list a few conventions that are used in some of the verification tasks,
  in order to express special information that is difficult to capture with the C language.
</p>

<p>
  <span style="text-decoration: line-through;">
    <strong>__VERIFIER_error(): </strong>
    For checking (un)reachability we use the function <tt>__VERIFIER_error()</tt>.
    The verification tool can assume the following implementation:<br>
    <tt>void __VERIFIER_error() { abort(); }</tt><br>
    Hence, a function call <tt>__VERIFIER_error()</tt> never returns and in the function <tt>__VERIFIER_error()</tt> the program terminates.
  </span>
</p>

<p>
  <span style="text-decoration: line-through;">
    <strong>__VERIFIER_assume(expression): </strong>
    A verification tool can assume that a function call
    <tt>__VERIFIER_assume(expression)</tt>
    has the following meaning: If 'expression' is evaluated to '0', then the function loops forever,
    otherwise the function returns (no side effects).
    The verification tool can assume the following implementation:<br />
    <tt>void __VERIFIER_assume(int expression) { if (!expression) { LOOP: goto LOOP; }; return; }</tt>
  </span>
</p>

<p>
  <strong>__VERIFIER_nondet_X(): </strong>
  In order to model nondeterministic values, the following functions can be assumed to return
  an arbitrary value of the indicated type:
  <tt>__VERIFIER_nondet_X()</tt>
  with <tt>X</tt> in {<tt>bool</tt>, <tt>char</tt>, <tt>int</tt>, <tt>int128</tt>, <tt>float</tt>, <tt>double</tt>, <tt>loff_t</tt>, <tt>long</tt>, <tt>longlong</tt>,
  <tt>pchar</tt>, <tt>pthread_t</tt>, <tt>sector_t</tt>, <tt>short</tt>, <tt>size_t</tt>, <tt>u32</tt>,
  <tt>uchar</tt>, <tt>uint</tt>, <tt>uint128</tt>, <tt>ulong</tt>, <tt>ulonglong</tt>, <tt>unsigned</tt>, <tt>ushort</tt>}
  (no side effects, <tt>pointer</tt> for <tt>void *</tt>, etc.).
  The verification tool can assume that the functions are implemented according to the following template:<br />
  <tt>X __VERIFIER_nondet_X() { X val; return val; }</tt>
</p>

<p>
  <strong>__VERIFIER_atomic_begin(): </strong>
  For modeling an atomic execution of a sequence of statements in a multi-threaded run-time environment,
  those statements can be placed between two function calls <tt>__VERIFIER_atomic_begin()</tt> and <tt>__VERIFIER_atomic_end()</tt>
  (deprecated but still valid: those statements can be placed in a function whose name matches <tt>__VERIFIER_atomic_</tt>).
  The verifiers are instructed to assume that the execution between those calls is not interrupted.
  The two calls need to occur within the same control-flow block; nesting or interleaving of those function calls is not allowed.
</p>

<p>
  <strong>malloc(), free(): </strong>
  We assume that the functions <tt>malloc</tt> and <tt>alloca</tt> always return
  a valid pointer, i.e., the memory allocation never fails,
  and function <tt>free</tt> always deallocates the memory and
  makes the pointer invalid for further dereferences.
</p>

<h3 id="setup">Competition Environment and Requirements</h3>

<h4>Competition Environment</h4>

<p>
  Each verification run will be started on a machine with
  a GNU/Linux operating system (x86_64-linux, Ubuntu 22.04);
  there are three resource limits for each verification run:
  a memory limit of 15 GB (14.6 GiB) of RAM,
  a runtime limit of 15 min of CPU time,
  and a limit to 4 processing units of a CPU.
  If a verifier hangs, i.e., does not continue to consume CPU time, then the verifier is killed after 15 min of wall time,
  and the resulting runtime is set to 15 min.
</p>


<h4 id="parameters">Benchmark Definition</h4>

<p>
  The competition environment feeds the candidates with parameters when started for a verification run.
  There is one global set of parameters that can be used to tune the verifier to the benchmark programs.
  Verifiers are forbidden from using the program name, its hash, or the current category to tune their parameters.
  This set of (command-line) parameters have to be defined in the competition contribution paper
  and in the benchmark definition.
  One parameter defines the specification file.
  There are categories that need to be verified with the information that a 64-bit architecture is used,
  thus, if the verifier has a parameter to handle this aspect, it needs to be defined.
</p>

<p>
  In order to participate at SV-COMP, a benchmark definition in the SV-COMP repository is necessary.
  Technically, the benchmark definition needs to be integrated into the SV-COMP repository
  under directory <a href="https://gitlab.com/sosy-lab/sv-comp/bench-defs/tree/main/benchmark-defs">benchmark-defs</a>
  using a <a href="https://gitlab.com/sosy-lab/sv-comp/bench-defs/merge_requests">pull request</a>.
</p>

<p>
  The benchmark definition defines the categories that the verifier is to be executed on,
  the parameters to be passed, and the resource limits.
</p>

<h4>Tool-Info Module</h4>

<p>
  In order to participate at SV-COMP, a tool-info module in the BenchExec repository is necessary.
  Technically, the tool-info module needs to be integrated into the BenchExec repository
  under directory <a href="https://github.com/sosy-lab/benchexec/tree/master/benchexec/tools">benchexec/tools</a>
  using a <a href="https://github.com/sosy-lab/benchexec/pulls">pull request</a>.
</p>

<p>
  The task of the tool-info module is (besides other tasks) to translate the output of a verifier to the results FALSE, TRUE, etc.
  For running the contributed verifier, the organizer follows the installation requirements and executes
  the verifier, relying on the tool-info module for correct execution of the verifier and correct interpretation of its results.
</p>

<p>
  The tool-info module must be tested, cf.
  <a href="https://github.com/sosy-lab/benchexec/blob/main/doc/tool-integration.md#testing-the-tool-integration">instructions on tool integration</a>.
</p>



<h4 id="verifier">Verifier</h4>

The submitted system has to meet the following requirements:
<ul>
  <li>The verifier is publicly available for download and the license
    (i) allows reproduction and evaluation by anybody,
    (ii) does not place any restriction on the verifier output (log files, witnesses), and
    (iii) allows (re-) distribution of the unmodified verifier archive
    for reproduction via the archives repository and Zenodo.
  </li>
  <li>The archive of the verifier contains a LICENSE file that satisfies the above requirements.
  </li>
  <li>The archive of the verifier contains a README file that describes the contents.
  </li>
  <li>The verifier is archived in a ZIP file (.zip), which contains exactly one directory (no tarbomb).
  </li>
  <li>The verifier does not exceed an stdout/stderr limit of at most 2 MB.
  </li>
  <li>The verifier has an option to report its version.
  </li>
  <li>The archive does not contain large amounts of unnecessary data, such as
    repository data (.svn), source files, aux folders like __MACOSX, and test files.
  </li>
  <li>The verifier should not require any special software on the competition machines; all necessary libraries and external tools
    should be contained in the archive. Standard packages that are available as Ubuntu packages can be requested.
  </li>
</ul>




<h3 id="qualification">Qualification</h3>

<p>
  <b>Verifier.</b> A verification tool is qualified to participate as competition candidate if the tool is
  (a) publicly available for download and fulfills the <a href="#verifier">above license requirements</a>,
  (b) works on the GNU/Linux platform (more specifically, it must run on an x86_64 machine with the latest Ubuntu LTS),
  (c) is installable with user privileges (no root access required, except for required standard Ubuntu packages)
  and without hard-coded absolute paths for access to libraries and non-standard external tools,
  (d) succeeds for more than 50 % of all training programs to parse the input and
  start the verification process (a tool crash during the verification phase does not disqualify), and
  (e) produces witness files (for violation and correctness) that adhere to the witness exchange format (syntactically correct).
  The competition organizer can always add verifiers from previous years as participants.
</p>
<p>
  <b>Person.</b> A person (participant) is qualified as competition contributor for a competition candidate if
  the person (a) is a contributing designer/developer
  of the submitted competition candidate
  (witnessed by occurrence of the person’s name on the tool's project web page, a tool paper,
  or in the revision logs)
  or (b) is authorized by the competition organizer (after the designer/developer was contacted about the participation).
</p>
<p>
  <b>Paper.</b> A paper is qualified if the quality of the description of the competition candidate suffices to run the tool in the competition
  and meets the scientific standards of TACAS as competition-candidate representation in the TACAS proceedings.
</p>

<p>
  Note: A verification tool can participate several times as different competition candidates, if a significant difference of the
  conceptual or technological basis of the implementation is justified in the accompanying description paper.
  This applies to different versions as well as different configurations,
  in order to avoid forcing developers to create a new tool for every new concept.
</p>

<h3 id="scores">Evaluation by Scores and Runtime</h3>

<p>
  The scores will be assigned according to the following table:
</p>

<table cellpadding="10px">
  <tbody>
    <tr>
      <td>Points</td>
      <td>Reported result</td>
      <td>Description</td>
    </tr>
    <tr>
      <td>0</td>
      <td><span style="color:orange">UNKNOWN</span></td>
      <td>Failure to compute verification result, out of resources, program crash.</td>
    </tr>
    <tr>
      <td>+1</td>
      <td><span style="color:green">FALSE</span> <br />correct</td>
      <td>The error in the program was found and a violation witness was confirmed.</td>
    </tr>
    <tr>
      <td>−16</td>
      <td><span style="color:red">FALSE</span> <br />incorrect</td>
      <td>An error is reported for a program that fulfills the
        specification (false alarm, incomplete analysis).</td>
    </tr>
    <tr>
      <td>+2</td>
      <td><span style="color:green">TRUE</span> <br />correct</td>
      <td>The program was analyzed to be free of errors and a correctness witness was confirmed.</td>
    </tr>
    <tr>
      <td>−32</td>
      <td><span style="color:red">TRUE</span> <br />incorrect</td>
      <td>The program had an error but the competition candidate did not find it
        (missed bug, unsound analysis).</td>
    </tr>

  </tbody>
</table>


<p>
  The results of type <span style="color:green">TRUE</span> yield higher absolute score values
  compared to type <span style="color:red">FALSE</span>,
  because it is expected to be 'easier' to detect errors than it is to prove correctness.
  The absolute score values for incorrect results are higher compared to correct results,
  because a single correct answer should not be able to compensate for a wrong answer.
  (An imaginary competition candidate that generates random answers should be ranked with a negative sum of points.)
</p>

<p>
  The participating competition candidates are ranked according to the sum of points.
  Competition candidates with the same sum of points are sub-ranked according to success-runtime.
  The success-runtime for a competition candidate is the total CPU time over all benchmarks for which the
  competition candidate reported a correct verification result.
</p>

<p>
  The participants have the opportunity to check the verification results against their own expected results
  and discuss inconsistencies with the competition chair.
</p>

<p>
  Please review the following illustration for
  the <a href="score-schema/svcomp21_score_unreach.svg">scoring schema for the reachability property</a>.
  <a href="score-schema/svcomp21_score_unreach.svg"><img width="100%" src="score-schema/svcomp21_score_unreach.svg" /></a>
  <!--
Please review the following illustrations: 
first the <a href="score-schema/svcomp17_score_unreach.svg">scoring schema for the reachability property</a> and 
second the <a href="score-schema/svcomp17_score_all.svg">scoring schema for all properties</a>.
<a href="score-schema/svcomp17_score_unreach.svg"><img width="100%" src="score-schema/svcomp17_score_unreach.png" /></a>
<a href="score-schema/svcomp17_score_all.svg"><img width="100%" src="score-schema/svcomp17_score_all.png" /></a>
-->
</p>

<h3>Opting-out from Categories</h3>

Every team can submit for every category
(including meta categories, i.e., categories that consist of verification tasks from other categories)
an opt-out statement.
In the results table, a dash is entered for that category; no execution results are reported in that category.

If a team participates (i.e., does not opt-out), *all* verification tasks that belong to that category
are executed with the verifier.
The obtained results are reported in the results table;
the scores for meta categories are weighted according to the established procedure.
(This means, a tool can participate in a meta category and at the same time opt-out from a sub-category,
with having the real results of the tool counted towards the meta category, but not reported for the sub-category.)


<h3 id="meta">Computation of Scores for Meta Categories</h3>

<p>
  A meta category is a category that consists of several sub-categories.
  The scores in such a meta category is computed from the normalized scores in its sub-categories.
</p>

<h4>Procedure</h4>

<p>
  The score for a (meta) category
  is computed from the scores of all k contained (sub-) categories
  using a normalization by the number of contained verification tasks:
  The normalized score sn_i of a verifier in category i is obtained by dividing the score s_i by the number of tasks n_i in category i
  (sn_i = s_i / n_i),
  then the sum st = SUM(sn_1, ..., sn_k) over the normalized scores of the categories
  is multiplied by the average number of tasks per category.
</p>

<h4>Motivation</h4>

<p>
  The goal is to reduce the influence of a verification task in a large category compared to a verification task
  in a small category, and thus, balance over the categories.
  Normalizing by score is not an option because we assigned higher positive scores for TRUE
  and higher negative scores for wrong results. (Normalizing by score would remove those desired differences.)
</p>

<h4>Example</h4>

<p>
  Category 1 has 10 verification tasks with result TRUE.<br />
  Category 2 has 10 verification tasks with result FALSE.
</p>

<p>
  Verifier A correctly solved 5 verification tasks in category 1 -> 10 points, and 5 verification tasks in category 2 -> 5 points.<br />
  Overall score of Verifier A: (( (10/10 = 1) + (5/10 = 0.5) ) = 1.5) * (10 + 10 = 20) / 2 = 15
</p>

<p>
  Verifier B correctly solved 10 verification tasks in category 1 -> 20 points, and 0 verification tasks in category 2 -> 0 points.<br />
  Overall score of Verifier B: (( (20/10 = 2) + (0/10 = 0.0) ) = 2.0) * (10 + 10 = 20) / 2 = 20
</p>

<p>
  Verifier C correctly solved 0 verification tasks in category 1 -> 0 points, and 10 verification tasks in category 2 -> 10 points.<br />
  Overall score of Verifier C: (( (0/10 = 0) + (10/10 = 1.0) ) = 1.0) * (10 + 10 = 20) / 2 = 10
<p>

<p>
  Verifier D correctly solved 8 verification tasks in category 1 -> 16 points, and 8 verification tasks in category 2 -> 8 points.<br />
  Overall score of Verifier D: (( (16/10 = 1.6) + (8/10 = 0.8) ) = 2.4) * (10 + 10 = 20) / 2 = 24
</p>

<p>
  Obtained ranking:<br />
  1. Verifier D<br />
  2. Verifier B<br />
  3. Verifier A<br />
  4. Verifier C
</p>

<p>
  Verifier D was strong in all categories, and thus, won category Overall.<br />
  Verifier B was even stronger than Verifier D in one category, but performed not so well in the other category, and thus, only got second overall.
</p>

<p style="text-align: right; font-size: 70%;">
  $LastChangedDate:: 2023-11-11 14:38:43 +0100 #$
</p>

<?php pageFooter(); ?>
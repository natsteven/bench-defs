#!.venv/bin/python

import pandas as pd
import os
import sys

if len(sys.argv) != 2:
    print("Provide results directory as argument")
    exit(1)

results_dir = sys.argv[1]

solver_results = pd.read_csv(results_dir + "solver-metrics.csv")

## get the spf recorded timings etc
z3_spf = pd.read_table(results_dir + "util/spf.results.z3.csv", header=[0,1,2], index_col=0)
# astr_spf = pd.read_table(results_dir + "util/spf.results.MAS.csv", header=[0,1,2], index_col=0)

## make them nice for merging
def process_spf_results(spf_df, solver_name):
    spf_df.columns = spf_df.columns.droplevel([0,1])
    spf_df.index = spf_df.index.map(os.path.basename)
    spf_df.index.name = 'bench'
    spf_df = spf_df.reset_index()
    spf_df['bench'] = spf_df['bench'].str.replace('.yml','', regex=False)
    spf_df[f'spf-{solver_name}-cputime (s)'] = spf_df['cputime (s)'].round(2)
    spf_df[f'spf-{solver_name}-walltime (s)'] = spf_df['walltime (s)'].round(2)
    spf_df = spf_df.rename(columns={'status': f'{solver_name}-status'})
    return spf_df[['bench', f'spf-{solver_name}-cputime (s)', f'spf-{solver_name}-walltime (s)', f'{solver_name}-status']]

z3_spf = process_spf_results(z3_spf, 'z3')
astr_spf = process_spf_results(astr_spf, 'astr')

results = pd.merge(solver_results, astr_spf, on="bench", how="left")
results = pd.merge(results, z3_spf, on="bench", how="left")

## get metadata
def get_log(bench_name, solver_name):
    log_path = f"{results_dir}spf.logfiles/String-{solver_name}.{bench_name}.yml.log"

    if os.path.exists(log_path):
        return os.path.abspath(log_path)
    else:
        return None

def get_bench(bench_name):
    for root, dirs, files in os.walk("sv-benchmarks/java"):
        if os.path.basename(root) == bench_name and "Main.java" in files:
            ret=os.path.join(root, "Main.java")
            return os.path.abspath(ret)
    return None

# def get_source(bench_name):
#     for root, dirs, files, in os.walk("/home/nat/Repos/good-programs"):
#         if f"{bench_name}.java" in files:
#             ret = os.path.join(root, f"{bench_name}.java")

results[['astr-log', 'z3-log', 'benchmark']] = results['bench'].apply(lambda x: pd.Series([get_log(x, 'MAS'), get_log(x, 'z3'), get_bench(x)]))

results.to_csv(results_dir + "combined_results.csv", index=False)

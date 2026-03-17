import pandas as pd
import os

Z3_RESULTS_DIR = "results-verified/spf-z3"
ASTR_RESULTS_DIR = "results-verified/spf-astr"

df_z3 = pd.read_csv(os.path.join(Z3_RESULTS_DIR, "solver-info.csv"))
df_astr = pd.read_csv(os.path.join(ASTR_RESULTS_DIR, "solver-info.csv"))
df = pd.merge(df_astr, df_z3, on="bench", how="outer")
df = df[df["Z3-calls"] > 0]
df = df.astype(int, errors='ignore')
print(df.head())

z3_spf_results = pd.read_table(os.path.join(Z3_RESULTS_DIR, "spf.results.csv"), header=[0,1,2], index_col=0)
astr_spf_results = pd.read_table(os.path.join(ASTR_RESULTS_DIR, "spf.results.csv"), header=[0,1,2], index_col=0)
z3_spf_results.columns = z3_spf_results.columns.droplevel([0,1])
astr_spf_results.columns = astr_spf_results.columns.droplevel([0,1])
z3_spf_results.index = z3_spf_results.index.map(os.path.basename)
z3_spf_results.index.name = 'bench'
z3_spf_results = z3_spf_results.reset_index()
z3_spf_results['bench'] = z3_spf_results['bench'].str.replace('.yml','', regex=False)
astr_spf_results.index = astr_spf_results.index.map(os.path.basename)
astr_spf_results.index.name = 'bench'
astr_spf_results = astr_spf_results.reset_index()
astr_spf_results['bench'] = astr_spf_results['bench'].str.replace('.yml','', regex=False)
z3_spf_results['cputime (s)'] = z3_spf_results['cputime (s)'].round(2)
z3_spf_results['walltime (s)'] = z3_spf_results['walltime (s)'].round(2)
z3_spf_results = z3_spf_results.rename(columns={'cputime (s)':'spf-z3-cputime (s)','walltime (s)':'spf-z3-walltime (s)','status':'z3-status'})
astr_spf_results['cputime (s)'] = astr_spf_results['cputime (s)'].round(2)
astr_spf_results['walltime (s)'] = astr_spf_results['walltime (s)'].round(2)
astr_spf_results = astr_spf_results.rename(columns={'cputime (s)':'spf-astr-cputime (s)','walltime (s)':'spf-astr-walltime (s)','status':'astr-status'})
z3_spf_results = z3_spf_results[['bench','spf-z3-cputime (s)','spf-z3-walltime (s)','z3-status']]
astr_spf_results = astr_spf_results[['bench','spf-astr-cputime (s)','spf-astr-walltime (s)','astr-status']]

df = pd.merge(df, z3_spf_results, on="bench", how="left")
df = pd.merge(df, astr_spf_results, on="bench", how="left")

df.to_csv("processed_results.csv", index=False)

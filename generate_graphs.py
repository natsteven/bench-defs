#!.venv/bin/python

import matplotlib.pyplot as plt
import sys
import pandas as pd

# if len(sys.argv) != 2:
#     print("Provide results directory as argument")
#     exit(1)

# results_dir = sys.argv[1]
run_1 = "results/03-20_15-39.results/"
run_2 = "results/03-20_16-02.results/"
run_3 = "results/03-20_16-28.results/"

results_1 = pd.read_csv(run_1 + "combined_results.csv")
results_2 = pd.read_csv(run_2 + "combined_results.csv")
results_3 = pd.read_csv(run_3 + "combined_results.csv")
results = pd.DataFrame()
results['astr-time (ms)'] = (results_1['astr-time (ms)'] + results_2['astr-time (ms)'] + results_3['astr-time (ms)']) / 3
results['z3-time (ms)'] = (results_1['z3-time (ms)'] + results_2['z3-time (ms)'] + results_3['z3-time (ms)']) / 3
results['z3-status'] = results_1['z3-status']
results['astr-status'] = results_1['astr-status']

mask_astr = ~results['astr-status'].isin(['true', 'false'])
mask_z3 = ~results['z3-status'].isin(['true', 'false'])
results.loc[mask_astr, 'astr-time (ms)'] = None
results.loc[mask_z3, 'z3-time (ms)'] = None
sorted = results.sort_values(by='z3-time (ms)', ascending=True).reset_index(drop=True)

# z3_times = results['z3-time (ms)']
# astr_times = results['astr-time (ms)']

plt.rcParams.update({
    'font.size': 11,
    'axes.labelsize': 12,
    'axes.titlesize': 13,
    'xtick.labelsize': 11,
    'ytick.labelsize': 11,
    'legend.fontsize': 10,
    'figure.titlesize': 13,
    'axes.linewidth': 1.2,
    'grid.linewidth': 0.7,
    'lines.linewidth': 1.5,
    'text.usetex': True,
    'font.family': 'serif'
})

# fig, ax = plt.subplots(figsize=(10, 6))
#
# solvers = {
#     'astr': 'astr-time (ms)',
#     'z3': 'z3-time (ms)'
# }
#
# for solver, time_col in solvers.items():
#     solver_times = sorted[time_col].dropna().reset_index(drop=True)
#         # [(results['z3-status'] == 'true') | 
#         # (results['z3-status'] == 'false')]
#     # [time_col].dropna().reset_index(drop=True)
#
#     solver_times = solver_times/1000
#     cum_times = solver_times.cumsum()
#
#     x = range(1, len(cum_times) + 1)
#
#     ax.plot(x, cum_times, label=solver, linewidth=2)
#
# ax.set_xlabel('Benchmarks completed')
# ax.set_ylabel('Cumulative time (s)')
# ax.set_title('Comparison on Benchmarks')
# ax.legend()
# ax.grid(True, alpha=0.3)
# plt.tight_layout()
# plt.savefig('plots/cactus-plot-spf.png', dpi=300)
#
# ## cactus plot accumatling every call to the solver during testing
all_calls = pd.read_csv(run_1 + "solver-call-times.csv")
# all_sorted = all_calls.sort_values(by='z3-time (ms)', ascending=True).reset_index(drop=True)
# fig, ax = plt.subplots(figsize=(10, 6))
#
# for solver, time_col in solvers.items():
#     solver_calls = all_sorted[time_col].dropna().reset_index(drop=True)
#     solver_calls = solver_calls/1000
#     cum_calls = solver_calls.cumsum()
#
#     x = range(1, len(cum_calls) + 1)
#
#     ax.plot(x, cum_calls, label=solver, linewidth=2)
#
# ax.set_xlabel('Calls to Solver')
# ax.set_ylabel('Cumulative time (s)')
# ax.set_title('Comparison across all calls')
# ax.legend()
# ax.grid(True, alpha=0.3)
# plt.tight_layout()
# plt.savefig('plots/cactus-plot-calls.png', dpi=300)
# plt.show()
#

pairwise = results.dropna(subset=['z3-time (ms)', 'astr-time (ms)'])
y = pairwise['z3-time (ms)']
x = pairwise['astr-time (ms)']

fig, ax = plt.subplots(figsize=(7, 7))
ax.scatter(x, y, alpha=0.6, label='Benchmarks')

min_val = min(x.min(), y.min())
max_val = max(x.max(), y.max())
ax.plot([min_val, max_val], [min_val, max_val], 'k--', label='x = y')

ax.set_xlabel('astr-time (ms)')
ax.set_ylabel('z3-time (ms)')
ax.set_title('Pairwise per bench Z3 vs ASTR')
ax.legend()
ax.grid(True, alpha=0.3)
plt.tight_layout()
plt.savefig('plots/pairwise-bench.png', dpi=300)
# plt.show()

pairwise_all = all_calls.dropna(subset=['z3-time (ms)', 'astr-time (ms)'])
y = pairwise_all['z3-time (ms)']
x = pairwise_all['astr-time (ms)']

fig, ax = plt.subplots(figsize=(7, 7))
ax.scatter(x, y, alpha=0.6, label='Benchmarks')

min_val = min(x.min(), y.min())
max_val = max(x.max(), y.max())
ax.plot([min_val, max_val], [min_val, max_val], 'k--', label='x = y')

ax.set_xlabel('astr-time (ms)')
ax.set_ylabel('z3-time (ms)')
ax.set_title('Pairwise Call Z3 vs ASTR')
ax.legend()
ax.grid(True, alpha=0.3)
plt.tight_layout()
plt.savefig('plots/pairwise-calls.png', dpi=300)
plt.show()

# times = [
#     all_calls['z3-time (ms)'].dropna().values,
#     all_calls['astr-time (ms)'].dropna().values
# ]
# labels = ['Z3', 'ASTR']
#
# fig, ax = plt.subplots(figsize=(6, 6))
# ax.violinplot(times, showmeans=True)
# ax.set_xticks([1, 2])
# ax.set_xticklabels(labels)
# ax.set_ylabel('Time (ms)')
# ax.set_title('Solver Time Distributions')
# plt.tight_layout()
# plt.savefig('plots/violin-z3-astr.png', dpi=300)
# plt.show()

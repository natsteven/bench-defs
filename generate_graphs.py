#!.venv/bin/python

import matplotlib.pyplot as plt
import sys
import pandas as pd

if len(sys.argv) != 2:
    print("Provide results directory as argument")
    exit(1)

results_dir = sys.argv[1]
results = pd.read_csv(results_dir + "combined_results.csv")

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

fig, ax = plt.subplots(figsize=(10, 6))

solvers = {
    'astr': 'astr-time (ms)',
    'z3': 'z3-time (ms)'
}

for solver, time_col in solvers.items():
    solver_times = results[
        (results['z3-status'] == 'true') | 
        (results['z3-status'] == 'false')
    ][time_col].dropna().reset_index(drop=True)
    
    solver_times = solver_times/1000
    cum_times = solver_times.cumsum()
    
    x = range(1, len(cum_times) + 1)
    
    ax.plot(x, cum_times, label=solver, linewidth=2)

ax.set_xlabel('Benchmarks completed')
ax.set_ylabel('Cumulative time (s)')
ax.set_title('Comparison on Benchmarks solved by Z3')
ax.legend()
ax.grid(True, alpha=0.3)
plt.tight_layout()
plt.savefig('plots/cactus-plot-spf.png', dpi=300)

## cactus plot accumatling every call to the solver during testing
all_calls = pd.read_csv(results_dir + "solver-call-times.csv")
fig, ax = plt.subplots(figsize=(10, 6))

for solver, time_col in solvers.items():
    solver_calls = all_calls[time_col].dropna().reset_index(drop=True)
    solver_calls = solver_calls/1000
    cum_calls = solver_calls.cumsum()
    
    x = range(1, len(cum_calls) + 1)
    
    ax.plot(x, cum_calls, label=solver, linewidth=2)

ax.set_xlabel('Calls to Solver')
ax.set_ylabel('Cumulative time (s)')
ax.set_title('Comparison across all calls')
ax.legend()
ax.grid(True, alpha=0.3)
plt.tight_layout()
plt.savefig('plots/cactus-plot-calls.png', dpi=300)
plt.show()

import pandas as pd

all = pd.read_csv("~/Repos/bench-defs/combined_results.csv")

# with open("tmp/issues-in-nice-looking.txt") as f:
#     nice_issues = [line.strip().split('.')[1] for line in f]

# filtered = all[all['bench'].isin(nice_issues)]

# print(filtered[['bench', 'ASTR-time (ms)', 'ASTR-calls', 'ASTR-error', 'SPF-error', 'Z3-time (ms)', 'Z3-calls', 'Z3-error', 'z3-status', 'astr-status']])
# print('\n'.join(map(str, all['benchmark'].values)))
no_error = all[all['ASTR-error'] == 0]
no_error = no_error[no_error['SPF-error'] == 0]
print('\n'.join(map(str, no_error['benchmark'].values)))

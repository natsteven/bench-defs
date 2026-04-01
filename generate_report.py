#!.venv/bin/python

import sys
import pandas as pd

if len(sys.argv) != 2:
    print("Provide results directory as argument")
    exit(1)

results_dir = sys.argv[1]
df = pd.read_csv(results_dir + "combined_results.csv")
# weird = df[df['Z3-calls'] != df['ASTR-calls']]
# print(weird)

# df=df[df['ASTR-error'] == 0]
# df=df[df['SPF-error'] == 0]
# df=df[df['ASTR-time (ms)'] > 0]

# df = df[df['Z3-calls'] == df['ASTR-calls']]

# df.to_csv("nice_looking.csv", index=False)


# filter="tmp/issues-in-nice-looking.txt"

# nice=pd.read_csv(filter, header=None)
# nice=nice[0].apply(lambda x: x.split(".")[1])
# nice=set(nice)

# df = pd.read_csv("tmp/nice_looking.csv")
html_out = [
    """
<!DOCTYPE html>
<html>
<head>
<title>Solver Benchmark Report</title>
<link rel="stylesheet" href="https://cdn.datatables.net/2.0.0/css/dataTables.dataTables.css" />
<style>
    body { font-family: sans-serif; margin: 20px; background-color: #f9f9f9; }
    table { border-collapse: collapse; width: 100%; background-color: #ffffff; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; vertical-align: top; }
    th { background-color: #e2e8f0; }
    a { color: #0066cc; text-decoration: none; font-weight: bold; }
    a:hover { text-decoration: underline; }
    .missing { color: #999; font-style: italic; font-size: 0.9em; }
</style>
</head>
<body>
<h2>Benchmark Solver Results</h2>
<table id="benchmarkTable" class="display">
"""
]

solver_columns = [
    "bench",
    "astr-time (ms)",
    "astr-error",
    "z3-time (ms)",
    "z3-error",
    "spf-error",
    "astr_call",
    "z3_call",
]
spf_columns = [
    "spf-astr-cputime (s)",
    "spf-astr-walltime (s)",
    "astr-status",
    "spf-z3-cputime (s)",
    "spf-z3-walltime (s)",
    "z3-status",
]
data_columns = solver_columns + spf_columns
link_columns = ["astr-log", "z3-log", "benchmark"]
headers = data_columns + link_columns

# Create a single, properly formatted <thead> block
html_out.append("<thead>")

# 1st Row: Main Headers
html_out.append("<tr>")
for header in headers:
    html_out.append(f"<th>{header}</th>")
html_out.append("</tr>")

# 2nd Row: Filter Inputs (Only for data_columns)
html_out.append("<tr class='filter-row'>")
for i, header in enumerate(headers):
    # Only add the 'filterable' class if it's a data column
    if i < len(data_columns) - 4:
        html_out.append("<th class='filterable'></th>")
    else:
        html_out.append("<th></th>")  # Leave blank for link columns
html_out.append("</tr>")

html_out.append("</thead>")

html_out.append("<tbody>")


# def get_log_html(bench_name, solver_name):
#     log_filename = f"SV-COMP26_no-runtime-exception.{bench_name}.yml.log"
#     log_path_for_html = f"results-verified/{solver_name}/logfiles/{log_filename}"
#     log_path_for_os = os.path.join("results-verified", solver_name, "logfiles", log_filename)
#
#     if os.path.exists(log_path_for_os):
#     else:
#         return f"<span class='missing'>No log found</span>"
#
# def get_bench_html(bench_name):
#     for root, dirs, files in os.walk("sv-benchmarks/java/argv"):
#         if os.path.basename(root) == bench_name and "Main.java" in files:
#             ret=os.path.join(root, "Main.java")
#             return f"<a href='{ret}' target='_blank'>Bench</a>"
#     return f"<span class='missing'>No bench found</span>"
#
# def get_source_html(bench_name):
#     for root, dirs, files, in os.walk("/home/nat/Repos/good-programs"):
#         if f"{bench_name}.java" in files:
#             ret = os.path.join(root, f"{bench_name}.java")
#             return f"<a href='{ret}' target='_blank'>Source</a>"
#     return f"<span class='missing'>No source found</span>"
#
def strip_html_tags(text):
    return (
        text.replace("<a href='", "")
        .replace("' target='_blank'>", "")
        .replace("Source</a>", "")
        .replace("Bench</a>", "")
    )


def log_html(path, solver_name):
    if path:
        return f"<a href='{path}' target='_blank'>{solver_name} Log</a>"
    else:
        return f"<span class='missing'>No log found</span>"


def bench_html(path):
    if path:
        return f"<a href='{path}' target='_blank'>Bench</a>"
    else:
        return f"<span class='missing'>No bench found</span>"


for index, row in df.iterrows():
    # bench_name = str(row["bench"])
    # if bench_name not in nice:
    #     continue
    # if row["Z3-calls"] == row["ASTR-calls"]:
    # continue
    html_out.append("<tr>")

    # bad=False
    # if row["Z3-calls"] != row["ASTR-calls"]:
    #     print(bench_name)
    # bad=True

    # Add standard data columns
    for col in data_columns:
        cell_value = str(row.get(col, ""))
        html_out.append(f"<td>{cell_value}</td>")
        # if col == "Z3-calls" and cell_value == "0":
        #     bad=True

    # Add the interactive log links
    astr_log = log_html(row["astr-log"], "astr")
    z3_log = log_html(row["z3-log"], "z3")
    bench_path = bench_html(row["benchmark"])
    html_out.append(f"<td>{astr_log}</td>")
    html_out.append(f"<td>{z3_log}</td>")
    html_out.append(f"<td>{bench_path}</td>")

    html_out.append("</tr>")
    # print(row["benchmark"])

    # if bad:
    #     print(f"BAD BENCH: {strip_html_tags(bench_name)}")
    #     print(f"SOURCE: {strip_html_tags(source_path)}")
    #     print(strip_html_tags(bench_path))
    #     bad=False

html_out.append("</tbody></table>")

# 5. Inject DataTables JavaScript
html_out.append("""
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/2.0.0/js/dataTables.js"></script>
<script>
    $(document).ready(function() {
        // Setup - add a text input ONLY to cells with the 'filterable' class
        $('#benchmarkTable thead tr.filter-row th.filterable').each(function () {
            var colIdx = $(this).index(); // Get the actual column index
            var title = $('#benchmarkTable thead tr:eq(0) th').eq(colIdx).text();
            $(this).html('<input type="text" placeholder="Filter ' + title + '" style="width: 100%; box-sizing: border-box; padding: 4px; font-weight: normal;" />');
        });

        // Initialize DataTable
        var table = $('#benchmarkTable').DataTable({
            "pageLength": 50,
            "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
            "orderCellsTop": true,  
            initComplete: function () {
                var api = this.api();
                
                // Apply the search to each column
                api.columns().every(function (index) {
                    var that = this;
                    var filterCell = $('#benchmarkTable thead tr.filter-row th').eq(index);
                    
                    // Only bind the search event if it's a filterable column
                    if (filterCell.hasClass('filterable')) {
                        $('input', filterCell).on('keyup change clear', function () {
                            if (that.search() !== this.value) {
                                that.search(this.value).draw();
                            }
                        });
                    }
                });
            }
        });
    });
</script>
</body></html>
""")

# 6. Write to output file
with open(results_dir + "combined_report.html", "w", encoding="utf-8") as f:
    f.write("\n".join(html_out))

print("Successfully generated combined_solver_report.html!")

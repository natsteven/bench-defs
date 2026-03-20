import sqlite3
import os
import re
import json

# 1. Database Setup
DB_FILE = 'results_database.db'

def parse_log_file_queries(filepath):
    """
    Parses an SMT log file and returns a list of dictionaries containing query info.
    Handles both Z3 and ASTR result formatting.
    """
    extracted_queries = []
    
    if not os.path.exists(filepath):
        print(f"  [!] Missing log file: {filepath}")
        return extracted_queries

    with open(filepath, 'r') as f:
        content = f.read()

    # Regex breakdown:
    # 1. Grabs the query text -> Group 1
    # 2. Uses \*{10,} to jump over the divider asterisks
    # 3. Grabs the entire raw result block -> Group 2
    # 4. Jumps over the next divider asterisks
    # 5. Grabs the solver name (e.g., "Z3str3" or "ASTR") -> Group 3
    # 6. Grabs the time -> Group 4
    block_pattern = re.compile(
        r"SMT QUERY:(.*?)\s*\*{10,}\s*"
        r"(.*?)\s*\*{10,}\s*"
        r"(.*?)\s*Solver Time \(ms\):(\d+(?:\.\d+)?)",
        re.DOTALL
    )

    for match in block_pattern.finditer(content):
        # 1. Extract raw blocks
        raw_query = match.group(1).strip()
        raw_result = match.group(2).strip()
        solver_name = match.group(3).strip()
        time_ms = float(match.group(4))
        
        #  Clean up the query (Replace || with newlines)
        clean_query = re.sub(r'(\|\|)+', '\n', raw_query).strip()
        
        status = "unknown"
        model = None
        
        if raw_result.startswith("sat"):
            status = "sat"
            raw_model = re.sub(r"^sat,?\s*", "", raw_result).strip()
            assignments = re.findall(r'(\w+):\s*"(.*?)"', raw_model)
            models = {var: val for var, val in assignments}
            model = json.dumps(models)
        else:
            status = "unsat"

        extracted_queries.append({
            'time_ms': time_ms,
            'query': clean_query,
            'status': status,
            'model': model if model else None, # Enforce NULL if model is empty
            'solver': solver_name # Added this just in case you want to save it!
        })

    return extracted_queries

def main():
    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()
    
    cursor.execute("SELECT id, bench_name FROM benchmarks")
    rows = cursor.fetchall()

    for row in rows:
        id = row[0]
        bench = row[1]

        smt_query_log_path_prefix = '/home/nat/Repos/bench-defs/results/smt-queries.results/spf.logfiles/String-z3.'
        queries = parse_log_file_queries(smt_query_log_path_prefix + bench + '.yml.log')
        for q in queries:
            cursor.execute('''
                INSERT INTO smt_queries (benchmark_id, z3_time_ms, query, status, model)
                VALUES (?, ?, ?, ?, ?)
            ''', (id, q['time_ms'], q['query'], q['status'], q['model']))


    # Commit the transaction and close the connection
    conn.commit()
    conn.close()

if __name__ == '__main__':
    main()

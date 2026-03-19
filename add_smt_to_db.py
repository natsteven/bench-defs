import sqlite3
import csv
import os
import re

# 1. Database Setup
DB_FILE = 'results_database.db'

def parse_log_file_queries(filepath):
    """
    Parses an SMT log file and returns a list of dictionaries containing query info.
    """
    extracted_queries = []
    
    if not os.path.exists(filepath):
        print(f"  [!] Missing log file: {filepath}")
        return extracted_queries

    with open(filepath, 'r') as f:
        content = f.read()

    # Regex breakdown:
    # 1. Finds the time: "Solver Time (ms):71" -> Group 1
    # 2. Skips the asterisks and grabs the query text on the single line -> Group 2
    # 3. Grabs the boolean status -> Group 3
    # 4. Grabs everything after the status until it hits the next block of asterisks -> Group 4 (Model)
    block_pattern = re.compile(
        r"Solver Time \(ms\):(\d+(?:\.\d+)?)\s*\**\s*"
        r"SMT QUERY:(.*?)\s+"
        r"Satisfiable:\s*(true|false)\s*"
        r"(.*?)(?=\s*\*{10,}|$)",
        re.DOTALL
    )

    for match in block_pattern.finditer(content):
        # 1. Extract raw regex groups
        time_ms = float(match.group(1))
        raw_query = match.group(2)
        status_bool = match.group(3)
        raw_model = match.group(4).strip()
        
        # 2. Clean up the query
        # Replace 1 or more occurrences of "||" with a single newline
        # (Using (\|\|)+ ensures we don't accidentally replace a single SMT '|' symbol if one exists)
        clean_query = re.sub(r'(\|\|)+', '\n', raw_query).strip()
        
        # 3. Map status to 'sat' or 'unsat'
        status = "sat" if status_bool == "true" else "unsat"
        
        # 4. Handle the model
        # If unsat, the model text is likely empty, but we enforce None/NULL just to be safe
        model = raw_model if status == "sat" else None

        # 5. Append to our list mapping directly to the keys your INSERT statement uses
        extracted_queries.append({
            'time_ms': time_ms,
            'query': clean_query,
            'status': status,
            'model': model
        })

    return extracted_queries

def main():
    # Connect to SQLite (this creates the file if it doesn't exist)
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

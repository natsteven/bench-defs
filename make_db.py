import sqlite3
import csv
import os
import re

# 1. Database Setup
DB_FILE = 'results_database.db'
CSV_FILE = '/home/nat/Repos/bench-defs/results/03-17_20-11.results/combined_results.csv'

def setup_database(cursor):
    """Creates the tables if they don't exist."""
    
    # Table 1: The main benchmarks from your CSV
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS benchmarks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            bench_name TEXT,
            benchmark_path TEXT,
            benchmark_source TEXT,
            spf_time_astr_s REAL,
            spf_time_z3_s REAL,
            astr_time_ms REAL,
            z3_time_ms REAL,
            astr_status TEXT,
            z3_status TEXT,
            astr_log TEXT,
            z3_log TEXT
        )
    ''')

    # Table 2: The individual SMT queries linked to a benchmark
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS smt_queries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            benchmark_id INTEGER,
            z3_time_ms REAL,    -- extracted time for this specific query
            query TEXT,           -- the actual SMT query string
            status TEXT,    -- e.g. "sat", "unsat"
            model TEXT,     -- optional: the model if the query was satisfiable
            FOREIGN KEY (benchmark_id) REFERENCES benchmarks(id)
        )
    ''')

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
    
    print("Setting up database...")
    setup_database(cursor)
    
    print(f"Reading {CSV_FILE}...")
    with open(CSV_FILE, 'r') as file:
        reader = csv.DictReader(file)
        
        for row in reader:
            bench_path = row['benchmark']
            with open(bench_path, 'r') as f:
                source = f.read()
            # 1. Insert the main benchmark recor
            astr_log_path = row['astr-log']
            with open(astr_log_path, 'r') as f:
                astr_log = f.read()
            z3_log_path = row['z3-log']
            with open(z3_log_path, 'r') as f:
                z3_log = f.read()

            cursor.execute('''
                INSERT INTO benchmarks (
                    bench_name, benchmark_path, benchmark_source, spf_time_astr_s, spf_time_z3_s,
                    astr_time_ms, z3_time_ms, astr_status, z3_status, astr_log, z3_log
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ''', (
                row['bench'], 
                bench_path,
                source,
                row['spf-astr-walltime (s)'],
                row['spf-z3-walltime (s)'],
                row['astr-time (ms)'], 
                row['z3-time (ms)'],
                row['astr-status'], 
                row['z3-status'],
                astr_log,
                z3_log
            ))
            
            # Grab the ID of the benchmark we just inserted to use as our Foreign Key
            # benchmark_id = cursor.lastrowid
            #
            # smt_query_log_path_prefix = '/home/nat/Repos/bench-defs/results/smt-queries.results/spf.logfiles/String-z3.'
            # queries = parse_log_file_queries(smt_query_log_path_prefix + row['bench'] + '.yml.log')
            # for q in queries:
            #     cursor.execute('''
            #         INSERT INTO smt_queries (benchmark_id, z3_time_ms, query, status, model)
            #         VALUES (?, ?, ?, ?, ?)
            #     ''', (benchmark_id, q['time_ms'], q['query'], q['status'], q['model']))


    # Commit the transaction and close the connection
    conn.commit()
    conn.close()
    print("Finished importing data to SQLite!")

if __name__ == '__main__':
    main()

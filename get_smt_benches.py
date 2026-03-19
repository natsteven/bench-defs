import sqlite3
import os

DB_FILE = 'results_database.db'
OUTPUT_DIR = 'argv-string-smt'

def extract_longest_queries():
    if not os.path.exists(OUTPUT_DIR):
        os.makedirs(OUTPUT_DIR)

    conn = sqlite3.connect(DB_FILE)
    cursor = conn.cursor()

    cursor.execute("""
        SELECT bench_name, query, MAX(LENGTH(query))
        FROM v_benchmark_queries
        GROUP BY bench_name
    """)
    
    longest_queries = cursor.fetchall()
    
    for row in longest_queries:
        bench_name = row[0]
        query = row[1]
        
        filepath = os.path.join(OUTPUT_DIR, f"{bench_name}.smt2")
        with open(filepath, 'w') as f:
            f.write(query)
                
    conn.close()
    print("Done! All .smt2 files have been generated.")

if __name__ == '__main__':
    extract_longest_queries()

#!/usr/bin/env python3
"""
Recursively scans build/ directory for *.html files and extracts failed tests.
Outputs test class name, failed test name, and error log in a distinguishable format.
"""

import os
import re
import sys
from pathlib import Path


import html

def extract_failures_from_html(file_path):
    """Extract test failures from an HTML file using multiple parsing strategies."""
    failures = []
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
    except (UnicodeDecodeError, IOError) as e:
        print(f"  Warning: Could not read file: {e}", file=sys.stderr)
        return failures
    
    # Strategy 1: Handle Gradle/JUnit HTML report format
    # Format: <div class="test"> with <h3 class="failures">testName()</h3> and <pre>error stacktrace</pre>
    test_pattern = r'<div class="test">.*?<a name="([^"]+)"></a>.*?<h3 class="failures">([^<]+)</h3>.*?<pre>([\s\S]*?)</pre>'
    test_matches = re.findall(test_pattern, content, re.DOTALL)
    
    if test_matches:
        for anchor, method_name, error_log in test_matches:
            # Extract class name from file path
            path_parts = str(file_path).split('/')
            filename = path_parts[-1] if path_parts else 'Unknown'
            class_name = filename.replace('.html', '') if filename.endswith('.html') else filename
            
            # Decode HTML entities in the error log
            decoded_error = html.unescape(error_log)
            
            failures.append({
                'class': class_name,
                'test': method_name,
                'message': decoded_error.strip()
            })
    
    # Strategy 2: JUnit XML style (common in test reports)
    if not failures:
        junit_failure_pattern = r'<failure[^>]*message=["\']([^"\']*)["\'][^>]*>'
        junit_testcase_pattern = r'<testcase[^>]*class=["\']([^"\']*)["\'][^>]*name=["\']([^"\']*)["\']'
        
        junit_failures = re.findall(junit_failure_pattern, content)
        junit_testcases = re.findall(junit_testcase_pattern, content)
        
        if junit_testcases:
            for i, (class_name, test_name) in enumerate(junit_testcases):
                failures.append({
                    'class': class_name,
                    'test': test_name,
                    'message': junit_failures[i] if i < len(junit_failures) else 'Unknown error'
                })
    
    # Strategy 3: Pytest HTML style (look for failed test rows)
    if not failures:
        pytest_pattern = r'<tr[^>]*class=["\'][^"\']*fail[^"\']*["\'][^>]*>(.*?)</tr>'
        pytest_rows = re.findall(pytest_pattern, content, re.DOTALL)
        
        if pytest_rows:
            for row in pytest_rows:
                # Extract class and method from the row
                class_match = re.search(r'<td>[^<]*</td><td>([^<]+)</td>', row)
                method_match = re.search(r'<td>[^<]*</td><td>[^<]*</td><td>([^<]+)</td>', row)
                
                if class_match and method_match:
                    full_name = class_match.group(1)
                    if '.' in full_name:
                        class_name = full_name.rsplit('.', 1)[0]
                        test_name = full_name.rsplit('.', 1)[1]
                    else:
                        class_name = full_name
                        test_name = method_match.group(1)
                    
                    failures.append({
                        'class': class_name,
                        'test': test_name,
                        'message': method_match.group(1)
                    })
    
    # Strategy 4: Generic failure detection
    if not failures:
        failure_sections = re.findall(r'=== (?:FAILED|FAILURE) ===[\s\S]*?Test Class:\s*(.+?)[\s\S]*?Test Name:\s*(.+?)[\s\S]*?Error:\s*([\s\S]*?)(?=\n\n|\n===|\Z)', content, re.IGNORECASE)
        
        if failure_sections:
            for class_name, test_name, error_msg in failure_sections:
                failures.append({
                    'class': class_name.strip(),
                    'test': test_name.strip(),
                    'message': error_msg.strip()
                })
    
    return failures
    
    # Strategy 1: JUnit XML style (common in test reports)
    junit_failure_pattern = r'<failure[^>]*message=["\']([^"\']*)["\'][^>]*>'
    junit_testcase_pattern = r'<testcase[^>]*class=["\']([^"\']*)["\'][^>]*name=["\']([^"\']*)["\']'
    
    junit_failures = re.findall(junit_failure_pattern, content)
    junit_testcases = re.findall(junit_testcase_pattern, content)
    
    if junit_testcases:
        for i, (class_name, test_name) in enumerate(junit_testcases):
            failures.append({
                'class': class_name,
                'test': test_name,
                'message': junit_failures[i] if i < len(junit_failures) else 'Unknown error'
            })
    
    # Strategy 2: Pytest HTML style (look for failed test rows)
    if not failures:
        pytest_pattern = r'<tr[^>]*class=["\'][^"\']*fail[^"\']*["\'][^>]*>(.*?)</tr>'
        pytest_rows = re.findall(pytest_pattern, content, re.DOTALL)
        
        if pytest_rows:
            for row in pytest_rows:
                # Extract class and method from the row
                class_match = re.search(r'<td>[^<]*</td><td>([^<]+)</td>', row)
                method_match = re.search(r'<td>[^<]*</td><td>[^<]*</td><td>([^<]+)</td>', row)
                
                if class_match and method_match:
                    full_name = class_match.group(1)
                    if '.' in full_name:
                        class_name = full_name.rsplit('.', 1)[0]
                        test_name = full_name.rsplit('.', 1)[1]
                    else:
                        class_name = full_name
                        test_name = method_match.group(1)
                    
                    failures.append({
                        'class': class_name,
                        'test': test_name,
                        'message': method_match.group(1)
                    })
    
    # Strategy 3: Look for "FAILED" markers with context
    if not failures:
        failed_pattern = r'(?:Test Class|Class)[:\s]+([^\n]+)\n(?:Test|Method|Function)[:\s]+(test_\w+)\n(?:Result|Status)[:\s]+FAILED\n(?:Error|Message|Log)[:\s]*([\s\S]*?)(?=\n\n|\Z)'
        matches = re.findall(failed_pattern, content, re.IGNORECASE)
        
        if matches:
            for class_name, test_name, error_msg in matches:
                failures.append({
                    'class': class_name.strip(),
                    'test': test_name.strip(),
                    'message': error_msg.strip()
                })
    
    # Strategy 4: Generic failure detection
    if not failures:
        failure_sections = re.findall(r'=== (?:FAILED|FAILURE) ===[\s\S]*?Test Class:\s*(.+?)[\s\S]*?Test Name:\s*(.+?)[\s\S]*?Error:\s*([\s\S]*?)(?=\n\n|\n===|\Z)', content, re.IGNORECASE)
        
        if failure_sections:
            for class_name, test_name, error_msg in failure_sections:
                failures.append({
                    'class': class_name.strip(),
                    'test': test_name.strip(),
                    'message': error_msg.strip()
                })
    
    return failures


def format_output(file_path, failures):
    """Format failure information in a distinguishable way."""
    if not failures:
        return None
    
    output_lines = []
    output_lines.append("=" * 80)
    output_lines.append(f"FILE: {file_path}")
    output_lines.append("=" * 80)
    
    for i, failure in enumerate(failures, 1):
        output_lines.append(f"\n--- Failure #{i} ---")
        output_lines.append(f"  Test Class:  {failure['class']}")
        output_lines.append(f"  Test Name:   {failure['test']}")
        output_lines.append(f"  Error Log:   {failure['message']}")
    
    output_lines.append("\n" + "-" * 80 + "\n")
    
    return "\n".join(output_lines)


def scan_build_directory(build_dir="build"):
    """Recursively scan build directory for HTML files with test failures."""
    build_path = Path(build_dir)
    
    if not build_path.exists():
        print(f"Error: Directory '{build_dir}' does not exist.", file=sys.stderr)
        sys.exit(1)
    
    if not build_path.is_dir():
        print(f"Error: '{build_dir}' is not a directory.", file=sys.stderr)
        sys.exit(1)
    
    html_files = list(build_path.rglob("*.html"))
    
    if not html_files:
        print(f"No HTML files found in '{build_dir}'", file=sys.stderr)
        return
    
    print(f"\nScanning {len(html_files)} HTML file(s) in '{build_dir}'...\n")
    
    total_failures = 0
    
    for html_file in html_files:
        relative_path = html_file.relative_to(build_path)
        failures = extract_failures_from_html(html_file)
        
        if failures:
            output = format_output(str(relative_path), failures)
            if output:
                print(output)
                total_failures += len(failures)
    
    if total_failures == 0:
        print("No test failures found.")
    else:
        print(f"\n{'=' * 80}")
        print(f"TOTAL FAILURES: {total_failures}")
        print(f"{'=' * 80}\n")


if __name__ == "__main__":
    scan_build_directory()
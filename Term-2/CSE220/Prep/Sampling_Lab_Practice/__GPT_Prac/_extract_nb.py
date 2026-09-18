import json, sys

nb = json.load(open(r'd:\_b0et\CSE-Sessional-Coursework_Level-2\Term-2\CSE220\Prep\Sampling_Lab_Practice\gpt_prac.ipynb', encoding='utf-8'))

for i, c in enumerate(nb['cells']):
    ct = c['cell_type']
    src = ''.join(c['source'])
    if ct == 'code':
        print(f'=== CODE CELL {i} ===')
        print(src)
        print()
    else:
        # Just print first 3 lines of markdown for context
        lines = src.split('\n')
        header = '\n'.join(lines[:5])
        print(f'=== {ct.upper()} CELL {i} (first 5 lines) ===')
        print(header)
        print()


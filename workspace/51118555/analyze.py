import sys
import json
import re

f = open(sys.argv[1], "r")
x = f.read()
f.close

j = json.loads(x)

def split_str(y):
    row = re.split(r"\s+", y)
    return {"1m": float(row[0]), "5m": float(row[1]), "15m": float(row[2])}

g = list(map(split_str, j))
data = json.dumps(g)
print(data)

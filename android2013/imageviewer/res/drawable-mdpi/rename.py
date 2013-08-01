import os

for f in os.listdir(os.getcwd()):
    g = f
    if f.endswith(".png"):
        g = g.replace("-", "_")
    if g[0].isdigit() and g[1] == "_":
        g = g[2:]
    os.rename(f, g)

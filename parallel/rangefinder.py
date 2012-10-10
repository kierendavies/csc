#! /usr/bin/python3

import sys

filename = sys.argv[1]

xmin = 0
xmax = 0
ymin = 0
ymax = 0

with open(filename, "r") as fin:
    for line in fin.readlines():
        try:
            x, y = map(float, line.split()[1:3])
            if x < xmin: xmin = x
            if x > xmax: xmax = x
            if y < ymin: ymin = y
            if y > ymax: ymax = y
        except:
            print(line)

print("({}, {}; {}, {})".format(xmin, ymin, xmax, ymax))

#!/usr/bin/bash

trap exit SIGINT SIGTERM

for size in 500000 1000000 10000000 100000000; do
    java -Xmx2g SumSequential $size;
    java -Xmx2g SumThreaded $size;
done
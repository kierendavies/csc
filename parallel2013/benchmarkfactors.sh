#!/usr/bin/bash

trap exit SIGINT SIGTERM

for size in 500000 1000000 2000000; do
    java -Xmx2g FactorSequential $size;
    java -Xmx2g FactorThreaded $size;
done
Game of Life
============

Usage
-----

First build the project by running `make`.

Use the script files `lifeseq.sh` and `lifethr.sh` to execute Game of Life sequentially 
and threaded, respectively.

To see the available command-line options, use the `--help` flag.

    ./lifeseq.sh --help
    ./lifethr.sh --help

Most importantly, use `--no-draw` for benchmarking.  Do not time with `time`, 
as the program already times the simulation, excluding initialisation.  It is 
recommended that you use the following examples.

    ./lifeseq.sh --no-draw --height=500 --width=500 --generations=100 --pattern=gosperglidergun
    ./lifethr.sh --no-draw --infinite --height=500 --width=500 --generations=100 --pattern=gosperglidergun

Notes
-----

The grid of cells is toroidal by default.



A parallel speedup of almost exactly 2 times was achieved on a quadcore Intel i5.
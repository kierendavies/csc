# Sum and Factor

First build the project by running `make sf`.

To benchmark all input sizes, run these commands.

    ./benchmarksum.sh
    ./benchmarkfactors.sh

# Game of Life

First build the project by running `make life`.

Use the script files `lifeseq.sh` and `lifethr.sh` to execute Game of Life sequentially 
and threaded, respectively.

To see the available command-line options, use the `--help` flag.

    ./lifeseq.sh --help
    ./lifethr.sh --help

Most importantly, use `--no-draw` for benchmarking.  Do not time with `time`, 
as the program already times the simulation, excluding initialisation.  It is 
recommended that you use the following examples.

    ./lifeseq.sh --no-draw --generations=1200 --pattern=rpentomino
    ./lifethr.sh --no-draw --generations=1200 --pattern=rpentomino

Arbitrary patterns can be loaded by creating a new `.cells` file in the 
`patterns` directory, following the same format as the examples already there.

## Implementation notes

The grid of cells is initially small and dynamically resizes when live cells 
approach the boundaries.

A parallel speedup of approximately 2.5 times was achieved on the system described in `answers.md`, and it ran faster than the C reference implementation.
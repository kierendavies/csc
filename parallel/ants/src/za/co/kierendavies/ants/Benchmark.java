package za.co.kierendavies.ants;

public class Benchmark {
    static int xMin = -200;
    static int xMax = 200;
    static int yMin = -200;
    static int yMax = 200;
    static Bins bins = new Bins(xMin, yMin, xMax, yMax);

    static int[] cutoffs = {400, 2500, 10000, 40000};

    static int queries = 0;
    public static int[][] gets = new int[400][400];

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long elapsedTime;

        System.out.print("reading file(s)... ");
        if (args.length == 1) {
            bins.load(args[0]);
        } else {
            bins.loadParallel(args);
        }
        System.out.println("done in " + (System.currentTimeMillis() - startTime) + "ms");
        //System.err.println("" + lines + " data points in (" + xMin + ", " + yMin + "; " + xMax + ", " + yMax + ")");

        System.out.print("preprocessing...");
        startTime = System.currentTimeMillis();
        bins.preprocess();
        elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("done in " + elapsedTime + "ms");

        int size = 200;
        int incr = 25;

        /*System.out.print("comparing results... ");
        startTime = System.currentTimeMillis();
        for (int _xMin = (int)xMin; _xMin <= (int)xMax - size; _xMin += incr) {
            for (int _yMin = (int)yMin; _yMin <= (int)yMax - size; _yMin += incr) {
                for (int _xMax = _xMin + size; _xMax <= (int)xMax; _xMax += incr) {
                    for (int _yMax = _yMin + size; _yMax <= (int)yMax; _yMax += incr) {
                        //System.out.println(bins.querySequential(_xMin, _yMin, _xMax, _yMax));
                        gets = new int[400][400];
                        double seq = bins.querySequential(_xMin, _yMin, _xMax, _yMax);
                        //gets = new int[400][400];
                        double pll = bins.queryParallel(_xMin, _yMin, _xMax, _yMax);
                        if (seq != pll) {
                            //System.err.println("results differ by " + (pll - seq));
                            System.err.println("(" + _xMin + ", " + _yMin + "; " + _xMax + ", " + _yMax + ")");
                            System.err.println("pll = " + pll);
                            System.err.println("seq = " + seq);
                            *//*for (int x = 0; x < 200; ++x) {
                                for (int y = 0; y < 200; ++y) {
                                    System.err.print(gets[x][y]);
                                }
                                System.out.print("\n");
                            }
                            return;*//*
                        }
                        //else System.err.println("correct");
                    }
                }
            }
        }
        System.out.println("done in " + (System.currentTimeMillis() - startTime) + "ms");
        System.gc();*/

        System.out.print("querying (sequential)... ");
        startTime = System.currentTimeMillis();
        for (int _xMin = (int)xMin; _xMin <= (int)xMax - size; _xMin += incr) {
            for (int _yMin = (int)yMin; _yMin <= (int)yMax - size; _yMin += incr) {
                for (int _xMax = _xMin + size; _xMax <= (int)xMax; _xMax += incr) {
                    for (int _yMax = _yMin + size; _yMax <= (int)yMax; _yMax += incr) {
                        bins.querySequential(_xMin, _yMin, _xMax, _yMax);
                        ++queries;
                    }
                }
            }
        }
        elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(queries + " queries in " + elapsedTime + "ms, average " + ((double)elapsedTime / queries) + "ms");

        System.out.print("querying (sequential, smarter)... ");
        startTime = System.currentTimeMillis();
        for (int _xMin = (int)xMin; _xMin <= (int)xMax - size; _xMin += incr) {
            for (int _yMin = (int)yMin; _yMin <= (int)yMax - size; _yMin += incr) {
                for (int _xMax = _xMin + size; _xMax <= (int)xMax; _xMax += incr) {
                    for (int _yMax = _yMin + size; _yMax <= (int)yMax; _yMax += incr) {
                        bins.querySequentialSmarter(_xMin, _yMin, _xMax, _yMax);
                    }
                }
            }
        }
        elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(queries + " queries in " + elapsedTime + "ms, average " + ((double)elapsedTime / queries) + "ms");

        for (int cutoff : cutoffs) {
            System.gc();
            System.out.print("querying (parallel, cutoff " + cutoff + ")... ");
            bins.setParallelCutoff(cutoff);
            startTime = System.currentTimeMillis();
            for (int _xMin = (int)xMin; _xMin <= (int)xMax - size; _xMin += incr) {
                for (int _yMin = (int)yMin; _yMin <= (int)yMax - size; _yMin += incr) {
                    for (int _xMax = _xMin + size; _xMax <= (int)xMax; _xMax += incr) {
                        for (int _yMax = _yMin + size; _yMax <= (int)yMax; _yMax += incr) {
                            bins.queryParallel(_xMin, _yMin, _xMax, _yMax);
                        }
                    }
                }
            }
            elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println(queries + " queries in " + elapsedTime + "ms, average " + ((double)elapsedTime / queries) + "ms");
        }

        queries = 1000;
        System.out.print("querying (sequential)... ");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < queries; ++i) {
            bins.querySequential(-180, -180, 180, 180);
        }
        elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(queries + " queries in " + elapsedTime + "ms, average " + ((double)elapsedTime / queries) + "ms");

        System.out.print("querying (parallel)... ");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < queries; ++i) {
            bins.queryParallel(-180, -180, 180, 180);
        }
        elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println(queries + " queries in " + elapsedTime + "ms, average " + ((double)elapsedTime / queries) + "ms");
    }
}

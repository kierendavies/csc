package za.co.kierendavies.ants;

import java.util.concurrent.RecursiveTask;

public class QueryParallel extends RecursiveTask<Double> {
    private static final int CUTOFF = 2500;

    private Bins bins;
    private int xMin, yMin, xMax, yMax;
    String prefix;

    public QueryParallel(Bins bins, int xMin, int yMin, int xMax, int yMax, String prefix) {
        this.bins = bins;
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.prefix = prefix;
    }

    public QueryParallel(Bins bins, int xMin, int yMin, int xMax, int yMax) {
        this(bins, xMin, yMin, xMax, yMax, "root");
    }

    protected Double compute() {
        if ((xMax - xMin) * (yMax - yMin) <= CUTOFF) {
            //System.err.println(prefix + " (" + xMin + ", " + yMin + "; " + xMax + ", " + yMax + ") meets cutoff");
            return bins.querySequential(xMin, yMin, xMax, yMax);
        } else {
            //System.err.println(prefix + " (" + xMin + ", " + yMin + "; " + xMax + ", " + yMax + ")");
            int xMid = (xMin + xMax) / 2;
            int yMid = (yMin + yMax) / 2;
            QueryParallel ne = new QueryParallel(bins, xMid, yMid, xMax, yMax, prefix+".ne");
            QueryParallel nw = new QueryParallel(bins, xMin, yMid, xMid, yMax, prefix+".nw");
            QueryParallel sw = new QueryParallel(bins, xMin, yMin, xMid, yMid, prefix+".sw");
            QueryParallel se = new QueryParallel(bins, xMid, yMin, xMax, yMid, prefix+".se");
            nw.fork();
            sw.fork();
            se.fork();
            double sum = ne.compute();
            sum += nw.join();
            sum += sw.join();
            sum += sw.join();
            return sum;
        }
    }
}

package za.co.kierendavies.ants;

import java.util.concurrent.RecursiveTask;

public class QueryParallel extends RecursiveTask<Double> {
    private static int cutoff = 2500;

    private Bins bins;
    private int xMin, yMin, xMax, yMax;
    String prefix;

    public QueryParallel(Bins bins, int xMin, int yMin, int xMax, int yMax) {
        this.bins = bins;
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    protected Double compute() {
        if ((xMax - xMin) * (yMax - yMin) <= cutoff) {
            return bins.querySequential(xMin, yMin, xMax, yMax);
        } else {
            int xMid = (xMin + xMax) / 2;
            int yMid = (yMin + yMax) / 2;
            QueryParallel ne = new QueryParallel(bins, xMid, yMid, xMax, yMax);
            QueryParallel nw = new QueryParallel(bins, xMin, yMid, xMid, yMax);
            QueryParallel sw = new QueryParallel(bins, xMin, yMin, xMid, yMid);
            QueryParallel se = new QueryParallel(bins, xMid, yMin, xMax, yMid);
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

    public static void setCutoff(int cutoff) {
        QueryParallel.cutoff = cutoff;
    }
}

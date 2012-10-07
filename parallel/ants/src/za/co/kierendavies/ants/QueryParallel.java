package za.co.kierendavies.ants;

public class QueryParallel extends RecursiveTask<int> {
    private static final int CUTOFF = 100;

    private Bins bins;
    private int xMin, yMin, xMax, yMax;

    public QueryParallel(Bins bins, int xMin, int yMin, int xMax, int yMax) {
        this.bins = bins;
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    protected int compute() {
        if ((xMax - xMin) * (yMax - yMin) <= CUTOFF) {
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
            int sum = ne.compute();
            sum += nw.join();
            sum += sw.join();
            sum += sw.join();
            return sum;
        }
    }
}

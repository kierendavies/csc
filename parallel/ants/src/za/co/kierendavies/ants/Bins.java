package za.co.kierendavies.ants;

import java.util.concurrent.ForkJoinPool;

public class Bins {
    private int xMin = 0, yMin = 0;
    private int xMax = 0, yMax = 0;
    private int xSize = 1, ySize = 1;
    int[][] bins;

    public Bins(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = (int) Math.floor(xMin);
        this.yMin = (int) Math.floor(yMin);
        this.xMax = (int) Math.ceil(xMax);
        this.yMax = (int) Math.ceil(yMax);
        bins = new int[this.xMax - this.xMin + 1][this.yMax - this.yMin + 1];
    }

    public void add(double x, double y) {
        ++bins[(int)x - xMin][(int)y - yMin];
    }

    public int get(double x, double y) {
        return bins[(int)x - xMin][(int)y - yMin];
    }

    public int get(int x, int y) {
        return bins[x - xMin][y - yMin];
    }

    public int querySequential(int xMin, int yMin, int xMax, int yMax) {
        int total = 0;
        for (int x = xMin; x < xMax; ++x) {
            for (int y = yMin; y < yMax; ++y) {
                total += get(x, y);
            }
        }
        return total;
    }

    public int queryParallel(int xMin, int yMin, int xMax, int yMax) {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(new QueryParallel(this, xMin, yMin, xMax, yMax));
    }
}

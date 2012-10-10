package za.co.kierendavies.ants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ForkJoinPool;

public class Bins {
    private int xMin = 0, yMin = 0;
    private int xMax = 0, yMax = 0;
    private int xSize = 1, ySize = 1;
    private double[][] bins;
    private double[][] cumulative;

    public Bins(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = (int) Math.floor(xMin);
        this.yMin = (int) Math.floor(yMin);
        this.xMax = (int) Math.ceil(xMax);
        this.yMax = (int) Math.ceil(yMax);
        bins = new double[this.xMax - this.xMin + 1][this.yMax - this.yMin + 1];
    }

    public void load(String filename) {
        try {
            BufferedReader stream = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = stream.readLine()) != null) {
                String[] tokens = line.split(" ");
                add(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[4]));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void loadParallel(String[] filenames) {
        LoadParallel action = new LoadParallel(this, filenames);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(action);
    }

    public synchronized void add(double x, double y) {
        ++bins[(int)x - xMin][(int)y - yMin];
    }

    public void preprocess() {
        cumulative = new double[xMax - xMin + 1][yMax - yMin + 1];
        for (int x = 0; x <= this.xMax - this.xMin; ++x) {
            cumulative[x][0] = bins[x][0];
        }
        for (int y = 1; y <= yMax - yMin; ++y) {
            cumulative[0][y] = bins[0][y];
        }
        for (int x = 1; x <= this.xMax - this.xMin; ++x) {
            for (int y = 1; y <= yMax - yMin; ++y) {
                cumulative[x][y] = cumulative[x-1][y] + cumulative[x][y-1] - cumulative[x-1][y-1] + bins[x][y];
            }
        }
    }

    public void setParallelCutoff(int cutoff) {
        QueryParallel.setCutoff(cutoff);
    }

    public double get(int x, int y) {
        ++Benchmark.gets[x - xMin][y - yMin];
        return bins[x - xMin][y - yMin];
    }

    public double get(double x, double y) {
        return get((int) x, (int) y);
    }

    public double getCumulative(int x, int y) {
        if (x < xMin || y < yMin) return 0;
        ++Benchmark.gets[x - xMin][y - yMin];
        return cumulative[x - xMin][y - yMin];
    }

    public double getCumulative(double x, double y) {
        return getCumulative((int) x, (int) y);
    }

    public double querySequential(int xMin, int yMin, int xMax, int yMax) {
        int total = 0;
        for (int x = xMin; x < xMax; ++x) {
            for (int y = yMin; y < yMax; ++y) {
                total += get(x, y);
            }
        }
        return total;
    }

    public double querySequentialSmarter(int xMin, int yMin, int xMax, int yMax) {
        return getCumulative(xMax-1, yMax-1) - getCumulative(xMax-1, yMin-1) - getCumulative(xMin-1, yMax-1) + getCumulative(xMin-1, yMin-1);
    }

    public double queryParallel(int xMin, int yMin, int xMax, int yMax) {
        QueryParallel query = new QueryParallel(this, xMin, yMin, xMax, yMax);
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(query);
    }
}

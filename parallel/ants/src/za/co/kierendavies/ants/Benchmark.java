package za.co.kierendavies.ants;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;

public class Benchmark {
    private static String filename; //= "/home/kieren/csc/parallel/ant1.txt";

    public static void main(String[] args) {
        filename = args[0];
        BufferedReader fin;
        Scanner in;
        try {
            fin = new BufferedReader(new FileReader(filename));
            in = new Scanner(fin);
        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
            return;
        }

        Bins bins = new Bins(-180, -180, 180, 180);

        double xMin = -180;
        double xMax = 180;
        double yMin = -180;
        double yMax = 180;

        System.err.print("reading file... ");
        long startTime = System.currentTimeMillis();
        int lines = 0;
        try {
            while (fin.ready()) {
                String line = fin.readLine();
                String[] tokens = line.split(" ");
                //double x = in.nextDouble();
                //double y = in.nextDouble();
                int x=2, y=4;
                //for (x = 1; tokens[x].isEmpty(); ++x);
                //for (y = x + 1; tokens[y].isEmpty(); ++y);
                bins.add(Double.parseDouble(tokens[x]), Double.parseDouble(tokens[y]));
                //in.nextLine();
                ++lines;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.err.println("done in " + (System.currentTimeMillis() - startTime) + "ms");
//        System.err.println("" + lines + " data points in (" + xMin + ", " + yMin + "; " + xMax + ", " + yMax + ")");

        int size = 100;
        int incr = 20;

        System.err.print("comparing results... ");
        startTime = System.currentTimeMillis();
        for (int _xMin = (int)xMin; _xMin <= (int)xMax - size; _xMin += incr) {
            for (int _yMin = (int)yMin; _yMin <= (int)yMax - size; _yMin += incr) {
                for (int _xMax = _xMin + size; _xMax <= (int)xMax; _xMax += incr) {
                    for (int _yMax = _yMin + size; _yMax <= (int)yMax; _yMax += incr) {
                        //System.out.println(bins.querySequential(_xMin, _yMin, _xMax, _yMax));
                        double seq = bins.querySequential(_xMin, _yMin, _xMax, _yMax);
                        double pll = bins.queryParallel(_xMin, _yMin, _xMax, _yMax);
                        //if (seq != pll) System.err.println("results differ by " + (pll - seq));
                        //else System.err.println("correct");
                    }
                }
            }
        }
        System.err.println("done in " + (System.currentTimeMillis() - startTime) + "ms");
        System.gc();

        System.err.print("querying (sequential)... ");
        startTime = System.currentTimeMillis();
        for (int _xMin = (int)xMin; _xMin <= (int)xMax - size; _xMin += incr) {
            for (int _yMin = (int)yMin; _yMin <= (int)yMax - size; _yMin += incr) {
                for (int _xMax = _xMin + size; _xMax <= (int)xMax; _xMax += incr) {
                    for (int _yMax = _yMin + size; _yMax <= (int)yMax; _yMax += incr) {
                        //System.out.println(bins.querySequential(_xMin, _yMin, _xMax, _yMax));
                        bins.querySequential(_xMin, _yMin, _xMax, _yMax);
                    }
                }
            }
        }
        System.err.println("done in " + (System.currentTimeMillis() - startTime) + "ms");
        System.gc();

        System.err.print("querying (parallel)... ");
        startTime = System.currentTimeMillis();
        for (int _xMin = (int)xMin; _xMin <= (int)xMax - size; _xMin += incr) {
            for (int _yMin = (int)yMin; _yMin <= (int)yMax - size; _yMin += incr) {
                for (int _xMax = _xMin + size; _xMax <= (int)xMax; _xMax += incr) {
                    for (int _yMax = _yMin + size; _yMax <= (int)yMax; _yMax += incr) {
                        //System.out.println(bins.querySequential(_xMin, _yMin, _xMax, _yMax));
                        bins.queryParallel(_xMin, _yMin, _xMax, _yMax);
                    }
                }
                break;
            }
            break;
        }
        System.err.println("done in " + (System.currentTimeMillis() - startTime) + "ms");
        //System.gc();
    }
}

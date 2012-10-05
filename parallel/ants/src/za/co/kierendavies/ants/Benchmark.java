package za.co.kierendavies.ants;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;

public class Benchmark {
    private static final String filename = "/home/kieren/csc/parallel/ant1.txt";

    public static void main(String[] args) {
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
//        System.err.println("" + lines + " data points");
//        System.err.println("range (" + xMin + ", " + yMin + ")" +
//                             " to (" + xMax + ", " + yMax + ")");

        int size = 100;
        int incr = 30;

        System.err.print("querying (sequential)... ");
        startTime = System.currentTimeMillis();
        for (int xxMin = -180; xxMin <= 180 - size; xxMin += incr) {
            for (int yyMin = -180; yyMin <= 180 - size; yyMin += incr) {
                for (int xxMax = xxMin + size; xxMax <= 180; xxMax += incr) {
                    for (int yyMax = yyMin + size; yyMax <= 180; yyMax += incr) {
                        //System.out.println(bins.querySequential(xxMin, yyMin, xxMax, yyMax));
                        bins.querySequential(xxMin, yyMin, xxMax, yyMax);
                    }
                }
            }
        }
        System.err.println("done in " + (System.currentTimeMillis() - startTime) + "ms");

        System.err.print("querying (parallel)... ");
        startTime = System.currentTimeMillis();
        for (int xxMin = -180; xxMin <= 180 - size; xxMin += incr) {
            for (int yyMin = -180; yyMin <= 180 - size; yyMin += incr) {
                for (int xxMax = xxMin + size; xxMax <= 180; xxMax += incr) {
                    for (int yyMax = yyMin + size; yyMax <= 180; yyMax += incr) {
                        //System.out.println(bins.querySequential(xxMin, yyMin, xxMax, yyMax));
                        bins.queryParallel(xxMin, yyMin, xxMax, yyMax);
                    }
                }
                break;
            }
            break;
        }
        System.err.println("done in " + (System.currentTimeMillis() - startTime) + "ms");
    }
}

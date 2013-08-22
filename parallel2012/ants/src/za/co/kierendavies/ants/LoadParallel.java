package za.co.kierendavies.ants;

import java.util.concurrent.RecursiveAction;

public class LoadParallel extends RecursiveAction {
    private Bins bins;
    private String[] filenames;
    private int size;

    public LoadParallel(Bins bins, String[] filenames, int size) {
        this.bins = bins;
        this.filenames = filenames;
        this.size = size;
    }

    public LoadParallel(Bins bins, String[] filenames) {
        this(bins, filenames, filenames.length);
    }

    protected void compute() {
        if (size > 1) {
            LoadParallel next = new LoadParallel(bins, filenames, size-1);
            next.fork();
            bins.load(filenames[size-1]);
            next.join();
        } else {
            bins.load(filenames[0]);
        }
    }
}

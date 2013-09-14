public class SumThreaded extends Thread {
    static final int CUTOFF = 10000;

    long[] arr;
    int hi;
    int lo;
    long result;

    public SumThreaded(long[] arr, int lo, int hi) {
        this.arr = arr;
        this.lo = lo;
        this.hi = hi;
        this.result = 0;
    }

    public SumThreaded(long[] arr) {
        this(arr, 0, arr.length);
    }

    @Override
    public void run() {
        if (hi - lo <= CUTOFF) {
            for (int i = lo; i < hi; ++i) {
                this.result += arr[i];
            }
        } else {
            // only spawn one child thread; use this thread again for the other half
            SumThreaded child = new SumThreaded(arr, (lo + hi) / 2, hi);
            this.hi = (lo + hi) / 2;
            child.start();
            this.run();
            try {
                child.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.result += child.result;
        }
    }

    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        System.out.println("sum threaded, size = " + size);
        long[] arr = new long[size];
        for (int i = 0; i < size; ++i) {
            arr[i] = i + 1;
        }
        SumThreaded sum = new SumThreaded(arr);
        long time = System.nanoTime();
        sum.run();
        time = System.nanoTime() - time;
        System.out.println("\tresult = " + sum.result);
        System.out.println("\ttime = " + (time * 1e-9));
    }
}

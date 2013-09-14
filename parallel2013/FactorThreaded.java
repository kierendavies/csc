public class FactorThreaded extends Thread {
    static int cutoff;

    long[] arr;
    int hi;
    int lo;
    long result;

    public FactorThreaded(long[] arr, int lo, int hi) {
        this.arr = arr;
        this.lo = lo;
        this.hi = hi;
        this.result = 0;
    }

    public FactorThreaded(long[] arr) {
        this(arr, 0, arr.length);
    }

    public static long countFactors(long n) {
        if (n == 1) return 0;  // code below only works for n > 1
        long factors = 1;
        long factor = 2;
        long count = 0;
        while (n > 1) {
            while (n % factor == 0) {
                n /= factor;
                ++count;
            }
            factors *= count + 1;
            ++factor;
            count = 0;
        }
        factors -= 2;
        return factors;
    }

    @Override
    public void run() {
        if (hi - lo <= cutoff) {
            for (int i = lo; i < hi; ++i) {
                this.result += countFactors(arr[i]);
            }
        } else {
            // only spawn one child thread; use this thread again for the other half
            FactorThreaded child = new FactorThreaded(arr, (lo + hi) / 2, hi);
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
        if (args.length >= 2) {
            FactorThreaded.cutoff = Integer.parseInt(args[1]);
        } else {
            FactorThreaded.cutoff = 10000;
        }
        System.out.println("sum threaded, size = " + size);
        long[] arr = new long[size];
        for (int i = 0; i < size; ++i) {
            arr[i] = i + 1;
        }
        FactorThreaded sum = new FactorThreaded(arr);
        long time = System.nanoTime();
        sum.run();
        time = System.nanoTime() - time;
        System.out.println("\tresult = " + sum.result);
        System.out.println("\ttime = " + (time * 1e-9));
    }
}

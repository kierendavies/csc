import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings("serial")
public class FactorThreaded extends RecursiveTask<Long> {
    static int cutoff;

    long[] arr;
    int hi;
    int lo;

    public FactorThreaded(long[] arr, int lo, int hi) {
        this.arr = arr;
        this.lo = lo;
        this.hi = hi;
    }

    public FactorThreaded(long[] arr) {
        this(arr, 0, arr.length);
    }

    public static long countFactors(long n) {
        if (n == 1) return 0;
        long factors = 0;
        double sqrt = Math.sqrt(n);
        for (int d = 2; d < sqrt; ++d) {
            if (n % d == 0) {
                factors += 2;
            }
        }
        if (sqrt * sqrt == n) {
            ++factors;
        }
        return factors;
    }

    @Override
    public Long compute() {
        long result = 0;
        if (hi - lo <= cutoff) {
            for (int i = lo; i < hi; ++i) {
                result += countFactors(arr[i]);
            }
        } else {
            // only spawn one child thread; use this thread again for the other half
            FactorThreaded child = new FactorThreaded(arr, (lo + hi) / 2, hi);
            this.hi = (lo + hi) / 2;
            child.fork();
            result = this.compute();
            result += child.join();
        }
        return result;
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

        ForkJoinPool pool = new ForkJoinPool();
        long time = System.nanoTime();
        long result = pool.invoke(new FactorThreaded(arr));
        time = System.nanoTime() - time;
        System.out.println("\tresult = " + result);
        System.out.println("\ttime = " + (time * 1e-9));
    }
}

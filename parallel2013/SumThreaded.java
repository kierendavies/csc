import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@SuppressWarnings("serial")
public class SumThreaded extends RecursiveTask<Long> {
    static final int CUTOFF = 10000;

    long[] arr;
    int hi;
    int lo;

    public SumThreaded(long[] arr, int lo, int hi) {
        this.arr = arr;
        this.lo = lo;
        this.hi = hi;
    }

    public SumThreaded(long[] arr) {
        this(arr, 0, arr.length);
    }

    @Override
    public Long compute() {
        long result = 0;
        if (hi - lo <= CUTOFF) {
            for (int i = lo; i < hi; ++i) {
                result += arr[i];
            }
        } else {
            // only spawn one child thread; use this thread again for the other half
            SumThreaded child = new SumThreaded(arr, (lo + hi) / 2, hi);
            this.hi = (lo + hi) / 2;
            child.fork();
            result = this.compute();
            result += child.join();
        }
        return result;
    }

    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        System.out.println("sum threaded, size = " + size);
        long[] arr = new long[size];
        for (int i = 0; i < size; ++i) {
            arr[i] = i + 1;
        }

        ForkJoinPool pool = new ForkJoinPool();
        long time = System.nanoTime();
        long result = pool.invoke(new SumThreaded(arr));
        time = System.nanoTime() - time;
        System.out.println("\tresult = " + result);
        System.out.println("\ttime = " + (time * 1e-9));
    }
}

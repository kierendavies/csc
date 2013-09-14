public class FactorSequential {

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

    public static long sum(long[] arr) {
        long res = 0;
        for (long i : arr) {
            res += countFactors(i);
        }
        return res;
    }

    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        System.out.println("factor sequential, size = " + size);
        long[] arr = new long[size];
        for (int i = 0; i < size; ++i) {
            arr[i] = i + 1;
        }
        long time = System.nanoTime();
        long result = sum(arr);
        time = System.nanoTime() - time;
        System.out.println("\tresult = " + result);
        System.out.println("\ttime = " + (time * 1e-9));
    }
}

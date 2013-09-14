public class SumSequential {

    public static long sum(long[] arr) {
        long res = 0;
        for (long i : arr) {
            res += i;
        }
        return res;
    }

    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        System.out.println("sum sequential, size = " + size);
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

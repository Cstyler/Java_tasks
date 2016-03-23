import java.util.Scanner;

abstract class Kth {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        long k = in.nextLong();
        System.out.println(findKthDigit(k));
    }

    private static long findKthDigit(long k) {
        long rightSum = 0, leftSum, temp = 1;
        for (int i = 1; ; i++) {
            leftSum = rightSum;
            rightSum += 9 * temp * i;
            temp *= 10;
            if (rightSum > k) {
                long resultNum = temp / 10 + (k - leftSum) / i;
                return getNthDigit(resultNum, (k - leftSum) % i);
            }
        }
    }

    private static long getNthDigit(long num, long i) {
        long j, temp = num;
        for (j = 0; temp != 0; j++) {
            temp /= 10;
        }
        return (num / (long) Math.pow(10, (j - i - 1))) % 10;
    }
}
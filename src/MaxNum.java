import java.util.Arrays;
import java.util.Scanner;

class MaxNum {

    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        int n = scn.nextInt();
        Integer[] array = new Integer[n];
        for (int i = 0; i < n; i++) {
            array[i] = scn.nextInt();
        }
        Arrays.sort(array, (a, b) -> Integer.parseInt(Integer.toString(b) + Integer.toString(a)) - Integer.parseInt(Integer.toString(a) + Integer.toString(b)));
        for (Integer e : array) {
            System.out.print(e);
        }
    }
}

import java.util.ArrayList;
import java.util.Scanner;

public class MinDist {
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        String str = scn.nextLine();
        char x = scn.next().charAt(0), y = scn.next().charAt(0);
        System.out.println(findMinDist(str, x, y));
    }

    private static Integer findMinDist(String str, char x, char y) {
        ArrayList<Integer> xEntries = new ArrayList<>(1), yEntries = new ArrayList<>(1);
        for (Integer i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == x) {
                xEntries.add(i);
            } else if (c == y) {
                yEntries.add(i);
            }
        }
        ArrayList<Integer> temp1, temp2;
        temp1 = xEntries.size() > yEntries.size() ? yEntries : xEntries;
        temp2 = xEntries.size() > yEntries.size() ? xEntries : yEntries;
        Integer minDist = Math.abs(xEntries.get(0) - yEntries.get(0)) - 1;
        for (Integer i :
                temp1) {
            int index = binarySearch(i, temp2, 0, temp2.size() - 1);
            if (index == -1 || index == temp2.size() - 1) {
                index = index == -1 ? 0 : index;
                int curDist = Math.abs(i - temp2.get(index)) - 1;
                minDist = curDist <= minDist ? curDist : minDist;
            } else {
                int first = Math.abs(i - temp2.get(index)) - 1, second = Math.abs(i - temp2.get(index + 1)) - 1;
                first = first < second ? first : second;
                minDist = first <= minDist ? first : minDist;
            }
        }
        return minDist;
    }

    private static int binarySearch(int x, ArrayList<Integer> array, int low, int high) {
        int mid = 0;
        if (x < array.get(0)) {
            return -1;
        } else if (x > array.get(array.size() - 1)) {
            return array.size() - 1;
        }
        while (low < high) {
            mid = low / 2 + high / 2;
            if (array.get(mid) < x && array.get(mid + 1) > x) {
                return mid;
            } else if (array.get(mid - 1) < x && array.get(mid) > x) {
                return mid - 1;
            } else if (array.get(mid) < x) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return mid;
    }
}
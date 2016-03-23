import java.util.ArrayList;
import java.util.Scanner;

class Econom {
    private static ArrayList<String> stack;

    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        String s = scn.next();
        stack = new ArrayList<>();
        if (s.length() == 1) {
            System.out.println(0);
            return;
        }
        parseRec(s);
        System.out.println(stack.size() + 1);
    }

    private static String parseRec(String s) {
        if (s.length() == 1) {
            return s;
        }
        String firstToken, secondToken;
        if (s.charAt(2) == '(') {
            int i = 3;
            for (int pCount = 1; ; i++) {
                if (s.charAt(i) == '(') {
                    pCount++;
                } else if (s.charAt(i) == ')') {
                    pCount--;
                }
                if (pCount == 0) {
                    break;
                }
            }
            firstToken = s.substring(2, i + 1);
            if (s.charAt(i + 1) == '(') {
                secondToken = s.substring(i + 1, s.length() - 1);
            } else {
                secondToken = s.substring(i + 1, i + 2);
            }
        } else {
            firstToken = s.substring(2, 3);
            secondToken = s.charAt(3) == '(' ? s.substring(3, s.length() - 1) : s.substring(3, 4);
        }
        if (firstToken.length() != 1 && stack.indexOf(firstToken) == -1) {
            stack.add(firstToken);
        }
        if (secondToken.length() != 1 && stack.indexOf(secondToken) == -1) {
            stack.add(secondToken);
        }
        return parseRec(firstToken) + parseRec(secondToken);
    }
}
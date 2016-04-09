import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Econom {
	private static Set<String> stringSet = new HashSet<>();

	public static void main(String[] args) {
		String s = new Scanner(System.in).next();
		if (s.length() == 1) {
			System.out.println(0);
			return;
		}
		parseRec(s);
		System.out.println(stringSet.size() + 1);
	}

	private static String parseRec(String s) {
		if (s.length() == 1) {
			return s;
		}
		String firstToken, secondToken;
		if (s.charAt(2) == '(') {
			int i = skip(3, s);
			firstToken = s.substring(2, i + 1);
			secondToken = s.charAt(i + 1) == '(' ? s.substring(i + 1, s.length() - 1) : s.substring(i + 1, i + 2);
		} else {
			firstToken = s.substring(2, 3);
			secondToken = s.charAt(3) == '(' ? s.substring(3, s.length() - 1) : s.substring(3, 4);
		}
		if (firstToken.length() != 1) {
			stringSet.add(firstToken);
		}
		if (secondToken.length() != 1) {
			stringSet.add(secondToken);
		}
		return parseRec(firstToken) + parseRec(secondToken);
	}

	private static int skip(int i, String s) {
		for (int parenCount = 1; ; i++) {
			parenCount += s.charAt(i) == '(' ? 1 : s.charAt(i) == ')' ? -1 : 0;
			if (parenCount == 0) {
				break;
			}
		}
		return i;
	}
}
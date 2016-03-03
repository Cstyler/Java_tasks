import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class Gauss {
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        int n = scn.nextInt();
        Fraction[][] matrix = new Fraction[n][n + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n + 1; j++) {
                matrix[i][j] = new Fraction(scn.nextInt(), 1);
            }
        }
        for (Fraction f :
                findSolutions(matrix, n)) {
            System.out.println(f);
        }
    }

    private static Fraction[] findSolutions(Fraction[][] matrix, int n) {
        if (!GaussMatrix.convertToIdentityMatrix(matrix, n)) {
            System.out.println("No solution");
            return new Fraction[0];
        }
        Fraction[] result = new Fraction[n];
        for (int i = 0; i < n; i++) {
            if (matrix[i][i].getNum() != 0) {
                result[i] = matrix[i][n].mul(matrix[i][i].reverse());
            } else {
                System.out.println("No solution");
                return new Fraction[0];
            }
        }
        return result;
    }
}

class GaussMatrix {
    public static boolean convertToIdentityMatrix(Fraction[][] matrix, int n) {
        for (int i = 0; i < n; i++) {
            if (matrix[i][i].getNum() == 0) {
                if (!searchNotZeroLine(matrix, i)) {
                    return false;
                }
            }
            matrix[i] = mulLine(matrix[i], matrix[i][i].reverse());
            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                matrix[j] = sumLines(matrix[j], mulLine(matrix[i], matrix[j][i].negative()));
            }
        }
        return true;
    }

    private static boolean searchNotZeroLine(Fraction[][] matrix, int i) {
        for (int j = i + 1; j < matrix[0].length - 1; j++) {
            if (matrix[j][i].getNum() != 0) {
                Fraction[] c = matrix[i];
                matrix[i] = matrix[j];
                matrix[j] = c;
                return true;
            }
        }
        return false;
    }

    private static Fraction[] mulLine(Fraction[] x, Fraction coefficient) {
        Fraction[] temp = new Fraction[x.length];
        for (int i = 0; i < x.length; i++) {
            temp[i] = x[i].mul(coefficient);
        }
        return temp;
    }

    private static Fraction[] sumLines(Fraction[] x, Fraction[] y) {
        Fraction[] temp = new Fraction[x.length];
        for (int i = 0; i < x.length; i++) {
            temp[i] = x[i].add(y[i]);
        }
        return temp;
    }
}

class Fraction {
    private int num, den;

    public Fraction(int num, int den) {
        this.num = num;
        this.den = den;
        normalize(this);
    }

    private static int GCD(int a, int b) {
        return b == 0 ? a : GCD(b, a % b);
    }

    private static int LCM(int a, int b) {
        return a * b / GCD(a, b);
    }

    public int getNum() {
        return num;
    }

    public Fraction add(Fraction x) {
        int temp = LCM(x.den, den);
        Fraction newF = new Fraction(num * temp / den + x.num * temp / x.den, temp);
        normalize(newF);
        return newF;
    }

    public Fraction mul(Fraction x) {
        Fraction newF = new Fraction(num * x.num, den * x.den);
        normalize(newF);
        return newF;
    }

    public Fraction reverse() {
        return new Fraction(den, num);
    }

    public Fraction negative() {
        return new Fraction(-num, den);
    }

    private void normalize(Fraction f) {
        int temp = GCD(f.num, f.den);
        if (Math.abs(temp) > 1) {
            f.num /= temp;
            f.den /= temp;
        }
    }

    public String toString() {
        if ((num < 0 && den < 0) || (num >= 0 && den < 0)) {
            return -num + "/" + -den;
        } else {
            return num + "/" + den;
        }
    }
}

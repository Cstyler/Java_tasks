import java.math.BigInteger;
import java.util.Scanner;

public class FastFib {
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        System.out.println(calcFib(scn.nextInt()));
    }

    private static BigInteger calcFib(int n) {
        BigInteger[][] a = {{BigInteger.ONE, BigInteger.ONE}, {BigInteger.ONE, BigInteger.ZERO}};
        a = Matrix.powMatrix(a, n - 1);
        return a[1][0].add(a[1][1]);
    }
}

class Matrix {
    private static final BigInteger[][] IDENTITY_MATRIX = {{BigInteger.ONE, BigInteger.ZERO}, {BigInteger.ZERO, BigInteger.ONE}};

    public static BigInteger[][] powMatrix(BigInteger[][] matrix, int n) {
        if (n == 0) {
            return IDENTITY_MATRIX;
        } else if (n % 2 == 0) {
            return powMatrix(mulMatrix(matrix, matrix), n / 2);
        } else {
            return mulMatrix(powMatrix(matrix, n - 1), matrix);
        }
    }

    private static BigInteger[][] mulMatrix(BigInteger[][] a, BigInteger[][] b) {
        int size = a[0].length;
        BigInteger[][] result = new BigInteger[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[i][j] = BigInteger.ZERO;
                for (int k = 0; k < size; k++) {
                    result[i][j] = result[i][j].add(a[i][k].multiply(b[k][j]));
                }

            }
        }
        return result;
    }

    private static BigInteger calcScalarProduct(BigInteger[] x, BigInteger[] y) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < x.length; i++) {
            result = result.add(x[i].multiply(y[i]));
        }
        return result;
    }

    private static BigInteger[] getColumn(BigInteger[][] x, int n) {
        int size = x[0].length;
        BigInteger[] column = new BigInteger[size];
        for (int i = 0; i < size; i++) {
            column[i] = x[i][n];
        }
        return column;
    }
}

import java.util.*;
import static java.lang.Math.*;

public class E {
    static void p(Object ...args) {System.out.println(Arrays.deepToString(args));}
    static long MOD = 1_000_000_007;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        long N = in.nextLong();
        int K = in.nextInt();

        int need = 4;
        long temp2 = N + 100000;
        while (temp2 > 0) {
            need++;
            temp2 /= K;
        }

        long[][][] moveBy = new long[need][][];
        long[][] prefix = identity(K+1);
        for (int i = 1; i < need - 1; i++) {
            moveBy[i] = multiply(prefix, makeMatrix(K, i+1));
            prefix = multiply(pow(moveBy[i], K-1), prefix);
        }

        long at = 0;
        long[][] f = identity(K+1);
        long temp = N / K;
        for (int i = 1; temp > 0; i++) {
            f = multiply(pow(moveBy[i], (int)(temp % K)), f);
            temp /= K;
        }
        long[] start = new long[K+1];
        for (int i = 0; i < start.length; i++)
            start[i] = (1L<<(i)) % MOD;
        start[K] = 1;

        int position = (int)(N % K);
        long ans = 0;
        for (int i = 0; i < start.length; i++)
            ans += (start[i] * f[i][position]) % MOD;
        ans %= MOD;

        System.out.println(ans);
    }
    static long[][] identity(int A) {
        long[][] ans = new long[A][A];
        for (int i = 0; i < A; i++)
            ans[i][i] = 1;
        return ans;
    }
    static long[][] pow(long[][] M, int p) {
        long[][] ans = new long[M.length][M.length];
        for (int i = 0; i < ans.length ;i++)
            ans[i][i] = 1;
        long[][] go = new long[M.length][M.length];
        for (int i = 0; i < go.length ;i++)
            for (int j = 0; j < go.length ;j++)
                go[i][j] = M[i][j];
        while (p > 0) {
            if (p % 2 == 1) {
                ans = multiply(ans, go);
            }
            p /= 2;
            go = multiply(go, go);
        }
        return ans;
    }
    static long[][] makeMatrix(int K, int a) {
        long[][] res = new long[K+1][K+1];

        int[] v = new int[2*K];
        v[2*K-1] = 0;
        for (int i = 2*K-2; i >= 0; i--) {
            if (i == K-1) {
                v[i] = (K + (K-a + v[i+1]) % K) % K;
            } else {
                v[i] = (K + (K-1 + v[i+1]) % K) % K;
            }
        }
        for (int i = 0; i <= K; i++)
            res[K][i]++;
        for (int c = 0; c < K; c++) {
            for (int i = 0; i < K; i++) {
                boolean found = false;
                for (int j = c - 1 + K; j >= 0; j--) {
                    if (v[j] == i) {
                        found = true;
                        if (j < K) {
                            res[j][c] += 1;
                        } else {
                            for (int b = 0; b <= K; b++)
                                res[b][c] += res[b][j-K];
                        }
                        break;
                    }
                }
                if (!found) {
                    p(c, i, v);
                    throw new RuntimeException();
                }
            }
        }
        return res;
    }
    static long[][] multiply(long[][] A, long[][] B) {
        long[][] res = new long[A.length][A.length];
        for (int r = 0; r < A.length; r++) 
            for (int c = 0; c < A.length; c++) {
                for (int i = 0; i < A.length; i++)
                    res[r][c] += (A[r][i] * B[i][c]) % MOD;
                res[r][c] %= MOD;
            }
        return res;
    }
 }

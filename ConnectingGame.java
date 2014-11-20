/*
 * SRM 637
 */
import java.util.*;
import static java.lang.Math.*;
public class ConnectingGame {
    public String isValid(String[] B) {
        int N = B.length;
        int M = B[0].length();
        int[][] G = new int[200][200];
        int source = G.length - 2;
        int sink = G.length - 1;
        int sink1 = G.length - 3;
        int sink2 = G.length - 4;
        int sink3 = G.length - 5;
        int sink4 = G.length - 6;

        G[sink1][sink] = 1;
        G[sink2][sink] = 1;
        G[sink3][sink] = 1;
        G[sink4][sink] = 1;

        for (int i = 0; i < 62; i++) {
            G[2*i][2*i+1] = 1;
        }
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < M; c++) {
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nr = r + dr;
                        int nc = c + dc;
                        if (nr < 0 || nr >= N || nc < 0 || nc >= M)
                            continue;
                        int num1 = toNum(B[r].charAt(c));
                        int num2 = toNum(B[nr].charAt(nc));
                        if (num1 != num2) {
                            G[2*num1 + 1][2*num2] = 100;
                            G[2*num2 + 1][2*num1] = 100;
                        }
                    }
                }
            }
        }
        for (int c = 0; c < M; c++) {
            int num1 = toNum(B[0].charAt(c));
            G[2*num1 + 1][sink1] = 1;
            int num2 = toNum(B[N-1].charAt(c));
            G[2*num2 + 1][sink2] = 1;
        }
        for (int r = 0; r < N; r++) {
            int num1 = toNum(B[r].charAt(0));
            G[2*num1 + 1][sink3] = 1;
            int num2 = toNum(B[r].charAt(M-1));
            G[2*num2 + 1][sink4] = 1;
        }

        int[][] copy = new int[200][200];
        for (int r = 0; r + 1 < N; r++) {
            for (int c = 0; c + 1 < M; c++) {
                int num1 = toNum(B[r].charAt(c));
                int num2 = toNum(B[r+1].charAt(c));
                int num3 = toNum(B[r].charAt(c+1));
                int num4 = toNum(B[r+1].charAt(c+1));
                if (num1 == num2 || num1 == num3 || num1 == num4 ||
                    num2 == num3 || num2 == num4 || num3 == num4)
                    continue;
                for (int a = 0; a < G.length; a++)
                    for (int b = 0; b < G.length; b++)
                        copy[a][b] = G[a][b];
                copy[source][2*num1] = 1;
                copy[source][2*num2] = 1;
                copy[source][2*num3] = 1;
                copy[source][2*num4] = 1;
                //System.out.format("checking: (%d,%d)\n", r, c);
                if (maxflow(source, sink, copy) == 4) {
                    return "invalid";
                }
            }
        }

        return "valid";
    }
    int toNum(char ch) {
        if ('a' <= ch && ch <= 'z')
            return ch - 'a';
        else if ('A' <= ch && ch <= 'Z')
            return 26 + (ch - 'A');
        else
            return 52 + (ch - '0');
    }
    int maxflow(int source, int sink, int[][] G) {
        int flow = 0;
        boolean[] V = new boolean[G.length];
        while (true) {
            Arrays.fill(V, false);
            int nf = augment(source, sink, V, G);
            if (nf == 0)
                break;
            flow += nf;
        }
        //System.out.format("got flow: %d\n", flow);
        return flow;
    }
    int augment(int at, int sink, boolean[] V, int[][] G) {
        //System.out.format("\taugment: %d %d\n", at, sink);
        if (at == sink)
            return 1;
        if (V[at])
            return 0;
        V[at] = true;
        for (int i = 0; i < G.length; i++) {
            if (G[at][i] == 0)
                continue;
            int nf = augment(i, sink, V, G);
            if (nf == 0)
                continue;
            G[at][i]--;
            G[i][at]++;
            return 1;
        }
        return 0;
    }
}

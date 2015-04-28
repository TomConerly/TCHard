/*
 * SRM 657
 *
 * After accounting for the other of rows/columns there are only two possible
 * orientations for each component. Basically you place two rooks that are
 * connected. Then a rook connected to both must be in the same row, a rook
 * connected to one but not the other must be in that column. Keep on going and
 * get the total number of columns/rows used by that component.
 *
 * Then do a DP because you can use each component as (r, c) or (c, r).
 *
 */
import java.util.*;
import static java.lang.Math.*;


public class RookGraph {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  public int countPlacements(int NN, String[] graph) {
      choose = new long[200][200];
      choose[0][0] = 1;
      for (int n = 1; n < choose.length; n++)
          for (int k = 0; k <= n; k++)
              choose[n][k] = (choose[n-1][k] + (k > 0 ? choose[n-1][k-1] : 0)) % MOD;

      fact = new long[200];
      fact[0] = 1;
      for (int i = 1; i < fact.length; i++)
          fact[i] = (fact[i-1] * i)%MOD;

      N = NN;
      K = graph.length;
      G = new boolean[K][K];
      for (int i = 0; i < K; i++)
          for (int j = 0; j < K; j++)
              G[i][j] = graph[i].charAt(j) == '1';

      V = new boolean[K];
      R = new int[K];
      Arrays.fill(R, -1);
      C = new int[K];
      Arrays.fill(C, -1);
      previous = new int[K];
      Arrays.fill(previous, -1);
      moves = new ArrayList<Move>();
      int at = 0;
      for (int i = 0; i < K; i++) {
          if (V[i])
              continue;
          int column = 1;
          int row = 1;
          set(i, at, at, -1);
          for (int j = 0; j < K; j++) {
              if (V[j] || !G[i][j])
                  continue;
              set(j, at, at + 1, i);
              set(i, at, at, j);
              column++;
              break;
          }
          while (true) {
              boolean found = false;
              for (int j = 0; j < K; j++) {
                  if (V[j] || (R[j] == -1 && C[j] == -1))
                      continue;
                  int r = R[j] != -1 ? R[j] : row++ + at;
                  int c = C[j] != -1 ? C[j] : column++ + at;
                  set(j, r, c, previous[j]);
                  found = true;
              }
              if (!found)
                  break;
          }
          at += 1000;
          moves.add(new Move(row, column));
      }
      for (int i = 0; i < K; i++) {
          for (int j = i+1; j < K; j++) {
              if (R[i] == R[j] && C[i] == C[j])
                  return 0;
              if ((R[i] == R[j] || C[i] == C[j]) != G[i][j])
                  return 0;
          }
      }
      DP = new long[moves.size()][N+1][N+1];
      for (int a = 0; a < DP.length;a++)
          for (int b = 0; b < DP[0].length;b++)
              Arrays.fill(DP[a][b], -1);
      return (int)(solve(0, N, N) % MOD);
  }
  ArrayList<Move> moves;
  int N;
  long[][][] DP;
  long[][] choose;
  long[] fact;
  int K;
  boolean[][] G;
  int[] R, C, previous;
  boolean[] V;
  long MOD = 1_000_000_007L;

  long solve(int at, int rleft, int cleft) {
      if (rleft < 0 || cleft < 0)
          return 0;
      if (at == moves.size()) {
          return 1;
      }
      if (DP[at][rleft][cleft] != -1)
          return DP[at][rleft][cleft];

      long s1 = solve(at + 1, rleft - moves.get(at).r, cleft - moves.get(at).c);
      if (s1 > 0) {
          s1 = (s1 * choose[rleft][moves.get(at).r]) % MOD;
          s1 = (s1 * choose[cleft][moves.get(at).c]) % MOD;
      }

      long s2 = solve(at + 1, rleft - moves.get(at).c, cleft - moves.get(at).r);
      if (s2 > 0) {
          s2 = (s2 * choose[rleft][moves.get(at).c]) % MOD;
          s2 = (s2 * choose[cleft][ moves.get(at).r]) % MOD;
      }
      if (moves.get(at).c == 1 && moves.get(at).r == 1) {
          s2 = 0;
      }

      long ans = (s1 + s2) % MOD;
      ans = (ans * fact[moves.get(at).r]) % MOD;
      ans = (ans * fact[moves.get(at).c]) % MOD;
      DP[at][rleft][cleft] = ans;
      return ans;

  }
  void set(int i, int r, int c, int prev) {
      R[i] = r;
      C[i] = c;
      V[i] = true;
      previous[i] = prev;
      if (prev == -1)
          return;
      boolean rowShared = R[i] == R[prev];
      for (int j = 0; j < K; j++) {
          if (V[j] || !G[i][j])
              continue;
          previous[j] = i;
          if (G[prev][j] ^ rowShared) {
              C[j] = c;
          } else {
              R[j] = r;
          }
      }
  }
  class Move {
      int r, c;
      public Move(int r, int c) {
          this.r = r;
          this.c = c;
      }
  }
}

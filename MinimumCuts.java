/*
 * TCO 2015 2B
 *
 * dp[at][score if return to parent]
 *
 * To compute a state look at the 3 different moves (left, right, parent) left.
 * To a dp over all 8 states.
 */
import java.util.*;
import static java.lang.Math.*;

public class MinimumCuts {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  public int costPaid(int[] p, int[] c) {
      N = p.length + 1;
      child = new int[N][2];
      for (int[] chil : child)
          Arrays.fill(chil, -1);
      cost = new int[N][2];
      parent = new int[N];
      pcost = new int[N];
      parent[0] = -1;
      for (int i = 0; i < N - 1; i++) {
          parent[i+1] = p[i];
          pcost[i+1] = c[i];
          if (child[parent[i+1]][0] == -1) {
              child[parent[i+1]][0] = i + 1;
              cost[parent[i+1]][0] = c[i];
          } else {
              child[parent[i+1]][1] = i + 1;
              cost[parent[i+1]][1] = c[i];
          }
      }
      DP = new int[N][21000];
      for (int[] dp : DP)
          Arrays.fill(dp, -1);
      return solve(0, 0);
  }
  int N;
  int[] parent, pcost;
  int[][] child, cost;
  int[][] DP;
  int INF = 20001;

  int solve(int at, int trapp) {
      if (child[at][0] == -1)
          return INF;
      if (DP[at][trapp] != -1)
          return DP[at][trapp];
      int[] scores = new int[8];
      for (int i = 1; i < scores.length; i++) {
          for (int j = 0; j < 3; j++) {
              if ((i & (1<<j)) == 0)
                  continue;
              if (j == 2) {
                  scores[i] = max(scores[i], min(trapp, pcost[at] + scores[i - (1<<j)]));
              } else {
                  if (child[at][j] == -1) {
                      scores[i] = max(scores[i], scores[i - (1<<j)]);
                  } else {
                      scores[i] = max(scores[i], min(cost[at][j] + scores[i - (1<<j)], solve(child[at][j], scores[i-(1<<j)])));
                  }
              }
          }
      }
      DP[at][trapp] = scores[7];
      return DP[at][trapp];
  }

}

/*
 * SRM 638
 *
 * If you know the last piece burned and the total time you can check whether
 * that is possible. You will light all leaves who are further than time from
 * the last piece burned. You can use dijkstra's to simulate burning. So we
 * can check in O(N log N).
 *
 * There are 2 cases we have to check:
 * 1) We end at a leaf and the time is the distance from another leaf to this
 *    one.
 * 2) The last point is the midpoint between two leaves. 
 *
 * In both cases there are N^2 possibilities so we can do the entire algorithm
 * in O(N^2 log N).
 * 
 */
import java.util.*;
import static java.lang.Math.*;

public class CandleTimer {
    public int differentTime(int[] A, int[] B, int[] L) {
        for (int i = 0; i < L.length; i++)
            L[i] *= 2;
        N = A.length + 1;
        G = new ArrayList[N];
        E = new ArrayList<Edge>();
        for (int i = 0; i < N; i++)
            G[i] = new ArrayList<Edge>();
        for (int i = 0; i < A.length; i++) {
            Edge forward = new Edge(A[i], B[i], L[i]);
            G[A[i]].add(forward);
            E.add(forward);
            Edge backward = new Edge(B[i], A[i], L[i]);
            G[B[i]].add(backward);
            E.add(backward);
        }

        dist = getAllDists();

        HashSet<Integer> ans = new HashSet<Integer>();
        ArrayList<Edge> path = new ArrayList<Edge>();
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                if (G[i].size() != 1 || G[j].size() != 1)
                    continue;
                if (isPossible(i, j)) {
                    ans.add(dist[i][j] / 2);
                }
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (G[i].size() != 1 || G[j].size() != 1)
                    continue;
                if (isPossible2(i, j)) {
                    ans.add(dist[i][j]);
                }
            }
        }
        return ans.size();
    }
    int N;
    ArrayList<Edge>[] G;
    ArrayList<Edge> E;
    int[][] dist;

    public class Edge {
        int st, en, len;
        public Edge(int st, int en, int len) {
            this.st = st;
            this.en = en;
            this.len = len;
        }
    }

    int[][] getAllDists() {
        int[][] dist = new int[N][N];
        for (int[] d : dist)
            Arrays.fill(d, 100000000);
        for (int i = 0; i < N; i++)
            dist[i][i] = 0; 
        for (Edge e : E)
            dist[e.st][e.en] = e.len;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                for (int k = 0; k < N; k++)
                    dist[j][k] = min(dist[j][k], dist[j][i] + dist[i][k]);
        return dist;
    }

    boolean isPossible(int a, int b) {
        int burn = dist[a][b] / 2;
        Edge mid = null;
        for (Edge e : E) {
            if (dist[a][e.st] + e.len + dist[b][e.en] == dist[a][b] &&
                abs(dist[a][e.st] - dist[b][e.en]) <= e.len) {
                mid = e;
                break;
            }
        }
        // midpoint is t units from mid.st towards mid.en
        int t = (mid.len - (dist[a][mid.st] - dist[b][mid.en])) / 2;

        ArrayList<Integer> start = new ArrayList<Integer>();
        for (int i = 0; i < N; i++) {
            if (G[i].size() != 1 || min(dist[i][mid.st] + t, dist[i][mid.en] + mid.len - t) < burn)
                continue;
            start.add(i);
        }
        return canBurnInExactly(start, burn);
    }
    boolean isPossible2(int a, int b) {
        int burn = dist[a][b];
        ArrayList<Integer> start = new ArrayList<Integer>();
        for (int i = 0; i < N; i++) {
            if (G[i].size() != 1 || dist[i][b] < burn)
                continue;
            start.add(i);
        }
        return canBurnInExactly(start, burn);
    }
    boolean canBurnInExactly(ArrayList<Integer> start, int burn) {
        int[] burnTime = new int[N];
        Arrays.fill(burnTime, Integer.MAX_VALUE);
        PriorityQueue<State> pq = new PriorityQueue<State>();
        for (int st : start) {
            burnTime[st] = 0;
            pq.add(new State(st, 0));
        }
        while (pq.size() > 0) {
            State s = pq.poll();
            if (burnTime[s.at] < s.dist)
                continue;
            for (Edge e : G[s.at]) {
                int newTime = s.dist + e.len;
                if (newTime < burnTime[e.en]) {
                    burnTime[e.en] = newTime;
                    pq.add(new State(e.en, newTime));
                }
            }
        }
        for (Edge e : E) {
            int burnedFromStart = max(0, burn - burnTime[e.st]);
            int burnedFromEnd = max(0, burn - burnTime[e.en]);
            if (burnedFromStart + burnedFromEnd < e.len) {
                return false;
            }
        }
        return true;
    }
    public class State implements Comparable<State> {
        int at, dist;
        public State(int at, int dist) {
            this.at = at;
            this.dist = dist;
        }

        public int compareTo(State s) {
            if (dist < s.dist)
                return -1;
            if (dist > s.dist)
                return 1;
            return 0;
        }
    }
}

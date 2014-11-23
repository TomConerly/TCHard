/*
 * SRM 633
 *
 * You can deal with each prime separately. The resulting problem is assigning
 * a number to each vertex that satisifies rules that are min of two nodes = v
 * or max of two nodes = v. This is just 2sat (either one node or the other
 * must be the required value).
 */
import java.util.*;
import java.util.stream.*;
import static java.lang.Math.*;
import java.math.*;


public class GCDLCM {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  String valid = "Solution exists";
  String invalid = "Solution does not exist";
  public String possible(int N, String type, int[] A, int[] B, int[] C) {
      long t0 = System.currentTimeMillis();
      int M = A.length;
      Set<Integer> primes = Arrays.stream(C).flatMap(c -> IntStream.range(2, 100000).filter(d -> c % d == 0))
                                  .boxed().collect(Collectors.toSet());
      primes = primes.stream().filter(p -> BigInteger.valueOf(p).isProbablePrime(30)).collect(Collectors.toSet());
      for (int prime : primes) {
          int[] limit = Arrays.stream(C).map(c -> {
              int ans = 0;
              while (c % prime == 0) {
                  ans++;
                  c /= prime;
              }
              return ans;
          }).toArray();
          int[] min = new int[N];
          Arrays.fill(min, 0);
          int[] max = new int[N];
          Arrays.fill(max, 100);
          TreeMap<Vertex, ArrayList<Vertex>> G = new TreeMap<>();
          for (int i = 0; i < M; i++) {
              if (type.charAt(i) == 'G') {
                  min[A[i]] = max(min[A[i]], limit[i]);
                  min[B[i]] = max(min[B[i]], limit[i]);
              } else {
                  max[A[i]] = min(max[A[i]], limit[i]);
                  max[B[i]] = min(max[B[i]], limit[i]);
              }
          }
          for (int i = 0; i < N; i++)
              if (max[i] < min[i]) {
                  return invalid;
              }
          for (int i = 0; i < M; i++) {
              boolean aok = min[A[i]] <= limit[i] && limit[i] <= max[A[i]];
              boolean bok = min[B[i]] <= limit[i] && limit[i] <= max[B[i]];
              if (!aok && !bok)  {
                  return invalid;
              } else if (aok && bok) {
                  addEdge(G, A[i], B[i], limit[i], limit[i], false);
              } else if (aok) {
                  addEdge(G, A[i], -1, limit[i], -1, false);
              } else if (bok) {
                  addEdge(G, B[i], -1, limit[i], -1, false);
              }
          }
          for (int i = 0; i < N; i++) {
              for (int value1 = 0; value1 <= 40; value1++) {
                  Vertex v1 = new Vertex(i, value1, false);
                  if (!G.containsKey(v1))
                      continue;
                  for (int value2 = value1 + 1; value2 <= 40; value2++) {
                      Vertex v2 = new Vertex(i, value2, false);
                      if (!G.containsKey(v2))
                          continue;
                      addEdge(G, i, i, value1, value2, true);
                  }
              }
          }
          TreeSet<Vertex> visited = new TreeSet<>();
          for (Vertex v : G.keySet()) {
              if (v.negated)
                  continue;
              visited.clear();
              Vertex negated = new Vertex(v.node, v.value, !v.negated);
              if (dfs(v, negated, visited, G)) {
                  visited.clear();
                  if (dfs(negated, v, visited, G)) {
                      return invalid;
                  }
              }
          }
          Vertex t = new Vertex(-1, -1, false);
          Vertex f = new Vertex(-1, -1, true);
          if (G.containsKey(t)) {
              visited.clear();
              if (dfs(t, f, visited, G))
                  return invalid;
              visited.clear();
              if (dfs(f, t, visited, G))
                  return invalid;
          }
      }
      long t1 = System.currentTimeMillis();
      p(t1 - t0);
      return valid;
  }

  boolean dfs(Vertex at, Vertex goal, TreeSet<Vertex> visited, TreeMap<Vertex, ArrayList<Vertex>> G) {
      if (at.compareTo(goal) == 0)
          return true;
      if (visited.contains(at))
          return false;
      visited.add(at);
      for (Vertex v : G.get(at))
          if (dfs(v, goal, visited, G))
                  return true;
      return false;
  }

  Vertex addVertex(TreeMap<Vertex, ArrayList<Vertex>> G, int node, int value, boolean negated) {
      Vertex v = new Vertex(node, value, negated);
      if (!G.containsKey(v))
          G.put(v, new ArrayList<Vertex>());
      return v;
  }

  void addEdge(TreeMap<Vertex, ArrayList<Vertex>> G, int a, int b, int value1, int value2, boolean negated) {
        Vertex v1 = addVertex(G, a, value1, negated);
        Vertex nv1 = addVertex(G, a, value1, !negated);
        Vertex v2 = addVertex(G, b, value2, negated);
        Vertex nv2 = addVertex(G, b, value2, !negated);
        //p("adding edge", a, value1, negated, "to", b, value2, negated);
        G.get(nv1).add(v2);
        G.get(nv2).add(v1);
  }

  class Vertex implements Comparable<Vertex> {
      int node;
      int value;
      boolean negated;
      public Vertex(int node, int value, boolean negated) {
          this.node = node;
          this.value = value;
          this.negated = negated;
      }
      public int compareTo(Vertex v) {
          if (node < v.node)
              return -1;
          if (node > v.node)
              return 1;
          if (value < v.value)
              return -1;
          if (value > v.value)
              return 1;
          if (!negated && v.negated)
              return -1;
          if (negated && !v.negated)
              return 1;
          return 0;
      }
  }

// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			GCDLCMHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				GCDLCMHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class GCDLCMHarness {
	public static void run_test(int casenum) {
		if (casenum != -1) {
			if (runTestCase(casenum) == -1)
				System.err.println("Illegal input! Test case " + casenum + " does not exist.");
			return;
		}
		
		int correct = 0, total = 0;
		for (int i=0;; ++i) {
			int x = runTestCase(i);
			if (x == -1) {
				if (i >= 100) break;
				continue;
			}
			correct += x;
			++total;
		}
		
		if (total == 0) {
			System.err.println("No test cases run.");
		} else if (correct < total) {
			System.err.println("Some cases FAILED (passed " + correct + " of " + total + ").");
		} else {
			System.err.println("All " + total + " tests passed!");
		}
	}
	
	static boolean compareOutput(String expected, String result) { return expected.equals(result); }
	static String formatResult(String res) {
		return String.format("\"%s\"", res);
	}
	
	static int verifyCase(int casenum, String expected, String received) { 
		System.err.print("Example " + casenum + "... ");
		if (compareOutput(expected, received)) {
			System.err.println("PASSED");
			return 1;
		} else {
			System.err.println("FAILED");
			System.err.println("    Expected: " + formatResult(expected)); 
			System.err.println("    Received: " + formatResult(received)); 
			return 0;
		}
	}

	static int runTestCase(int casenum__) {
		switch(casenum__) {
		case 0: {
			int n                     = 4;
			String type               = "GLGLGLGL";
			int[] A                   = {0,0,1,1,2,2,3,3};
			int[] B                   = {1,1,2,2,3,3,0,0};
			int[] C                   = {6,12,6,12,6,12,6,12};
			String expected__         = "Solution exists";

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}
		case 1: {
			int n                     = 5;
			String type               = "GLGLGLGLGL";
			int[] A                   = {0,0,1,1,2,2,3,3,4,4};
			int[] B                   = {1,1,2,2,3,3,4,4,0,0};
			int[] C                   = {6,12,6,12,6,12,6,12,6,12};
			String expected__         = "Solution does not exist";

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}
		case 2: {
			int n                     = 11;
			String type               = "GGGGGGGGGG";
			int[] A                   = {0,0,0,0,0,0,0,0,0,0};
			int[] B                   = {1,2,3,4,5,6,7,8,9,10};
			int[] C                   = {2,3,5,7,11,13,17,19,23,29};
			String expected__         = "Solution exists";

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}
		case 3: {
			int n                     = 12;
			String type               = "GLLGGGLGLLGL";
			int[] A                   = {0,1,2,3,4,5,6,7,8,9,10,11};
			int[] B                   = {1,2,3,4,5,6,7,8,9,10,11,0};
			int[] C                   = {1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000};
			String expected__         = "Solution exists";

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}
		case 4: {
			int n                     = 12;
			String type               = "GLLGGGLGLLGL";
			int[] A                   = {0,1,2,3,4,5,6,7,8,9,10,11};
			int[] B                   = {1,2,3,4,5,6,7,8,9,10,11,0};
			int[] C                   = {1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,1000000000,999999999};
			String expected__         = "Solution does not exist";

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}
		case 5: {
			int n                     = 200;
			String type               = "G";
			int[] A                   = {11};
			int[] B                   = {20};
			int[] C                   = {19911120};
			String expected__         = "Solution exists";

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}

		// custom cases

      case 6: {
            Random R = new Random(23234234L);
			int n                     = 200;
			String type               = "";
            for (int i = 0; i < n; i++)
                type += R.nextBoolean() ? "G" : "L";
			int[] A                   = new int[n];
			int[] B                   = new int[n];
            for (int i = 0; i < n; i++) {
                A[i] = R.nextInt(200);
                B[i] = R.nextInt(200);
            }
			int[] C                   = new int[n];
            for (int i = 0; i < n; i++) {
                C[i] = 1;
                int k = R.nextInt(30);
                for (int j = 0; j < k; j++)
                    C[i] *= 2;
            }
			String expected__         = "Solution does not exist";

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}
      case 7: {
			int n                     = 4;
			String type               = "GLLLG";
			int[] A                   = {0, 0, 1, 1, 2};
			int[] B                   = {1, 1, 2, 3, 3};
			int[] C                   = {2, 2, 4, 8, 1};
			String expected__         = "Solution does not exist";

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}
/*      case 8: {
			int n                     = ;
			String type               = ;
			int[] A                   = ;
			int[] B                   = ;
			int[] C                   = ;
			String expected__         = ;

			return verifyCase(casenum__, expected__, new GCDLCM().possible(n, type, A, B, C));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

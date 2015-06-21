/*
 * TCO 2015 2A
 * The edges on the outside of the final structure will be the edges of the
 * triangles. So it's simple if all prob=1. For each edge we only need to know
 * the average starting coordinates (need to remember that if this edge is in
 * the other edges in the same tri must also be in).
 */
import java.util.*;
import static java.lang.Math.*;


public class TrianglePainting {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  public double expectedArea(int[] x1, int[] y1, int[] x2, int[] y2, int[] prob) {
      int N = x1.length;
      Edge[] E = new Edge[N*3];
      double ans1 = 0;
      Edge[] tri = new Edge[3];
      for (int i = 0; i < N; i++) {
          if (-y1[i] * x2[i] + x1[i]*y2[i] < 0) {
              int temp = x1[i];
              x1[i] = x2[i];
              x2[i] = temp;
              temp = y1[i];
              y1[i] = y2[i];
              y2[i] = temp;
          }

          E[3*i] = new Edge(x1[i], y1[i], prob[i] / 100.0);
          E[3*i + 1] = new Edge(x2[i] - x1[i], y2[i] - y1[i], prob[i] / 100.0);
          E[3*i + 2] = new Edge(-x2[i], -y2[i], prob[i] / 100.0);

          double area = abs(x1[i] * y2[i] - x2[i] * y1[i]) / 2.0;
          ans1 += area * prob[i] / 100.0;

          tri[0] = E[3*i];
          tri[1] = E[3*i + 1];
          tri[2] = E[3*i + 2];
          Arrays.sort(tri);
          ans1 -= score(tri);
      }
      Arrays.sort(E);
      return ans1 + score(E);
  }
  static double score(Edge[] E) {
      double sx = 0;
      double sy = 0;
      double ans2 = 0.0;
      for (Edge e : E) {
          ans2 += e.p * (sx * e.y - sy * e.x);
          sx += e.p * e.x;
          sy += e.p * e.y;
      }
      return ans2 / 2;
  }
  static class Edge implements Comparable<Edge> {
      int x, y;
      double p;
      public Edge(int x, int y, double p) {
          this.x = x;
          this.y = y;
          this.p = p;
      }
      public boolean top() {
          return y > 0 || (y == 0 && x > 0);
      }
      public int compareTo(Edge e) {
          if (top() && !e.top())
              return -1;
          if (!top() && e.top())
              return 1;
          int det = e.x * -y +  e.y * x;
          if (det > 0)
              return -1;
          else if (det < 0)
              return 1;
          else
              return 0;
      }
  }


// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			TrianglePaintingHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				TrianglePaintingHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class TrianglePaintingHarness {
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
	
	static final double MAX_DOUBLE_ERROR = 1E-9;
	static boolean compareOutput(double expected, double result){ if(Double.isNaN(expected)){ return Double.isNaN(result); }else if(Double.isInfinite(expected)){ if(expected > 0){ return result > 0 && Double.isInfinite(result); }else{ return result < 0 && Double.isInfinite(result); } }else if(Double.isNaN(result) || Double.isInfinite(result)){ return false; }else if(Math.abs(result - expected) < MAX_DOUBLE_ERROR){ return true; }else{ double min = Math.min(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR)); double max = Math.max(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR)); return result > min && result < max; } }
	static double relativeError(double expected, double result) { if (Double.isNaN(expected) || Double.isInfinite(expected) || Double.isNaN(result) || Double.isInfinite(result) || expected == 0) return 0; return Math.abs(result-expected) / Math.abs(expected); }
	
	static String formatResult(double res) {
		return String.format("%.10g", res);
	}
	
	static int verifyCase(int casenum, double expected, double received) { 
		System.err.print("Example " + casenum + "... ");
		if (compareOutput(expected, received)) {
			System.err.print("PASSED");
			double rerr = relativeError(expected, received);
			if (rerr > 0) System.err.printf(" (relative error %g)", rerr);
			System.err.println();
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
			int[] x1                  = {1,-2,-4};
			int[] y1                  = {2,3,-1};
			int[] x2                  = {2,2,-2};
			int[] y2                  = {-1,-1,1};
			int[] prob                = {100,100,100};
			double expected__         = 52.5;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}
		case 1: {
			int[] x1                  = {1,-2,-4};
			int[] y1                  = {2,3,-1};
			int[] x2                  = {2,2,-2};
			int[] y2                  = {-1,-1,1};
			int[] prob                = {50,50,50};
			double expected__         = 15.0;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}
		case 2: {
			int[] x1                  = {1};
			int[] y1                  = {1};
			int[] x2                  = {1};
			int[] y2                  = {-1};
			int[] prob                = {1};
			double expected__         = 0.01;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}
		case 3: {
			int[] x1                  = {1,1,1,1,1,1,1,1,1,1};
			int[] y1                  = {-1,1,-1,1,-1,1,-1,1,-1,1};
			int[] x2                  = {1,1,1,1,1,1,1,1,1,1};
			int[] y2                  = {1,-1,1,-1,1,-1,1,-1,1,-1};
			int[] prob                = {10,20,30,40,50,60,70,80,90,100};
			double expected__         = 31.899999999999995;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}
		case 4: {
			int[] x1                  = {-6,-2,-10,9,8,-1,10,5,7,3};
			int[] y1                  = {-5,2,-5,6,6,-10,8,7,-4,-5};
			int[] x2                  = {5,-1,-1,-8,6,7,10,-6,6,3};
			int[] y2                  = {0,-5,-7,4,10,0,10,-3,-3,-4};
			int[] prob                = {71,100,43,59,51,41,11,53,3,27};
			double expected__         = 940.1964999999999;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}
		case 5: {
			int[] x1                  = {34,-71,19,78,69,-73,27,64,-100,70,-87,50,8,-97,46,-46,-30,-40,-30,-23,77,81,48,93,-40,70, 37,-66,53,-87,-85,38,-90,63,-16,24,-2,-60,-88,67,-56,-8,-80,-19,-84,35,95,-24,-26,-15};
			int[] y1                  = {58,-24,-80,33,-98,61,99,79,-34,29,-1,-70,70,90,43,25,-50,-54,73,18,36,8,41,3,26,-6,-80, -22,65,33,-100,-1,80,-19,-6,-8,-4,-86,-43,-34,0,-93,-61,92,74,-77,32,-78,-56,-21};
			int[] x2                  = {-78,-100,-1,27,67,-31,-82,-24,44,-26,12,36,-36,-71,-84,3,59,28,-26,-79,-47,56,-75,-44, -85,-72,56,53,-27,53,-19,-65,14,62,96,-44,12,-20,-57,83,59,71,85,-62,21,24,-38,20,52,-64};
			int[] y2                  = {90,-41,79,18,7,-85,-88,-16,12,38,-7,12,-27,-43,-30,-93,48,-19,58,54,70,73,81,89,-35,-75, 63,-73,66,-90,-25,44,-53,59,-14,83,18,-35,-81,49,-11,-63,-87,-92,-83,-43,60,-42,5,-96};
			int[] prob                = {9,61,1,16,78,4,12,1,17,4,30,28,13,6,4,14,11,6,55,9,66,5,14,8, 70,3,2,6,3,15,8,1,2,12,1,20,37,1,3,66,3,11,2,1,21,2,1,1,27,11};
			double expected__         = 306025.109;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}

		// custom cases

        case 6: {
			int[] x1                  = {2,-2,-4};
			int[] y1                  = {-1,3,-1};
			int[] x2                  = {1,2,-2};
			int[] y2                  = {2,-1,1};
			int[] prob                = {100,100,100};
			double expected__         = 52.5;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}
        case 7: {
			int[] x1                  = {2,-2,-2};
			int[] y1                  = {-1,3,1};
			int[] x2                  = {1,2,-4};
			int[] y2                  = {2,-1,-1};
			int[] prob                = {100,100,100};
			double expected__         = 52.5;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}
		case 8: {
			int[] x1                  = {1,1,1,1,1,1,1,1,1,1};
			int[] y1                  = {1,1,1,1,1,1,1,1,1,1};
			int[] x2                  = {1,1,1,1,1,1,1,1,1,1};
			int[] y2                  = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
			int[] prob                = {10,20,30,40,50,60,70,80,90,100};
			double expected__         = 31.899999999999995;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}

		case 9: {
			int[] x1                  = {1,1,1,1,1,1,1,1,1,1};
			int[] y1                  = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
			int[] x2                  = {1,1,1,1,1,1,1,1,1,1};
			int[] y2                  = {1,1,1,1,1,1,1,1,1,1};
			int[] prob                = {10,20,30,40,50,60,70,80,90,100};
			double expected__         = 31.899999999999995;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}
/*      case 6: {
			int[] x1                  = ;
			int[] y1                  = ;
			int[] x2                  = ;
			int[] y2                  = ;
			int[] prob                = ;
			double expected__         = ;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}*/
/*      case 7: {
			int[] x1                  = ;
			int[] y1                  = ;
			int[] x2                  = ;
			int[] y2                  = ;
			int[] prob                = ;
			double expected__         = ;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}*/
/*      case 8: {
			int[] x1                  = ;
			int[] y1                  = ;
			int[] x2                  = ;
			int[] y2                  = ;
			int[] prob                = ;
			double expected__         = ;

			return verifyCase(casenum__, expected__, new TrianglePainting().expectedArea(x1, y1, x2, y2, prob));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

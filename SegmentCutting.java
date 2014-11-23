/*
 * SRM 634
 *
 * Key insight is that the line splits the points into two sets. It will 
 * cross all line segments whose end points are on opposite sides of the line.
 * Because it's squared length we can separate x and y. The final answer works
 * out to:
 * num below * sum of squared x of above +
 * num above * sum of squared x of below -
 * 2 * sum of x below * sum of x above +
 * same terms for y
 *
 * Pick a point and rotate a line around it. Keep track of the values for each
 * side of the line. You can update them in O(1) when a point moves from one
 * side to the other. It's a good thing to know that there are N^2 partitions of
 * N points and we can generate all partitions in N^2 time.
 *
 */
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import static java.lang.Math.*;


public class SegmentCutting {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  public long maxValue(int[] X, int[] Y) {
      long t0 = System.currentTimeMillis();
      int N = X.length;
      Point[] P = new Point[N];
      for (int i = 0; i < N; i++)
          P[i] = new Point(X[i], Y[i]);

      long ans = 0;
      for (Point p : P) {
          Predicate<Point> isAbove = q -> q.y >= p.y;
          Predicate<Point> notCenter = q -> q.x != p.x || q.y != p.y;
          List<Point> above = Arrays.stream(P).filter(isAbove.and(notCenter)).collect(Collectors.toList());
          List<Point> below = Arrays.stream(P).filter(isAbove.negate().and(notCenter)).collect(Collectors.toList());

          Collections.sort(above, (a, b) -> ccw(p, a, b));
          Collections.sort(below, (a, b) -> ccw(p, a, b));
          Div divAbove = new Div(above);
          Div divBelow = new Div(below);
          ans = max(ans, divAbove.score(divBelow, p));
          //p(p, below, above);
          int belowIdx = 0;
          int aboveIdx = 0;
          while (aboveIdx < above.size() || belowIdx < below.size()) {
              boolean swapAbove;
              if (aboveIdx == above.size()) {
                  swapAbove = false;
              } else if (belowIdx == below.size()) {
                  swapAbove = true;
              } else {
                  Point abovep = above.get(aboveIdx);
                  Point belowp = below.get(belowIdx);
                  Point negAbovep = abovep.sub(p).negate().add(p);
                  //p("\t\t", "negAbovep", negAbovep, ccw(p, negAbovep, belowp));
                  swapAbove = ccw(p, negAbovep, belowp) < 0;
              }
              if (swapAbove) {
                  //p("\t", "above->below", above.get(aboveIdx));
                  divBelow.add(above.get(aboveIdx));
                  divAbove.remove(above.get(aboveIdx));
                  aboveIdx++;
              } else {
                  //p("\t", "below->above", below.get(belowIdx));
                  divAbove.add(below.get(belowIdx));
                  divBelow.remove(below.get(belowIdx));
                  belowIdx++;
              }
              ans = max(ans, divAbove.score(divBelow, p));
          }
      }
      long t1 = System.currentTimeMillis();
      p(t1-t0);
		
      return ans;
  }

  int ccw(Point a, Point b, Point c) {
      Point v1 = b.sub(a);
      Point v2 = c.sub(a);
      return v1.perp().dot(v2);
  }

  public class Div {
      int count;
      long sumX;
      long sumSquaredX;
      long sumY;
      long sumSquaredY;

      Div(List<Point> P) {
          for (Point p : P)
              add(p);
      }

      void add(Point p) {
          count++;
          sumX += p.x;
          sumY += p.y;
          sumSquaredX += p.x * p.x;
          sumSquaredY += p.y * p.y;
      }
      void remove(Point p) {
          count--;
          sumX -= p.x;
          sumY -= p.y;
          sumSquaredX -= p.x * p.x;
          sumSquaredY -= p.y * p.y;
      }
      long score(Div d) {
          return count * (d.sumSquaredX + d.sumSquaredY) +
                 d.count * (sumSquaredX + sumSquaredY) -
                 2 * (sumX * d.sumX + sumY * d.sumY);
      }
      long score(Div d, Point p) {
          add(p);
          long s1 = score(d);
          remove(p);
          d.add(p);
          long s2 = score(d);
          d.remove(p);
          return max(s1, s2);
      }
  }

  public class Point {
      int x, y;
      public Point(int x, int y) {
          this.x = x;
          this.y = y;
      }
      Point add(Point p) {
          return new Point(x+p.x, y+p.y);
      }
      Point sub(Point p) {
          return new Point(x-p.x, y-p.y);
      }
      Point perp() {
          return new Point(-y, x);
      }
      int dot(Point p) {
          return x * p.x + y * p.y;
      }
      Point negate() {
          return new Point(-x, -y);
      }
      public String toString() {
          return String.format("(%d,%d)", x, y);
      }
  }


// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			SegmentCuttingHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				SegmentCuttingHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class SegmentCuttingHarness {
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
	
	static boolean compareOutput(long expected, long result) { return expected == result; }
	static String formatResult(long res) {
		return String.format("%d", res);
	}
	
	static int verifyCase(int casenum, long expected, long received) { 
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
			int[] x                   = {0,3};
			int[] y                   = {0,4};
			long expected__           = 25;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}
		case 1: {
			int[] x                   = {0,0,1,1};
			int[] y                   = {0,1,0,1};
			long expected__           = 6;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}
		case 2: {
			int[] x                   = {-6, 3, -4};
			int[] y                   = {2, 0, 5};
			long expected__           = 159;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}
		case 3: {
			int[] x                   = {0, 2,-2, 4,-4, 2,-2, 0};
			int[] y                   = {1, 2, 2, 4, 4, 6, 6, 5};
			long expected__           = 430;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}
		case 4: {
			int[] x                   = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
			int[] y                   = {1, 4, 9, 16, 25, 36, 49, 64, 81, 100};
			long expected__           = 94640;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}
		case 5: {
			int[] x                   = {-24,11,-235,49,13,-247,-575,80,35,29,1,-9,-1,-1,-27,-3,-20,13,-33,111, -36,30,-303,-645,-23,-5,393,227,89,145,6,515,234,271,-901,239,-368,-642, -3,125,-63,2,221,-113,1,6,3,-31,-6,-49,-785,314,363,-2,34,15,465,11,32, -72,394,20,-8,-448,429,-7,-88,-11,-42,-8,2,-707,-231,-143,76,-10,-152, -66,24,-73,778,-29,807,-63,722,-804,413,-21,-2,-131,26,3,-23,101,-551, -58,21,180,-15,-88,410,604,74,956,62,275,-352,-93,291,184,70,564,-594, -101,-391,18,115,-329,8,-4,-218,77,218,-145,27,-34,598,-87,-243,-323,34, 211,7,-145,-49,528,-13,10,-778,184,-65,101,-232,503,-6,69,553,-14,25,26, 854,955,-10,-490,-674,-4,9,-373,42,-121,-528,67,-32,23,74,81,-752,-7, -122,-464,-174,-722,46,961,57,-105,269,48,64,-585,-107,641,9,27,297,5, -44,107,-600,113,468,-24,-104,277,10,5,75,106,-591,38};
			int[] y                   = {-1,-2,62,-8,-1,28,-47,241,-10,-3,3,-41,1,95,111,-4,-114,503,1,19,3, -26,615,762,7,-52,399,-174,-987,30,-49,165,67,551,-214,-36,-108,-242, -967,69,698,-120,-298,20,-43,32,-171,2,-9,8,50,948,-2,-30,-3,53,-411, 410,141,-8,292,41,71,-523,-329,-284,8,84,34,559,-160,2,73,12,276,-16, -33,-63,119,869,127,47,963,-7,-995,146,-161,775,17,-5,-167,-315,59, -345,10,766,-10,-6,850,12,-5,-17,295,-89,14,71,-11,-305,13,-524,181, -279,-11,15,437,349,20,10,-749,82,25,336,-6,-794,-944,895,-52,72,198, 194,-988,641,-59,-434,-524,-381,368,6,-14,125,-55,319,-666,-610,-274, 359,-39,206,28,-719,-150,51,-366,-341,89,13,-635,-287,56,35,665,103,-81, 156,-4,888,-48,96,-48,340,61,396,272,-16,335,219,-388,15,324,24,-194,9, -310,7,-25,5,39,-342,118,-92,425,-336,796,950,-419,-812,-488,428,-32,-9};
			long expected__           = 5887914054L;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}

		// custom cases

        /*
      case 6: {
            Random R = new Random();
			int[] x                   = new int[1000];
			int[] y                   = new int[1000];
            for (int i = 0; i < 1000;i++) {
                x[i] = R.nextInt(1000);
                y[i] = R.nextInt(1000);
            }
			long expected__           = 114981397200L;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}*/
/*      case 7: {
			int[] x                   = ;
			int[] y                   = ;
			long expected__           = ;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}*/
/*      case 8: {
			int[] x                   = ;
			int[] y                   = ;
			long expected__           = ;

			return verifyCase(casenum__, expected__, new SegmentCutting().maxValue(x, y));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

/*
 * SRM 642
 *
 * DP state is how many rounds, number of intervals in the future that will
 * cover 0, and the second pick. The value is the expected value of naming that
 * second pick after the return value of 0 is the number of intervals given.
 * There are O(N^3) states and we can compute each one in O(1)
 */
import java.util.*;
import static java.lang.Math.*;


public class WheelofFortune {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  public boolean covered(int pos, int len, int at, int N) {
      return (pos <= at && at < pos + len) || (at < pos && (pos + len - N) > at);
  }

  public double maxExpectedValue(int N, int[] S) {
      int[][][][] waysCovered = new int[2][2][S.length][N];
      for (int i = 0; i < S.length; i++) {
          for (int k = 0; k < N; k++) {
              for (int p = 0; p < N; p++) {
                  int coverk = covered(p, S[i], k, N) ? 1 : 0;
                  int cover0 = covered(p, S[i], 0, N) ? 1 : 0;
                  waysCovered[cover0][coverk][i][k]++;
              }
          }
      }
      
      double[][] prevEV = new double[S.length+1][N];
      double[][] prevPR = new double[S.length+1][N];
      Arrays.fill(prevPR[0], 1.0);
      
      double[][] curEV = new double[S.length+1][N];
      double[][] curPR = new double[S.length+1][N];
      for (int at = S.length - 1; at >= 0; at--) {
          for (int hit0 = 0; hit0 <= S.length; hit0++) {
              for (int k = 0; k < N; k++) {
                  curEV[hit0][k] = 0.0;
                  curPR[hit0][k] = 0.0;

                  for (int cover0 = 0; cover0 <= 1; cover0++) {
                      for (int coverk = 0; coverk <= 1; coverk++) {
                          if (hit0 == 0 && cover0 == 1)
                              continue;
                          double p = (waysCovered[cover0][coverk][at][k] / (double)N) * prevPR[hit0-cover0][k];
                          curPR[hit0][k] += p;
                          curEV[hit0][k] += p * (prevEV[hit0-cover0][k] + coverk);
                      }
                  }
                  if (curPR[hit0][k] > 0) {
                      curEV[hit0][k] /= curPR[hit0][k];
                  }
              }
          }
          double[][] temp = curEV;
          curEV = prevEV;
          prevEV = temp;
          
          temp = curPR;
          curPR = prevPR;
          prevPR = temp;
      }
      double[][] prob = new double[S.length+1][S.length+1];
      prob[S.length][0] = 1.0;
      for (int at = S.length-1; at >= 0; at--) {
          for (int score = 0; score <= S.length; score++){
              if (score > 0)
                  prob[at][score] += (S[at] / (double)N) * prob[at+1][score-1];
              prob[at][score] += (1- (S[at] / (double)N)) * prob[at+1][score];
          }
      }
      double ans = 0.0;
      for (int score = 0; score <= S.length; score++) {
          double p = prob[0][score];
          double max = 0;
          for (int k = 1; k < N; k++) {
              if (prevPR[score][k] > 0)
                  max = max(max, prevEV[score][k]);
          }
          ans += p * (max + score);
      }
      return ans;
  }


// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			WheelofFortuneHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				WheelofFortuneHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class WheelofFortuneHarness {
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
			int N                     = 4;
			int[] s                   = {2};
			double expected__         = 1.25;

			return verifyCase(casenum__, expected__, new WheelofFortune().maxExpectedValue(N, s));
		}
		case 1: {
			int N                     = 6;
			int[] s                   = {1,1,1,1,1,1};
			double expected__         = 2.0000000000000004;

			return verifyCase(casenum__, expected__, new WheelofFortune().maxExpectedValue(N, s));
		}
		case 2: {
			int N                     = 20;
			int[] s                   = {1,20,1,20,1};
			double expected__         = 4.299999999999999;

			return verifyCase(casenum__, expected__, new WheelofFortune().maxExpectedValue(N, s));
		}
		case 3: {
			int N                     = 10;
			int[] s                   = {3,1,4,1,5,9,2,6,5,3,5,8,9,7,9,3,2,3,8,4,6,2,6,4,3,3,8,3,2,7,9,5};
			double expected__         = 31.973469385798197;

			return verifyCase(casenum__, expected__, new WheelofFortune().maxExpectedValue(N, s));
		}
		case 4: {
			int N                     = 15;
			int[] s                   = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
			double expected__         = 16.691531334568044;

			return verifyCase(casenum__, expected__, new WheelofFortune().maxExpectedValue(N, s));
		}

		// custom cases

/*      case 5: {
			int N                     = ;
			int[] s                   = ;
			double expected__         = ;

			return verifyCase(casenum__, expected__, new WheelofFortune().maxExpectedValue(N, s));
		}*/
/*      case 6: {
			int N                     = ;
			int[] s                   = ;
			double expected__         = ;

			return verifyCase(casenum__, expected__, new WheelofFortune().maxExpectedValue(N, s));
		}*/
/*      case 7: {
			int N                     = ;
			int[] s                   = ;
			double expected__         = ;

			return verifyCase(casenum__, expected__, new WheelofFortune().maxExpectedValue(N, s));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

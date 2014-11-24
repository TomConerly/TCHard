/*
 * SRM 632
 *
 * You have to alternate between top and bottom taking some length. You can
 * iterate over the number of times you switch from top to bottom. Now the
 * problem is given that you will hit the top K times how many ways are there
 * to cover the whole thing. There is a DP with N^2 states and N to compute
 * where you can do the computation in O(1) with an optimization.
 */
import java.util.*;
import static java.lang.Math.*;


public class PatternLock {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  long MOD = 1_000_000_007;
  public int solve(int N, int MOD) {
      long[][] DP = new long[N+1][N+1];
      DP[0][0] = 1;
      for (int left = 1; left <= N; left++) {
          for (int moves = 1; moves <= N; moves++) {
              DP[left][moves] = (left * DP[left-1][moves-1] + 2*DP[left-1][moves]) % MOD;
          }
      }

      long ans = 0;
      for (int topmoves = 1; topmoves <= N; topmoves++) {
          for (int bottommoves = topmoves-1; bottommoves <= topmoves; bottommoves++) {
              ans = (ans + 2 * DP[N][topmoves] * DP[N][bottommoves]) % MOD;
          }
      }
      return (int)ans;
  }


// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			PatternLockHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				PatternLockHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class PatternLockHarness {
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
	
	static boolean compareOutput(int expected, int result) { return expected == result; }
	static String formatResult(int res) {
		return String.format("%d", res);
	}
	
	static int verifyCase(int casenum, int expected, int received) { 
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
			int N                     = 1;
			int MOD                   = 12345667;
			int expected__            = 2;

			return verifyCase(casenum__, expected__, new PatternLock().solve(N, MOD));
		}
		case 1: {
			int N                     = 2;
			int MOD                   = 324124124;
			int expected__            = 24;

			return verifyCase(casenum__, expected__, new PatternLock().solve(N, MOD));
		}
		case 2: {
			int N                     = 3;
			int MOD                   = 5325352;
			int expected__            = 504;

			return verifyCase(casenum__, expected__, new PatternLock().solve(N, MOD));
		}
		case 3: {
			int N                     = 500;
			int MOD                   = 1000000007;
			int expected__            = 286169049;

			return verifyCase(casenum__, expected__, new PatternLock().solve(N, MOD));
		}

		// custom cases

      case 4: {
			int N                     = 10;
			int MOD                   = 1000000007;
			int expected__            = 925258141;

			return verifyCase(casenum__, expected__, new PatternLock().solve(N, MOD));
		}
/*      case 5: {
			int N                     = ;
			int MOD                   = ;
			int expected__            = ;

			return verifyCase(casenum__, expected__, new PatternLock().solve(N, MOD));
		}*/
/*      case 6: {
			int N                     = ;
			int MOD                   = ;
			int expected__            = ;

			return verifyCase(casenum__, expected__, new PatternLock().solve(N, MOD));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

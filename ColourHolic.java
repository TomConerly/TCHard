/*
 * SRM 635
 *
 * DP state is which colors you have added and how many adjacent pairs have
 * the same color. The first and last colors you add are the big ones. Going
 * from 2 to 3 colors has 200^2 transitions, but there are only 200 non zero
 * states at that point so the runtime is low.
 *
 * Important to note that compute n choose k mod p can be done quickly be
 * precomputing factorials and inverse factorials. 
 */
import java.util.*;
import static java.lang.Math.*;
import java.math.*;

public class ColourHolic {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  long choose(int n, int k) {
      long ans = (fact[n] * invfact[k]) % MOD;
      ans = (ans * invfact[n - k]) % MOD;
      return ans;
  }
  long power(long base, long exp) {
      long ans = 1;
      while (exp > 0) {
          if (exp % 2 == 1)
              ans = (ans * base) % MOD;
          base = (base * base) % MOD;
          exp /= 2;
      }
      return ans;
  }
  
  long MOD = 1_000_000_009;

  long[] fact;
  long[] invfact;

  public int countSequences(int[] A) {
      fact = new long[60000];
      fact[0] = 1;
      invfact = new long[60000];
      invfact[0] = 1;
      for (int i = 1; i < fact.length; i++)
          fact[i] = (fact[i-1] * i) % MOD;
      for (int i = 1; i < invfact.length; i++)
          invfact[i] = (invfact[i-1] * power(i, MOD-2)) % MOD;

      Arrays.sort(A);
      
      int N = A[3] + 1000;
      long[] prev = new long[N];
      prev[A[3]-1] = 1;

      long[] next = new long[N];

      {
          go(prev, next, A[3], A[0], A[1] + A[2]);
          long[] temp = prev;
          prev = next;
          next = temp;
          Arrays.fill(next, 0L);
      }
      {
          go(prev, next, A[0] + A[3], A[1], A[2]);
          long[] temp = prev;
          prev = next;
          next = temp;
          Arrays.fill(next, 0L);
      }
      long ans = 0;
      {
          int totalPlaced = A[0] + A[1] + A[3];
          for (int numRepeat = 0; numRepeat < prev.length; numRepeat++) {
              if (prev[numRepeat] == 0)
                  continue;
              int numNonRepeat = (totalPlaced + 1) - numRepeat;
              int breakRepeat = numRepeat;
              int breakNonRepeat = A[2] - breakRepeat;
              if (breakNonRepeat < 0)
                  continue;
              long ways = (prev[numRepeat] * choose(numNonRepeat, breakNonRepeat)) % MOD;
              ans += ways;
              ans %= MOD;
          }
      }
      return (int)ans;
  }

  void go(long[] prev, long[] next, int totalPlaced, int toPlace, int toPlaceAfter) {
      for (int numRepeat = 0; numRepeat < prev.length; numRepeat++) {
          if (prev[numRepeat] == 0)
              continue;
          int numNonRepeat = (totalPlaced + 1) - numRepeat;
          for (int breakRepeat = 0; breakRepeat <= min(toPlace, numRepeat); breakRepeat++) {
              for (int breakNonRepeat = 0; breakNonRepeat <= min(toPlace - breakRepeat, numNonRepeat); breakNonRepeat++) {
                  if (toPlace > 0 && breakRepeat + breakNonRepeat == 0)
                      continue;
                  int duplicate = toPlace - breakRepeat - breakNonRepeat ;
                  int newNumRepeat = numRepeat - breakRepeat + duplicate;
                  if (newNumRepeat > toPlaceAfter)
                      continue;
                  long ways = (prev[numRepeat] * choose(numRepeat, breakRepeat)) % MOD;
                  ways = (ways * choose(numNonRepeat, breakNonRepeat)) % MOD;
                  int options = breakRepeat + breakNonRepeat;
                  if (options > 0)
                      ways = (ways * choose(duplicate + options - 1, options - 1)) % MOD;
                  next[newNumRepeat] += ways;
                  next[newNumRepeat] %= MOD;
              }
          }
      }
  }


// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			ColourHolicHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				ColourHolicHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class ColourHolicHarness {
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
			int[] n                   = {1,0,2,3};
			int expected__            = 10;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
		case 1: {
			int[] n                   = {1,1,1,1};
			int expected__            = 24;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
		case 2: {
			int[] n                   = {42,42,42,9001};
			int expected__            = 0;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
		case 3: {
			int[] n                   = {8,0,0,8};
			int expected__            = 2;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
		case 4: {
			int[] n                   = {0,5,1,3};
			int expected__            = 4;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
		case 5: {
			int[] n                   = {4,2,1,3};
			int expected__            = 1074;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
		case 6: {
			int[] n                   = {15,900,357,22};
			int expected__            = 0;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
		case 7: {
			int[] n                   = {110,30000,200,29999};
			int expected__            = 118115350;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}

		// custom cases

      case 8: {
			int[] n                   = {200, 200, 50000, 50000};
			int expected__            = 947551947;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
      case 9: {
			int[] n                   = {0, 0, 0, 1};
			int expected__            = 1;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
      case 10: {
			int[] n                   = {0, 0, 1, 1};
			int expected__            = 2;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
      case 11: {
			int[] n                   = {0, 0, 1, 2};
			int expected__            = 1;

			return verifyCase(casenum__, expected__, new ColourHolic().countSequences(n));
		}
		default:
			return -1;
		}
	}
}

// END CUT HERE

/*
 * SRM 640
 *
 * Insight is that for all of the differences you can divide out primes one by
 * one and it will be fast enough. For all small primes (< sqrt(10^9)) you can
 * divide out those factors in O(N + num times prime appears as factor). Then
 * you are only left with primes.
 */
import java.util.*;
import static java.lang.Math.*;


public class TwoNumberGroups {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  static long MOD = 1_000_000_007;
  public int solve(int[] A, int[] numA, int[] B, int[] numB) {
      long t0 = System.currentTimeMillis();
      int N = A.length;
      int M = B.length;
      int[][] diffs = new int[N][M];
      for (int i = 0; i < N; i++)
          for (int j = 0; j < M; j++)
              diffs[i][j] = A[i] - B[j];

      boolean[] isPrime = new boolean[35000];
      Arrays.fill(isPrime, true);
      isPrime[0] = false;
      isPrime[1] = false;
      for (int i = 2; i < isPrime.length; i++) {
          if (!isPrime[i])
              continue;
          for (int j = i*2; j < isPrime.length; j += i)
              isPrime[j] = false;
      }

      Entry[] EA = new Entry[N];
      for (int i = 0; i < N; i++)
          EA[i] = new Entry();
      Entry[] EB = new Entry[M];
      for (int i = 0; i < M; i++)
          EB[i] = new Entry();

      long ans = 0;
      for (int p = 0; p < isPrime.length; p++) {
          if (!isPrime[p])
              continue;
          for (int i = 0; i < N; i++) {
              EA[i].index = i;
              EA[i].mod = A[i] % p;
          }
          Arrays.sort(EA);
          for (int i = 0; i < M; i++) {
              EB[i].index = i;
              EB[i].mod = B[i] % p;
          }
          Arrays.sort(EB);
          int at = 0;
          for (Entry e : EA) {
              while (at < EB.length && EB[at].mod < e.mod) {
                  at++;
              }
              if (at == EB.length) {
                  break;
              }
              int temp = at;
              while (temp < EB.length && EB[temp].mod == e.mod) {
                  if (diffs[e.index][EB[temp].index] != 0) {
                      ans += ((numA[e.index] * (long)numB[EB[temp].index])%MOD) * (long)p;
                      ans %= MOD;
                      while (diffs[e.index][EB[temp].index] % p == 0) {
                          diffs[e.index][EB[temp].index] /= p;
                      }
                  }
                  temp++;
              }
          }
      }
      for (int i = 0; i < N; i++) {
          for (int j = 0; j < M; j++) {
              if (diffs[i][j] != 0 && diffs[i][j] != 1 && diffs[i][j] != -1) {
                  ans += ((numA[i] * (long)numB[j]) % MOD) * (long)abs(diffs[i][j]);
                  ans %= MOD;
              }
          }
      }

      long t1 = System.currentTimeMillis();
      p(t1 - t0);
      return (int)(ans % MOD);	
  }

  static class Entry implements Comparable<Entry> {
      int index, mod;
      public int compareTo(Entry e) {
          if (mod < e.mod)
              return -1;
          if (mod > e.mod)
              return 1;
          return 0;
      }
  }


// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			TwoNumberGroupsHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				TwoNumberGroupsHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class TwoNumberGroupsHarness {
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
			int[] A                   = {1};
			int[] numA                = {2};
			int[] B                   = {3,7};
			int[] numB                = {1,1};
			int expected__            = 14;

			return verifyCase(casenum__, expected__, new TwoNumberGroups().solve(A, numA, B, numB));
		}
		case 1: {
			int[] A                   = {100};
			int[] numA                = {2};
			int[] B                   = {1};
			int[] numB                = {1};
			int expected__            = 28;

			return verifyCase(casenum__, expected__, new TwoNumberGroups().solve(A, numA, B, numB));
		}
		case 2: {
			int[] A                   = {5,1};
			int[] numA                = {1,1};
			int[] B                   = {12,999999894};
			int[] numB                = {1,1};
			int expected__            = 202073;

			return verifyCase(casenum__, expected__, new TwoNumberGroups().solve(A, numA, B, numB));
		}
		case 3: {
			int[] A                   = {1};
			int[] numA                = {1};
			int[] B                   = {1};
			int[] numB                = {100000};
			int expected__            = 0;

			return verifyCase(casenum__, expected__, new TwoNumberGroups().solve(A, numA, B, numB));
		}
		case 4: {
			int[] A                   = {11795180,41472124,44285836,84280940,117000811,150317409,154188370,167804776,225797581, 240995620,301931440,306528163,327332717,333523124,341325123,350292524,400857064,401290197, 426573408,427972026,448467719,563926065,574489831,579516358,609409829,659343788,678481187, 775710113,806992032,831437250,839580344,842388073,869876247,899553191,902366903,975081878};
			int[] numA                = {1188,1769,1782,1757,1527,4958,3083,4439,3621,3958,2655,2250,2079,3885,3598, 3236,3035,2286,7340,4127,3126,2904,2592,3082,3789,2776,3907,2368,4005,4863, 4482,3307,2459,1436,1656,2007};
			int[] B                   = {11795180,41472124,44285836,84280940,117000811,150317409,154188370,167804776,225797581,240995620, 301931440,306528163,327332717,333523124,341325123,350292524,400857064,401290197,426573408, 427972026,448467719,563926065,574489831,579516358,609409829,659343788,678481187,775710113, 806992032,831437250,839580344,842388073,869876247,899553191,902366903,942362007,975081878};
			int[] numB                = {3024,902,798,2,1426,4959,3082,4439,3622,3958,2653,2249,2081,3884,3599, 3237,3033,2285,7340,4125,3127,2904,2592,3082,3789,2775,3907,2369,4006, 4863,4483,3307,623,2303,2642,1757,2107};
			int expected__            = 601548244;

			return verifyCase(casenum__, expected__, new TwoNumberGroups().solve(A, numA, B, numB));
		}

		// custom cases

      case 5: {
                    Random R = new Random(24234234L);
                    int[] A = new int[1000];
                    int[] numA = new int[1000];
                    int[] B = new int[1000];
                    int[] numB = new int[1000];
                    for (int i = 0; i < 1000; i++) {
                        A[i] = R.nextInt(1000000000) + 1;
                        numA[i] = R.nextInt(100000) + 1;
                        B[i] = R.nextInt(1000000000) + 1;
                        numB[i] = R.nextInt(100000) + 1;
                    }
/*                    System.out.println(Arrays.toString(A));
                    System.out.println(Arrays.toString(numA));
                    System.out.println(Arrays.toString(B));
                    System.out.println(Arrays.toString(numB));*/

            int expected__ = 637917853;
			return verifyCase(casenum__, expected__, new TwoNumberGroups().solve(A, numA, B, numB));
		}
/*      case 6: {
			int[] A                   = ;
			int[] numA                = ;
			int[] B                   = ;
			int[] numB                = ;
			int expected__            = ;

			return verifyCase(casenum__, expected__, new TwoNumberGroups().solve(A, numA, B, numB));
		}*/
/*      case 7: {
			int[] A                   = ;
			int[] numA                = ;
			int[] B                   = ;
			int[] numB                = ;
			int expected__            = ;

			return verifyCase(casenum__, expected__, new TwoNumberGroups().solve(A, numA, B, numB));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

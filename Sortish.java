/*
 * SRM 636
 *
 * Pick the first 7 elements and the last 7 then do meet in the middle.
 */
import java.util.*;
import java.util.stream.*;
import static java.lang.Math.*;

public class Sortish {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  public long ways(int sortedness, int[] S) {
      long t0 = System.currentTimeMillis();
      int N = S.length;
      int numMissing = (int)Arrays.stream(S).filter(s -> s == 0).count();
      Set<Integer> used = Arrays.stream(S).filter(s-> s > 0).boxed().collect(Collectors.toSet());
      missing = new int[numMissing];
      int at = 0;
      for (int i = 1; i <= N; i++)
          if (!used.contains(i))
              missing[at++] = i;
		
      // score[i][j] is score of putting missing[j] at location i
      score = new int[numMissing][numMissing];
      at = 0;
      for (int i = 0; i < N; i++) {
          if (S[i] != 0)
              continue;
          for (int j = 0; j < numMissing; j++) {
              for (int k = 0; k < N; k++) {
                  if (S[k] == 0)
                      continue;
                  if ((k < i && S[k] < missing[j]) || (k > i && S[k] > missing[j])) {
                      score[at][j]++;
                  }
              }
          }
          at++;
      }
      int startingScore = 0;
      for (int i = 0; i < N; i++)
          for (int j = i + 1; j < N; j++)
              if (S[i] != 0 && S[j] != 0 && S[i] < S[j])
                  startingScore++;

      numGreater = new int[numMissing][1<<numMissing];
      for (int i = 0; i < numMissing; i++) {
          for (int j = 0; j < (1<<numMissing); j++) {
              for (int k = 0; k < numMissing; k++) {
                  if (bitSet(j, k) && missing[i] < missing[k])
                      numGreater[i][j]++;
              }
          }
      }

      int firstHalfLen = numMissing / 2;
      int secondHalfLen = numMissing - firstHalfLen;
      long ans = 0;
      for (int i = 0; i < (1<<numMissing); i++) {
          if (Integer.bitCount(i) != firstHalfLen)
              continue;
          int first = i;
          int second = (~i) & ((1<<numMissing) - 1);
          int between = 0;
          for (int j = 0; j < numMissing; j++) {
              if (!bitSet(first, j))
                  continue;
              for (int k = 0; k < numMissing; k++) {
                  if (!bitSet(second, k))
                      continue;
                  if (missing[j] < missing[k])
                      between++;
              }
          }
          HashMap<Integer, Integer> firstOptions = new HashMap<>();
          HashMap<Integer, Integer> secondOptions = new HashMap<>();
          fillOptions(0, first, 0, firstOptions);
          fillOptions(firstHalfLen, second, 0, secondOptions);
          for (int key : firstOptions.keySet()) {
              int need = sortedness - startingScore - between - key;
              if (secondOptions.containsKey(need))
                  ans += firstOptions.get(key) * (long)secondOptions.get(need);
          }
          //p(first, second, firstOptions, secondOptions);
          //p("\t", startingScore, between);
      }
      long t1 = System.currentTimeMillis();
      p("took: ", t1-t0);
      return ans;
  }

  static int[] missing;
  static int[][] score;
  static int[][] numGreater;
  void fillOptions(int at, int left, int scoreSoFar, HashMap<Integer, Integer> res) {
      if (left == 0) {
          if (!res.containsKey(scoreSoFar))
              res.put(scoreSoFar, 1);
          else
              res.put(scoreSoFar, res.get(scoreSoFar)+1);
          return;
      }
      int temp = left;
      for (int i = Integer.numberOfTrailingZeros(temp); i < 32; ){
          fillOptions(at + 1, left - (1<<i), scoreSoFar + numGreater[i][left] + score[at][i], res);

          temp -= 1<<i;
          i = Integer.numberOfTrailingZeros(temp);
      }
  }
  boolean bitSet(int set, int at) {
      return (set & (1<<at)) != 0;
  }


// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			SortishHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				SortishHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class SortishHarness {
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
			int sortedness            = 5;
			int[] seq                 = {4, 0, 0, 2, 0};
			long expected__           = 2;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}
		case 1: {
			int sortedness            = 4;
			int[] seq                 = {0, 0, 0, 0};
			long expected__           = 5;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}
		case 2: {
			int sortedness            = 2;
			int[] seq                 = {1, 3, 2};
			long expected__           = 1;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}
		case 3: {
			int sortedness            = 3;
			int[] seq                 = {0, 0, 2, 0, 0, 0};
			long expected__           = 4;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}
		case 4: {
			int sortedness            = 87;
			int[] seq                 = {2,0};
			long expected__           = 0;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}
		case 5: {
			int sortedness            = 30;
			int[] seq                 = {0, 6, 3, 0, 0, 2, 10, 0, 0, 0};
			long expected__           = 34;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}
		case 6: {
			int sortedness            = 100;
			int[] seq                 = {0, 13, 0, 0, 12, 0, 0, 0, 2, 0, 0, 10, 5, 0, 0, 0, 0, 0, 0, 7, 15, 16, 20};
			long expected__           = 53447326;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}

		// custom cases

/*      case 7: {
			int sortedness            = ;
			int[] seq                 = ;
			long expected__           = ;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}*/
/*      case 8: {
			int sortedness            = ;
			int[] seq                 = ;
			long expected__           = ;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}*/
/*      case 9: {
			int sortedness            = ;
			int[] seq                 = ;
			long expected__           = ;

			return verifyCase(casenum__, expected__, new Sortish().ways(sortedness, seq));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

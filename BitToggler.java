/*
 * SRM 641
 *
 * Let's look at a very similar game. In this game you start with N bits and
 * every move you randomly flip one. Each time you move the cost is the cost of
 * doing a random move from the bit you flip (using the costs from the original
 * game). This game is identical to the original game except that we don't pay
 * for the first move in the game, and we pay for an extra move at the end. It's
 * easy to add the cost of the first move. The cost of the second move is
 * harder, first let's solve this game.
 *
 * In the second game the EV is:
 * EV[bits] = 1/N sum_i EV[bits | (1<<i)] + moveCost[i]
 * EV[bits] = some constant + 1/N sum EV[bits | (1<<i)]
 * Notice there is symetry here. All bit patterns with the same number of bits
 * set have the same expected value. Thus there are only N+1 states so we can
 * easily solve the system of linear equations.
 *
 * This solves the second game, but we still need to remove the cost of the
 * extra move at the end. In the second game we need to make the last move free.
 * Let's compute the probability that the last move is to flip bit i. Once we
 * have that for all i's we can properly adjust the cost.
 *
 * For a fixed set of bits the probability that all the 0's are the last bit
 * is the same (it's symetric) and the probability for the 1's are the same. The
 * probability only depends on the number of bits set and whether we are
 * interested in a 0 bit or a 1 bit. That leads to a system of linear equations
 * that we can solve.
 *
 * From there we take the solution from the second game, add in the cost of the
 * first move, remove the cost of the second move and we have the answer.
 */
import java.util.*;
import static java.lang.Math.*;


public class BitToggler {
  public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

  public double[] expectation(int N, String[] bits, int[] pos) {
    double[] moveCost = new double[N];
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            moveCost[i] += (1.0/N) * abs(i-j);
        }
    }
    //p(moveCost);

    double[][] E = new double[N+2][N+2];
    E[0][0] = 1.0;
    E[N][N] = 1.0;
    E[N+1][N+1] = 1.0;
    for (int i = 1; i < N; i++) {
        E[i][i] = -1.0;
        E[i][i-1] = i / (double)N;
        E[i][i+1] = (N - i) / (double)N;
        for (int j = 0; j < N; j++) {
            E[i][N+1] += (1.0 / N) * moveCost[j];
        }
    }
    //p(E);
    double[][] invE = invert(E);
    double[] expected = new double[N + 1];
    for (int i = 0; i <= N; i++) {
        expected[i] = invE[i][N+1];
    }
    //p(expected);

    double[][] P = new double[2*N - 1][2*N-1];
    int yes = 2*N - 2;
    P[yes][yes] = 1.0;
    for (int bs = 1; bs < N; bs++) {
        for (int bit = 0; bit <= 1; bit++) {
            int numZero = N - bs - (bit == 0 ? 1 : 0);
            int numSet = bs - (bit == 1 ? 1 : 0);
            int me = toIndex(bs, bit, N);
            P[me][me] = -1;
            if (bs > 1)
                P[me][toIndex(bs - 1, bit, N)] = bs == 1 ? 0 : (numSet / (double)N);
            if (bs < N - 1)
                P[me][toIndex(bs + 1, bit, N)] = bs == N-1 ? 0 : (numZero / (double)N);
            if ((bs == 1 && bit == 1) || (bs == N-1 && bit == 0)) {
                P[me][yes] = 1 / (double)N;
            } else {
                P[me][toIndex(bs + (bit == 0 ? 1 : -1), 1-bit, N)] = 1 / (double)N;
            }
        }
    }
    double[][] invP = invert(P);
    double[][] prob = new double[N][2];
    for (int bs = 1; bs < prob.length; bs++) {
        for (int bit = 0; bit <= 1; bit++) {
            prob[bs][bit] = invP[toIndex(bs, bit, N)][yes];
        }
    }

    double[] ans = new double[bits.length];
    for (int i = 0; i < bits.length; i++) {
        int numSet = 0;
        for (int c = 0; c < N; c++)
            if (bits[i].charAt(c) == '1')
                numSet++;
        ans[i] = expected[numSet];
        if (numSet != 0 && numSet != N) {
            ans[i] += moveCost[pos[i]];
        }
        if (numSet == 0 || numSet == N)
            continue;
        for (int c = 0; c < N; c++) {
            int bit = bits[i].charAt(c) == '1' ? 1 : 0;
            ans[i] -= prob[numSet][bit] * moveCost[c];

        }
    }
    return ans;
  }
  static int toIndex(int bs, int bit, int N) {
      return 2 * (bs - 1) + bit;
  }
  double eps = 1e-9;

  double[][] invert(double[][] M) {
      int L = M.length;
      double[][] inv = new double[L][L];
      for (int i = 0; i < L; i++)
          inv[i][i] = 1.0;
      for (int i = 0; i < L; i++) {
          int row = -1;
          for (int r = i; r < L; r++) {
              if (abs(M[r][i]) > eps) {
                  row = r;
                  break;
              }
          }
          swap(M, inv, row, i);
          mult(M, inv, i, 1.0 / M[i][i]);
          for (int r = i + 1; r < L; r++) {
              sub(M, inv, row, M[r][i], r);
          }
      }
      for (int i = L - 1; i >= 0; i--) {
          for (int c = L - 1; c > i; c--) {
              sub(M, inv, c, M[i][c], i);
          }
      }
      return inv;
  }
  void swap(double[][] orig, double[][] inverse, int r1, int r2) {
      swap(orig, r1, r2);
      swap(inverse, r1, r2);
  }
  void swap(double[][] M, int r1, int r2) {
      for (int c = 0; c < M.length; c++) {
          double temp = M[r1][c];
          M[r1][c] = M[r2][c];
          M[r2][c] = temp;
      }
  }
  void sub(double[][] orig, double[][] inverse, int r1, double f, int r2) {
      sub(orig, r1, f, r2);
      sub(inverse, r1, f, r2);
  }
  void sub(double[][] M, int r1, double f, int r2) {
      for (int c = 0; c < M.length; c++) {
          M[r2][c] -= M[r1][c] * f;
      }
  }
  void mult(double[][] orig, double[][] inverse, int r, double f) {
      mult(orig, r, f);
      mult(inverse, r, f);
  }
  void mult(double[][] M, int r, double f) {
      for (int c = 0; c < M.length; c++)
          M[r][c] *= f;
  }



// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			BitTogglerHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				BitTogglerHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class BitTogglerHarness {
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
	static boolean compareOutput(double[] expected, double[] result) { if (expected.length != result.length) return false; for (int i=0; i<expected.length; ++i) if (!compareOutput(expected[i], result[i])) return false; return true; }
	static double relativeError(double[] expected, double[] result) { double ret = 0.0; for (int i=0; i<expected.length; ++i) { ret = Math.max(ret, relativeError(expected[i], result[i])); } return ret; }
	
	static String formatResult(double[] res) {
		String ret = "";
		ret += "{";
		for (int i=0; i<res.length; ++i) {
			if (i > 0) ret += ",";
			ret += String.format(" %.10g", res[i]);
		}
		ret += " }";
		return ret;
	}
	
	static int verifyCase(int casenum, double[] expected, double[] received) { 
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
			int n                     = 1;
			String[] bits             = {"0","1"};
			int[] pos                 = {0,0};
			double[] expected__       = {0.0, 0.0 };

			return verifyCase(casenum__, expected__, new BitToggler().expectation(n, bits, pos));
		}
		case 1: {
			int n                     = 2;
			String[] bits             = {"01"};
			int[] pos                 = {0};
			double[] expected__       = {0.5 };

			return verifyCase(casenum__, expected__, new BitToggler().expectation(n, bits, pos));
		}
		case 2: {
			int n                     = 4;
			String[] bits             = {"1000","0010","0011","1010"};
			int[] pos                 = {0,1,2,3};
			double[] expected__       = {8.9375, 8.5625, 9.75, 10.25 };

			return verifyCase(casenum__, expected__, new BitToggler().expectation(n, bits, pos));
		}
		case 3: {
			int n                     = 20;
			String[] bits             = {"01101111001001001111","01011100001001010111","00000000000000010000","10111111101111111011"
,"11101111111011010100","11111010110000000001","11000000100001100110","11111001010011101100"
,"11111111111111110111","01000011100010001000","01101111110111011111","11110111111111111110"
,"11111111111010110111","00100010000010000010","01000000000010000000","01110011111011000010"
,"11111101110001110111","11010010000000100000","10010101100000101000"};
			int[] pos                 = {7,6,1,1,7,9,0,8,16,16,14,4,9,8,11,3,12,15,11};
			double[] expected__       = {3695311.183337145, 3695341.038888902, 3486510.5078947134, 3690384.721723784, 3695146.5166465323, 3695333.788886834, 3695264.399989025, 3695310.983335239, 3486508.9763158862, 3695148.3166833473, 3693970.9054913437, 3670001.842105858, 3690381.122961273, 3693970.005510611, 3670000.4482457424, 3695335.888887129, 3694859.0889757653, 3694860.2888290025, 3695260.0000010678 };

			return verifyCase(casenum__, expected__, new BitToggler().expectation(n, bits, pos));
		}

		// custom cases
/*      case 6: {
			int n                     = ;
			String[] bits             = ;
			int[] pos                 = ;
			double[] expected__       = ;

			return verifyCase(casenum__, expected__, new BitToggler().expectation(n, bits, pos));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

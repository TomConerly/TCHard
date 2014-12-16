/*
 * SRM 641
 *
 * Fairly straight forward link cut tree problem. Can also be solved in O(N *
 * sqrt(N))
 *
 */
import java.util.*;
import static java.lang.Math.*;


public class TaroTreeRequests {
    public void p(Object...args) {System.out.println(Arrays.deepToString(args));}

    int genNextRandom() {
        curValue = (curValue * 1999 + 17) % 1_000_003;
        return curValue;
    }
    int curValue;
    Node[] nodes;

    public long getNumber(int N, int M, int startValue, int maxValue, int maxHeight) {
        curValue = startValue;

        nodes = new Node[N];
        nodes[0] = new Node(0, 0);
        
        for (int i = 1; i < N; i++) {
            int value = genNextRandom() % maxValue;
            Node p = nodes[max(0, i - 1 - (genNextRandom() % maxHeight))];
            nodes[i] = new Node(i, value);
            link(p, nodes[i]);
        }

        long ans = 0;
        for (int i = 0; i < M; i++) {
            int x = genNextRandom() % N;
            int y = genNextRandom() % N;
            ans += query(nodes[min(x, y)], nodes[max(x,y)]);
        }
        return ans;
    }

    void recalculate(Node u) {
        u.maxEdgeCost = u.realParentCost;
        if (u.left != null)
            u.maxEdgeCost = max(u.maxEdgeCost, u.left.maxEdgeCost);
        if (u.right != null)
            u.maxEdgeCost = max(u.maxEdgeCost, u.right.maxEdgeCost);
    }

    long query(Node u, Node v) {
        if (u == v) {
            return -1;
        }
        access(u);
        Node t = u.left;
        while (t != null && t.right != null)
            t = t.right;
        if (t != null) {
            cut(u);
        }
        Node ur = findRoot(u);
        Node vr = findRoot(v);
        if (ur != vr) {
            link(v, u);
            return 0;
        }
        access(v);
        splay(u);
        int cost = 0;
        if (u.right != null) {
            cost = u.right.maxEdgeCost;
        }
        if (t != null) {
            link(t, u);
        }
        return cost;
    }

    int getValue(Node x) {
        if (x == null)
            return -1;
        return x.idx;
    }
    public class Node {
        Node left, right, parent, pathParent;
        int idx, maxEdgeCost, realParentCost;
        public Node(int idx, int realParentCost) {
            this.idx = idx;
            this.realParentCost = realParentCost;
            this.maxEdgeCost = realParentCost;
        }
    }

    Node findRoot(Node x) {
        access(x);
        Node root = x;
        while (root.left != null) {
            root = root.left;
        }
        access(root);
        return root;
    }

    void link(Node x, Node y) {
        access(x);
        access(y);
        y.left = x;
        x.parent = y;
        y.pathParent = x.pathParent;
        x.pathParent = null;
        recalculate(y);
    }

    void cut(Node x) {
        access(x);
        if (x.left != null) {
            x.left.parent = null;
            x.left = null;
        }
        recalculate(x);
    }

    void access(Node x) {
        Node prev = null;
        for (Node t = x; t != null; t = t.pathParent) {
            splay(t);
            if (t.right != null) {
                t.right.pathParent = t;
                t.right.parent = null;
            }
            t.right = prev;
            if (prev != null) {
                prev.parent = t;
                prev.pathParent = null;
            }
            prev = t;
            recalculate(t);
        }
        splay(x);
    }

    void splay(Node x) {
        while (x.parent != null) {
            if (isLeftChild(x)) {
                if (x.parent.parent != null && isLeftChild(x.parent)) {
                    rotateLeft(x.parent);
                }
                rotateLeft(x);
            } else {
                if (x.parent.parent != null && !isLeftChild(x.parent)) {
                    rotateRight(x.parent);
                }
                rotateRight(x);
            }
        }
    }
    boolean isLeftChild(Node x) {
        return x.parent.left == x;
    }

    void rotateLeft(Node x) {
        Node p = x.parent;
        Node c = x.right;

        updateParent(x, p);
        updateRight(x, p);
        updateLeft(p, c);
        recalculate(p);
        recalculate(x);
    }
    void rotateRight(Node x) {
        Node p = x.parent;
        Node c = x.left;

        updateParent(x, p);
        updateLeft(x, p);
        updateRight(p, c);
        recalculate(p);
        recalculate(x);
    }
    void updateLeft(Node x, Node n) {
        if (x != null) {
            x.left = n;
        }
        if (n != null) {
            n.parent = x;
        }
    }
    void updateRight(Node x, Node n) {
        if (x != null) {
            x.right = n;
        }
        if (n != null) {
            n.parent = x;
        }
    }
    void updateParent(Node x, Node p) {
        Node g = p.parent;
        if (g != null) {
            if (isLeftChild(p)) {
                g.left = x;
            } else {
                g.right = x;
            }
        } else {
            x.pathParent = p.pathParent;
            p.pathParent = null;
        }
        x.parent = g;
    }

// BEGIN CUT HERE
   public static void main(String[] args) {
		if (args.length == 0) {
			TaroTreeRequestsHarness.run_test(-1);
		} else {
			for (int i=0; i<args.length; ++i)
				TaroTreeRequestsHarness.run_test(Integer.valueOf(args[i]));
		}
	}
// END CUT HERE
}

// BEGIN CUT HERE
class TaroTreeRequestsHarness {
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
			int N                     = 4;
			int M                     = 4;
			int startValue            = 47;
			int maxValue              = 7;
			int maxHeight             = 2;
			long expected__           = 8;

			return verifyCase(casenum__, expected__, new TaroTreeRequests().getNumber(N, M, startValue, maxValue, maxHeight));
		}
		case 1: {
			int N                     = 7;
			int M                     = 10;
			int startValue            = 74;
			int maxValue              = 7;
			int maxHeight             = 3;
			long expected__           = 40;

			return verifyCase(casenum__, expected__, new TaroTreeRequests().getNumber(N, M, startValue, maxValue, maxHeight));
		}
		case 2: {
			int N                     = 10;
			int M                     = 4;
			int startValue            = 103;
			int maxValue              = 100;
			int maxHeight             = 2;
			long expected__           = 220;

			return verifyCase(casenum__, expected__, new TaroTreeRequests().getNumber(N, M, startValue, maxValue, maxHeight));
		}
		case 3: {
			int N                     = 10000;
			int M                     = 10000;
			int startValue            = 984848;
			int maxValue              = 1000000;
			int maxHeight             = 2;
			long expected__           = 6632008328L;

			return verifyCase(casenum__, expected__, new TaroTreeRequests().getNumber(N, M, startValue, maxValue, maxHeight));
		}
		case 4: {
			int N                     = 200000;
			int M                     = 200000;
			int startValue            = 7584;
			int maxValue              = 948984;
			int maxHeight             = 4;
			long expected__           = 75766570928L;

			return verifyCase(casenum__, expected__, new TaroTreeRequests().getNumber(N, M, startValue, maxValue, maxHeight));
		}

		// custom cases

/*      case 5: {
			int N                     = ;
			int M                     = ;
			int startValue            = ;
			int maxValue              = ;
			int maxHeight             = ;
			long expected__           = ;

			return verifyCase(casenum__, expected__, new TaroTreeRequests().getNumber(N, M, startValue, maxValue, maxHeight));
		}*/
/*      case 6: {
			int N                     = ;
			int M                     = ;
			int startValue            = ;
			int maxValue              = ;
			int maxHeight             = ;
			long expected__           = ;

			return verifyCase(casenum__, expected__, new TaroTreeRequests().getNumber(N, M, startValue, maxValue, maxHeight));
		}*/
/*      case 7: {
			int N                     = ;
			int M                     = ;
			int startValue            = ;
			int maxValue              = ;
			int maxHeight             = ;
			long expected__           = ;

			return verifyCase(casenum__, expected__, new TaroTreeRequests().getNumber(N, M, startValue, maxValue, maxHeight));
		}*/
		default:
			return -1;
		}
	}
}

// END CUT HERE

package bml2;


public class BMLSimulation {

	// Static parameter:
	static final int L = 256;
	// Time interval:
	static final int stepIgnored = 2_000_000;
	static final int stepToObtain = 100_000;
	// Trials:
	static final int timesTrial = 20;

	/**
	 * 1回のシミュレーションを行なう
	 * @param k
	 * @param tau
	 * @param P
	 * @return 平均速度
	 */
	public static double simulate(int k, int tau, double P) {
		ExtendedBML bml = new ExtendedBML(L, k);
		bml.setTau(tau);
		bml.setP(P);

		double[] vs = new double[timesTrial];

		for (int trial = 0; trial < timesTrial; trial++) {
			// 初期化
			try {
				bml.initialize();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return -1;
			}

			// 空回し
			for (int step = 0; step < stepIgnored; step++) {
				bml.move();
			}

			// 1ステップに動こうとする車の台数(N/2)
			int Nhalf = k * L;
			// データ取得
			int countMoved = 0;
			int countToMove = 0;

			for (int step = 0; step < stepToObtain; step++) {
				countMoved += bml.move();
				countToMove += Nhalf;
			}

			vs[trial] = ((double)countMoved) / countToMove;
		}

		// vsの平均値を返す
		double v = 0;
		for (double _v : vs) v += _v;
		return (v / timesTrial);
	}


	/**
	 * シミュレーションの再現実験
	 *
	 *
	 *
	 */
	public static void main(String[] args) {

	}
}

package bml2;

import java.io.FileWriter;
import java.io.IOException;


public class BMLSimulation {

	// Static parameter:
	static final int L = 256;
	// Time interval:
	static final int stepIgnored = 5000; //(標準2_000_000)
	static final int stepToObtain = 2000; //(標準100_000)
	// Trials:
	static final int timesTrial = 1;
	// Data file name
	static final String fileName = "SimulationData.csv";

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
//			System.out.println("Trial " + trial);
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
	 * 文字列を表示してファイルに書き込みます。
	 * @param fw FileWriterオブジェクト
	 * @param str 文字列
	 */
	public static void putFile(FileWriter fw, String str) {
		String br = System.getProperty("line.separator");
		System.out.println(str);

		MyTwitter.tweet(str);

		try {
			fw.write(str + br);
		} catch (IOException e) {
			System.out.println("ファイルに書き込めません。");
			try {
				fw.close();
			} catch (IOException e1) {
			}
			System.exit(1);
		}
	}


	/**
	 * シミュレーションの再現実験
	 *
	 * 論文図２に示される、異なるPにおける256*256格子上の
	 * 密度ρに対する平均速度Vのプロットデータを取得します。
	 *
	 */
	public static void main(String[] args) {

		int tau = 2;
		double P = 0.5;
		int k = 2;

		System.out.println("シミュレーションを開始します。");

		long tStart = System.currentTimeMillis();
		simulate(k, tau, P);
		long tEnd = System.currentTimeMillis();

		System.out.println("経過時間：" + ((double)(tEnd - tStart)/1000) + "秒");
	}
}

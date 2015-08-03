package bml2;

import java.util.ArrayList;
import java.util.Random;

public class ExtendedBML {
	private int[][] siteX, siteY; // 横方向・縦方向の正方格子
	private int L; // 正方格子の一辺の数
	private int k; // 最小密度の倍数定数
	private int tau = 1; // 信号機の周期（本論文における設定値は tau = 2）
	private double P = 1.0; // スロースタート効果（本論文における設定値は P = 0.5）
	private int current = 0; // 現在の段階
	private static Random random;

	/**
	 * コンストラクタ
	 * @param L 正方格子の一辺の数（正の偶数）
	 * @param k 最小密度の倍数定数（ρ=k*ρmin）
	 */
	ExtendedBML(int L, int k) {
		random = new Random();

		if (L <= 0 || L % 2 != 0) {
			System.out.println("[err] Lは正の偶数でなければなりません。");
			System.exit(1);
		}
		if (k < 1 || L/2 < k) {
			System.out.println("[err] kは1以上L/2以下でなければなりません。");
			System.exit(1);
		}

		this.L = L;
		this.k = k;
		siteX = new int[L][L];
		siteY = new int[L][L];
	}

	/**
	 * 乱数のシード値を指定するコンストラクタ
	 * @param L
	 * @param k
	 * @param 乱数のシード値
	 */
	ExtendedBML(int L, int k, int seed) {
		this(L, k);
		random = new Random(seed);
	}

	/**
	 * 描画のために系の状態を与える
	 */
	public int[][][] getSites() {
		return new int[][][] {siteX, siteY};
	}

	/**
	 * Lを返します。
	 */
	public int getL() {
		return L;
	}

	/**
	 * 系の情報を表示する
	 */
	public void showInfo() {
		System.out.println("正方格子: L^2 = " + L + " x " + L);
		System.out.println("密度の倍数: k = " + k);
		System.out.println("車の総数:   N = " + (2*k*L));
		System.out.println("密度:      ρ = " + (2.0*k/L));
	}

	/**
	 * 車の総数を取得する
	 */
	public int getN() {
		return 2 * k * L;
	}

	/**
	 * 初期配置の基本状態をセットする
	 */
	void initialize() throws Exception {
		final int empty = 0, up = 1, down = 2, left = 3, right = 4;
		final int maxTrials = this.L * 10; // 最大試行回数を 10L とする。
		int[][] temp = new int[L][L];
		int x, y, rnd, trial;

		current = 0; // リセット

		// ランダムにセットする
		// k回繰り返す
		for (int i = 0; i < this.k; i++) {
			x = 2*i; y = 0;
			// L/2回繰り返す
			for (int j = 0; j < L/2; j++) {

				// 上向き車 @ (x,y)
				for (trial = 0; trial < maxTrials; trial++) {
					rnd = random.nextInt(L);
					if (temp[x][rnd] == empty) {
						temp[x][rnd] = up;
						break;
					}
				}
				if (trial == maxTrials) {
					throw new Exception("maxTrials回を超えても設定できません。");
				}
				// 左向き車 @ (x+1,y)
				for (trial = 0; trial < maxTrials; trial++) {
					rnd = random.nextInt(L);
					if (temp[rnd][y] == empty) {
						temp[rnd][y] = left;
						break;
					}
				}
				if (trial == maxTrials) {
					throw new Exception("maxTrials回を超えても設定できません。");
				}
				// 右向き車 @ (x,y+1)
				for (trial = 0; trial < maxTrials; trial++) {
					rnd = random.nextInt(L);
					if (temp[rnd][y+1] == empty) {
						temp[rnd][y+1] = right;
						break;
					}
				}
				if (trial == maxTrials) {
					throw new Exception("maxTrials回を超えても設定できません。");
				}
				// 下向き車 @ (x+1,y+1)
				for (trial = 0; trial < maxTrials; trial++) {
					rnd = random.nextInt(L);
					if (temp[x+1][rnd] == empty) {
						temp[x+1][rnd] = down;
						break;
					}
				}
				if (trial == maxTrials) {
					throw new Exception("maxTrials回を超えても設定できません。");
				}

				x += 2; y += 2;
				if (x >= L) x = 0;
			}
		}

		// 初期状態をsiteX, siteYにコピー
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				// 配列の初期化
				siteX[i][j] = 0;
				siteY[i][j] = 0;

				switch (temp[i][j]) {
				case left:
					// fall through
				case right:
					siteX[i][j] = 1;
					break;
				case up:
					// fall through
				case down:
					siteY[i][j] = 1;
					break;
				}
			}
		}
	}


	/**
	 * サイトの状態をコンソールに表示する
	 */
	public void show() {
		for (int y = 0; y < L; y++) {
			for (int x = 0; x < L; x++) {

				if (siteX[x][y] == 1) {
					// 横方向の車の場合
					System.out.print((y % 2 == 0) ? "→" : "←");
				} else if (siteY[x][y] == 1) {
					// 縦方向の車の場合
					System.out.print((x % 2 == 0) ? "↑" : "↓");
				} else {
					// 空の場合
					System.out.print("　");
				}
			}
			System.out.println();
		}
	}

	/**
	 * tauのセッター
	 * @param tau 新しい this.tau の値
	 */
	public void setTau(int tau) {
		if (tau <= 0) {
			System.out.println("[err] 信号機の周期 tau は1以上でなければなりません。");
		} else {
			this.tau = tau;
		}
	}

	/**
	 * Pのセッター
	 * @param P 新しい this.P の値
	 */
	public void setP(double P) {
		if (P < 0 || 1 < P) {
			System.out.println("[err] スロースタート効果 P は0以上1以下でなければなりません。");
		} else {
			this.P = P;
		}
	}

	/**
	 * 1回動かす
	 * this.currentによって移動方向ならびにスロースタート効果を決定する
	 * @return そのステップで動いた車の数
	 */
	public int move() {
		int count;
		if (current == 0) {
			count = moveHorizontal(true);
		} else if (current < tau) {
			count = moveHorizontal(false);
		} else if (current == tau) {
			count = moveVertical(true);
		} else {
			// assert (current < 2 * tau);
			count = moveVertical(false);
		}

		current++;
		if (current == 2 * tau) {
			current = 0;
		}

		return count;
	}


	/**
	 * 1周期分動かす
	 * 信号機の周期 (this.tau) が考慮される。
	 * 車は、同じ方向にtauの回数だけ連続して動く。
	 * 最初は横方向の車が動く。
	 */
	public void move1period() {
		for (int i = 0; i < 2 * tau; i++) {
			move();
		}
	}

	/**
	 * 横方向の車を１ステップ動かす
	 * @param ss スロースタート効果を適用する場合、true
	 * @return そのステップで動いた車の数
	 */
	private int moveHorizontal(boolean ss) {
		int[][] temp = new int[L][L];
		int i=0, next, prev=0, rnd;
		int count = 0;
		//test
		int cntTemp = 0;

		// 列を j=0→(L-1) まで回す
		for (int j = 0; j < L; j++) {

			// 行を１回余分に回す
			for (int n = 0; n <= L; n++) {

				if (j % 2 == 0) {
					// jが偶数の場合、右向き
					i = (n == L ? 0 : n);
					next = (i == L-1 ? 0 : i+1); // 進行方向前方の車のi座標
					prev = (i == 0 ? L-1 : i-1); // 進行方向後方の車のi座標
				} else {
					// jが奇数の場合、左向き
					i = (n == L ? L-1 : L-1-n);
					next = (i == 0 ? L-1 : i-1);
					prev = (i == L-1 ? 0 : i+1);
				}
				if (n == L) break; // 最終回だったらbreak

				// スロースタート効果がない場合、rnd = 0
				// スロースタート効果がある場合、移動しないとき、rnd = 1
				// 移動しないのは(1-P)の確率で起こる
				rnd = (ss ? ((random.nextDouble() >= P) ? 1 : 0) : 0);

				// 出て行くときの回数を数える
				if (siteX[i][j]*(1-siteX[next][j])*(1-siteY[next][j])*(1-rnd) == 1)
					count++;

				temp[i][j] = siteX[i][j]*(siteX[next][j]+siteY[next][j]+(1-siteX[next][j])*(1-siteY[next][j])*rnd)
						   + (1-siteX[i][j])*(1-siteY[i][j])*siteX[prev][j]*(1-temp[prev][j]);
			}
			// プラス１回
			temp[i][j] = (1-siteX[i][j])*(1-siteY[i][j])*siteX[prev][j]*(1-temp[prev][j])
					   + (1 - (1-siteX[i][j])*(1-siteY[i][j])*siteX[prev][j])*temp[i][j];
		}

		//ここで計測する
		for (i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				if (siteX[i][j] != temp[i][j])
					cntTemp++;
			}
		}
		assert (2*count == cntTemp);


		// tempをsiteXにコピー
		for (i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				siteX[i][j] = temp[i][j];
			}
		}

		return count;
	}



	/**
	 * 縦方向の車を１ステップ動かす
	 * @param ss スロースタート効果を適用する場合、true
	 * @return そのステップで動いた車の数
	 */
	private int moveVertical(boolean ss) {
		int[][] temp = new int[L][L];
		int j=0, next, prev=0, rnd;
		int count = 0;
		int count2 = 0;

		// 行を i=0→(L-1) まで回す
		for (int i = 0; i < L; i++) {

			// 列を１回余分に回す
			for (int n = 0; n <= L; n++) {

				if (i % 2 == 0) {
					// iが偶数の場合、上向き
					j = (n == L ? L-1 : L-1-n);
					next = (j == 0 ? L-1 : j-1); // 進行方向前方の車のj座標
					prev = (j == L-1 ? 0 : j+1); // 進行方向後方の車のj座標
				} else {
					// iが奇数の場合、下向き
					j = (n == L ? 0 : n);
					next = (j == L-1 ? 0 : j+1);
					prev = (j == 0 ? L-1 : j-1);
				}
				if (n == L) break; // 最終回だったらbreak;

				// スロースタート効果がない場合、rnd = 0
				// スロースタート効果がある場合、移動しないとき、rnd = 1
				// 移動しないのは(1-P)の確率で起こる
				rnd = (ss ? ((random.nextDouble() >= P) ? 1 : 0) : 0);

				if (siteY[i][j] *(1-siteX[i][next])*(1-siteY[i][next])*(1-rnd) == 1)
					count++;

				temp[i][j] = siteY[i][j]*(siteX[i][next]+siteY[i][next]+(1-siteX[i][next])*(1-siteY[i][next])*rnd)
						   + (1-siteX[i][j])*(1-siteY[i][j])*siteY[i][prev]*(1-temp[i][prev]);
			}
			// プラス１回
			temp[i][j] = (1-siteX[i][j])*(1-siteY[i][j])*siteY[i][prev]*(1-temp[i][prev])
					   + (1 - (1-siteX[i][j])*(1-siteY[i][j])*siteY[i][prev])*temp[i][j];
		}

		for (int i = 0; i < L; i++) {
			for (j = 0; j < L; j++) {
				if (siteY[i][j] != temp[i][j])
					count2++;
			}
		}
		assert (2*count == count2);

		// tempをsiteYにコピー
		for (int i = 0; i < L; i++) {
			for (j = 0; j < L; j++) {
				siteY[i][j] = temp[i][j];
			}
		}

		return count;
	}



	public void check() {
		for (int i = 0; i < L; i++) {
			int countX = 0, countY = 0;
			for (int j = 0; j < L; j++) {
				countX += siteX[j][i];
				countY += siteY[i][j];
			}
			if (countX != k || countY != k) {
				System.out.println("Error");
				return;
			}
		}
		System.out.println("OK");
	}


	/**
	 * デッドロックを数える。<注：境界まで探していない>
	 */
	public ArrayList<Integer> countDeadlocks() {
		int count = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < L - 2; i+=2) {
			for (int j = 0; j < L - 2; j+=2) {
				if (siteX[i][j] == 1 && siteX[i+1][j+1] == 1 && siteY[i+1][j] == 1 && siteY[i][j+1] == 1) {
					count++;
					list.add(i); list.add(j);
				}
				if (siteY[i+1][j+1] == 1 && siteY[i+2][j+2] == 1 && siteX[i+2][j+1] == 1 && siteX[i+1][j+2] == 1) {
					count++;
					list.add(i+1); list.add(j+1);
				}
			}
		}

		return list;
	}
}

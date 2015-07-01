package bml2;

import java.util.Random;

public class ExtendedBML {
	private int[][] siteX, siteY; // 横方向・縦方向の正方格子
	private int L; // 正方格子の一辺の数
	private int k; // 最小密度の倍数定数
	private int tau = 1; // 信号機の周期
	private double P = 1.0; // スロースタート効果
	private static Random random = new Random();

	/**
	 * コンストラクタ
	 * @param L 正方格子の一辺の数（正の偶数）
	 * @param k 最小密度の倍数定数（ρ=k*ρmin）
	 */
	ExtendedBML(int L, int k) {
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
	 * 系の情報を表示する
	 */
	public void showInfo() {
		System.out.println("正方格子: L^2 = " + L + " x " + L);
		System.out.println("密度の倍数: k = " + k);
		System.out.println("車の総数:   N = " + (2*k*L));
		System.out.println("密度:      ρ = " + (2.0*k/L));
	}

	/**
	 * 初期配置の基本状態をセットする
	 */
	void initialize() throws Exception {
		final int empty = 0, up = 1, down = 2, left = 3, right = 4;
		final int maxTrials = this.L * 7; // 最大試行回数を 2L とする。
		int[][] temp = new int[L][L];
		int x, y, rnd, trial;

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


		// <siteX, siteY初期化>
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				siteX[i][j] = 0;
				siteY[i][j] = 0;
			}
		}


		// 初期状態をsiteX, siteYにコピー
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
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
	 * 1周期分動かす
	 * 信号機の周期 (this.tau) が考慮される。
	 * 車は、同じ方向にtauの回数だけ連続して動く。
	 * 最初は横方向の車が動く。
	 */
	public void move1period() {
		for (int i = 0; i < tau; i++) {
			this.moveHorizontal(i == 0);
		}
		for (int i = 0; i < tau; i++) {
			this.moveVertical(i == 0);
		}
	}

	/**
	 * 横方向の車を１ステップ動かす
	 * @param ss スロースタート効果を適用する場合、true
	 */
	private void moveHorizontal(boolean ss) {
		int[][] temp = new int[L][L];

		// 列を j=0→(L-1) まで回す
		for (int j = 0; j < L; j++) {

			// 行を１回余分に回す
			for (int n = 0; n <= L; n++) {
				int i, next, prev, rnd;

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

				// スロースタート効果がない場合、rnd = 0
				// スロースタート効果がある場合、移動しないとき、rnd = 1
				// 移動しないのは(1-P)の確率で起こる
				rnd = (ss ? ((random.nextDouble() >= P) ? 1 : 0) : 0);

				temp[i][j] = siteX[i][j]*(siteX[next][j]+siteY[next][j]+rnd)
						   + (1-siteX[i][j])*(1-siteY[i][j])*siteX[prev][j]*(1-temp[prev][j]);
			}
		}

		// tempをsiteXにコピー
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				siteX[i][j] = temp[i][j];
			}
		}
	}



	/**
	 * 縦方向の車を１ステップ動かす
	 * @param ss スロースタート効果を適用する場合、true
	 */
	private void moveVertical(boolean ss) {
		int[][] temp = new int[L][L];

		// 行を i=0→(L-1) まで回す
		for (int i = 0; i < L; i++) {

			// 列を１回余分に回す
			for (int n = 0; n <= L; n++) {
				int j, next, prev, rnd;

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

				// スロースタート効果がない場合、rnd = 0
				// スロースタート効果がある場合、移動しないとき、rnd = 1
				// 移動しないのは(1-P)の確率で起こる
				rnd = (ss ? ((random.nextDouble() >= P) ? 1 : 0) : 0);

				temp[i][j] = siteX[i][j]*(siteX[i][next]+siteY[i][next]+rnd)
						   + (1-siteX[i][j])*(1-siteY[i][j])*siteX[i][prev]*(1-temp[i][prev]);
			}
		}

		// tempをsiteXにコピー
		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				siteX[i][j] = temp[i][j];
			}
		}
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
			}
		}
	}


	public static void main(String[] args) {
		int lat = 10;

		for (int k = 1; k <= lat/2; k++) {
			ExtendedBML bml = new ExtendedBML(lat, k);

			int count = 0;
			for (int i = 0; i < 1000; i++) {
				try {
					bml.initialize();
				} catch (Exception e) {
					continue;
				}
				count++;
			}
			System.out.println("k=" + k + ", ρ=" + 2.0*k/lat + " 成功率 " + count/1000.0 * 100 + "%");
		}
	}
}

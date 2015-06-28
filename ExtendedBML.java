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
	void initialize() {
		final int empty = 0, up = 1, down = 2, left = 3, right = 4;
		int[][] temp = new int[L][L];
		int x, y, rnd;

		// 対角線上に基本形を配置する
		// k回繰り返す
		for (int i = 0; i < k; i++) {
			x = 2*i; y = 0;
			// L/2回繰り返す
			for (int j = 0; j < L/2; j++) {
				temp[x  ][y  ] = up;
				temp[x+1][y  ] = left;
				temp[x  ][y+1] = right;
				temp[x+1][y+1] = down;

				x += 2; y += 2;
				if (x >= L) x = 0;
			}
		}

		// ランダムに散らばらせる
		// k回繰り返す
		for (int i = 0; i < k; i++) {
			x = 2*i; y = 0;
			// L/2回繰り返す
			for (int j = 0; j < L/2; j++) {
				// オフセット(x,y)の渋滞ユニットをランダムに移動する
				// 上向き車 @ (x,y)
				rnd = random.nextInt(L); // [0,L-1]の乱数を振る
				if (temp[x][rnd] == empty) {
					temp[x][rnd] = up;
					temp[x][y] = empty;
				}
				// 左向き車 @ (x+1,y)
				rnd = random.nextInt(L);
				if (temp[rnd][y] == empty) {
					temp[rnd][y] = left;
					temp[x+1][y] = empty;
				}
				// 右向き車 @ (x,y+1)
				rnd = random.nextInt(L);
				if (temp[rnd][y+1] == empty) {
					temp[rnd][y+1] = right;
					temp[x][y+1] = empty;
				}
				// 下向き車 @ (x+1,y+1)
				rnd = random.nextInt(L);
				if (temp[x+1][rnd] == empty) {
					temp[x+1][rnd] = down;
					temp[x+1][y+1] = empty;
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

	// 1周期分動かす
	// 信号機の周期（this.tau）が考慮される。
	// 車は、同じ方向に、tauの回数だけ連続して動く。
	// 最初は横方向の車が動く。
	public void move1period() {
		this.moveHorizontalSlowStart();
		for (int i = 0; i < this.tau - 1; i++) {
			this.moveHorizontal();
		}
		this.moveVerticalSlowStart();
		for (int i = 0; i < this.tau - 1; i++) {
			this.moveVertical();
		}
	}

	/**
	 * 横方向の車を１ステップ動かす
	 */
	private void moveHorizontal() {
		int[][] temp = new int[L][L];

		for (int i = 0; i < L; i++) {
			for (int j = 0; j < L; j++) {
				temp[i][j] = siteX[i][j]*(siteX[i+1][j]+siteY[i+1][j]) /*動けない場合*/
						+0
						+0;
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
	 * 横方向の車を１ステップ動かす（スロースタート効果あり）
	 */
	private void moveHorizontalSlowStart() {

	}

	/**
	 * 縦方向の車を１ステップ動かす
	 */
	private void moveVertical() {

	}

	/**
	 * 縦方向の車を１ステップ動かす（スロースタート効果あり）
	 */
	private void moveVerticalSlowStart() {

	}

	public static void main(String[] args) {
		int l = 16;
		ExtendedBML bml = new ExtendedBML(l, 3);
		int[][] test = new int[l][l];

		for (int i = 0; i < 10000; i++) {
			bml.initialize();
			for (int j = 0; j < l; j++) {
				for (int k = 0; k < l; k++) {
					if(bml.siteX[j][k] == 1 || bml.siteY[j][k] == 1)
						test[j][k]++;
				}
			}
		}

		for (int j = 0; j < l; j++) {
			for (int i = 0; i < l; i++) {
				System.out.print(test[i][j] + " ");
			}
			System.out.println();
		}
		
	}
}

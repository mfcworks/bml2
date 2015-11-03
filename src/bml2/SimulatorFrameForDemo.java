package bml2;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SimulatorFrameForDemo extends JFrame implements Runnable {

	private static int L = 100;
	private static double P = 0.5;
	private static int tau = 2;
	private static int k_low = 1;
	private static int k_mid = 3;
	private static int k_high = 6;

	private static int SLEEP_TIME = 10;
	private static int RANDOM_SEED = 20;

	/* ウィンドウ・コントロール類 */
	public static SimulatorFrameForDemo frame; // メインウィンドウ
	private JPanel panel; // 描画パネル
	private JButton btnLowdensity;
	private JButton btnMiddledensity;
	private JButton btnHighdensity;

	private static int PANEL_SIZE = 725;

	/* BMLのインスタンス */
	private ExtendedBML bml;

	/* スレッド分割関係 */
	private Thread animationThread;
	private boolean running = false;
	private int step = 0;

	/**
	 * コンストラクタ Frameを作成します。
	 */
	public SimulatorFrameForDemo() {

		setTitle("Extended BML Simulator デモンストレーション用");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, PANEL_SIZE + 130, PANEL_SIZE + 30);
		getContentPane().setLayout(null);

		// パネル
		panel = new SimulatorPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, PANEL_SIZE, PANEL_SIZE);
		getContentPane().add(panel);
		getContentPane().setSize(PANEL_SIZE + 130, PANEL_SIZE);

		btnLowdensity = new JButton("低密度");
		btnLowdensity.setBounds(746, 115, 91, 21);
		getContentPane().add(btnLowdensity);
		btnLowdensity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				running = false;
				animationThread.interrupt();
				bml = new ExtendedBML(L, k_low, RANDOM_SEED);
				bml.setP(P);
				bml.setTau(tau);
				try {
					bml.initialize();
				} catch (Exception e1) {
				}
				running = true;
				animationThread.interrupt();
			}
		});

		btnMiddledensity = new JButton("中密度");
		btnMiddledensity.setBounds(746, 146, 91, 21);
		getContentPane().add(btnMiddledensity);
		btnMiddledensity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				running = false;
				animationThread.interrupt();
				bml = new ExtendedBML(L, k_mid, RANDOM_SEED);
				bml.setP(P);
				bml.setTau(tau);
				try {
					bml.initialize();
				} catch (Exception e1) {
				}
				running = true;
				animationThread.interrupt();
			}
		});

		btnHighdensity = new JButton("高密度");
		btnHighdensity.setBounds(746, 177, 91, 21);
		getContentPane().add(btnHighdensity);
		btnHighdensity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				running = false;
				animationThread.interrupt();
				bml = new ExtendedBML(L, k_high, RANDOM_SEED);
				bml.setP(P);
				bml.setTau(tau);
				try {
					bml.initialize();
				} catch (Exception e1) {
				}
				running = true;
				animationThread.interrupt();
			}
		});

		animationThread = new Thread(this, "Test");
		animationThread.start();
	}

	@Override
	public void run() {
		while (true) {
			if (running) {
				bml.move();
				step++;
				panel.repaint();
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * アプリケーションを開始します。
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new SimulatorFrameForDemo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * inner class SimulatorPanel
	 */
	public class SimulatorPanel extends JPanel {
		final int delta = 20; // 1つのマスのサイズ
		final int sx = 10, sy = 10; // 描画開始座標

		/**
		 * Panel内部を描画します。
		 */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (bml != null) {
				int L = bml.getL();

				if (L <= 20) {
					drawGrid(g);
					drawArrows(g);
				} else {
					drawCircles(g);
				}
			}
		}

		/**
		 * （下請けメソッド）グリッドを描画します。
		 */
		private void drawGrid(Graphics g) {
			for (int i = 0; i <= L; i++) {
				g.drawLine(sx, sy + delta * i, sx + delta * L, sy + delta * i);
				g.drawLine(sx + delta * i, sy, sx + delta * i, sy + delta * L);
			}
		}

		/**
		 * （下請けメソッド）矢印を描画します。
		 */
		private void drawArrows(Graphics g) {
			int[][][] sites;
			int[][] siteX, siteY;
			int index;

			final int RIGHT = 0, LEFT = 1, UP = 2, DOWN = 3;
			final String[] arrow = { "→", "←", "↑", "↓" };
			final Color[] color = { Color.GRAY, Color.BLACK, Color.RED,
					Color.BLUE };

			sites = bml.getSites();
			siteX = sites[0];
			siteY = sites[1];

			for (int i = 0; i < L; i++) {
				for (int j = 0; j < L; j++) {
					// 横方向の矢印の描画
					if (siteX[i][j] == 1) {
						index = (j % 2 == 0 ? RIGHT : LEFT);
						g.setColor(color[index]);
						// (x, y)は文字列のベースラインなので、位置を微調整する
						g.drawString(arrow[index], sx + delta * i + 5, sy
								+ delta * j + 15);
					}
					// 縦方向の矢印の描画
					if (siteY[i][j] == 1) {
						index = (i % 2 == 0 ? UP : DOWN);
						g.setColor(color[index]);
						g.drawString(arrow[index], sx + delta * i + 5, sy
								+ delta * j + 15);
					}
				}
			}
		}

		/**
		 * （下請けメソッド）小さい円で状態を描画します。
		 */
		private void drawCircles(Graphics g) {

			final int d = 7;

			g.setColor(Color.BLACK);
			g.drawRect(sx, sy, d * L, d * L);

			int[][][] sites = bml.getSites();
			int[][] siteX = sites[0];
			int[][] siteY = sites[1];

			for (int i = 0; i < L; i++) {
				for (int j = 0; j < L; j++) {
					if (siteX[i][j] == 1) {
						if (j % 2 == 0) {
							g.setColor(Color.GRAY);
							g.fillOval(sx + d * i, sy + d * j, d, d);
						} else {
							g.setColor(Color.BLACK);
							g.fillOval(sx + d * i, sy + d * j, d, d);
						}
					}
					if (siteY[i][j] == 1) {
						if (i % 2 == 0) {
							g.setColor(Color.RED);
							g.fillOval(sx + d * i, sy + d * j, d, d);
						} else {
							g.setColor(Color.BLUE);
							g.fillOval(sx + d * i, sy + d * j, d, d);
						}
					}
				}
			}

		}

	}
}

package bml2;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class SimulatorFrame extends JFrame implements Runnable {

	private static final int L = 20;//380; // 格子数
	private static final int k = 4;  // 密度指定

	public static SimulatorFrame frame;

	// BMLのインスタンス
	private ExtendedBML bml;

	// 操作ボタン
	private JButton btnStart;
	private JButton btnAAA;

	// 描画パネル
	private JPanel panel;

	public boolean running = false;

	private Thread animationThread;
	private JLabel lblDeadlocks;


	/**
	 * コンストラクタ
	 * Frameを作成します。
	 */
	public SimulatorFrame() {

		bml = new ExtendedBML(L, k);
		try {
			bml.initialize();
		} catch (Exception e1) {
		}
//		bml.setP(0.5);
		bml.setTau(3);

		setTitle("Extended BML Model Simulator");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 820);
		getContentPane().setLayout(null);
		getContentPane().setSize(900, 800);

		// パネル
		panel = new SimulatorPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 800, 800);
		getContentPane().add(panel);

		// ボタン1
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (running) {
					btnStart.setText("Start");
				} else {
					btnStart.setText("Stop");
				}
				running = !running;
				animationThread.interrupt();
			}
		});
		btnStart.setBounds(814, 653, 68, 21);
		getContentPane().add(btnStart);

		lblDeadlocks = new JLabel("deadlocks");
		lblDeadlocks.setBounds(814, 573, 50, 13);
		getContentPane().add(lblDeadlocks);

		animationThread = new Thread(this, "Test");
		animationThread.start();
	}

	@Override
	public void run() {
		while (true) {
			if (running) {
				bml.move();
				panel.repaint();
				ArrayList<Integer> deadlocks = bml.countDeadlocks();
				//System.out.println("running");
			}
			try {
				Thread.sleep(20);
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
					frame = new SimulatorFrame();
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
		final int delta = 20;		// 1つのマスのサイズ
		final int sx = 10, sy = 10;	// 描画開始座標

		/**
		 * Panel内部を描画します。
		 */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			//drawGrid(g);
			//drawArrows(g);
			drawCircles(g);
		}

		/**
		 * （下請けメソッド）グリッドを描画します。
		 */
		private void drawGrid(Graphics g) {
			for (int i = 0; i <= L; i++) {
				g.drawLine(sx, sy+delta*i, sx+delta*L, sy+delta*i);
				g.drawLine(sx+delta*i, sy, sx+delta*i, sy+delta*L);
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
			final String[] arrow = {"→", "←", "↑", "↓"};
			final Color[]  color = {Color.GRAY, Color.BLACK, Color.RED, Color.BLUE};

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
						g.drawString(arrow[index], sx+delta*i + 5, sy+delta*j + 15);
					}
					// 縦方向の矢印の描画
					if (siteY[i][j] == 1) {
						index = (i % 2 == 0 ? UP : DOWN);
						g.setColor(color[index]);
						g.drawString(arrow[index], sx+delta*i + 5, sy+delta*j + 15);
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
			g.drawRect(sx, sy, d*L, d*L);

			int[][][] sites = bml.getSites();
			int[][] siteX = sites[0];
			int[][] siteY = sites[1];

			ArrayList<Integer> deadlocks = bml.countDeadlocks();
			int deadlocksSize = deadlocks.size();
			for (int i = 0; i < deadlocksSize; ) {
				int x = deadlocks.get(i); i++;
				int y = deadlocks.get(i); i++;
				g.setColor(Color.CYAN);
				g.fillRect(sx+d*x, sy+d*y, d*2, d*2);
			}

			lblDeadlocks.setText("" + deadlocksSize/2);

			for (int i = 0; i < L; i++) {
				for (int j = 0; j < L; j++) {
					if (siteX[i][j] == 1) {
						if (j % 2 == 0) {
							g.setColor(Color.GRAY);
							g.fillOval(sx+d*i, sy+d*j, d, d);
						} else {
							g.setColor(Color.BLACK);
							g.fillOval(sx+d*i, sy+d*j, d, d);
						}
					}
					if (siteY[i][j] == 1) {
						if (i % 2 == 0) {
							g.setColor(Color.RED);
							g.fillOval(sx+d*i, sy+d*j, d, d);
						} else {
							g.setColor(Color.BLUE);
							g.fillOval(sx+d*i, sy+d*j, d, d);
						}
					}
				}
			}


		}




	}
}

package bml2;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class SimulatorFrame extends JFrame implements Runnable {

	private static final int L = 20; // 格子数
	private static final int k = 1;  // 密度指定

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

		setTitle("Extended BML Model Simulator");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		getContentPane().setLayout(null);

		// パネル
		panel = new SimulatorPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 480, 480);
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
		btnStart.setBounds(492, 334, 91, 21);
		getContentPane().add(btnStart);

		animationThread = new Thread(this, "Test");
		animationThread.start();
	}

	@Override
	public void run() {
		while (true) {
			if (running) {
				bml.move1period();
				panel.repaint();
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

			drawGrid(g);
			drawArrows(g);
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
	}
}

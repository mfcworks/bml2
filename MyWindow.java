package bml2;

import java.awt.Color;

import javax.swing.JFrame;

public class MyWindow extends JFrame {

	// コンストラクタ
	public MyWindow() {
		super();
		initializeComponent();
	}

	private void initializeComponent() {
		// ウィンドウ
		this.setTitle("MyWindow");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(640, 480);
		this.setResizable(false);
		this.setLocationByPlatform(true); // OSから与えられるデフォルトの位置に表示する

		this.getContentPane().setBackground(Color.CYAN);


		// ウィンドウの表示
		//frame.pack();
		this.setVisible(true);
	}

//	@Override
//	public void paint(Graphics g) {
//		this.setBackground(Color.CYAN);
//	//	g.setColor(Color.RED);
//	//	g.drawRect(100, 100, 150, 150);
//	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MyWindow();
			}
		});

	}

}

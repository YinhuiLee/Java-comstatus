package comstatus.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @classname SingleThreadScheduler
 * @description 单线程调度器
 * 
 */
public class SingleThreadScheduler {
	private static int i = 0; // 线程计数
	private static ComStatusFrame frame;

	public static void main(String[] args) {
		System.out.println("线程启动 ...");
		frame = new ComStatusFrame();
		init();
	}

	/**
	 * 启动线程并定时执行
	 */
	public static void init() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			i++;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String msg = "T" + i + ": " + df.format(new Date());
			System.out.println(msg);
			frame.label.setText(msg); // 更新UI
			try {
				// 休眠3s
				Thread.sleep(3000);
				msg = "T" + i + ": " + df.format(new Date());
				System.out.println(msg);
				frame.label.setText(msg);// 更新UI
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, 2, 5, TimeUnit.SECONDS);
	}
}

/**
 * UI
 *
 */
class ComStatusFrame extends Frame {
	private Panel pan;
	public Label label;

	public ComStatusFrame() {
		this.setTitle("ComStatus");
		this.setLocation(100, 100);
		this.setSize(800, 300);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		pan = new Panel();
		pan.setBackground(Color.orange);
		label = new Label();
		label.setSize(800, 300);
		label.setFont(new Font("Times New Roman Italic", Font.BOLD, 50));
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		label.setText("T0: " + df.format(new Date()));
		pan.add(label);
		this.add(pan);
		this.setVisible(true);
	}
}

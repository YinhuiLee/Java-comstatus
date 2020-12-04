package comstatus.mypackage;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.DimensionUIResource;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.JFreeChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.sun.management.OperatingSystemMXBean;

import af.swing.image.AfImageView;
import af.swing.layout.AfXLayout;
import af.swing.layout.AfYLayout;
import isclab.iot.model.DataPoint;
import isclab.iot.uploader.DataUploader;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

/**
 * @classname SingleThreadScheduler
 * @description 单线程调度器
 * 
 */
public class SingleThreadScheduler {
	private static int time = 0; // 时间计数
	private static ComStatusFrame frame;
	private static LimitQueue<SystemResource> sysRes = new LimitQueue<SystemResource>(10);
	private static SystemResource stmRes = null;

	public static void main(String[] args) {
		System.out.println("系统资源监控软件运行中 ...");
		frame = new ComStatusFrame();
		init();
		uploadLocalData();// 上传本地资源使用情况

	}

	public LimitQueue<SystemResource> getSystemResources() {
		return sysRes;
	}

	/**
	 * 启动线程 定时获取系统资源使用情况
	 */
	public static void init() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			try {
				SystemResource systemResource = new SystemResource();
				SystemInfo systemInfo = new SystemInfo();
				OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
				MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

				/* 操作系统信息 */
				// System.out.println("============================== 操作系统信息
				// =============================");
				// 操作系统
				String osName = System.getProperty("os.name");
				// 获得线程总数
				ThreadGroup parentThread;
				parentThread = Thread.currentThread().getThreadGroup();
				while (parentThread.getParent() != null) {
					parentThread = parentThread.getParent();
				}

				int totalThread = parentThread.activeCount();

				/*
				 * System.out.println("操作系统: " + osName); System.out.println("程序启动时间: " + new
				 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss") .format(new
				 * Date(ManagementFactory.getRuntimeMXBean().getStartTime())));
				 * System.out.println("pid: " + System.getProperty("PID"));
				 * System.out.println("总线程数: " + totalThread);
				 */

				// 磁盘使用情况
				File[] files = File.listRoots();
				List<DiskUsage> disks = new ArrayList<DiskUsage>();
				for (File file : files) {
					double total = file.getTotalSpace() * 1.0 / 1024 / 1024 / 1024;
					double free = file.getFreeSpace() * 1.0 / 1024 / 1024 / 1024;
					String path = file.getPath().charAt(0) + "" + file.getPath().charAt(1);
					disks.add(new DiskUsage(path, total, free));
				}
				systemResource.setDisks(disks);

				/* 堆内存信息 */
				// System.out.println("\n============================== 堆内存信息
				// =============================");
				// 椎内存使用情况
				MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
				// 初始的总内存(B)
				long initTotalMemorySize = memoryUsage.getInit();
				// 最大可用内存(B)
				long maxMemorySize = memoryUsage.getMax();
				// 已使用的内存(B)
				long usedMemorySize = memoryUsage.getUsed();
				systemResource.setJvmUsage(new JVMUsage(initTotalMemorySize, maxMemorySize, usedMemorySize));

				/*
				 * System.out.println("初始的总内存(JVM): " + new
				 * DecimalFormat("#.#").format(initTotalMemorySize * 1.0 / 1024 / 1024) + "M");
				 * System.out.println( "最大可用内存(JVM): " + new
				 * DecimalFormat("#.#").format(maxMemorySize * 1.0 / 1024 / 1024) + "M");
				 * System.out.println( "已使用的内存(JVM): " + new
				 * DecimalFormat("#.#").format(usedMemorySize * 1.0 / 1024 / 1024) + "M");
				 */

				/* 物理内存信息 */
				// System.out.println("\n============================== 物理内存信息
				// =============================");
				// 总的物理内存(B)
				long totalMemorySizeByte = osmxb.getTotalPhysicalMemorySize();
				long totalMemorySizeByte2 = systemInfo.getHardware().getMemory().getTotal();
				// 总的物理内存(G)
				double totalMemorySize = totalMemorySizeByte / 1024.0 / 1024.0 / 1024.0;

				String totalMemorySize2 = new DecimalFormat("#.##").format(totalMemorySizeByte2 / 1024.0 / 1024 / 1024)
						+ "G";
				// 剩余的物理内存(B)
				double freePhysicalMemorySizeByte = osmxb.getFreePhysicalMemorySize();
				long freePhysicalMemorySizeByte2 = systemInfo.getHardware().getMemory().getAvailable();
				// 剩余的物理内存(G)
				double freePhysicalMemorySize = freePhysicalMemorySizeByte / 1024.0 / 1024.0 / 1024.0;
				// 剩余的物理内存
				String freePhysicalMemorySize1 = new DecimalFormat("#.##")
						.format(freePhysicalMemorySizeByte / 1024.0 / 1024 / 1024) + "G";
				String freePhysicalMemorySize2 = new DecimalFormat("#.##")
						.format(freePhysicalMemorySizeByte2 * 1.0 / 1024 / 1024 / 1024) + "G";
				// 已使用的物理内存
				double usedMemory = totalMemorySize - freePhysicalMemorySize;
				String usedMemory2 = new DecimalFormat("#.##")
						.format((totalMemorySizeByte2 - freePhysicalMemorySizeByte2) * 1.0 / 1024 / 1024 / 1024) + "G";
				double memoryUsedRate = 1 - (freePhysicalMemorySizeByte * 1.0 / totalMemorySizeByte);// 物理内存使用率

				// 将物理内存信息加入系统资源使用对象
				systemResource.setMemUsage(new MemUsage(totalMemorySize, usedMemory));

				/*
				 * System.out.println("总的物理内存: " + totalMemorySize2);
				 * System.out.println("剩余的物理内存: " + freePhysicalMemorySize);
				 * System.out.println("剩余的物理内存: " + freePhysicalMemorySize2);
				 * System.out.println("已使用的物理内存: " + usedMemory2);
				 */

				/* JVM信息 */
				/*
				 * System.out.
				 * println("\n============================== JVM信息 ============================="
				 * ); System.out.println("JAVA_HOME: " + System.getProperty("java.home"));
				 * System.out.println("JAVA_VERSION: " + System.getProperty("java.version"));
				 * System.out.println("USER_HOME: " + System.getProperty("user.home"));
				 * System.out.println("USER_NAME: " + System.getProperty("user.name"));
				 */

				/* CPU信息 */
				// System.out.println("\n============================== CPU信息
				// =============================");
				printlnCpuInfo(systemInfo, systemResource);// 获取并保存CPU信息
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 1, 2, TimeUnit.SECONDS);
	}

	/**
	 * 
	 * 获取CPU资源使用情况
	 * 
	 * @param systemInfo
	 * @param systemResource
	 * @throws InterruptedException
	 */
	private static void printlnCpuInfo(SystemInfo systemInfo, SystemResource systemResource)
			throws InterruptedException {
		CentralProcessor processor = systemInfo.getHardware().getProcessor();
		long[] prevTicks = processor.getSystemCpuLoadTicks();
		// 睡眠1s
		TimeUnit.SECONDS.sleep(1);
		long[] ticks = processor.getSystemCpuLoadTicks();
		long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
				- prevTicks[CentralProcessor.TickType.NICE.getIndex()];
		long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
				- prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
		long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
				- prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
		long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
				- prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
		long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
				- prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
		long user = ticks[CentralProcessor.TickType.USER.getIndex()]
				- prevTicks[CentralProcessor.TickType.USER.getIndex()];
		long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
				- prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
		long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
				- prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
		long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

		// 将CPU使用信息加入系统资源使用对象
		int coreNumber = processor.getLogicalProcessorCount();
		double cSysRate = cSys * 1.0 / totalCpu * 100;// 系统使用率
		double userRate = user * 1.0 / totalCpu * 100;// 用户使用率
		systemResource.setCpuUsage(new CPUUsage(coreNumber, cSysRate, userRate));

		/*
		 * System.out.println("cpu核数: " + Runtime.getRuntime().availableProcessors());
		 * System.out.println("cpu核数: " + processor.getLogicalProcessorCount());
		 * System.out.println("cpu系统使用率: " + new DecimalFormat("#.##%").format(cSys *
		 * 1.0 / totalCpu)); System.out.println("cpu用户使用率: " + new
		 * DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
		 * System.out.println("cpu当前等待率: " + new DecimalFormat("#.##%").format(iowait *
		 * 1.0 / totalCpu)); System.out.println("cpu当前空闲率: " + new
		 * DecimalFormat("#.##%").format(idle * 1.0 / totalCpu));
		 * System.out.format("CPU load: %.1f%% (counting ticks)%n",
		 * processor.getSystemCpuLoadBetweenTicks() * 100);
		 * System.out.format("CPU load: %.1f%% (OS MXBean)%n",
		 * processor.getSystemCpuLoad() * 100);
		 */

		// 系统资源使用对象加入队列
		sysRes.offer(systemResource);
		time++;
		stmRes = systemResource;
	}

	private static void uploadLocalData() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			if (stmRes != null) {
				/*
				 * 创建数据上传对象，并设置你自己的设备ID和传感器ID，以及用户的apiKey
				 * 说明：此处设备ID为45，传感器ID为220，apiKey为06ce184dea333df978cc6a6e67d65ed7，均为测试使用
				 */
				DataUploader du = new DataUploader(62, 236, "06ce184dea333df978cc6a6e67d65ed7");
				/*
				 * 构造数据点对象
				 */
				DataPoint point = new DataPoint();

				// 设置该数据点的时间戳，Date类型
				point.setTimestamp(new Date());

				// 设置该数据点所包含的值，浮点型数组，可以包含多项，例如CPU占用率，内存占用率等等，但每次上传的数据列表长度最好要相同
				// 想传几个指标就设几个
				List<Float> values = new ArrayList<Float>();
				values.add(new Double(stmRes.getCpuUsage().getcSysRate() + stmRes.getCpuUsage().getUserRate())
						.floatValue()); // 添加浮点值，例如CPU占用率
				values.add(new Double(stmRes.getMemUsage().getUsedMemory()).floatValue()); // 添加浮点值，例如内存使用量
				double totalDisks = 0;
				double freeDisks = 0;
				for (DiskUsage d : stmRes.getDisks()) {
					totalDisks += d.getTotal();
					freeDisks += d.getFree();
				}
				values.add(new Double(totalDisks - freeDisks).floatValue());// 磁盘使用量

				point.setValues(values);

				// 调用upload方法完成数据上传，如果数据上传成功则返回所上传数据的JSON字符串，如果不成功返回空
				if (du.upload(point).equals(null)) {
					System.out.println("上传数据失败！");
					try {
						Thread.sleep(100000);// 休眠
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// System.out.println(du.upload(point));
			}
		}, 2, 5, TimeUnit.SECONDS);
	}

	public int getTime() {
		return time;
	}

	public SystemResource getSystemResource() {
		return stmRes;
	}
}

/**
 * Swing UI
 *
 */
class ComStatusFrame extends JFrame implements ActionListener {
	private JPanel nJPan;
	private JPanel wJPan;
	private ChartPanel cJPan;
	private JPanel cenPan;
	private JButton CPUBtn;
	private JButton MEMBtn;
	private JButton DiskBtn;
	private SingleThreadScheduler sts = new SingleThreadScheduler();
	private boolean haveCJPan = false;// 记录是否已加入组件cJPan
	private boolean haveCenPan = false;// 记录是否已加入组件cenPan
	private String command;
	public DefaultPieDataset diskDataset = new DefaultPieDataset();// 磁盘数据
	private DefaultCategoryDataset cpuDataset = new DefaultCategoryDataset();// CPU数据
	private DefaultCategoryDataset memDataset = new DefaultCategoryDataset();// MEM数据
	private List<DiskUsage> disks = new ArrayList<DiskUsage>();// 磁盘数据
	private Comparable<?> previousSection = "C:";// 饼图标记

	/**
	 * 
	 * 主界面
	 */
	public ComStatusFrame() {
		this.setTitle("系统资源监控器");
		this.setBackground(Color.WHITE);
		this.setLocation(100, 100);
		this.setSize(1600, 900);
		setIconImage(Toolkit.getDefaultToolkit().getImage(SingleThreadScheduler.class.getResource("/images/边框图片.png")));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);// 取消点叉自动关闭
		this.setLayout(new BorderLayout());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "真的要退出程序么?", "请确认退出", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					System.out.println("系统资源监控软件运行结束！");
					System.exit(0);
				}
			}
		});
		nJPan = new JPanel();// 暂时没用到
		wJPan = new JPanel();
		cenPan = new JPanel();
		wJPan.setBackground(Color.WHITE);
		wJPan.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 2, new Color(189, 174, 173)));
		cenPan.setBackground(Color.WHITE);
		Border padding = BorderFactory.createEmptyBorder(0, 8, 0, 0);
		Border border = BorderFactory.createMatteBorder(2, 2, 0, 0, new Color(189, 174, 173));
		border = BorderFactory.createCompoundBorder(padding, border);
		cenPan.setBorder(border);
		cenPan.setLayout(new BorderLayout());
		this.add(cenPan, BorderLayout.CENTER);

		// 背景图片
		AfImageView imageView = new AfImageView();
		cenPan.add(imageView, BorderLayout.CENTER);
		imageView.setScaleType(AfImageView.FIT_XY);
		imageView.setBackground(Color.gray);
		try {
			URL imageUrl = this.getClass().getResource("/images/动漫壁纸.jpg");
			BufferedImage bfImage = ImageIO.read(imageUrl);
			Image image = bfImage;
			imageView.setImage(image);

		} catch (Exception e) {
			e.printStackTrace();
		}

		wJPan.setLayout(new AfYLayout(8));
		JPanel CPUPan = new JPanel();
		JPanel MEMPan = new JPanel();
		JPanel DiskPan = new JPanel();
		CPUPan.setLayout(new AfXLayout(8));
		CPUPan.setBackground(Color.WHITE);
		DiskPan.setLayout(new AfXLayout(8));
		DiskPan.setBackground(Color.WHITE);
		MEMPan.setLayout(new AfXLayout(8));
		MEMPan.setBackground(Color.WHITE);
		Border smallPadding = BorderFactory.createEmptyBorder(16, 8, 16, 8);
		CPUPan.setBorder(smallPadding);
		MEMPan.setBorder(smallPadding);
		DiskPan.setBorder(smallPadding);

		// 小CPU曲线
		// 曲线名称
		String CPUSeries = "CPU";

		// 定义图表对象
		JFreeChart chart = ChartFactory.createLineChart("", // 折线图名称
				"", // 横坐标名称
				"", // 纵坐标名称
				cpuDataset, // 数据
				PlotOrientation.VERTICAL, // 水平显示图像
				false, // include legend
				false, // tooltips
				false // urls
		);
		CategoryPlot plot = chart.getCategoryPlot();
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		plot.setRangeGridlinesVisible(true); // 是否显示格子线

		renderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		// 设置y轴范围
		rangeAxis.setRange(0, 100);
		plot.setRangeAxis(rangeAxis);
		rangeAxis.setVisible(false);// 坐标轴不可见

		rangeAxis.setUpperMargin(0.20);// 上边距

		rangeAxis.setTickLabelPaint(new ChartColor(92, 179, 204));

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setVisible(false);

		// 设置总的背景颜色
		chart.setBackgroundPaint(ChartColor.WHITE);

		// 设置图的背景颜色
		plot.setBackgroundPaint(ChartColor.WHITE);

		// 设置表格线颜色
		plot.setRangeGridlinePaint(new ChartColor(92, 179, 204));

		// 改变线条粗细
		renderer.setSeriesStroke(0, new BasicStroke(2.0F));
		// 改变线条颜色
		renderer.setSeriesPaint(0, new Color(36, 134, 185));

		// 边框颜色
		chart.setBorderPaint(new Color(92, 179, 204));
		chart.setBorderVisible(false);

		ChartPanel CPUChart = new ChartPanel(chart);
		CPUPan.add(CPUChart, "60%");

		// CPU文字
		JPanel CPUTxtPan = new JPanel();
		CPUTxtPan.setLayout(new AfYLayout(8));
		CPUTxtPan.setBackground(Color.WHITE);
		JLabel CPULabel = new JLabel("CPU");
		CPULabel.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 30));
		String CPUUse = "";
		DecimalFormat df = new DecimalFormat("#.0");
		JLabel CPUUseLabel = new JLabel(CPUUse);
		CPUUseLabel.setFont(new Font("TIMES NEW ROMAN", Font.PLAIN, 20));
		CPUUseLabel.setBackground(Color.WHITE);
		CPUUseLabel.setForeground(Color.GRAY);
		CPUTxtPan.add(CPULabel, "60%");
		CPUTxtPan.add(CPUUseLabel, "40%");
		CPUPan.add(CPUTxtPan, "40%");
		wJPan.add(CPUPan, "20%");

		// 小内存曲线
		// 曲线名称
		String MEMSeries = "MEM";

		// 定义图表对象
		JFreeChart MEMChart = ChartFactory.createLineChart("", // 折线图名称
				"", // 横坐标名称
				"", // 纵坐标名称
				memDataset, // 数据
				PlotOrientation.VERTICAL, // 水平显示图像
				false, // include legend
				false, // tooltips
				false // urls
		);
		CategoryPlot memPlot = MEMChart.getCategoryPlot();
		LineAndShapeRenderer memRenderer = (LineAndShapeRenderer) memPlot.getRenderer();
		memPlot.setRangeGridlinesVisible(true); // 是否显示格子线

		memRenderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见
		NumberAxis memRangeAxis = (NumberAxis) memPlot.getRangeAxis();
		// 设置y轴范围
		memPlot.setRangeAxis(memRangeAxis);
		memRangeAxis.setVisible(false);// 坐标轴不可见

		memRangeAxis.setUpperMargin(0.20);// 上边距

		memRangeAxis.setTickLabelPaint(new ChartColor(92, 179, 204));

		CategoryAxis memDomainAxis = memPlot.getDomainAxis();
		memDomainAxis.setVisible(false);

		// 设置总的背景颜色
		MEMChart.setBackgroundPaint(ChartColor.WHITE);

		// 设置图的背景颜色
		memPlot.setBackgroundPaint(ChartColor.WHITE);

		// 设置表格线颜色
		memPlot.setRangeGridlinePaint(new ChartColor(186, 47, 123));

		// 改变线条粗细
		memRenderer.setSeriesStroke(0, new BasicStroke(2.0F));
		// 改变线条颜色
		memRenderer.setSeriesPaint(0, new Color(186, 47, 123));

		// 边框颜色
		MEMChart.setBorderPaint(new Color(92, 179, 204));
		MEMChart.setBorderVisible(false);

		ChartPanel memChart = new ChartPanel(MEMChart);
		MEMPan.add(memChart, "60%");

		// MEM文字
		JPanel MEMTxtPan = new JPanel();
		MEMTxtPan.setLayout(new AfYLayout(8));
		MEMTxtPan.setBackground(Color.WHITE);
		JLabel MEMLabel = new JLabel("内存");
		MEMLabel.setFont(new Font("微软雅黑", Font.PLAIN, 30));
		String MEMUse = "";
		JLabel MEMUseLabel = new JLabel(MEMUse);
		MEMUseLabel.setFont(new Font("TIMES NEW ROMAN", Font.PLAIN, 20));
		MEMUseLabel.setBackground(Color.WHITE);
		MEMUseLabel.setForeground(Color.GRAY);
		MEMTxtPan.add(MEMLabel, "60%");
		MEMTxtPan.add(MEMUseLabel, "40%");
		MEMPan.add(MEMTxtPan, "40%");
		wJPan.add(MEMPan, "20%");

		// 小磁盘图
		// 定义图表对象
		JFreeChart diskChart = ChartFactory.createPieChart("", diskDataset, false, true, false);
		PiePlot pie = (PiePlot) diskChart.getPlot();

		pie.setNoDataMessage("还未获取数据，请稍等！");
		
		// 设置PieChart是否显示为圆形
		pie.setCircular(true);
		// 间距
		pie.setLabelGap(0.01D);
		
		//取消标签
		pie.setLabelGenerator(null);//取消图中标签

		// 设置总的背景颜色
		chart.setBackgroundPaint(ChartColor.WHITE);

		// 设置图的背景颜色
		pie.setBackgroundPaint(ChartColor.WHITE);
		
		// 设置饼图边框
		pie.setOutlinePaint(Color.GREEN); // 设置绘图面板外边的填充颜色

		ChartPanel diskChartPan = new ChartPanel(diskChart);
		DiskPan.add(diskChartPan,"60%");
		
		//磁盘文字
		JPanel DiskTxtPan = new JPanel();
		DiskTxtPan.setLayout(new AfYLayout(8));
		DiskTxtPan.setBackground(Color.WHITE);
		JLabel DiskLabel = new JLabel("磁盘");
		DiskLabel.setFont(new Font("微软雅黑", Font.PLAIN, 30));
		String DiskUse = "";
		JLabel DiskUseLabel = new JLabel(DiskUse);
		DiskUseLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
		DiskUseLabel.setBackground(Color.WHITE);
		DiskUseLabel.setForeground(Color.GRAY);
		DiskTxtPan.add(DiskLabel, "60%");
		DiskTxtPan.add(DiskUseLabel, "40%");
		DiskPan.add(DiskTxtPan, "40%");
		DiskPan.validate();// 刷新
		wJPan.add(DiskPan, "20%");
		
		this.add(wJPan, BorderLayout.WEST);
		wJPan.setPreferredSize(new DimensionUIResource(300, 0));
		
		//鼠标事件
		CPUPan.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				//CPUPan.setBorder(border);
			}
		});

		this.setVisible(true);

		// 开新线程拿数据
		LimitQueue<SystemResource> sysRes = sts.getSystemResources();
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			// 磁盘
			diskDataset.clear();
			double total = 0;
			double free = 0;
			for (SystemResource s : sysRes.getQueue()) {
				disks = s.getDisks();
				for (DiskUsage d : disks) {
					total += d.getTotal();
					free += d.getFree();
					diskDataset.setValue(d.getDisk(), d.getTotal());
				}
			}
			// CPU
			cpuDataset.clear();
			int x = 0;
			for (SystemResource s : sysRes.getQueue()) {
				cpuDataset.addValue(s.getCpuUsage().getcSysRate() + s.getCpuUsage().getUserRate(), CPUSeries,
						String.valueOf(sts.getTime() - sysRes.size() + x));
				x++;
			}
			plot.setDataset(cpuDataset);
			x = 0;
			// MEM
			memDataset.clear();
			for (SystemResource s : sysRes.getQueue()) {
				memDataset.addValue(s.getMemUsage().getUsedMemory(), MEMSeries,
						String.valueOf(sts.getTime() - sysRes.size() + x));
				x++;
			}
			memPlot.setDataset(memDataset);

			// 文本框更新
			if (sts.getSystemResource() != null) {
				// CPU
				CPUUseLabel.setText(df.format(sts.getSystemResource().getCpuUsage().getcSysRate()
						+ sts.getSystemResource().getCpuUsage().getUserRate()) + "%");
				// MEM
				memRangeAxis.setRange(0, sts.getSystemResource().getMemUsage().getTotalMemorySize());
				String txt = "<html>" + df.format(sts.getSystemResource().getMemUsage().getUsedMemory()) + "/"
						+ df.format(sts.getSystemResource().getMemUsage().getTotalMemorySize()) + " GB<br/>"
						+ df.format(sts.getSystemResource().getMemUsage().getUsedMemory()
								/ sts.getSystemResource().getMemUsage().getTotalMemorySize() * 100)
						+ "%<br/></html>";
				MEMUseLabel.setText(txt);
				//磁盘
				String diskTxt = "<html>总空间  " + df.format(total) + " GB<br/>可用空间 "
						+ df.format(free)
						+ " GB<br/></html>";
				DiskUseLabel.setText(diskTxt);
			}
		}, 1, 2, TimeUnit.SECONDS);
	}

	/**
	 * 
	 * Button时间处理
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		command = e.getActionCommand();

		if (command.equals("CPU")) {// CPU资源监控
			int result = JOptionPane.showConfirmDialog(null, "是否要切换到CPU资源监控？", "请确认切换", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				CPUChart();
			}
		}
		if (command.equals("物理内存")) {// 物理内存资源监控
			int result = JOptionPane.showConfirmDialog(null, "是否要切换到物理内存资源监控？", "请确认切换", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				MEMChart();
			}
		}
		if (command.equals("磁盘")) {// 磁盘资源监控
			int result = JOptionPane.showConfirmDialog(null, "是否要切换到物理内存资源监控？", "请确认切换", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				DiskChart();
			}
		}
	}

	/**
	 * 
	 * CPU资源使用图
	 * 
	 * @return
	 */
	private void CPUChart() {
		int i = 0;
		// 曲线名称
		String series = "CPU";
		// 数据
		LimitQueue<SystemResource> sysRes = sts.getSystemResources();
		System.out.println(sysRes.size());
		for (SystemResource s : sysRes.getQueue()) {
			cpuDataset.addValue(s.getCpuUsage().getcSysRate() + s.getCpuUsage().getUserRate(), series,
					String.valueOf(sts.getTime() - sysRes.size() + i));
			i++;
		}
		// 定义图表对象
		JFreeChart chart = ChartFactory.createLineChart("", // 折线图名称
				"", // 横坐标名称
				"", // 纵坐标名称
				cpuDataset, // 数据
				PlotOrientation.VERTICAL, // 水平显示图像
				false, // include legend
				false, // tooltips
				false // urls
		);
		CategoryPlot plot = chart.getCategoryPlot();
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		plot.setRangeGridlinesVisible(true); // 是否显示格子线
		plot.setBackgroundAlpha(0.5f); // 设置背景透明度
		renderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);
		// 设置y轴范围
		rangeAxis.setRange(0, 100);
		plot.setRangeAxis(rangeAxis);
		rangeAxis.setVisible(false);// 坐标轴不可见

		rangeAxis.setUpperMargin(0.20);// 上边距

		Font font = new Font("微软雅黑", Font.BOLD, 18);
		chart.getTitle().setFont(font);
		rangeAxis.setLabelFont(font);
		rangeAxis.setTickLabelPaint(new ChartColor(92, 179, 204));

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLabelFont(font);
		domainAxis.setVisible(false);

		// 设置总的背景颜色
		chart.setBackgroundPaint(ChartColor.WHITE);
		// 设置标题颜色
		chart.getTitle().setPaint(new ChartColor(92, 179, 204));

		// 设置图的背景颜色
		plot.setBackgroundPaint(ChartColor.WHITE);
		// 设置表格线颜色
		plot.setRangeGridlinePaint(new ChartColor(92, 179, 204));

		// 改变线条粗细
		renderer.setSeriesStroke(0, new BasicStroke(2.0F));
		// 改变线条颜色
		renderer.setSeriesPaint(0, new Color(36, 134, 185));

		if (this.haveCJPan) {
			this.remove(cJPan);
		}
		cJPan = new ChartPanel(chart);
		this.add(cJPan, "Center");
		this.validate();// 刷新
		this.haveCJPan = true;

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			if (!command.equals("CPU")) {
				System.out.println("CPU资源监控停止！");
				int er = 1 / 0;// 人为发生异常
			}
			cpuDataset.clear();
			int x = 0;
			for (SystemResource s : sysRes.getQueue()) {
				cpuDataset.addValue(s.getCpuUsage().getcSysRate() + s.getCpuUsage().getUserRate(), series,
						String.valueOf(sts.getTime() - sysRes.size() + x));
				x++;
			}
			plot.setDataset(cpuDataset);
		}, 2, 5, TimeUnit.SECONDS);
	}

	/**
	 * 
	 * 内存资源使用图
	 */
	private void MEMChart() {
		int i = 0;
		DefaultCategoryDataset linedataset = new DefaultCategoryDataset();
		// 曲线名称
		String series = "MEM";
		// 数据
		LimitQueue<SystemResource> sysRes = sts.getSystemResources();
		System.out.println(sysRes.size());
		for (SystemResource s : sysRes.getQueue()) {
			linedataset.addValue(s.getMemUsage().getUsedMemory(), series,
					String.valueOf(sts.getTime() - sysRes.size() + i));
			i++;
		}
		// 定义图表对象
		JFreeChart chart = ChartFactory.createLineChart("物理内存资源监控", // 折线图名称
				"时间", // 横坐标名称
				"物理内存使用量(G)", // 纵坐标名称
				linedataset, // 数据
				PlotOrientation.VERTICAL, // 水平显示图像
				true, // include legend
				true, // tooltips
				false // urls
		);
		CategoryPlot plot = chart.getCategoryPlot();
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		plot.setRangeGridlinesVisible(true); // 是否显示格子线
		plot.setBackgroundAlpha(0.3f); // 设置背景透明度
		renderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRangeIncludesZero(true);
		rangeAxis.setUpperMargin(0.20);
		rangeAxis.setLabelAngle(Math.PI / 2.0);

		Font font = new Font("微软雅黑", Font.BOLD, 18);
		chart.getTitle().setFont(font);
		rangeAxis.setLabelFont(font);
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLabelFont(font);

		if (this.haveCJPan) {// 检查是否已经有JFreeChart
			this.remove(cJPan);
		}
		cJPan = new ChartPanel(chart);
		this.add(cJPan, "Center");
		this.validate();// 刷新
		this.haveCJPan = true;

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			if (!command.equals("物理内存")) {
				System.out.println("物理内存资源监控停止！");
				int er = 1 / 0;// 人为发生异常
			}
			linedataset.clear();
			int x = 0;
			for (SystemResource s : sysRes.getQueue()) {
				linedataset.addValue(s.getMemUsage().getUsedMemory(), series,
						String.valueOf(sts.getTime() - sysRes.size() + x));
				x++;
			}
			plot.setDataset(linedataset);
		}, 2, 5, TimeUnit.SECONDS);
	}

	/**
	 * 
	 * 磁盘资源使用图
	 */
	private void DiskChart() {

		// 定义图表对象
		JFreeChart chart = ChartFactory.createPieChart("", diskDataset, false, true, false);
		// 三个部分设置字体的方法分别如下:
		TextTitle textTitle = chart.getTitle();
		textTitle.setFont(new Font("宋体", Font.BOLD, 20));
		LegendTitle legend = chart.getLegend();
		if (legend != null) {
			legend.setItemFont(new Font("宋体", Font.BOLD, 20));
		}
		PiePlot pie = (PiePlot) chart.getPlot();
		pie.setLabelFont(new Font("宋体", Font.BOLD, 12));
		pie.setNoDataMessage("还未获得数据，请稍等！");
		// 设置PieChart是否显示为圆形
		pie.setCircular(true);
		// 间距
		pie.setLabelGap(0.01D);

		// 设置总的背景颜色
		chart.setBackgroundPaint(ChartColor.WHITE);

		// 设置图的背景颜色
		pie.setBackgroundPaint(ChartColor.WHITE);
		
		pie.setLabelBackgroundPaint(null);//标签背景颜色

		pie.setLabelOutlinePaint(null);//标签边框颜色

		pie.setLabelShadowPaint(null);//标签阴影颜色

		this.remove(cenPan);
		cenPan = new JPanel();
		cenPan.setBackground(Color.WHITE);
		Border padding = BorderFactory.createEmptyBorder(0, 8, 0, 0);
		Border border = BorderFactory.createMatteBorder(2, 2, 0, 0, new Color(189, 174, 173));
		border = BorderFactory.createCompoundBorder(padding, border);
		cenPan.setBorder(border);

		cJPan = new ChartPanel(chart);

		// 设置大容器背景
		cenPan.setBackground(Color.WHITE);

		// 设置大容器边框

		// 设置饼图边框
		pie.setOutlinePaint(Color.WHITE); // 设置绘图面板外边的填充颜色

		JPanel smallPie = new JPanel();
		smallPie.setLayout(new AfXLayout(8));
		smallPie.setBackground(Color.WHITE);
		smallPie.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(228, 223, 215)));

		// 设置布局
		cenPan.setLayout(new AfYLayout(8));
		cenPan.add(cJPan, "60%");
		cenPan.add(smallPie, "40%");

		this.add(cenPan, "Center");
		this.validate();// 刷新
		this.haveCJPan = true;
		this.haveCenPan = true;

		// 添加事件监听器

		cJPan.addChartMouseListener(new ChartMouseListener() {

			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
				JFreeChart chart = event.getChart();
				ChartEntity entity = event.getEntity();
				PiePlot plot = (PiePlot) chart.getPlot();

				if (chart != null) {
					if (entity instanceof PieSectionEntity) {
						// 重置前一个突出显示的Section
						plot.setExplodePercent(previousSection, 0);

						// 突出显示当前鼠标指向的Section
						PieSectionEntity pieEntity = (PieSectionEntity) entity;
						plot.setExplodePercent(pieEntity.getSectionKey(), 0.2);

						// 记住当前鼠标指向的Section的key
						previousSection = pieEntity.getSectionKey();
					}
				}
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent event) {

				// 获取图表信息
				DefaultPieDataset diskData = new DefaultPieDataset();
				JFreeChart chart = event.getChart();
				ChartEntity entity = event.getEntity();
				DiskUsage disk = new DiskUsage("", 0, 0);
				if (chart != null) {
					if (entity instanceof PieSectionEntity) {

						// 获取需要显示的磁盘
						PieSectionEntity pieEntity = (PieSectionEntity) entity;
						for (DiskUsage d : disks) {
							if (pieEntity.getSectionKey().equals(d.getDisk())) {
								disk = d;
								break;
							}
						}

						// 设置数据
						diskData.setValue("已使用", disk.getTotal());
						diskData.setValue("未使用", disk.getTotal() - disk.getFree());

						// 定义图表对象
						JFreeChart smallChart = ChartFactory.createPieChart("", diskData, false, true, false);

						PiePlot pie = (PiePlot) smallChart.getPlot();
						pie.setLabelFont(new Font("宋体", Font.BOLD, 12));
						pie.setNoDataMessage("还未获取到数据，请稍等！");
						// 设置PieChart是否显示为圆形
						pie.setCircular(true);
						// 间距
						pie.setLabelGap(0.01D);

						// 设置总的背景颜色
						chart.setBackgroundPaint(ChartColor.WHITE);

						// 设置图的背景颜色
						pie.setBackgroundPaint(ChartColor.WHITE);

						// 设置饼图边框
						pie.setOutlinePaint(Color.WHITE); // 设置绘图面板外边的填充颜色

						// 添加饼图
						cenPan.remove(smallPie);
						smallPie.removeAll();
						smallPie.setBackground(Color.WHITE);
						smallPie.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(228, 223, 215)));
						smallPie.add(new ChartPanel(smallChart), "50%");

						// 添加文字容器
						JPanel labelPan = new JPanel();
						labelPan.setLayout(new AfYLayout(8));
						labelPan.setBackground(Color.WHITE);

						// 磁盘文字
						JLabel diskText = new JLabel(disk.getDisk());
						diskText.setFont(new Font("微软雅黑", Font.BOLD, 30));
						diskText.setBackground(Color.WHITE);

						// 小数格式
						DecimalFormat df = new DecimalFormat("#.00");
						JLabel totalText = new JLabel("总空间     " + df.format(disk.getTotal()) + "G");
						totalText.setFont(new Font("微软雅黑", Font.BOLD, 20));
						diskText.setBackground(Color.WHITE);

						JLabel usedText = new JLabel("占用空间  " + df.format(disk.getTotal() - disk.getFree()) + "G");
						usedText.setFont(new Font("微软雅黑", Font.BOLD, 20));
						diskText.setBackground(Color.WHITE);

						JLabel freeText = new JLabel("空闲空间  " + df.format(disk.getFree()) + "G");
						freeText.setFont(new Font("微软雅黑", Font.BOLD, 20));
						diskText.setBackground(Color.WHITE);

						// 添加标签
						labelPan.add(diskText, "40%");
						labelPan.add(totalText, "20%");
						labelPan.add(usedText, "20%");
						labelPan.add(freeText, "20%");

						smallPie.add(labelPan, "50%");
						cenPan.add(smallPie, "40%");
						cenPan.validate();
					}
				}
			}
		});
	}
}

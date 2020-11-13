package comstatus.base;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import com.sun.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

/**
 * @classname SystemMonitor
 * @description 系统资源监控类，用于定时获取操作系统的资源情况
 */
public class SystemMonitor {
	
	public static void main(String[] args) {
		System.out.println("系统监控程序启动 ...");
		init();
	}

	/**
	 * 启动
	 */
	public static void init() {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			try {
				SystemInfo systemInfo = new SystemInfo();
				OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
				MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

				/* 操作系统信息 */
				System.out.println("============================== 操作系统信息 =============================");
				// 操作系统
				String osName = System.getProperty("os.name");
				// 获得线程总数
				ThreadGroup parentThread;
				parentThread = Thread.currentThread().getThreadGroup();
				while (parentThread.getParent() != null) {
					parentThread = parentThread.getParent();
				}

				int totalThread = parentThread.activeCount();

				System.out.println("操作系统: " + osName);
				System.out.println("程序启动时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date(ManagementFactory.getRuntimeMXBean().getStartTime())));
				System.out.println("pid: " + System.getProperty("PID"));
				System.out.println("总线程数: " + totalThread);

				// 磁盘使用情况
				File[] files = File.listRoots();
				for (File file : files) {
					String total = new DecimalFormat("#.#").format(file.getTotalSpace() * 1.0 / 1024 / 1024 / 1024)
							+ "G";
					String free = new DecimalFormat("#.#").format(file.getFreeSpace() * 1.0 / 1024 / 1024 / 1024) + "G";
					String un = new DecimalFormat("#.#").format(file.getUsableSpace() * 1.0 / 1024 / 1024 / 1024) + "G";
					String path = file.getPath();
					System.out.println(path + " 总空间: " + total + ", 可用空间: " + un + ", 空闲空间: " + free);
				}

				/* 堆内存信息 */
				System.out.println("\n============================== 堆内存信息 =============================");
				// 椎内存使用情况
				MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
				// 初始的总内存(B)
				long initTotalMemorySize = memoryUsage.getInit();
				// 最大可用内存(B)
				long maxMemorySize = memoryUsage.getMax();
				// 已使用的内存(B)
				long usedMemorySize = memoryUsage.getUsed();

				System.out.println("初始的总内存(JVM): "
						+ new DecimalFormat("#.#").format(initTotalMemorySize * 1.0 / 1024 / 1024) + "M");
				System.out.println(
						"最大可用内存(JVM): " + new DecimalFormat("#.#").format(maxMemorySize * 1.0 / 1024 / 1024) + "M");
				System.out.println(
						"已使用的内存(JVM): " + new DecimalFormat("#.#").format(usedMemorySize * 1.0 / 1024 / 1024) + "M");

				/* 物理内存信息 */
				System.out.println("\n============================== 物理内存信息 =============================");
				// 总的物理内存(B)
				long totalMemorySizeByte = osmxb.getTotalPhysicalMemorySize();
				long totalMemorySizeByte2 = systemInfo.getHardware().getMemory().getTotal();
				// 总的物理内存
				String totalMemorySize = new DecimalFormat("#.##").format(totalMemorySizeByte / 1024.0 / 1024 / 1024)
						+ "G";
				String totalMemorySize2 = new DecimalFormat("#.##").format(totalMemorySizeByte2 / 1024.0 / 1024 / 1024)
						+ "G";
				// 剩余的物理内存(B)
				long freePhysicalMemorySizeByte = osmxb.getFreePhysicalMemorySize();
				long freePhysicalMemorySizeByte2 = systemInfo.getHardware().getMemory().getAvailable();
				// 剩余的物理内存
				String freePhysicalMemorySize = new DecimalFormat("#.##")
						.format(freePhysicalMemorySizeByte / 1024.0 / 1024 / 1024) + "G";
				String freePhysicalMemorySize2 = new DecimalFormat("#.##")
						.format(freePhysicalMemorySizeByte2 * 1.0 / 1024 / 1024 / 1024) + "G";
				// 已使用的物理内存
				String usedMemory = new DecimalFormat("#.##")
						.format((totalMemorySizeByte - freePhysicalMemorySizeByte) / 1024.0 / 1024 / 1024) + "G";
				String usedMemory2 = new DecimalFormat("#.##")
						.format((totalMemorySizeByte2 - freePhysicalMemorySizeByte2) * 1.0 / 1024 / 1024 / 1024) + "G";
				String memoryUsedRate = new DecimalFormat("#.##%")
						.format(1 - (freePhysicalMemorySizeByte * 1.0 / totalMemorySizeByte));
				System.out.println("总的物理内存: " + totalMemorySize);
				System.out.println("总的物理内存: " + totalMemorySize2);
				System.out.println("剩余的物理内存: " + freePhysicalMemorySize);
				System.out.println("剩余的物理内存: " + freePhysicalMemorySize2);
				System.out.println("已使用的物理内存: " + usedMemory);
				System.out.println("已使用的物理内存: " + usedMemory2);
				System.out.println("物理内存使用率: " + memoryUsedRate);

				/* JVM信息 */
				System.out.println("\n============================== JVM信息 =============================");
				System.out.println("JAVA_HOME: " + System.getProperty("java.home"));
				System.out.println("JAVA_VERSION: " + System.getProperty("java.version"));
				System.out.println("USER_HOME: " + System.getProperty("user.home"));
				System.out.println("USER_NAME: " + System.getProperty("user.name"));

				/* CPU信息 */
				System.out.println("\n============================== CPU信息 =============================");
				printlnCpuInfo(systemInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 2, 5, TimeUnit.SECONDS);
	}

	/**
	 * 打印CPU信息
	 * 
	 * @param systemInfo s
	 */
	private static void printlnCpuInfo(SystemInfo systemInfo) throws InterruptedException {
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

		System.out.println("cpu核数: " + Runtime.getRuntime().availableProcessors());
		System.out.println("cpu核数: " + processor.getLogicalProcessorCount());
		System.out.println("cpu系统使用率: " + new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu));
		System.out.println("cpu用户使用率: " + new DecimalFormat("#.##%").format(user * 1.0 / totalCpu));
		System.out.println("cpu当前等待率: " + new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu));
		System.out.println("cpu当前空闲率: " + new DecimalFormat("#.##%").format(idle * 1.0 / totalCpu));
		System.out.format("CPU load: %.1f%% (counting ticks)%n", processor.getSystemCpuLoadBetweenTicks() * 100);
		System.out.format("CPU load: %.1f%% (OS MXBean)%n", processor.getSystemCpuLoad() * 100);
	}

}
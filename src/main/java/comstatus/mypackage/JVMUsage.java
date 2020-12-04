package comstatus.mypackage;

public class JVMUsage {
	private long initTotalMemorySize;// 初始的总内存(B)
	private long maxMemorySize;// 最大可用内存(B)
	private long usedMemorySize;// 已使用的内存(B)
	
	public JVMUsage(long initTotalMemorySize, long maxMemorySize, long usedMemorySize) {
		this.initTotalMemorySize = initTotalMemorySize;
		this.maxMemorySize = maxMemorySize;
		this.usedMemorySize = usedMemorySize;
	}

	public long getInitTotalMemorySize() {
		return initTotalMemorySize;
	}

	public void setInitTotalMemorySize(long initTotalMemorySize) {
		this.initTotalMemorySize = initTotalMemorySize;
	}

	public long getMaxMemorySize() {
		return maxMemorySize;
	}

	public void setMaxMemorySize(long maxMemorySize) {
		this.maxMemorySize = maxMemorySize;
	}

	public long getUsedMemorySize() {
		return usedMemorySize;
	}

	public void setUsedMemorySize(long usedMemorySize) {
		this.usedMemorySize = usedMemorySize;
	}
}

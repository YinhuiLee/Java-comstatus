package comstatus.mypackage;

public class MemUsage {
	private double totalMemorySize;//总的物理内存(G)
	private double usedMemory;//已使用物理内存(G)
	
	public MemUsage(double totalMemorySize, double usedMemory) {
		this.usedMemory = usedMemory;
		this.totalMemorySize = totalMemorySize;
	}

	public double getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(double usedMemory) {
		this.usedMemory = usedMemory;
	}

	public double getTotalMemorySize() {
		return totalMemorySize;
	}

	public void setTotalMemorySize(double totalMemorySize) {
		this.totalMemorySize = totalMemorySize;
	}
}

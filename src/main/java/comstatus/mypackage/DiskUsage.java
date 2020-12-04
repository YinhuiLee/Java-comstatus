package comstatus.mypackage;

/**
 * 单个磁盘使用情况
 * 
 */
public class DiskUsage {
	private String disk;
	private double total;
	private double free;
	
	public DiskUsage(String disk, double total, double free) {
		this.disk = disk;
		this.total = total;
		this.free = free;
	}

	public String getDisk() {
		return disk;
	}

	public void setDisk(String disk) {
		this.disk = disk;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getFree() {
		return free;
	}

	public void setFree(double free) {
		this.free = free;
	}
	
}

package comstatus.mypackage;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 系统资源使用类（描述系统所有资源使用情况）
 *
 */
public class SystemResource {
	private List<DiskUsage> disks= new ArrayList<DiskUsage>();
	private JVMUsage jvmUsage;
	private MemUsage memUsage;
	private CPUUsage cpuUsage;
	
	public MemUsage getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(MemUsage memUsage) {
		this.memUsage = memUsage;
	}

	public CPUUsage getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(CPUUsage cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public SystemResource() {
		
	}

	public JVMUsage getJvmUsage() {
		return jvmUsage;
	}

	public void setJvmUsage(JVMUsage jvmUsage) {
		this.jvmUsage = jvmUsage;
	}

	public List<DiskUsage> getDisks() {
		return disks;
	}

	public void setDisks(List<DiskUsage> disks) {
		this.disks = disks;
	}
	
	public DiskUsage findDiskByPath(String path) {
		for (DiskUsage d : disks) {
			if (path.equals(d.getDisk())) {
				return d;
			}
		}
		return null;
	}
}

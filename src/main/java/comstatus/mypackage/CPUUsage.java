package comstatus.mypackage;

public class CPUUsage {
	private int coreNumber;//核数
	private double cSysRate;//系统使用率
	private double userRate;//用户使用率
	
	public CPUUsage(int coreNumber, double cSysRate, double userRate) {
		this.coreNumber = coreNumber;
		this.cSysRate = cSysRate;
		this.userRate = userRate;
	}

	public int getCoreNumber() {
		return coreNumber;
	}

	public void setCoreNumber(int coreNumber) {
		this.coreNumber = coreNumber;
	}

	public double getcSysRate() {
		return cSysRate;
	}

	public void setcSysRate(double cSysRate) {
		this.cSysRate = cSysRate;
	}

	public double getUserRate() {
		return userRate;
	}

	public void setUserRate(double userRate) {
		this.userRate = userRate;
	}
}

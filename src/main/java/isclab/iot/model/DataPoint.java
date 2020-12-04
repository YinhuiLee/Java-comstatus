package isclab.iot.model;

import java.util.Date;
import java.util.List;

/**
 * 数据模型，无需修改
 * 
 * @author xiaodong
 *
 */
public class DataPoint {
	private Date timestamp;
	private List<Float> values;

	public DataPoint() {
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<Float> getValues() {
		return values;
	}

	public void setValues(List<Float> values) {
		this.values = values;
	}
}

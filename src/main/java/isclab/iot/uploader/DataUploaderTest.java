package isclab.iot.uploader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import isclab.iot.model.DataPoint;

/**
 * 数据上传测试类
 * 
 * @author xiaodong
 *
 */
public class DataUploaderTest {
	public static void main(String[] args) {

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
		//想传几个指标就设几个
		List<Float> values = new ArrayList<Float>(); 
		values.add(1.0F); // 添加浮点值，例如CPU占用率
		values.add(2.0F); // 添加浮点值，例如内存占用率
		
		point.setValues(values);

		// 调用upload方法完成数据上传，如果数据上传成功则返回所上传数据的JSON字符串，如果不成功返回空
		System.out.println(du.upload(point));
	}
}

package isclab.iot.uploader;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import isclab.iot.model.DataPoint;

/**
 * 数据上传类，无需修改
 * 
 * @author xiaodong
 *
 */
public class DataUploader {
	private final String url = "https://iot.isclab.top/api/v1.0";
	private int deviceId;
	private int sensorId;
	private String apiKey;

	public DataUploader(int deviceId, int sensorId, String apiKey) {
		this.deviceId = deviceId;
		this.sensorId = sensorId;
		this.apiKey = apiKey;
	}

	public String upload(DataPoint point) {
		HttpPost httpPost = new HttpPost(url + "/device/" + deviceId + "/sensor/" + sensorId + "/points");
		httpPost.setHeader("IOT-ApiKey", apiKey);
		CloseableHttpClient client = HttpClients.createDefault();
		

		JSONObject jsonParam = new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		jsonParam.put("timestamp", formatter.format(point.getTimestamp()));
		jsonParam.put("values", point.getValues());

		StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");

		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		String respContent = null;

		try {
			HttpResponse resp = client.execute(httpPost);
			if (resp.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = resp.getEntity();
				respContent = EntityUtils.toString(httpEntity, "UTF-8");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return respContent;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public int getSensorId() {
		return sensorId;
	}

	public String getApiKey() {
		return apiKey;
	}

}

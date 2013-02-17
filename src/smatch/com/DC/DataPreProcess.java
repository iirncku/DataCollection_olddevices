package smatch.com.DC;

import java.util.ArrayList;
import java.util.List;
/** @description 存放濾波器的類別 */
public class DataPreProcess {
	private List<Float> rawData;
	public DataPreProcess(List<Float> data) {
		this.rawData = data;
	}
	/* 計算Peak時所需的濾波處理，採用Moving Average */
	public List<Float> movingAverage(List<Float> data) {
		List<Float> result = new ArrayList<Float>();
		for (int i = 0; i < data.size() - 4; i++) {
			float temp = 0;
			for (int j = i; j < i + 5; j++) {
				 temp += data.get(j);
			}
			result.add(temp / 5);
		}
		return result;
	}
	public List<Float> getMOA() {
		return movingAverage(rawData);
	}
}
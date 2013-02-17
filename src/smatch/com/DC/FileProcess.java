package smatch.com.DC;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 這支程式用來處理日週月資料存放
 * */
public class FileProcess {
	private String firstPath;
	private DateFormat datemormat1;
	private DateFormat datemormat2;
	private DateFormat datemormat3;
	public FileProcess() {
		this.datemormat1 = new SimpleDateFormat("MM-WW-dd");
		this.datemormat2 = new SimpleDateFormat("MM-WW");
		this.datemormat3 = new SimpleDateFormat("MM");
	}
	
	public void setRootPath(String rootpath) {
		this.firstPath = rootpath;
	}
	
	//以日命名
	public File createDayFileByDate(Date date) {
		String fileName = datemormat1.format(date);
		File file = new File(firstPath + "Day" + "//" + fileName + ".txt");
		return file;
	}
	
	//以週命名
	public File createWeekFileByDate(Date date) {
		String fileName = datemormat2.format(date);
		File file = new File(firstPath + "Week" + "//" + fileName + ".txt");
		return file;
	}
	
	//以月命名
	public File createMonthFileByDate(Date date) {
		String fileName = datemormat3.format(date);
		File file = new File(firstPath + "Month" + "//" + fileName + ".txt");
		return file;
	}
	/*
	 * 合併檔案
	 * */
	public void readFile(File file, int[] text) {
		try {
			String x = null;
			String[] y = null;
			int[] result = new int[8];
			BufferedReader bufIn = new BufferedReader(new FileReader(file));
			while((x = bufIn.readLine()) != null) {
				y = x.split("\t");
			}
			for (int i = 0; i < y.length ; i++) {
				result[i] = Integer.valueOf(y[i]) + text[i];
			}
			writeFile(file, result);
			bufIn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void writeFile(File file, int[] text) {
		try {
			BufferedWriter bufOut = new BufferedWriter(new FileWriter(file));
			bufOut.write(String.valueOf(text[0]) + "\t" + 
						 String.valueOf(text[1]) + "\t" + 
						 String.valueOf(text[2]) + "\t" + 
						 String.valueOf(text[3]) + "\t" + 
						 String.valueOf(text[4]) + "\t" + 
						 String.valueOf(text[5]) + "\t" + 
						 String.valueOf(text[6]) + "\t" + 
						 String.valueOf(text[7]));
			bufOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void checkDir(String dirName) {
		File file = new File(firstPath + dirName);
		if (!file.exists()) {
			file.mkdir();
		}
	}
}

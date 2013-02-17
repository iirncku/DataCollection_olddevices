/*=====================================================================================*/
/*Project : 		DataCollection App
/*執行功能：	負責記錄Raw Data與存檔
/*關聯檔案：	Feature.java
/*=====================================================================================*/
package smatch.com.DC;

import static android.hardware.SensorManager.SENSOR_ACCELEROMETER;
import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;
import static android.hardware.SensorManager.SENSOR_DELAY_UI;
import static android.hardware.SensorManager.SENSOR_ORIENTATION;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import smatch.com.DC.DC.MyHandler;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActService extends Service implements SensorListener {
	private String filename = null;
	private SensorManager sensormgr=null;
	/*		記錄ACCELEROMETER 資料		*/
	private List<Float> x_list = new ArrayList<Float>();
	private List<Float> y_list = new ArrayList<Float>();
	private List<Float> z_list = new ArrayList<Float>();
	private List<Float> Fx_list = new ArrayList<Float>();
	private List<Float> Fy_list = new ArrayList<Float>();
	private List<Float> Fz_list = new ArrayList<Float>();
	/*		記錄ORIENTATION 資料		*/
	private List<Float> v0_list = new ArrayList<Float>();
	private List<Float> v1_list = new ArrayList<Float>();
	private List<Float> v2_list = new ArrayList<Float>();
	
	float x,y,z,v0,v1,v2;
	Timer timer;
	MyHandler hm;
	SimpleDateFormat DTFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	public ActService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		filename = intent.getExtras().getString("filename");		
		hm=new MyHandler(Looper.getMainLooper());
		start();
		
	}
	
	public void start(){
		timer = new Timer(true);
		/*		註冊Sensor服務		*/
		sensormgr = (SensorManager)getSystemService(SENSOR_SERVICE);
    	sensormgr.registerListener(ActService.this,SENSOR_ACCELEROMETER|SENSOR_ORIENTATION,SENSOR_DELAY_FASTEST);
    	/*		設置計時器，以20Hz的頻率收集感測器資料		*/
		TimerTask GetSensorData = new TimerTask() {
			public void run() {
				Message msg =hm.obtainMessage(1);
				hm.sendMessage(msg);
			}
		};
		timer.scheduleAtFixedRate(GetSensorData, 50, 50);
	}
	
	@Override
	public void onDestroy() {
		/*		結束Service前將Raw存檔		*/
		Message msg =hm.obtainMessage(2);
		hm.sendMessage(msg);
		super.onDestroy();
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	/*		隨時記錄Sensor各個值的改變		*/
	@Override
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		synchronized (this)
		{
			if (sensor == SENSOR_ACCELEROMETER)
			{
				x=values[0];
				y=values[1];
				z=values[2];
			}
			else
			{
				if(sensor == SENSOR_ORIENTATION)
				{	
					v0=values[0];
					v1=values[1];
					v2=values[2];
				}
			}
		}
	}

	class MyHandler extends Handler
	{
		 public MyHandler(Looper looper) {
             super(looper);
         }
         public void handleMessage(Message msg) {
        	 if(msg.what==1)
        	 {
        		 x_list.add(x);
    			 y_list.add(y);
    			 z_list.add(z);
    			 v1_list.add(v1);
    			 v2_list.add(v2);
    			 v0_list.add(v0);
    			 Fx_list.add(x);
    			 Fy_list.add(y);
    			 Fz_list.add(z);
        		 if(Fx_list.size()==140){
        			 WriteFeatureFile(Fx_list,Fy_list,Fz_list);
        			 Fx_list.clear();
        			 Fy_list.clear();
        			 Fz_list.clear();
        		 }
        		 else{
        		 }
        	 }
        	 else if(msg.what==2){
        		 timer.cancel();
        		 timer=null;
        		 Date date1 = new Date();
     			//收資料的日期與時間作為檔名
     			String startTime = DTFormat.format(date1);
    			 write(x_list,y_list,z_list,0,startTime);
				 write(v0_list,v1_list,v2_list,1,startTime);
				 x_list.clear();
    			 y_list.clear();
    			 z_list.clear();
    			 v1_list.clear();
    			 v2_list.clear();
    			 v0_list.clear();
    			 Fx_list.clear();
    			 Fy_list.clear();
    			 Fz_list.clear();
    			 sensormgr.unregisterListener(ActService.this);
    			 sensormgr=null;
        	 }
         }
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	/*		把Raw存檔記錄		*/
	public void write(List<Float> x, List<Float> y, List<Float> z, int id, String startTime)
    {
		try
		{
			// 當id為0時，寫入加速度資料;當id為1時，寫入方位角資料
			if(id == 0)
			{
				File fileDir = new File("/sdcard/Smatch_DC/");
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}

				String file_path = "/sdcard/Smatch_DC/"+filename+"_"+startTime+"_accelerometer.txt";
				File file = new File(file_path);
				BufferedWriter bufOut = new BufferedWriter(new FileWriter(file));
				bufOut.write(x.size()+"\n");
				for(int i = 0; i < x.size(); i++)          	
					bufOut.append(x.get(i)+"\t"+y.get(i)+"\t"+z.get(i)+"\n");							
				bufOut.close();
				Toast.makeText(ActService.this, "存檔完畢", Toast.LENGTH_LONG).show();
			}
			else if(id == 1)
			{
				String file_path = "/sdcard/Smatch_DC/"+filename+"_"+startTime+"_orientation.txt";
				File file = new File(file_path);
				BufferedWriter bufOut = new BufferedWriter(new FileWriter(file));
				bufOut.write(x.size()+"\n");
				for(int i = 0; i < x.size(); i++)          	
					bufOut.append(x.get(i)+"\t"+y.get(i)+"\t"+z.get(i)+"\n");							
				bufOut.close();
				Toast.makeText(ActService.this, "存檔完畢", Toast.LENGTH_LONG).show();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/*		每7秒計算一次特徵，並記錄在檔案中		*/
	public void WriteFeatureFile(List<Float> x_list,
			List<Float> y_list, List<Float> z_list) {
		/*	建立特徵的類別，利用特徵類別的方法計算完特徵，以陣列方式傳回	*/
		Features features = new Features(x_list, y_list, z_list);
		double[] featureSet = features.getFeatureSet();
		
		Date thisdate = new Date();
		String featureDT = DTFormat.format(thisdate);
		try {
			/*		將特徵存檔		*/
			File fileDir2 = new File("/sdcard/Smatch_DC/");
			if (!fileDir2.exists()) {
				fileDir2.mkdirs();
			}
			String file_path2 = "/sdcard/Smatch_DC/" + filename + "-"
					 + ".txt";
			File file2 = new File(file_path2);
			BufferedWriter bufOut = new BufferedWriter(new FileWriter(file2,
					true));
			bufOut.append(featureDT + "\t" + featureSet[0] + "\t" + featureSet[1]
					+ "\t" + featureSet[2] + "\t" + featureSet[3] + "\t"
					+ featureSet[4] + "\t" + featureSet[5] + "\t"
					+ featureSet[6] + "\t" + featureSet[7] + "\t"
					+ featureSet[8] + "\t" + featureSet[9] + "\t"
					+ featureSet[10] + "\t" + featureSet[11] + "\t"
					+ featureSet[12] + "\t" + featureSet[13] + "\t"
					+ featureSet[14] + "\t" + featureSet[15] + "\t"
					+ featureSet[16] + "\t" + featureSet[17] + "\t"
					+ featureSet[18] + "\t" + featureSet[19] + "\t"
					+ featureSet[20] + "\t" + featureSet[21] + "\t"
					+ featureSet[22] + "\t"
					+ featureSet[23] + "\t" + featureSet[24] + "\t" + featureSet[25]
					+ "\n");
			bufOut.close();
		} catch (Exception e) {
		}
	}

}

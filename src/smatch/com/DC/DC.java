package smatch.com.DC;

import static android.hardware.SensorManager.SENSOR_ACCELEROMETER;
import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;
import static android.hardware.SensorManager.SENSOR_ORIENTATION;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import android.text.Editable;
import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class DC extends Activity implements SensorListener {
	TextView tv;
	final IWindowManager windowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
	private EditText edit1;
	private Editable name; //存檔檔名
	private Button button1;
	private Button button2;
	private SensorManager sensormgr=null;
	private List<Float> x_list = new ArrayList<Float>();
	private List<Float> y_list = new ArrayList<Float>();
	private List<Float> z_list = new ArrayList<Float>();
	private List<Float> v0_list = new ArrayList<Float>();
	private List<Float> v1_list = new ArrayList<Float>();
	private List<Float> v2_list = new ArrayList<Float>();
	private List<Float> Fx_list = new ArrayList<Float>();
	private List<Float> Fy_list = new ArrayList<Float>();
	private List<Float> Fz_list = new ArrayList<Float>();
	float x,y,z,v0,v1,v2;
	Timer timer;
	MyHandler hm;
	private int time=600;
	private boolean isRunning=false;
	SimpleDateFormat DTFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private NotificationManager notificationManager=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		edit1 = (EditText)findViewById(R.id.EditText01); 
		name = edit1.getText();
		tv =(TextView) findViewById(R.id.tv);
		button1 = (Button)findViewById(R.id.Button01);	
        button2 = (Button)findViewById(R.id.Button02);
        hm=new MyHandler(Looper.getMainLooper());
		setListeners();
		
    }
    
    private void simulateKeystroke(int KeyCode) {
        doInjectKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyCode));
        doInjectKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyCode));
    }
    private void doInjectKeyEvent(KeyEvent kEvent) {
        
        try {
             windowManager.injectKeyEvent(kEvent, true);
                                 
        } catch (Exception e) {
             e.printStackTrace();
        }
        
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	/*////////////////		 動態變更字體大小，透過音量鍵		/////////////////////*/
    	if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
    		if(isRunning){
    			new Thread(new Runnable() {
                    public void run() {
                         /* Simulate a KeyStroke to the menu-button. */
                        simulateKeystroke(KeyEvent.KEYCODE_MENU);
                    }
               }).start(); /* And start the Thread. */
    			return true;
    		}
    		else{
    			// TODO Auto-generated method stub
//    			notificationManager.cancel(0);
    			return super.onKeyDown(keyCode, event);
        	}
    	}else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
    		if(isRunning){
//    			Toast.makeText(DC.this, "UP", Toast.LENGTH_SHORT).show();
    			return true;
    		}
    		else{
//    			noti();
        		return super.onKeyDown(keyCode, event);
    		}
    	}else if(keyCode==KeyEvent.KEYCODE_BACK){
    		if(isRunning)
    			return true;
    		else{
        		return super.onKeyDown(keyCode, event);
        	}
    	}else if(keyCode==KeyEvent.KEYCODE_HOME){
    		if(isRunning)
    			return true;
    		else{
        		return super.onKeyDown(keyCode, event);
        	}
    	}else if(keyCode==KeyEvent.KEYCODE_SEARCH){
    		if(isRunning)
    			return true;
    		else{
        		return super.onKeyDown(keyCode, event);
        	}
    	}else if(keyCode==KeyEvent.KEYCODE_MENU){
    		if(isRunning)
    			return super.onKeyDown(keyCode, event);
    		else{
        		return super.onKeyDown(keyCode, event);
        	}
    	}
    	/*////////////////		 			  			/////////////////////*/
    	else{
    		return super.onKeyDown(keyCode, event);
    	}
    }
    
    public void noti() {
		// 設定當按下這個通知之後要執行的activity
//		notificationManager.cancel(0);
		// Log.d("Record","!!!!!!!"+Qt.size());
    	Notification notification = new Notification();
		// 設定出現在狀態列的圖示
		notification.icon = R.drawable.icon;
		// 顯示在狀態列的文字
		notification.tickerText = "notification on status bar.";
		// 會有通知預設的鈴聲、振動、light
		notification.defaults = Notification.DEFAULT_LIGHTS;
		// 設定通知的標題、內容
		notification.flags=Notification.FLAG_NO_CLEAR;
		notification.setLatestEventInfo(DC.this, "Data Collection", "Collecting",
				null);
		// 送出Notification
		notificationManager.notify(0, notification);
	}
    
    private void setListeners() {
		button1.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){
			if(isRunning){
				return;
			}
			Intent myServIntent = new Intent(DC.this, ActService.class);
			String filename= edit1.getText().toString();
			myServIntent.putExtra("filename", filename);
			startService(myServIntent);
			isRunning=true;
			edit1.setEnabled(false);
//			noti();
			
		}});
		button2.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){
			if(!isRunning){
				return;
			}
			
		}});
	}
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(0, 0, 0, "開始紀錄");
        menu.add(0, 1, 0, "暫停紀錄");
        //menu.getItem(1).setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        //依據itemId來判斷使用者點選哪一個item
        switch(item.getItemId()) {
            case 0:
            	if(isRunning){
            		break;
    			}
    			Intent myServIntent = new Intent(DC.this, ActService.class);
    			String filename= edit1.getText().toString();
    			myServIntent.putExtra("filename", filename);
    			startService(myServIntent);
    			isRunning=true;
    			edit1.setEnabled(false);
//    			noti();
                break;
            case 1:
            	if(!isRunning)
            		break;
    			new AlertDialog.Builder(DC.this).setTitle("Alert")
    				.setMessage("是否暫停？").setPositiveButton("是",
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog, int which) {
    									Intent myServIntent = new Intent(DC.this, ActService.class);
    									stopService(myServIntent);
    									isRunning=false;
    									notificationManager.cancel(0);
    									edit1.setEnabled(true);
    							}
    						}).setNegativeButton("否",
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog,	int which) {
    								// Do nothing, Back to system
    							}
    						}).show();
    			break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
    
//    private void setListeners() {
//		button1.setOnClickListener(start);
//		button2.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){
//			if(!isRunning){
//				return;
//			}
//			Message msg =hm.obtainMessage(2);
//			hm.sendMessage(msg);
//		}});
//	}
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:

				break;
	
			case MotionEvent.ACTION_UP:
				break;
		}

		return true;
    }
    private Button.OnClickListener start = new Button.OnClickListener() {

		public void onClick(View v) {	
			if(isRunning){
				return;
			}
			timer = new Timer(true);
			sensormgr = (SensorManager)getSystemService(SENSOR_SERVICE);
	    	sensormgr.registerListener(DC.this,SENSOR_ACCELEROMETER|SENSOR_ORIENTATION,SENSOR_DELAY_FASTEST);
	    	TimerTask GetSensorData = new TimerTask() {
				public void run() {
					Message msg =hm.obtainMessage(1);
					hm.sendMessage(msg);
					Log.d("Time","Send");
				}
			};
			timer.scheduleAtFixedRate(GetSensorData, 50, 50);
			isRunning=true;
		}
	};
	@Override
	public void onAccuracyChanged(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

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
    			 sensormgr.unregisterListener(DC.this);
    			 sensormgr=null;
    			 isRunning=false;
        	 }
         }
	}
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
				
				name = edit1.getText();
				String file_path = "/sdcard/Smatch_DC/"+name+"_"+startTime+"_accelerometer.txt";
				File file = new File(file_path);
				BufferedWriter bufOut = new BufferedWriter(new FileWriter(file));
				bufOut.write(x.size()+"\n");
				for(int i = 0; i < x.size(); i++)          	
					bufOut.append(x.get(i)+"\t"+y.get(i)+"\t"+z.get(i)+"\n");							
				bufOut.close();
				Toast.makeText(DC.this, "存檔完畢", Toast.LENGTH_LONG).show();
			}
			else if(id == 1)
			{
				name = edit1.getText();
				String file_path = "/sdcard/Smatch_DC/"+name+"_"+startTime+"_orientation.txt";
				File file = new File(file_path);
				BufferedWriter bufOut = new BufferedWriter(new FileWriter(file));
				bufOut.write(x.size()+"\n");
				for(int i = 0; i < x.size(); i++)          	
					bufOut.append(x.get(i)+"\t"+y.get(i)+"\t"+z.get(i)+"\n");							
				bufOut.close();
				Toast.makeText(DC.this, "存檔完畢", Toast.LENGTH_LONG).show();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void WriteFeatureFile(List<Float> x_list,
			List<Float> y_list, List<Float> z_list) {
		Features features = new Features(x_list, y_list, z_list);
		double[] featureSet = features.getFeatureSet();

		Date thisdate = new Date();
		String featureDT = DTFormat.format(thisdate);

		try {
			File fileDir2 = new File("/sdcard/Smatch_DC/");
			if (!fileDir2.exists()) {
				fileDir2.mkdirs();
			}
			String file_path2 = "/sdcard/Smatch_DC/" + name + "-"
					 + ".txt";
			File file2 = new File(file_path2);
			BufferedWriter bufOut = new BufferedWriter(new FileWriter(file2,
					true));
			// for (int i = 1; i < time.size(); i++) {
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
			// }
			bufOut.close();
		} catch (Exception e) {
		}
//		Toast.makeText(DC.this, "Features saved", Toast.LENGTH_SHORT).show();
	}
}
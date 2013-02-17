/*=====================================================================================*/
/*Project : 		DataCollection App
/*執行功能：	程式主畫面，控制開始記錄與停止
/*關聯檔案：	Main.xml, ActService.java
/*=====================================================================================*/
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

public class DC extends Activity {
	private EditText edit1;	
	private Editable name; 		//存檔檔名
	/*		開始與暫停記錄按鈕		*/
	private Button button1;
	private Button button2;
	private boolean isRunning=false;	//FLAG 判斷是否正在收集資料

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		/*			維持程式全螢幕與保持不待機			*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		///////////////////////////////////////////////////////////////
		setContentView(R.layout.main);
        edit1 = (EditText)findViewById(R.id.EditText01); 
		name = edit1.getText();
		button1 = (Button)findViewById(R.id.Button01);	
        button2 = (Button)findViewById(R.id.Button02);
		setListeners();	//設置按鈕事件
		
    }
    
	/*			複寫觸碰事件，取消部分按鍵在收資料時的功能			*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*		取代音量鍵功能		*/
    	if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
    		if(isRunning){
    			return true;
    		}
    		else{
    			return super.onKeyDown(keyCode, event);
        	}
    	}else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
    		if(isRunning){
    			return true;
    		}
    		else{
        		return super.onKeyDown(keyCode, event);
    		}
    	}/*		取代返回鍵功能		*/
		else if(keyCode==KeyEvent.KEYCODE_BACK){
    		if(isRunning)
    			return true;
    		else{
        		return super.onKeyDown(keyCode, event);
        	}
    	}/*		取代搜尋鍵功能		*/
		else if(keyCode==KeyEvent.KEYCODE_SEARCH){
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
    	else{
    		return super.onKeyDown(keyCode, event);
    	}
    }
    
	
	/*		設置開始記錄與暫停記錄按鈕之事件		*/
    private void setListeners() {
		/*		開始記錄之事件		*/
		button1.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){
			if(isRunning){
				return;		// 收集資料中不作用
			}
			/*		開啟資料收集之Service，並傳入存檔檔名		*/
			Intent myServIntent = new Intent(DC.this, ActService.class);
			String filename= edit1.getText().toString();
			myServIntent.putExtra("filename", filename);
			startService(myServIntent);
			/*		變更程式狀態		*/
			isRunning=true;
			edit1.setEnabled(false);			
		}});
		button2.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){
			if(!isRunning){
				return;
			}
			
		}});
	}
	/*		建立Menu選項		*/
    public boolean onCreateOptionsMenu(Menu menu) {
        //參數1:群組id, 參數2:itemId, 參數3:item順序, 參數4:item名稱
        menu.add(0, 0, 0, "開始紀錄");
        menu.add(0, 1, 0, "暫停紀錄");
        return super.onCreateOptionsMenu(menu);
    }
    /*		設置Menu事件		*/
    public boolean onOptionsItemSelected(MenuItem item) {
        //依據itemId來判斷使用者點選哪一個item
        switch(item.getItemId()) {
			/*		與開始按鈕相同		*/
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
                break;
			/*		停止記錄		*/
            case 1:
            	if(!isRunning)
            		break;
				/*		跳出對話視窗詢問是否停止		*/
    			new AlertDialog.Builder(DC.this).setTitle("Alert")
    				.setMessage("是否暫停？").setPositiveButton("是",
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog, int which) {
										/*		關閉Service		*/
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
}
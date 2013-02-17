/*=====================================================================================*/
/*Project : 		DataCollection App
/*����\��G	�{���D�e���A����}�l�O���P����
/*���p�ɮסG	Main.xml, ActService.java
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
	private Editable name; 		//�s���ɦW
	/*		�}�l�P�Ȱ��O�����s		*/
	private Button button1;
	private Button button2;
	private boolean isRunning=false;	//FLAG �P�_�O�_���b�������

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		/*			�����{�����ù��P�O�����ݾ�			*/
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		///////////////////////////////////////////////////////////////
		setContentView(R.layout.main);
        edit1 = (EditText)findViewById(R.id.EditText01); 
		name = edit1.getText();
		button1 = (Button)findViewById(R.id.Button01);	
        button2 = (Button)findViewById(R.id.Button02);
		setListeners();	//�]�m���s�ƥ�
		
    }
    
	/*			�ƼgĲ�I�ƥ�A������������b����Ʈɪ��\��			*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*		���N���q��\��		*/
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
    	}/*		���N��^��\��		*/
		else if(keyCode==KeyEvent.KEYCODE_BACK){
    		if(isRunning)
    			return true;
    		else{
        		return super.onKeyDown(keyCode, event);
        	}
    	}/*		���N�j�M��\��		*/
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
    
	
	/*		�]�m�}�l�O���P�Ȱ��O�����s���ƥ�		*/
    private void setListeners() {
		/*		�}�l�O�����ƥ�		*/
		button1.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){
			if(isRunning){
				return;		// ������Ƥ����@��
			}
			/*		�}�Ҹ�Ʀ�����Service�A�öǤJ�s���ɦW		*/
			Intent myServIntent = new Intent(DC.this, ActService.class);
			String filename= edit1.getText().toString();
			myServIntent.putExtra("filename", filename);
			startService(myServIntent);
			/*		�ܧ�{�����A		*/
			isRunning=true;
			edit1.setEnabled(false);			
		}});
		button2.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){
			if(!isRunning){
				return;
			}
			
		}});
	}
	/*		�إ�Menu�ﶵ		*/
    public boolean onCreateOptionsMenu(Menu menu) {
        //�Ѽ�1:�s��id, �Ѽ�2:itemId, �Ѽ�3:item����, �Ѽ�4:item�W��
        menu.add(0, 0, 0, "�}�l����");
        menu.add(0, 1, 0, "�Ȱ�����");
        return super.onCreateOptionsMenu(menu);
    }
    /*		�]�mMenu�ƥ�		*/
    public boolean onOptionsItemSelected(MenuItem item) {
        //�̾�itemId�ӧP�_�ϥΪ��I����@��item
        switch(item.getItemId()) {
			/*		�P�}�l���s�ۦP		*/
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
			/*		����O��		*/
            case 1:
            	if(!isRunning)
            		break;
				/*		���X��ܵ����߰ݬO�_����		*/
    			new AlertDialog.Builder(DC.this).setTitle("Alert")
    				.setMessage("�O�_�Ȱ��H").setPositiveButton("�O",
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog, int which) {
										/*		����Service		*/
    									Intent myServIntent = new Intent(DC.this, ActService.class);
    									stopService(myServIntent);
    									isRunning=false;
    									notificationManager.cancel(0);
    									edit1.setEnabled(true);
    							}
    						}).setNegativeButton("�_",
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
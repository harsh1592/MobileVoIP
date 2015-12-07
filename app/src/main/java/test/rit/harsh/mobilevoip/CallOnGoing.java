package test.rit.harsh.mobilevoip;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CallOnGoing extends Activity {
    private Button end;
    public StartStreaming startStreaming;
    public StartReceiving startReceiving;
    InetAddress callerIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("In CallOnGoing", "started on call");

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String killSwitch = (String) b.get("kill");
        if (killSwitch.equals("1")){
            Log.d("inside killswitch", "killed");
            finish();
            return;
        }
        Log.d("inside killswitch", "after fisish");
        callerIP = (InetAddress) b.get("IP_address");
        Info message = (Info) b.get("message");

        ClientHelper ch = new ClientHelper(callerIP,message);
        ch.start();

        stopService(new Intent(CallOnGoing.this,StartStreaming.class));
        stopService(new Intent(CallOnGoing.this, StartReceiving.class));

       // if(!isMyServiceRunning(StartReceiving.class)) {
            startService(new Intent(getBaseContext(), StartReceiving.class));
       // }
       // if(!isMyServiceRunning(StartStreaming.class)) {
            Intent streamIntent = new Intent(getBaseContext(), StartStreaming.class);
            streamIntent.putExtra("IP_address", callerIP);
            startService(streamIntent);
       // }

        setContentView(R.layout.activity_call_on_going);
        end = (Button) findViewById (R.id.end_btn);
        end.setOnClickListener(stopListener);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private final View.OnClickListener stopListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            ClientHelper ch = new ClientHelper(callerIP, new Info("END"));
            ch.start();
            stopService(new Intent(CallOnGoing.this,StartStreaming.class));
            stopService(new Intent(CallOnGoing.this,StartReceiving.class));
            Log.d("VS", "Recorder released");
            Intent intent = new Intent(CallOnGoing.this, MainActivity.class);
            startActivity(intent);
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

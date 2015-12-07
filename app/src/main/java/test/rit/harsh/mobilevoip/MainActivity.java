package test.rit.harsh.mobilevoip;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    private EditText ipAddress;
    private TextView self;
    private Button call;
    public InetAddress ipaddress = null;
    int port = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipAddress = (EditText) findViewById (R.id.ip_editText);

        call = (Button) findViewById (R.id.call_btn);
        self = (TextView) findViewById(R.id.self_txt);

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String selfip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        self.setText("My IP: " + selfip);
        call.setOnClickListener(startListener);

       // if(!isMyServiceRunning(ServerHelper.class)){
            startService(new Intent(getBaseContext(), ServerHelper.class));
       // }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    private final View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            //Creating a message for connection initiation
            Info hello=new Info("Hello");
            try {
                ipaddress = InetAddress.getByName(ipAddress.getText().toString());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            //sending the hello to peer
            Intent callIntent = new Intent(getBaseContext(),CallOnGoing.class);
            callIntent.putExtra("IP_address",ipaddress);
            callIntent.putExtra("message", hello);
            callIntent.putExtra("kill", "0");
            startActivity(callIntent);
        }
    };
}

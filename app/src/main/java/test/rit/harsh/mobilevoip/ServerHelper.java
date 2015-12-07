package test.rit.harsh.mobilevoip;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerHelper extends Service{
	DatagramSocket datasocket = null;
	Info rneighbours = null;
	public InetAddress callerIP;

	public int onStartCommand(Intent intent, int flags, int startId){
		return START_STICKY;
	}

    public void onCreate(){
        new Thread(new serverStarter(this)).start();
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

	private void displayNotification(InetAddress s) {
		PendingIntent dismissIntent = Notification.getDismissIntent(1, this, s);
		PendingIntent receiveCallIntent = Notification.getReceiveCallIntern(1, this, s);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
				        .setSmallIcon(R.mipmap.ic_launcher)
						.setContentTitle("Mobile VOIP")
						.setContentText(s.toString()+" calling")
						.setAutoCancel(true)
						.addAction(R.drawable.ic_call_end_black, "Dismiss", dismissIntent)
						.addAction(R.drawable.ic_call_black, "Answer", receiveCallIntent);

        NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, mBuilder.build());
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	public void onUpdate(InetAddress ip){
		String s = ip.toString();
		Intent i = new Intent("edu.rit.csci759.mobile.RECEIVE_JSON");
		i.putExtra("value", s);
		//sendBroadcast(i);
		displayNotification(ip);
		Log.d("Broadcast", s);
	}

	public class serverStarter implements Runnable{
		ServerHelper obj;
		public serverStarter(ServerHelper obj){
			this.obj = obj;
		}
		@Override
		public void run() {
			byte[] buffer = new byte[8000];
			try {
				datasocket = new DatagramSocket(9000);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			DatagramPacket packet_data = new DatagramPacket(buffer, buffer.length);
            int count = 0;
			while (true) {
				// the server is always running to accept packets
				// this is used to check time of the neighbor packets
				try {
					// gets packts from neighbours
					Log.d("ServerHelper", "listening");
					datasocket.receive(packet_data);
                    count++;
					Log.d("ServerHelper", "received");
					callerIP = packet_data.getAddress();
					buffer = packet_data.getData();
					ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer));
					Info rneighbours = (Info) in.readObject();

					if(rneighbours.message.equals("Hello")){
						Log.d("ServerHelper", rneighbours.message);
						obj.onUpdate(callerIP); // LOOK INTO
					}else if(rneighbours.message.equals("END")){
                        stopService(new Intent(ServerHelper.this, StartStreaming.class));
                        stopService(new Intent(ServerHelper.this, StartReceiving.class));

                        Intent intent = new Intent(ServerHelper.this, CallOnGoing.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("kill", "1");
                        getApplication().startActivity(intent);
                        //startActivity(new Intent(ServerHelper.this,MainActivity.class));
						Log.d("Received", rneighbours.message);
					}else{
                        Log.d("Received", rneighbours.message);
                    }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

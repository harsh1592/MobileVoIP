package test.rit.harsh.mobilevoip;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by patil on 12/4/2015.
 */
public class StartReceiving extends Service {
    //Audio Configuration.
    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int port = 13000;
    private AudioTrack speaker;
    public boolean status;

    public int onStartCommand(Intent intent, int flags, int startId){
        status = true;
        new Thread(new speakerThread()).start();
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if(speaker!=null) {
            //speaker.release();
            //status = false;
        }
        super.onDestroy();
    }
    private class speakerThread implements Runnable {
        @Override
        public void run() {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                Log.d("VR", "Socket Created");

                int minimumBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                byte[] outputBuffer = new byte[7500]; //7104 ,7168
                speaker = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sampleRate,channelConfig,audioFormat,minimumBufferSize,AudioTrack.MODE_STREAM);
                speaker.play();
                Log.d("status",String.valueOf(status));
                while(status == true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(outputBuffer,outputBuffer.length);
                        socket.receive(packet);
                        Log.d("VR", "Packet Received in startReceiving");
                        //reading content from packet
                        outputBuffer=packet.getData();
                        Log.d("VR", "Packet data read into buffer of size:" + packet.getData().length);
                        //sending data to the Audiotrack
                        speaker.write(outputBuffer, 0, minimumBufferSize);
                        Log.d("VR", "Writing buffer content to speaker");
                        speaker.flush();
                        System.gc();
                    } catch(IOException e) {
                        Log.e("VR","IOException");
                    }
                }
            } catch (SocketException e) {
                Log.e("VR", "SocketException");
            }
        }
    }
}

package test.rit.harsh.mobilevoip;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by patil on 12/4/2015.
 */
public class StartStreaming extends Service {
    InetAddress ipadAddress;
   /* public StartStreaming(InetAddress ipaddress){
        this.ipadAddress = ipaddress;
    }*/
    //Audio Configuration.
    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int port = 13000;
    private AudioRecord recorder;
    public boolean status;


    public int onStartCommand(Intent intent, int flags, int startId){
        status = true;
        this.ipadAddress = (InetAddress) intent.getExtras().get("IP_address");
        new Thread(new micThread()).start();
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(recorder!=null) {
            recorder.release();
            status = false;
        }
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class micThread implements Runnable {
        @Override
        public void run() {
            try {
                int minimumBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                DatagramSocket socket = new DatagramSocket();
                byte[] inBuffer = new byte[minimumBufferSize];

                Log.d("VS", "Buffer of size " + minimumBufferSize + " created");
                DatagramPacket sendingPacket;

                Log.d("VS", "Address is retrieved");
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minimumBufferSize);
                //recorder.release();
                Log.d("VS", "Recorder has been initialized");
                recorder.startRecording();
                while (status == true) {
                    //reading data from MIC into buffer
                    System.out.println("sending:"+ipadAddress.toString());
                    minimumBufferSize = recorder.read(inBuffer, 0, inBuffer.length);
                    System.out.println("minbuff:"+minimumBufferSize);
                    //putting buffer in the packet
                    sendingPacket = new DatagramPacket(inBuffer, inBuffer.length, ipadAddress, port);
                    socket.send(sendingPacket);
                    System.gc();
                }
            } catch (UnknownHostException e) {
                Log.e("VS", "UnknownHostException");
            } catch (IOException e) {
                Log.e("VS", "IOException");
            }
        }
    }
}

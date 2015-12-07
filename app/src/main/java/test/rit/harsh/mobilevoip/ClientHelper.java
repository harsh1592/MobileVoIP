package test.rit.harsh.mobilevoip;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ClientHelper extends Thread {

	InetAddress ip = null;
	Info packet = null;
	File packet1 = null;

	ClientHelper(InetAddress ip, Info packet) {
		this.ip = ip;
		this.packet = packet;
	}

	public void run() {
		try {
			DatagramSocket datagramSocket = new DatagramSocket();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			ObjectOutputStream data = new ObjectOutputStream(buffer);
			data.writeObject(packet);
			data.flush();
			data.close();

			byte[] Buf = buffer.toByteArray();
			DatagramPacket packet = new DatagramPacket(Buf, Buf.length, ip, 9000);
			int count = 1;

			// send 5 time
			while(count<2) {
				Log.d("CientHelper", "Try:"+count);
				count++;
				datagramSocket.send(packet);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("I am here");
		}
	}
}

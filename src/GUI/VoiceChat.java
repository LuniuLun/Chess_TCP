package GUI;

import javax.swing.JPanel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class VoiceChat extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */

	public Integer port_server = 8888;
	public String add_server = "localhost";
	JButton btn_start;
	JButton btn_stop;
	Integer Port_TCP;

	public static AudioFormat getAudioFormat() {
		Float sampleRate = 8000.0F;
		Integer sampleSizeInbits = 16;
		Integer channel = 2;
		Boolean signed = true;
		Boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
	}

	TargetDataLine audio_in;

	public static AudioFormat getAudioFormatOut() {
		Float sampleRate = 8000.0F;
		Integer sampleSizeInbits = 16;
		Integer channel = 2;
		Boolean signed = true;
		Boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
	}

	public SourceDataLine audio_out;

	public VoiceChat(int port) {
		this.Port_TCP = port;
		setLayout(null);

		JButton btn_OnMic = new JButton("Call");
		btn_OnMic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btn_OnMic.getText().equals("Call")) {
					try {
						init_audio();
						btn_OnMic.setText("Off Calling");
					} catch (UnknownHostException | SocketException | LineUnavailableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		btn_OnMic.setBounds(10, 10, 105, 21);
		add(btn_OnMic);
	}

	public void init_audio() throws LineUnavailableException, UnknownHostException, SocketException {
		AudioFormat format = getAudioFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("not support");
			System.exit(0);
		}
		audio_in = (TargetDataLine) AudioSystem.getLine(info);
		audio_in.open(format);
		audio_in.start();
		recorder_thread r = new recorder_thread();
		InetAddress inet = InetAddress.getByName(add_server);

		r.audio_in = audio_in;
		r.dout = new DatagramSocket();
		r.server_ip = inet;
		r.server_port = port_server;
		ChatBox.calling = true;
		r.start();

		DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
		if (!AudioSystem.isLineSupported(info_out)) {
			System.out.println("not support");
			System.exit(0);
		}
		audio_out = (SourceDataLine) AudioSystem.getLine(info_out);
		audio_out.open(format);
		audio_out.start();
	}

	public class recorder_thread extends Thread {
		public TargetDataLine audio_in = null;
		public DatagramSocket dout;
		byte byte_buff[] = new byte[512];
		public InetAddress server_ip;
		public int server_port;

		@Override
		public void run() {
			String checkData = "1";
			try {
				DatagramPacket datacheck = new DatagramPacket(checkData.getBytes(), checkData.getBytes().length,
						server_ip, port_server);
				dout.send(datacheck);
				DatagramPacket dataport = new DatagramPacket(Port_TCP.toString().getBytes(),
						Port_TCP.toString().getBytes().length, server_ip, port_server);
				dout.send(dataport);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Calling...");
			while (ChatBox.calling) {
				audio_in.read(byte_buff, 0, byte_buff.length);
				checkData = "0";
				try {
					DatagramPacket datacheck = new DatagramPacket(checkData.getBytes(), checkData.getBytes().length,
							server_ip,
							port_server);
					dout.send(datacheck);
					DatagramPacket data = new DatagramPacket(byte_buff, byte_buff.length, server_ip, server_port);
					dout.send(data);
					receiveAudioData();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private void receiveAudioData() throws IOException {
			byte[] receiveBuffer = new byte[512];
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			dout.receive(receivePacket);
			if (receiveBuffer != null) {
				// Phát âm thanh lên loa
				audio_out.write(receiveBuffer, 0, receiveBuffer.length);
			}
		}
	}
}

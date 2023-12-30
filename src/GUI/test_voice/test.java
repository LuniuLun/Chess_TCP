package GUI.test_voice;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import GUI.ChatBox;

import javax.swing.JButton;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class test extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public Integer port_server = 8888;
	public String add_server = "localhost";
	JButton btn_start;
	JButton btn_stop;

	public static AudioFormat getAudioFormat() {
		Float sampleRate = 8000.0F;
		Integer sampleSizeInbits = 16;
		Integer channel = 2;
		Boolean signed = true;
		Boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
	}
	TargetDataLine audio_in;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test frame = new test();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void init_audio() throws LineUnavailableException, UnknownHostException, SocketException {
		AudioFormat format = getAudioFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if(!AudioSystem.isLineSupported(info)) {
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
		btn_start.setVisible(false);
		btn_stop.setVisible(true);
	}
	/**
	 * Create the frame.
	 */
	public test() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		btn_start = new JButton("Start");
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					init_audio();
				} catch (UnknownHostException | SocketException | LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btn_start.setBounds(79, 156, 85, 21);
		contentPane.add(btn_start);

		btn_stop = new JButton("Stop");
		btn_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatBox.calling = false;
			}
		});
		btn_stop.setBounds(258, 156, 85, 21);
		contentPane.add(btn_stop);
	}
}

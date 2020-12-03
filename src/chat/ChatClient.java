package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import protocol.Chat;

public class ChatClient extends JFrame {

	private static final String TAG = "ChatClient : ";
	private ChatClient chatClient = this;
	
	private static final int PORT = 10000;
	
	private JButton btnConnect, btnSend;
	private JTextField tfHost, tfChat;
	private JTextArea taChatList;
	private ScrollPane scrollPane;
	
	private JPanel topPanel, bottomPanel;
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	
	public ChatClient() {
		init();			// ���� ���
		setting();		// ������ ����
		batch();		// ������
		listener();
		
		setVisible(true);
	}
	
	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1", 20);	// 20���ڱ��� �ۼ� ����
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30);	//����(rows) 30ĭ, ����(columns) 10ĭ
		scrollPane = new ScrollPane();
		topPanel = new JPanel();
		bottomPanel = new JPanel();
	}
	
	private void setting() {
		setTitle("ä�� �ٴ�� Ŭ���̾�Ʈ");
		setSize(350, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		taChatList.setBackground(Color.orange);
		taChatList.setForeground(Color.blue);
	}
	
	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		scrollPane.add(taChatList);
		
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void listener() {
		btnConnect.addActionListener(new ActionListener() {		// ���� ���Ͽ� ����	
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();	// �Լ��� ���� �������� �Լ��� ȣ���Ѵ�
			}
		});
		
		btnSend.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
	}
	
	private void send() {
		String chat = tfChat.getText();	// ����ڰ� ���� ���ڸ� �����´�
		// 1. taChatList�� �Ѹ���
//		taChatList.append(chat + "\n");
		// setText()�� ����ϸ� ������ ���� ���ڿ� ���������
		// append()�� ����ϸ� ��� �߰��ȴ�. ������ �߰��Ǳ� ������ \n �߰�
		
		// 2. ������ ����
		try {
			writer.println(Chat.ALL+":"+chat);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 3. tfChat ����
		tfChat.setText("");
	}
	
	private void connect() {
		String host = tfHost.getText();	// tfHost�� �����͸� ���´�
		try {
			tfHost.setEditable(false);
			socket = new Socket(host, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// ���°� : ���� �����尡 �Ѵ�
			writer = new PrintWriter(socket.getOutputStream());	//�÷��� �ϱ� ������ , true
			ReaderThread rt = new ReaderThread();
			rt.start();
		} catch (Exception e1) {
			System.out.println(TAG + "���� ���� ���� : " + e1.getMessage());
		}
	}
	
	class ReaderThread extends Thread {
		// while ���鼭 �����κ��� �޽����� �޾Ƽ� taChatList�� �Ѹ��� reader.readline()
		@Override
		public void run() {
			try {
				String chat = null;
				while((chat = reader.readLine()) != null) {
					taChatList.append(chat + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		new ChatClient();
	}
}

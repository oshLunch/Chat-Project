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
		init();			// 내부 요소
		setting();		// 프레임 세팅
		batch();		// 디자인
		listener();
		
		setVisible(true);
	}
	
	private void init() {
		btnConnect = new JButton("connect");
		btnSend = new JButton("send");
		tfHost = new JTextField("127.0.0.1", 20);	// 20글자까지 작성 가능
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30);	//가로(rows) 30칸, 세로(columns) 10칸
		scrollPane = new ScrollPane();
		topPanel = new JPanel();
		bottomPanel = new JPanel();
	}
	
	private void setting() {
		setTitle("채팅 다대다 클라이언트");
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
		btnConnect.addActionListener(new ActionListener() {		// 서버 소켓에 연결	
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();	// 함수를 따로 만들어놓고 함수만 호출한다
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
		String chat = tfChat.getText();	// 사용자가 적은 글자를 가져온다
		// 1. taChatList에 뿌리기
//		taChatList.append(chat + "\n");
		// setText()를 사용하면 이전에 적은 글자에 덮어씌워진다
		// append()를 사용하면 계속 추가된다. 옆으로 추가되기 때문에 \n 추가
		
		// 2. 서버로 전송
		try {
			writer.println(Chat.ALL+":"+chat);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 3. tfChat 비우기
		tfChat.setText("");
	}
	
	private void connect() {
		String host = tfHost.getText();	// tfHost의 데이터를 들고온다
		try {
			tfHost.setEditable(false);
			socket = new Socket(host, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// 쓰는것 : 메인 스레드가 한다
			writer = new PrintWriter(socket.getOutputStream());	//플러쉬 하기 싫으면 , true
			ReaderThread rt = new ReaderThread();
			rt.start();
		} catch (Exception e1) {
			System.out.println(TAG + "서버 연결 에러 : " + e1.getMessage());
		}
	}
	
	class ReaderThread extends Thread {
		// while 돌면서 서버로부터 메시지를 받아서 taChatList에 뿌리기 reader.readline()
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

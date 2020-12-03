package chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import logtest.LogTest;
import protocol.Chat;

public class ChatServer {

	private static final String TAG = "ChatServer : ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // 연결된 클라이언트 클래스(소켓)을 담는 컬렉션
	// 소켓을 담으면 상태를 저장 못함
	// 소켓을 담고 싶다면 클래스에 소켓을 콤포지션해서 클래스를 담는다

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "클라이언트 연결 대기중...");
			// 메인 스레드의 역
			while (true) {
				Socket socket = serverSocket.accept(); // 클라이언트 연결 대기
				System.out.println("연결 성공");
				ClientInfo clientInfo = new ClientInfo(socket);
				clientInfo.start();
				vc.add(clientInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ClientInfo extends Thread {

		Socket socket;
		BufferedReader reader;
		PrintWriter writer = null; // BufferedWriter 함수와 다른 점 : 내려쓰기 함수를 지원
		String userId;

		public ClientInfo(Socket socket) {
			this.socket = socket; // ClientInfo 클래스가 socket을 담는다
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
			} catch (Exception e) {
				System.out.println("서버 연결 실패 : " + e.getMessage());
			}
		}

		@Override
		public void run() { // 메인 스레드가 아니라 새로운 스레드
			// 역할 : 클라이언트로부터 받은 메시지를 모든 클라이언트에게 재전송

			try {
				writer.println("사용하실 아이디를 입력하세요");
				writer.flush();

				userId = reader.readLine();

				String chat = null;
				String[] idGubun = userId.split(":");
				String idProtocol = idGubun[0];
				String idName = idGubun[1];

				writer.println("[" + idName + "]님께서 입장하셨습니다");
				writer.flush();

				while ((chat = reader.readLine()) != null) {
					String[] chatGubun = chat.split(":");
					String chatProtocol = chatGubun[0];
					String chatMsg = chatGubun[1];

					for (ClientInfo clientInfo : vc) {
						if (chatProtocol.equals(Chat.ALL)) {
							clientInfo.writer.println("[" + idName + "]" + chatMsg);
							clientInfo.writer.flush();
						}

						LogTest makeLog = new LogTest();

						makeLog.setFilePath("Chat_Log.txt");
						makeLog.makeLogfile("[" + idName + "]" + chatMsg);
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static class LogTest {

		private static String filePath = "log.txt";

		public void makeLogfile(Object o) {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss", Locale.getDefault());
			String date = sdf.format(cal.getTime());
			String out = "[" + date + "] " + o;
			System.out.println(out);

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
				bw.append(out);
				bw.newLine();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			LogTest.filePath = filePath;
		}

	}

	public static void main(String[] args) {
		new ChatServer();
	}
}

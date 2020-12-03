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
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ Ŭ����(����)�� ��� �÷���
	// ������ ������ ���¸� ���� ����
	// ������ ��� �ʹٸ� Ŭ������ ������ ���������ؼ� Ŭ������ ��´�

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����...");
			// ���� �������� ��
			while (true) {
				Socket socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ���
				System.out.println("���� ����");
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
		PrintWriter writer = null; // BufferedWriter �Լ��� �ٸ� �� : �������� �Լ��� ����
		String userId;

		public ClientInfo(Socket socket) {
			this.socket = socket; // ClientInfo Ŭ������ socket�� ��´�
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream());
			} catch (Exception e) {
				System.out.println("���� ���� ���� : " + e.getMessage());
			}
		}

		@Override
		public void run() { // ���� �����尡 �ƴ϶� ���ο� ������
			// ���� : Ŭ���̾�Ʈ�κ��� ���� �޽����� ��� Ŭ���̾�Ʈ���� ������

			try {
				writer.println("����Ͻ� ���̵� �Է��ϼ���");
				writer.flush();

				userId = reader.readLine();

				String chat = null;
				String[] idGubun = userId.split(":");
				String idProtocol = idGubun[0];
				String idName = idGubun[1];

				writer.println("[" + idName + "]�Բ��� �����ϼ̽��ϴ�");
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

package logtest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LogTest {
	
	private static String filePath = "log.txt";

	public static void makeLogfile(Object o) {
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

	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		LogTest.filePath = filePath;
	}

	public static void main(String[] args) {
		System.out.println(LogTest.getFilePath());
		LogTest.setFilePath("log2.txt");
		LogTest.makeLogfile("초기화를 실시합니다");
	}
}

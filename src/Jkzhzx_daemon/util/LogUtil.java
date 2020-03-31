package Jkzhzx_daemon.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志的处理
 * 
 * @author Administrator
 * 
 */
public class LogUtil {

	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat sdf2 = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	// 添加日志
	public static void addLog(String info) {
		// PrintWriter printer = null;
		FileWriter writer = null;
		try {
			File file = new File("./logs.txt");
			if(file.exists()&&file.length()>500000){//大约500k
				String path = file.getAbsolutePath().replace(".\\logs.txt", "");
				File dest = new File(path+"logs"+sdf2.format(new Date())+".txt");
				//重命名
				if(file.renameTo(dest)){
				}else{
					file.delete();
				}	
				file = new File("./logs.txt");
			}
			// 增量写log
			writer = new FileWriter(file, true);
			writer.write(info = (sdf.format(new Date()) + ":" + info));
			//System.out.println(info);
			writer.write("\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	// 添加日志
		public static void testFile(String info) {
			// PrintWriter printer = null;
			FileWriter writer = null;
			try {
				// 增量写log
				writer = new FileWriter("./file.txt", true);
				writer.write(info);
				//System.out.println(info);
				writer.write("\r\n");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}
}

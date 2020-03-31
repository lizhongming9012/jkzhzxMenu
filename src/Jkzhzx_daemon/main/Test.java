package Jkzhzx_daemon.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

public class Test {
	public static void main(String args[]) {
		Properties prop = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					"F:/workspaces/jkzhzx/src/user.properties"));
			prop.load(in);
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext()) {
				String key = it.next();
				System.out.println(key + ":" + prop.getProperty(key));
			}
			in.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
}

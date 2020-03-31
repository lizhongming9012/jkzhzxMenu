package Jkzhzx_daemon.main;

import Jkzhzx_daemon.service.JkzhzxService;

public class Main {
	public static void main(String args[]) throws Exception{
				JkzhzxService jkzhzx = new JkzhzxService();
				try {
					jkzhzx.doAction(args, args, 0, args, args, args);
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
}
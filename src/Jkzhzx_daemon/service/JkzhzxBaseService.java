package Jkzhzx_daemon.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import Jkzhzx_daemon.resource.User;

/**
 * 监控系统的基本service
 * 
 * @author Administrator
 * 
 */
public abstract class JkzhzxBaseService {
	// 查询名称
	protected String serviceName = null;
	// 模拟浏览器
	protected CloseableHttpClient httpclient = null;
	// ajax请求的id
	protected String userIdAjax = null;

	// // 模拟浏览器cookie
	// protected BasicCookieStore cookieStore = null;

	public JkzhzxBaseService() {
	}

	/**
	 * 初始化浏览器
	 */
	// private void initBrower() {
	// cookieStore = new BasicCookieStore();
	// // 模拟浏览器
	// httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
	// .build();
	// }

	/**
	 * 构造函数
	 * 
	 * @param serviceName
	 * @param httpclient
	 */
	public JkzhzxBaseService(String serviceName, CloseableHttpClient httpclient) {
		super();
		this.serviceName = serviceName;
		this.httpclient = httpclient;
	}

	/**
	 * 执行定义的动作
	 * 
	 * @param type
	 * 动作类型
	 * 
	 * @return
	 * @throws Exception
	 */
	public void doAction(String[] users, String[] pwds, int type, String[] ids,
			String[] texts, String[] actions) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 准备工作 判断数据是否存在等待并非每个service都需要重写 true 表示无需进行查询操作 false表示需要进行查询操作
	 * 
	 * @return
	 */
	public abstract boolean prepareWork() throws Exception;

	/**
	 * 登陆
	 * 
	 * @param user
	 * @throws Exception
	 */
	public abstract void login(User user) throws Exception;

	/**
	 * 登出
	 * 
	 * @throws Exception
	 */
	public abstract void logout() throws Exception;

	/**
	 * 将返回请求转化为json对象
	 * 
	 * @param response
	 * @return
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public JSONObject getResponseJson(CloseableHttpResponse response)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		String line = null;
		BufferedReader br = null;
		try {
			String charset = null;
			Header[] headers = response.getHeaders("Content-Type");
			if (headers != null) {
				for (Header header : headers) {
					String str = header.toString();
					if (str.contains("utf-8") || str.contains("UTF-8")) {
						charset = "UTF-8";
					} else if (str.contains("gb") || str.contains("GB")) {
						charset = "GBK";
					}
				}
			}
			// System.out.println("getResponseJson:" + charset);
			br = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent(), charset));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			line = sb.toString().trim();
			// 返回内容是否能够转化为json
			if (line != null && !line.isEmpty() && JSONUtils.mayBeJSON(line)) {
				JSONObject json = JSONObject.fromObject(line);
				return json;
			} else {
				// 如果返回内容不是json格式，可能是数据错误或者session过期
				throw new Exception("返回数据请求格式错误或者无数据返回!");
			}
		} catch (Exception e) {
			throw new Exception("将返回请求转化为json对象错误");
		}
	}




}

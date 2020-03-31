package Jkzhzx_daemon.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import Jkzhzx_daemon.service.JkzhzxBaseService;
import Jkzhzx_daemon.util.FilePath;
import Jkzhzx_daemon.resource.JkMenu;
import Jkzhzx_daemon.resource.User;

public class JkzhzxService extends JkzhzxBaseService {
	private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; "
			+ "Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; "
			+ ".NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; Tablet PC 2.0; InfoPath.2)";
	private BasicCookieStore cookieStore = null;
	// 加载配置文件
	static Properties userprop = new Properties();
	static Properties menuprop = new Properties();
	static String userPath = FilePath.getRealPath()
			+ "\\config\\user.properties";
	static String menuPath = FilePath.getRealPath()
			+ "\\config\\jkmenu.properties";
	static {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
			// userPath));
					"F:/workspaces/jkzhzx/config/user.properties")); // 程序内部调试绝对地址
			InputStream is = new BufferedInputStream(new FileInputStream(
			// menuPath));
					"F:/workspaces/jkzhzx/config/user.properties")); // 程序内部调试绝对地址
			userprop.load(in);
			menuprop.load(is);
			// System.out.println(userprop.getProperty("users"));
			System.out.println("获取的当前路径为：" + userPath + "\n" + menuPath);
			in.close();
		} catch (IOException e) {
			System.out.println("加载配置文件错误！\n" + "获取的当前路径为：" + userPath + "\n"
					+ menuPath);
		}
	}

	/**
	 * 
	 * 打开浏览器
	 * 
	 * */
	public void openBrower() {
		cookieStore = new BasicCookieStore();
		// 模拟浏览器
		httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.build();
	}

	/**
	 * 关闭浏览器
	 * 
	 * @throws IOException
	 * */
	private void closeBrower() throws IOException {
		if (httpclient != null) {
			httpclient.close();
			httpclient = null;
		}
	}

	@Override
	public void doAction(String[] users, String[] pwds, int type, String[] ids,
			String[] texts, String[] actions) throws Exception {
		// TODO Auto-generated method stub
		users = userprop.getProperty("users").split(",");
		pwds = userprop.getProperty("pwds").split(",");
		ids = menuprop.getProperty("ids").split(",");
		texts = menuprop.getProperty("texts").split(",");
		actions = menuprop.getProperty("actions").split(",");
		if (users != null && pwds != null && users.length == pwds.length
				&& ids != null && texts != null && ids.length == texts.length) {
			// 生成用户list
			List<User> list = new ArrayList<User>();
			for (int i = 0; i < users.length; ++i) {
				User user = new User();
				user.setName(users[i]);
				user.setPwd(pwds[i]);
				list.add(user);
			}
			// 生成菜单list
			List<JkMenu> menulist = new ArrayList<JkMenu>();
			for (int i = 0; i < ids.length; ++i) {
				JkMenu menu = new JkMenu();// 配置文件中的菜单
				menu.setId(ids[i]);
				menu.setText(texts[i]);
				menu.setAction(actions[i]);
				menulist.add(menu);
			}
			for (int j = 0; j < list.size(); ++j) {
				User user = list.get(j);
				// System.out.println("————————————————————————————————————————————————用户名为："
				// +user.getName());
				try {
					// 打开浏览器
					openBrower();
					// 用户登陆
					login(user);
					// 获取权限菜单信息
					ArrayList<JkMenu> jkMenulist = new ArrayList<JkMenu>();
					jkMenulist = getMenu();// getMenu()方法中获得的用户的权限菜单
					for (int k = 0; k < jkMenulist.size(); ++k) {
						JkMenu jkMenu = jkMenulist.get(k);
						for (int u = 0; u < menulist.size(); ++u) {
							JkMenu menu = menulist.get(u);
							System.out.println("————————————" + menu.getId()
									+ "\n————————————" + menu.getText()
									+ "\n————————————" + menu.getAction());
							if(jkMenu.getId().equals(menu.getId())){
								System.out.println("该用户拥有的权限是"+menu.getText());
								menu.getAction();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// 用户登出
					logout();
					// 关闭浏览器
					closeBrower();
					Thread.sleep(3000);
				}
			}
		}
	}

	@Override
	public boolean prepareWork() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(User user) throws Exception {
		// TODO Auto-generated method stub
		CloseableHttpResponse response = null;
		try {
			// 构造登陆请求表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("j_username", user.getName()
					.trim()));
			formparams.add(new BasicNameValuePair("j_password", user.getPwd()
					.trim()));
			formparams.add(new BasicNameValuePair("rdmCode", ""));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logincmd.cmd?method=doLogin");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setHeader("Referer",
					"http://76.12.152.1:7001/pc/logincmd.cmd?method=loginOut");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null
					&& result
							.contains("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd")) {
				System.out
						.println("——————————————————————————————————————————————————————————————————登陆成功");
			} else {
				throw new Exception(
						"——————————————————————————————————————————————————————————————————登陆失败，用户名或密码错误!");
			}
			// Thread.sleep(7000);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void logout() throws Exception {
		// TODO Auto-generated method stub
		HttpGet httpget = null;
		CloseableHttpResponse response = null;
		try {
			// 退出系统
			httpget = new HttpGet(
					"http://76.12.152.1:7001/pc/logincmd.cmd?method=loginOut");
			httpget.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			response = httpclient.execute(httpget);
			if (response != null) {
				response.close();
				System.out
						.println("——————————————————————————————————————————————————————————————————退出成功");
			}
			// Thread.sleep(7000);
		} catch (Exception e) {
			throw e;
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * 获取权限菜单
	 * 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<JkMenu> getMenu() throws Exception {
		CloseableHttpResponse response = null;
		ArrayList<JkMenu> jkMenulist = new ArrayList<JkMenu>();
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			// formparams.add(new BasicNameValuePair("gndm",
			// "H0000001H0100382"));
			// formparams.add(new BasicNameValuePair("gnmc","全省税收收入分税种完成情况监控"));
			// formparams
			// .add(new BasicNameValuePair(
			// "url",
			// "/sssrCmd.cmd?method=init㈧zyDm=110102㈧zndm=04㈧gndm=H0000001H0100382㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/getmenu.cmd?method=getTreeInfoById");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setHeader("Referer",
					"http://76.12.152.1:7001/pc/logincmd.cmd?method=doLogin");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			// String result = EntityUtils.toString(response.getEntity(),
			// "utf-8");
			// System.out.println(result);
			// 分析结果
			JSONObject json = getResponseJson(response);
			JSONArray list = json.optJSONArray("row");
			if (list != null && list.equals("{{\"rows\":[null]}}")) {
				System.out.println("无权限");
			} else if (list != null) {
				for (int m = 0; m < list.size(); ++m) {
					JSONObject obj = list.optJSONObject(m);
					if (obj != null) {
						String id = obj.optString("id", "");
						String text = obj.optString("text", "");
						// 将权限菜单信息加入Arraylist
						JkMenu jkMenu = new JkMenu();
						jkMenu.setId(id);
						jkMenu.setText(text);
						jkMenulist.add(jkMenu);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return jkMenulist;
	}

	/**
	 * 税收收入
	 * 
	 * @return
	 * @throws Exception
	 */
	private void sssr() throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("gndm", "H0000001H0100382"));
			// formparams.add(new BasicNameValuePair("gnmc","全省税收收入分税种完成情况监控"));
			formparams
					.add(new BasicNameValuePair(
							"url",
							"/sssrCmd.cmd?method=init㈧zyDm=110102㈧zndm=04㈧gndm=H0000001H0100382㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null && result.contains("保存成功")) {
				System.out.println("打开成功");
			} else {
				throw new Exception("打开失败或无权限");
			}
			// Thread.sleep(3000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 税收征管
	 * 
	 * @return
	 * @throws Exception
	 */
	private void sszg() throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("gndm", "H0000001H0100283"));
			// formparams.add(new
			// BasicNameValuePair("gnmc","全省出口退（免）税备案企业情况监控"));
			formparams
					.add(new BasicNameValuePair(
							"url",
							"/sszgCmd.cmd?method=init㈧zyDm=120601㈧zndm=04㈧gndm=H0000001H0100283㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null && result.contains("保存成功")) {
				System.out.println("打开成功");
			} else {
				throw new Exception("打开失败或无权限");
			}
			// Thread.sleep(3000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 纳税服务
	 * 
	 * @return
	 * @throws Exception
	 */
	private void nsfw() throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("gndm", "H0000001H0100470"));
			// formparams.add(new BasicNameValuePair("gnmc","排队情况"));
			formparams
					.add(new BasicNameValuePair(
							"url",
							"/qjjkCmd.cmd?method=init㈧zyDm=140001㈧zndm=04㈧gndm=H0000001H0100470㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null && result.contains("保存成功")) {
				System.out.println("打开成功");
			} else {
				throw new Exception("打开失败或无权限");
			}
			// Thread.sleep(3000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 营改增
	 * 
	 * @return
	 * @throws Exception
	 */
	private void ygz() throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("gndm", "H0000001H0100368"));
			// formparams.add(new
			// BasicNameValuePair("gnmc","试点纳税人增值税销售额和应纳税额情况"));
			formparams
					.add(new BasicNameValuePair(
							"url",
							"/ygzCmd.cmd?method=init㈧zyDm=131001㈧zyDm=131002㈧zndm=04㈧gndm=H0000001H0100368㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null && result.contains("保存成功")) {
				System.out.println("打开成功");
			} else {
				throw new Exception("打开失败或无权限");
			}
			// Thread.sleep(3000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 数字人事
	 * 
	 * @return
	 * @throws Exception
	 */
	private void szrs() throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("gndm", "H0000001H0100305"));
			// formparams.add(new BasicNameValuePair("gnmc","全省机构人员情况监控"));
			formparams
					.add(new BasicNameValuePair(
							"url",
							"/szrsCmd.cmd?method=init㈧zyDm=150101㈧zyDm=150102㈧zyDm=150103㈧zyDm=150104㈧zyDm=150105㈧zndm=04㈧gndm=H0000001H0100305㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null && result.contains("保存成功")) {
				System.out.println("打开成功");
			} else {
				throw new Exception("打开失败或无权限");
			}
			// Thread.sleep(3000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 教育培训
	 * 
	 * @return
	 * @throws Exception
	 */
	private void jypx() throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("gndm", "H0000001H0100321"));
			// formparams.add(new BasicNameValuePair("gnmc","全省国税系统教育培训情况统计"));
			formparams
					.add(new BasicNameValuePair(
							"url",
							"/jypxCmd.cmd?method=init㈧zyDm=200102㈧zyDm=200103㈧zyDm=200104㈧zndm=04㈧gndm=H0000001H0100321㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null && result.contains("保存成功")) {
				System.out.println("打开成功");
			} else {
				throw new Exception("打开失败或无权限");
			}
			// Thread.sleep(3000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 组织绩效
	 * 
	 * @return
	 * @throws Exception
	 */
	private void zzjx() throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("gndm", "H0000001H0100315"));
			// formparams.add(new BasicNameValuePair("gnmc","省局考评市局情况"));
			formparams
					.add(new BasicNameValuePair(
							"url",
							"/jxkhCmd.cmd?method=init㈧zyDm=1601001㈧zndm=04㈧gndm=H0000001H0100315㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null && result.contains("保存成功")) {
				System.out.println("打开成功");
			} else {
				throw new Exception("打开失败或无权限");
			}
			// Thread.sleep(3000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 涉税舆情
	 * 
	 * @return
	 * @throws Exception
	 */
	private void ssyq() throws Exception {
		CloseableHttpResponse response = null;
		try {
			// 构造表单webForms
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("gndm", "H0000001H0100319"));
			// formparams.add(new BasicNameValuePair("gnmc","涉税舆情"));
			formparams
					.add(new BasicNameValuePair(
							"url",
							"/ssyqCmd.cmd?method=init㈧zyDm=170100㈧zndm=04㈧gndm=H0000001H0100319㈧gwxh=null㈧gwssswjg=null"));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
					"UTF-8");
			HttpPost httppost = new HttpPost(
					"http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
			httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// System.out.println(result);
			// 分析结果判断登陆是否成功
			if (result != null && result.contains("保存成功")) {
				System.out.println("打开成功");
			} else {
				throw new Exception("打开失败或无权限");
			}
			// Thread.sleep(3000);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 信息交换
	 * 
	 * @return
	 * @throws Exception
	 */
	/*
	 * private void xxjh() throws Exception { CloseableHttpResponse response =
	 * null; try { // 构造表单webForms List<NameValuePair> formparams = new
	 * ArrayList<NameValuePair>(); formparams.add(new BasicNameValuePair("gndm",
	 * "")); // formparams.add(new BasicNameValuePair("gnmc",""));
	 * formparams.add(new BasicNameValuePair("url", "")); UrlEncodedFormEntity
	 * entity = new UrlEncodedFormEntity(formparams, "UTF-8"); // 启动手动巡检
	 * HttpPost httppost = new HttpPost(
	 * "http://76.12.152.1:7001/pc/logCmd.cmd?method=saveCzLog");
	 * httppost.setHeader("User-Agent", JkzhzxService.USER_AGENT);
	 * httppost.setHeader("Content-Type",
	 * "application/x-www-form-urlencoded; charset=UTF-8");
	 * httppost.setEntity(entity); response = httpclient.execute(httppost);
	 * String result = EntityUtils.toString(response.getEntity(), "utf-8"); //
	 * System.out.println(result); // 分析结果判断登陆是否成功 if (result != null &&
	 * result.contains("保存成功")) { System.out.println("打开成功"); } else { throw new
	 * Exception("打开失败或无权限"); } // Thread.sleep(3000); } catch (Exception e) {
	 * throw e; } }
	 */
}

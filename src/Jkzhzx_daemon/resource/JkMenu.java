package Jkzhzx_daemon.resource;

/**
* 
* 监控指挥中心菜单类
* 
* @author lsadmin
* 
*/
	public class JkMenu {
		private String id;// 菜单ID
		private String text;// 菜单名称
		private String action;//执行的动作
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}

	}


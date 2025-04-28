package com.yumu.hexie.web.user.req;

import java.io.Serializable;

public class UserAgent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6833275410788736648L;

	private String user_agent;
	private String user_system;
	private String user_browser;
	
	public UserAgent() {
		super();
	}

	public UserAgent(String user_agent) {
		this.user_agent = user_agent;
		if (user_agent.indexOf("Windows") >= 0) {
			int index = user_agent.indexOf("Windows");
			String subStr = user_agent.substring(index);
			int endIndex = subStr.indexOf(";");
			if (endIndex < 0)
				endIndex = subStr.indexOf(")");
			if (endIndex >= 0) {
				this.user_system = subStr.substring(0, endIndex);
			} else {
				this.user_system = "Windows";
			}
		} else if (user_agent.indexOf("Macintosh") >= 0) {
			int index = user_agent.indexOf("Mac OS X");
			if (index >= 0) {
				String subStr = user_agent.substring(index);
				int endIndex = subStr.indexOf(";");
				if (endIndex < 0)
					endIndex = subStr.indexOf(")");
				if (endIndex >= 0) {
					this.user_system = "Macintosh " + subStr.substring(0, endIndex);
				} else {
					this.user_system = "Macintosh";
				}
			} else {
				this.user_system = "Macintosh";
			}
		} else if (user_agent.indexOf("iPad") >= 0) {
			int index = user_agent.indexOf("Mobile");
			if (index >= 0) {
				String subStr = user_agent.substring(index);
				int endIndex = subStr.indexOf(" ");
				if (endIndex >= 0) {
					this.user_system = "iPad " + subStr.substring(0, endIndex);
				} else {
					this.user_system = "iPad";
				}
			} else {
				this.user_system = "iPad";
			}
		} else if (user_agent.indexOf("iPhone") >= 0) {
			int index = user_agent.indexOf("Mobile");
			if (index >= 0) {
				String subStr = user_agent.substring(index);
				int endIndex = subStr.indexOf(" ");
				if (endIndex >= 0) {
					this.user_system = "iPhone " + subStr.substring(0, endIndex);
				} else {
					this.user_system = "iPhone";
				}
			} else {
				this.user_system = "iPhone";
			}
		} else if (user_agent.indexOf("Linux") >= 0) {
			if (user_agent.indexOf("Android") >= 0) {
				int index = user_agent.indexOf("Android");
				if (index >= 0) {
					String subStr = user_agent.substring(index);
					int endIndex = subStr.indexOf(";");
					if (endIndex >= 0) {
						this.user_system = subStr.substring(0, endIndex);
					} else {
						this.user_system = "Android";
					}
				} else {
					this.user_system = "Android";
				}
			} else {
				this.user_system = "Linux";
			}
		} else {
			this.user_system = "Unknown";
		}
		if (user_agent.indexOf("MSIE") >= 0) {
			int index = user_agent.indexOf("MSIE");
			if (index >= 0) {
				String subStr = user_agent.substring(index);
				int endIndex = subStr.indexOf(";");
				if (endIndex >= 0) {
					this.user_browser = subStr.substring(0, endIndex);
				} else {
					this.user_browser = "MSIE";
				}
			} else {
				this.user_browser = "MSIE";
			}
		} else if (user_agent.indexOf("Safari") >= 0) {
			if (user_agent.indexOf("Chrome") >= 0) {
				int index = user_agent.indexOf("Chrome");
				if (index >= 0) {
					String subStr = user_agent.substring(index);
					int endIndex = subStr.indexOf(" ");
					if (endIndex >= 0) {
						this.user_browser = subStr.substring(0, endIndex);
					} else {
						this.user_browser = "Chrome";
					}
				} else {
					this.user_browser = "Chrome";
				}
			} else if (user_agent.indexOf("CriOS") >= 0) {
				int index = user_agent.indexOf("CriOS");
				if (index >= 0) {
					String subStr = user_agent.substring(index);
					int endIndex = subStr.indexOf(" ");
					if (endIndex >= 0) {
						this.user_browser = "Chrome " + subStr.substring(0, endIndex);
					} else {
						this.user_browser = "Chrome";
					}
				} else {
					this.user_browser = "Chrome";
				}
			} else {
				int index = user_agent.indexOf("Version");
				if (index >= 0) {
					String subStr = user_agent.substring(index);
					int endIndex = subStr.indexOf(" ");
					if (endIndex >= 0) {
						this.user_browser = "Safari " + subStr.substring(0, endIndex);
					} else {
						this.user_browser = "Safari";
					}
				} else {
					this.user_browser = "Safari";
				}
			}
		} else if (user_agent.indexOf("Firefox") >= 0) {
			int index = user_agent.indexOf("Firefox");
			this.user_browser = user_agent.substring(index);
		} else if (user_agent.indexOf("Opera") >= 0) {
			int index = user_agent.indexOf("Version");
			if (index >= 0) {
				this.user_browser = "Opera " + user_agent.substring(index);
			} else {
				this.user_browser = "Opera";
			}
		} else {
			this.user_browser = "Unknown";
		}
	}

	public String getUser_agent() {
		return this.user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getUser_browser() {
		return this.user_browser;
	}

	public void setUser_browser(String user_browser) {
		this.user_browser = user_browser;
	}

	public String getUser_system() {
		return this.user_system;
	}

	public void setUser_system(String user_system) {
		this.user_system = user_system;
	}

	public static void main(String[] args) {
		UserAgent ua = new UserAgent("Opera/9.80 (iPad; Opera Mini/7.0.5/28.2690; U; zh) Presto/2.8.119 Version/11.10");
		System.out.println("system=[" + ua.getUser_system() + "],browser=[" + ua.getUser_browser() + "]");
	}

}

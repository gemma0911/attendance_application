package DAO;

import java.io.Serializable;

public class Status implements Serializable {
	private static final long serialVersionUID = 1L;
	private String username;
	private String time;
	private String ip;
	private int host;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getHost() {
		return host;
	}
	public void setHost(int host) {
		this.host = host;
	}
	
	
}

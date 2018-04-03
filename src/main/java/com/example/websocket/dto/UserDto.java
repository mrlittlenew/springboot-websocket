package com.example.websocket.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDto implements Serializable {

	private static final long serialVersionUID = 8946191825896276676L;

	private String uid;
	private String name;
	@JsonIgnore
	private boolean online;
	@JsonIgnore
	private Date createTime=new Date();
	@JsonIgnore
	private Date lastOnlineTime=new Date();
	@JsonIgnore
	private Date lastActiveTime=new Date();
	
	public Date getLastOnlineTime() {
		return lastOnlineTime;
	}
	public void setLastOnlineTime(Date lastOnlineTime) {
		this.lastOnlineTime = lastOnlineTime;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastActiveTime() {
		return lastActiveTime;
	}
	public void setLastActiveTime(Date lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}
	@JsonIgnore
	List<WebSocketSession> sessionList=new ArrayList<WebSocketSession>();
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<WebSocketSession> getSessionList() {
		return sessionList;
	}
	public void setSessionList(List<WebSocketSession> sessionList) {
		this.sessionList = sessionList;
	}
	
	public void online(){
		lastOnlineTime=new Date();
	}
	
	public void active(){
		lastActiveTime=new Date();
	}

	

}

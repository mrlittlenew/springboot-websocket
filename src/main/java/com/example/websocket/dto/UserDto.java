package com.example.websocket.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDto implements Serializable {

	private static final long serialVersionUID = 8946191825896276676L;

	private String uid;
	private String name;
	
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

	

}

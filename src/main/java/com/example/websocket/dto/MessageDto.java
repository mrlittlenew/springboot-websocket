package com.example.websocket.dto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3535114478267673273L;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final String TYPE_MSG="MSG";//普通信息
	public static final String TYPE_ONLINE="ONLINE";//上下线广播信息
	public static final String TYPE_SYS="SYS";//系统通知广播信息
	public static final String TYPE_USER="USER";//更新用户信息
	
	
	private String type;
	private String text;
	private UserDto sender;
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	private Date sendTime = new Date();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}
	
	public String getHtmlText() {
		if(text==null){
			return null;
		}
		String htmlText = text.replace("\n", "<br/>");
		return htmlText;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UserDto getSender() {
		return sender;
	}

	public void setSender(UserDto sender) {
		this.sender = sender;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	
	public static MessageDto build(WebSocketMessage<?> webSocketMessage) {
		String msgTxt = webSocketMessage.getPayload().toString();
		System.out.println("MessageDto build TextMessageJson:"+msgTxt);
		ObjectMapper mapper = new ObjectMapper();
		MessageDto message=null;
		try {
			message= mapper.readValue(msgTxt, MessageDto.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}

	public static MessageDto buildSystemInfo(String msgTxt) {
		UserDto system=new UserDto();
		system.setName("System");
		
		MessageDto message = new MessageDto();
		message.setType(TYPE_SYS);
		message.setText(msgTxt);
		message.setSender(system);
		return message;
	}

	
	public static MessageDto build(UserDto sender,String type, String msgTxt) {
		MessageDto message = new MessageDto();
		message.setType(type);
		message.setText(msgTxt);
		message.setSender(sender);
		return message;
	}

	public TextMessage buildTextMessage() {
		ObjectMapper mapper = new ObjectMapper();
		String json="";
		try {
			json = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		logger.debug("getTextMessageJson:"+json);
		TextMessage textMessage = new TextMessage(json);
		return textMessage;
	}

}

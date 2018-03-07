package com.example.websocket.sockjs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.websocket.dto.MessageDto;
import com.example.websocket.dto.UserDto;

public class MyHandler implements WebSocketHandler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static CopyOnWriteArraySet<UserDto> userSet = new CopyOnWriteArraySet<UserDto>();
	private static CopyOnWriteArraySet<MessageDto> messageHistorySet = new CopyOnWriteArraySet<MessageDto>();
	private static int onlineCount=0;
	 

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.debug("afterConnectionEstablished");
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		logger.debug("handleMessage");
		UserDto sender = findOnlineUser(session);
		MessageDto messageDto = MessageDto.build(message);
		switch (messageDto.getType()) {
		case MessageDto.TYPE_MSG:
			// 加入消息利是
			messageDto.setSender(sender);
			messageHistorySet.add(messageDto);
			sendMessage(messageDto);
			break;
		case MessageDto.TYPE_USER:
			handleUserLogin(session, messageDto.getSender());
			break;
		}
		
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.debug("handleTransportError");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		logger.debug("afterConnectionClosed");
		UserDto sender = findOnlineUser(session);
		//移除关闭的session
		List<WebSocketSession> sessionList=cleanUserSessionList(sender);
		//如果user下没有可用的连接，即offline
		if(sessionList.size()==0){
			subOnlineCount();
			updateOnlineCount();
			MessageDto offlineInfo = MessageDto.buildSystemInfo(sender.getName()+ "Offline!");
			sendMessage(offlineInfo);
		}
	}
	
	@Override
	public boolean supportsPartialMessages() {
		logger.debug("supportsPartialMessages");
		return false;
	}
	

	private void handleUserLogin(WebSocketSession session, UserDto userDto) {
		MessageDto messageDto=null;
		String uid = userDto.getUid();
		UserDto userlogin = null;
		// 查找已经存在uid user
		for (UserDto user : userSet) {
			if (user.getUid().equals(uid)) {
				logger.debug("found user uid:" + uid);
				userlogin = user;
				// 设置新的名字
				if(!userDto.getName().equals(userlogin.getName())){
					//广播新名字
					messageDto = MessageDto.buildSystemInfo(userlogin.getName()+" 更改名字为: "+userDto.getName());
					
					userlogin.setName(userDto.getName());
				}
				
			}
		}

		// 如果没有，创建一个
		if (userlogin == null) {
			userlogin = new UserDto();
			userlogin.setUid(UUID.randomUUID().toString());
			userlogin.setName(userDto.getName());
			userSet.add(userlogin);
		}
		// 更新客户端用户信息
		updateUserInfo(session, userlogin);
		
		//移除关闭的session
		List<WebSocketSession> sessionList=cleanUserSessionList(userlogin);

		// 添加新的session
		boolean self=false;
		if (!sessionList.contains(session)) {
			sessionList.add(session);
			if(sessionList.size()==1){
				addOnlineCount();
			}else{
				self=true;
			}
			messageDto = MessageDto.buildSystemInfo(userlogin.getName()+" Online!");
		}
		
		//更新页面信息
		updateOnlineCount();
		LoadMessageHistory(session);

		if(messageDto!=null&&self){
			sendMessage(session,messageDto);
		}else if(messageDto!=null){
			sendMessage(messageDto);
		}

	}

	public List<WebSocketSession> cleanUserSessionList(UserDto user){
		List<WebSocketSession> sessionList=user.getSessionList();
		List<WebSocketSession> newList =new ArrayList<WebSocketSession>();
		for(WebSocketSession item:sessionList){
			if(item.isOpen()){
				newList.add(item);
			}
		}
		user.setSessionList(newList);
		return newList;
	}



	public void sendMessage(MessageDto messageDto) {
		for (UserDto user : userSet) {
			List<WebSocketSession> sessionList = user.getSessionList();
			for (WebSocketSession session : sessionList) {
				try {
					session.sendMessage(messageDto.buildTextMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendMessage(WebSocketSession session, MessageDto messageDto) {
		try {
			session.sendMessage(messageDto.buildTextMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void LoadMessageHistory(WebSocketSession session) {
		for (MessageDto item : messageHistorySet) {
			sendMessage(session, item);
		}

	}

	private void updateOnlineCount() {
		MessageDto onlineInfo = MessageDto.build(null, "ONLINE", getOnlineCount() + "");
		sendMessage(onlineInfo);
	}

	private UserDto findOnlineUser(WebSocketSession session) {
		for (UserDto user : userSet) {
			List<WebSocketSession> sessionList = user.getSessionList();
			if (sessionList.contains(session)) {
				return user;
			}
		}
		return null;
	}

	private void updateUserInfo(WebSocketSession session, UserDto userlogin) {
		MessageDto userInfo = MessageDto.build(userlogin, MessageDto.TYPE_USER, "");
		sendMessage(session, userInfo);
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}
	
	public static synchronized int addOnlineCount() {
		return onlineCount++;
	}
	public static synchronized int subOnlineCount() {
		return onlineCount--;
	}

}

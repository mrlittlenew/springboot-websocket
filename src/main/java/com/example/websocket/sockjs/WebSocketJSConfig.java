package com.example.websocket.sockjs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.websocket.repository.MessageRepository;
@Configuration
@EnableWebSocket
public class WebSocketJSConfig implements WebSocketConfigurer {
	
	@Autowired
	private MessageRepository messageRep;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    	registry.addHandler(myHandler(), "/webSocket").setAllowedOrigins("*");
    	registry.addHandler(myHandler(), "/webSocketSockJS").setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new MyHandler(messageRep);
    }

}
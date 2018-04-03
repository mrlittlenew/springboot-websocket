package com.example.websocket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.websocket.domain.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	public List<Message> findAllByOrderBySendTime();
}
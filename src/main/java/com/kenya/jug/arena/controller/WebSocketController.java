package com.kenya.jug.arena.controller;

import com.kenya.jug.arena.io.SocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    @MessageMapping("/message")
    @SendTo("/topic/lobby")
    public void processMessage(SocketMessage message) {
    }
}

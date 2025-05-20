package com.kenya.jug.arena;

import com.kenya.jug.arena.controller.WebSocketController;
import com.kenya.jug.arena.io.SocketMessage;
import com.kenya.jug.arena.io.UserRequest;
import com.kenya.jug.arena.service.AppUserDetailsService;
import com.kenya.jug.arena.service.UserServiceImpl;
import com.kenya.jug.arena.util.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class WebSocketIntegrationTest {
    @LocalServerPort
    private int port;

    @MockitoSpyBean
    private WebSocketController webSocketController;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    private static final WebSocketHttpHeaders webSocketHeaders = new WebSocketHttpHeaders();

    @BeforeAll
    static void globalSetup(
            @Autowired UserServiceImpl userService,
            @Autowired AppUserDetailsService appUserDetailsService,
            @Autowired JwtUtil jwtUtil
    ) {
        UserRequest userRequest = new UserRequest(
                "test",
                "user",
                "testuser@mail.com",
                "secret"
        );
        userService.createUser(userRequest);
        var userDetails = appUserDetailsService.loadUserByUsername(userRequest.getEmail());
        String jwtToken = jwtUtil.generateToken(userDetails);

        assertNotNull(jwtToken, "JWT token is null");
        assertTrue(jwtToken.length() > 0, "JWT token is empty");
        webSocketHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
    }

    @BeforeEach
    public void setUp() throws Exception {
        String url = "ws://localhost:" + port + "/api/v1.0/ws";
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(webSocketClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.stompSession = this.stompClient
                .connectAsync(url, webSocketHeaders, new StompSessionHandlerAdapter() {} , this.port)
                .get(10, TimeUnit.SECONDS);
    }

    @Test
    public void shouldConnect() {
        assertNotNull(stompSession, "STOMP session is null after connection");
        assertTrue(stompSession.isConnected(), "STOMP session is not connected");
    }

    @Test
    void shouldReceiveMessageInController() {
        ArgumentCaptor<SocketMessage> captor = ArgumentCaptor.forClass(SocketMessage.class);
        SocketMessage message = new SocketMessage("Hello world");
        stompSession.send("/app/message", message);

        await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(webSocketController, atLeastOnce()).processMessage(captor.capture())
                );

        verify(webSocketController, atLeastOnce()).processMessage(captor.capture());
        SocketMessage received = captor.getValue();
        assertEquals(
                message.getContent(),
                received.getContent(),
                "Wanted: " + message.getContent() + ", but got: " + received.getContent()
        );
    }

    @Test
    void shouldDisconnect() {
        stompSession.disconnect();

        await()
                .atMost(1, TimeUnit.SECONDS)
                .until(() -> !stompSession.isConnected());

        assertFalse(stompSession.isConnected(), "STOMP session is still connected");
    }

    @Test
    void shouldThrowExceptionWhenNotAuthorized() {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
        String url = "ws://localhost:" + port + "/api/v1.0/ws";

        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        assertThrows(Exception.class, () -> {
            stompClient
                    .connectAsync(url, headers, new StompSessionHandlerAdapter() {}, this.port)
                    .get(10, TimeUnit.SECONDS);
        }, "Expected exception not thrown");
    }
}
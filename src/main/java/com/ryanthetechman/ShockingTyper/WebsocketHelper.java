package com.ryanthetechman.ShockingTyper;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class WebsocketHelper extends WebSocketServer {
    public ClientConnectCallback onClientConnectCallback = new ClientConnectCallback();
    public ClientDisconnectCallback onClientDisconnectCallback = new ClientDisconnectCallback();
    public MessageReceivedCallback onMessageReceivedCallback = new MessageReceivedCallback();
    public ErrorCallback onErrorCallback = new ErrorCallback();
    public StartCallback onStartCallback = new StartCallback();

    private boolean debugMode = false;

    public WebsocketHelper(int port) {
        super(new InetSocketAddress(port));
    }

    public WebsocketHelper(int port, boolean debugMode) {
        super(new InetSocketAddress(port));
        this.debugMode = debugMode;
    }

    /**
     * Sends A message to the specified client
     * @param client
     * @param message
     */
    public void sendMessage(@NotNull WebSocket client, @NotNull String message){
        client.send(message);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        onClientConnectCallback.dispatch(conn, handshake);
        if (debugMode) System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        //conn.send("Hello!");
        broadcast("New Connection: " + handshake.getResourceDescriptor());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        onClientDisconnectCallback.dispatch(conn, code, reason, remote);
        if (debugMode) System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        broadcast("Closed Connection: " + conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        onMessageReceivedCallback.dispatch(conn, message);
        if (debugMode) System.out.println("Message from client: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        onErrorCallback.dispatch(conn, e);
        e.printStackTrace();
        if (conn != null) {
            //System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        }
    }

    @Override
    public void onStart() {
        onStartCallback.dispatch();
        if (debugMode) System.out.println("Starting Websocket");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}

interface MessageReceivedListener{void dispatch(WebSocket client, String message);}
class MessageReceivedCallback{
    private final List<MessageReceivedListener> listeners = new ArrayList<>();
    public void addListener(MessageReceivedListener listener) {listeners.add(listener);}

    void dispatch(WebSocket client, String message){
        for(MessageReceivedListener listener : listeners){
            listener.dispatch(client, message);
        }
    }
}

interface ClientConnectListener{void dispatch(WebSocket client, ClientHandshake handshake);}
class ClientConnectCallback{
    private final List<ClientConnectListener> listeners = new ArrayList<>();
    public void addListener(ClientConnectListener listener) {listeners.add(listener);}

    void dispatch(WebSocket client, ClientHandshake handshake){
        for(ClientConnectListener listener : listeners){
            listener.dispatch(client, handshake);
        }
    }
}

interface ClientDisconnectListener{void dispatch(WebSocket client, int code, String reason, boolean remote);}
class ClientDisconnectCallback{
    private final List<ClientDisconnectListener> listeners = new ArrayList<>();
    public void addListener(ClientDisconnectListener listener) {listeners.add(listener);}

    void dispatch(WebSocket client, int code, String reason, boolean remote){
        for(ClientDisconnectListener listener : listeners){
            listener.dispatch(client, code, reason, remote);
        }
    }
}

interface ErrorListener {void dispatch(@Nullable WebSocket client, Exception exception);}
class ErrorCallback{
    private final List<ErrorListener> listeners = new ArrayList<>();
    public void addListener(ErrorListener listener) {listeners.add(listener);}

    void dispatch(WebSocket client, Exception exception){
        for(ErrorListener listener : listeners){
            listener.dispatch(client, exception);
        }
    }
}

interface StartListener {void dispatch();}
class StartCallback {
    private final List<StartListener> listeners = new ArrayList<>();
    public void addListener(StartListener listener) {listeners.add(listener);}

    void dispatch(){
        for(StartListener listener : listeners){
            listener.dispatch();
        }
    }
}
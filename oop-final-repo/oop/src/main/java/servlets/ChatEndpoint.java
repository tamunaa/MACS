package servlets;


import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;


import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataBase.*;
import objects.messages.Message;
import objects.messages.NoteMessage;

@ServerEndpoint(value = "/messages.jsp", configurator = ChatEndpoint.ChatEndpointConfigurator.class)
public class ChatEndpoint {

    public static class MessageObject{

        private String type;
        private String recipient;
        private String message;
        private String sender;
        public String getType() {
            return type;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getMessage() {
            return message;
        }

        public String getSender(){
            return sender;
        }

    }

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    public static class ChatEndpointConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
            super.modifyHandshake(config, request, response);
            HttpSession httpSession = (HttpSession) request.getHttpSession();


            MessageDAO messageDAO = (MessageDAO) httpSession.getServletContext().getAttribute("messageDAO");
            UserDAO userDAO = (UserDAO) httpSession.getServletContext().getAttribute("userDAO");

            ChatEndpoint.setDAOS(messageDAO, userDAO);
        }

    }


    public static MessageDAO messageDAO;
    public static UserDAO userDAO;
    public static void setDAOS(MessageDAO messageDAO, UserDAO userDAO) {
        ChatEndpoint.messageDAO = messageDAO;
        ChatEndpoint.userDAO = userDAO;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        sessions.add(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Gson gson = new Gson();
        MessageObject messageObject = gson.fromJson(message, MessageObject.class);

        String messageType = messageObject.getType();

        if ("message".equals(messageType)) {
            String recipient = messageObject.getRecipient();
            String messageText = messageObject.getMessage();
            String sender = messageObject.getSender();

            int senderId = userDAO.getIDByUsername(sender);
            int recipientId = userDAO.getIDByUsername(recipient);


            Message newMessage = new NoteMessage(senderId, recipientId, messageText);
            messageDAO.addMessage(newMessage);

            for (Session s : sessions) {
                s.getBasicRemote().sendText(messageText);
            }
        }
    }




    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }
}

package northwind.websocket.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class AddedProductHandler extends TextWebSocketHandler{
	
	private final Map<String, WebSocketSession> idToActiveSession = new ConcurrentHashMap<>();
	


	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		idToActiveSession.put(session.getId(), session);
		System.out.println(String.format("Connection to client with sessionId=%s,TextMessageSizeLimit=%d,RemoteUrl=%s established.",
				session.getId(),session.getTextMessageSizeLimit(),session.getRemoteAddress()));		
	}
	
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
		System.out.println(String.format("Product received from server with sessionId=%s,product=%s",session.getId(),message.getPayload()));
		
		
    }
    
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		idToActiveSession.remove(session.getId());
		System.out.println(String.format("Connection to client with sessionId=%s closed.",session.getId()));
		
	}
}

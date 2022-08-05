package northwind.websocket.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import northwind.service.ProductService;

@Component
public class AddedProductDispatcher extends TextWebSocketHandler{
	
	private final Map<String, WebSocketSession> idToActiveSession = new ConcurrentHashMap<>();
	private final Map<String, String> sessionIdToProductId = new ConcurrentHashMap<>();
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ScheduledExecutorService  executorService;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		idToActiveSession.put(session.getId(), session);
		sessionIdToProductId.put(session.getId(), Integer.toString(1));
		System.out.println(String.format("Connection to client with sessionId=%s,principal=%s,TextMessageSizeLimit=%d,RemoteUrl=%s established.",
				session.getId(),session.getPrincipal().getName(),session.getTextMessageSizeLimit(),session.getRemoteAddress()));

		HttpHeaders headers = session.getHandshakeHeaders();
		for(var entry:headers.entrySet()) {
			System.out.println(entry.getKey()+"="+entry.getValue());
		}
		
		executorService.scheduleAtFixedRate(()->{
			idToActiveSession.forEach((sessionId,webSocketSession)->{
				int productId = new Random().nextInt(75) + 1;
				String productJson = productService.getProductWithId(Integer.toString(productId));
				try {
					webSocketSession.sendMessage(new TextMessage(productJson));
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			});
		}, 0, 5, TimeUnit.SECONDS);
		
	}
	
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
		String productJson = productService.getProductWithId(Integer.toString(1));
		session.sendMessage(new TextMessage(productJson));
		System.out.println(String.format("Product sent to client with sessionId=%s,product=%s",session.getId(),productJson));
    }
    
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		idToActiveSession.remove(session.getId());
		sessionIdToProductId.remove(session.getId());
		System.out.println(String.format("Connection to client with sessionId=%s,principal=%s closed.",session.getId(),session.getPrincipal().getName()));
	}
}

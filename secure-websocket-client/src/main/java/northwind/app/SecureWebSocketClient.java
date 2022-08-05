package northwind.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import northwind.config.AppConfig;
import northwind.ssl.client.WebSocketClient;


@SpringBootApplication
@Import({AppConfig.class})
public class SecureWebSocketClient  implements ApplicationRunner {

	@Autowired
	private WebSocketClient webSocketClient;
	
	public static void main(String[] args) {
		SpringApplication.run(SecureWebSocketClient.class, args);
		System.err.println("##########SecureWebSocketClient########");
		
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		webSocketClient.request("wss://server:8443/products-stream-ws");
		
	}

}

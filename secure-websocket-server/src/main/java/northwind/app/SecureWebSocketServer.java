package northwind.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import northwind.config.AppConfig;
import northwind.config.WebSocketConfig;


@SpringBootApplication
@Import({AppConfig.class,WebSocketConfig.class})
public class SecureWebSocketServer  extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    	return application.sources(SecureWebSocketServer.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(SecureWebSocketServer.class, args);
		System.err.println("##########SecureWebSocketServer#######");
		
	}

}

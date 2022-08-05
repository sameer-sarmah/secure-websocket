package northwind.ssl.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import northwind.exception.CoreException;
import northwind.util.KeyStoreUtil;
import northwind.websocket.handler.AddedProductHandler;

@Component
public class WebSocketClient {

	@Value("${server.ssl.key-store}")
	private String keystoreFile;
	@Value("${server.ssl.key-store-password}")
	private String keystorePwd;
	@Value("${server.ssl.key-password}")
	private String keyPwd;
	@Value("${server.ssl.key-store-type}")
	private String keyStoreType;
	@Autowired
	private KeyStoreUtil keyStoreUtil;

	final static Logger logger = Logger.getLogger(WebSocketClient.class);

	public void request(String url) throws CoreException {

		try {
			KeyStore keystore = keyStoreUtil.readStore();
			List<String> publicKeys = new ArrayList<String>();
			publicKeys.add("client");
			publicKeys.add("server");
			analyseKeystore(keystore,publicKeys,"client");
			SSLContext sslContext = SSLContexts.custom()
												.loadKeyMaterial(keystore, keyPwd.toCharArray())
												.loadTrustMaterial(keystore, new TrustSelfSignedStrategy())
												.build();
		
			StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
			webSocketClient.getUserProperties().clear();
			webSocketClient.getUserProperties().put("org.apache.tomcat.websocket.SSL_CONTEXT", sslContext);

			WebSocketHandler webSocketHandler = new AddedProductHandler();
			ListenableFuture<WebSocketSession> webSocketSessionFuture = webSocketClient.doHandshake(webSocketHandler, new WebSocketHttpHeaders(), URI.create(url));
			WebSocketSession webSocketSession = webSocketSessionFuture.get();

		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
			throw new CoreException(e.getMessage(), 500);

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new CoreException(e.getMessage(), 500);
		} catch (KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new CoreException(e.getMessage(), 500);
		} catch (Exception e) {
			throw new CoreException(e.getMessage(), 500);
		}
	}
	
	public static void analyseKeystore(KeyStore keyStore,List<String> publicKeys,String privateKeyName) {
		try {
			System.out.println(String.format("Size of keystore: %s, type of keystore: %s ",keyStore.size(),keyStore.getType()));
			publicKeys.stream().forEach((publicKey) ->{
				try {
					Certificate clientCertificate = keyStore.getCertificate(publicKey);
					analyseCertificate(clientCertificate);
				} catch (KeyStoreException e) {
					e.printStackTrace();
				}	
			});
			Key privateKey = keyStore.getKey(privateKeyName, "password".toCharArray());
			System.out.println(String.format("algorithm : %s,format : %s",privateKey.getAlgorithm(),privateKey.getFormat()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void analyseCertificate(Certificate certificate) {
		PublicKey serverPublicKey = certificate.getPublicKey();
		System.out.println(String.format("algorithm : %s,format : %s",serverPublicKey.getAlgorithm(),serverPublicKey.getFormat()));
		try {
			certificate.verify(serverPublicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			e.printStackTrace();
		}
	}
}



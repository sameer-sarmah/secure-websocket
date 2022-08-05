package northwind.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import northwind.client.ApacheHttpClient;
import northwind.exception.CoreException;
import northwind.util.HttpMethod;

@Component
public class ProductService {

	public String getProductWithId(String productId) {
		String url = "https://services.odata.org/Northwind/Northwind.svc/Products";
		String jsonResponse = "";
		Map<String, String> queryParams = new HashMap<String, String>();
		queryParams.put("$format", "json");
		queryParams.put("$filter", "ProductID eq "+productId);
		try {
			ApacheHttpClient httpClient = new ApacheHttpClient();
			jsonResponse = httpClient.request(url, HttpMethod.GET, Collections.<String, String>emptyMap(), queryParams,
					null);
			JsonObject response = new Gson().fromJson(jsonResponse, JsonObject.class);
			return  response.get("value").toString();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}

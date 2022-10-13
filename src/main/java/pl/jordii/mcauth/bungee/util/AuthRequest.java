package pl.jordii.mcauth.bungee.util;

import com.google.gson.Gson;
import pl.jordii.mcauth.bungee.config.AuthProxyConfig;
import pl.jordii.mcauth.common.rest.PremiumAccountCheckResponse;
import pl.jordii.mcauth.common.database.Callback;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

@Singleton
public class AuthRequest {

    private final ExecutorService executorService;
    private final AuthProxyConfig authConfig;

    @Inject
    public AuthRequest(ExecutorService executorService, AuthProxyConfig authConfig) {
        this.executorService = executorService;
        this.authConfig = authConfig;
    }

    public void request(String name, Callback<PremiumAccountCheckResponse> callback) {
        this.executorService.execute(() -> {
            try {
                URL url = new URL("https://api.kamcio96.pl/profile/");
                String parameters = "?auth=" + authConfig.getApiKey() + "&name=" + name;

                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet getRequest = new HttpGet(url + parameters);
                CloseableHttpResponse response = httpClient.execute(getRequest);

                Scanner scanner = new Scanner(response.getEntity().getContent());
                StringBuilder responseBuilder = new StringBuilder();

                while (scanner.hasNext()) {
                    responseBuilder.append(scanner.nextLine());
                }

                PremiumAccountCheckResponse authResponse = new Gson().fromJson(responseBuilder.toString(), PremiumAccountCheckResponse.class);
                authResponse.setResponseCode(response.getStatusLine().getStatusCode());

                callback.accept(authResponse);

                response.close();
                httpClient.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

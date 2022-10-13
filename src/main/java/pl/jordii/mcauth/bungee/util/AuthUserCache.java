package pl.jordii.mcauth.bungee.util;

import com.google.common.collect.Maps;
import pl.jordii.mcauth.common.rest.PremiumAccountCheckResponse;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class AuthUserCache {

    private final Map<String, PremiumAccountCheckResponse> firstConnection = Maps.newHashMap();

    public void handshake(String nickname, PremiumAccountCheckResponse apiResponse) {
        firstConnection.put(nickname, apiResponse);
    }

    public PremiumAccountCheckResponse getInfo(String nickname) {
        return firstConnection.get(nickname);
    }

    public void forget(String nickname) {
        firstConnection.remove(nickname);
    }

}

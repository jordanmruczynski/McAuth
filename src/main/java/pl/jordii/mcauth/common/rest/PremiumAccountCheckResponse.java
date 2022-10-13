package pl.jordii.mcauth.common.rest;

import com.google.gson.annotations.SerializedName;
import pl.jordii.mcauth.common.util.UUIDFormatter;

import java.util.UUID;

public class PremiumAccountCheckResponse {

    @SerializedName(value = "id")
    private String rawUUID;

    @SerializedName(value = "name")
    private String playerName;

    @SerializedName(value = "premium")
    private boolean premium;

    private int responseCode;
    private UUID playerUUID;

    public UUID getPlayerUuid() {
        if (this.playerUUID == null) {
            this.playerUUID = UUIDFormatter.interpolateDashes(this.rawUUID);
            return this.playerUUID;
        }
        return this.playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isPremium() {
        return premium;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

}

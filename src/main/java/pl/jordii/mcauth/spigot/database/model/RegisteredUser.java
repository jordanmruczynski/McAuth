package pl.jordii.mcauth.spigot.database.model;

import java.util.UUID;

public class RegisteredUser {

    private UUID uuid;
    private String name;
    private boolean premium;
    private String hashedPassword;
    private String salt;

    public RegisteredUser(UUID uuid, String name, boolean premium, String salt, String hashedPassword) {
        this.uuid = uuid;
        this.name = name;
        this.premium = premium;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}

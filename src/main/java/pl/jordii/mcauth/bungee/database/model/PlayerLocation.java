package pl.jordii.mcauth.bungee.database.model;

public class PlayerLocation {

    private final String logoutType;
    private final double sectorX;
    private final double sectorZ;

    public PlayerLocation(String logoutType, double sectorX, double sectorZ) {
        this.logoutType = logoutType;
        this.sectorX = sectorX;
        this.sectorZ = sectorZ;
    }

    public String getLogoutType() {
        return logoutType;
    }

    public double getSectorX() {
        return sectorX;
    }

    public double getSectorZ() {
        return sectorZ;
    }

}

package pl.jordii.mcauth.bungee.database.model;

public class SimpleSector {

    private final int id;
    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;

    public SimpleSector(int id, int minX, int maxX, int minZ, int maxZ) {
        this.id = id;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public int getId() {
        return id;
    }

    public boolean contains(double x, double z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

}

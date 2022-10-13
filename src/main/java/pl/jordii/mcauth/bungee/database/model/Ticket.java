package pl.jordii.mcauth.bungee.database.model;

public class Ticket {

    private int id;
    private String name;
    private String createDate;
    private String admin;
    private int cooldown;

    public Ticket(String name, String createDate, String admin, int cooldown) {
        this.name = name;
        this.createDate = createDate;
        this.admin = admin;
        this.cooldown = cooldown;
    }

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String create_date) {
        this.createDate = create_date;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}

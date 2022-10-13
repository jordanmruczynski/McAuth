package pl.jordii.mcauth.common.database;

public interface DatabaseConnection {

    boolean connect();

    void disconnect();

    boolean isConnected();

}

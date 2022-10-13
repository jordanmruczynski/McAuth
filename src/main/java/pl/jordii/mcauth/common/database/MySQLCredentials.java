package pl.jordii.mcauth.common.database;

public abstract class MySQLCredentials {

    public abstract String getHost();

    public abstract int getPort() ;

    public abstract String getDatabase();

    public abstract String getPassword();

    public abstract String getUsername();

}

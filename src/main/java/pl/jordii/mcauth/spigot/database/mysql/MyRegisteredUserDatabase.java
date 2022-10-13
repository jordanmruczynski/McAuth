package pl.jordii.mcauth.spigot.database.mysql;

import pl.jordii.mcauth.common.database.MySQL;
import pl.jordii.mcauth.spigot.database.dao.RegisteredUserDatabase;
import pl.jordii.mcauth.spigot.database.model.RegisteredUser;
import pl.jordii.mcauth.common.database.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@Singleton
public class MyRegisteredUserDatabase extends RegisteredUserDatabase {

    private final MySQL mySQL;
    private PreparedStatement loadUserStatement;
    private PreparedStatement createUserStatement;
    private PreparedStatement deleteUserStatement;

    @Inject
    public MyRegisteredUserDatabase(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    @Override
    public void prepare() throws SQLException {
        this.mySQL.asyncQueryUpdate("CREATE TABLE IF NOT EXISTS registered_users (" +
                "uuid VARCHAR(40) UNIQUE NOT NULL PRIMARY KEY, " +
                "name VARCHAR(20) NOT NULL, " +
                "premium INT NOT NULL, " +
                "salt VARCHAR(255), " +
                "password_hash TEXT NOT NULL" +
                ")");

        this.loadUserStatement = this.mySQL.prepareStatement(
                "SELECT name, premium, salt, password_hash FROM registered_users WHERE uuid = ?"
        );

        this.createUserStatement = this.mySQL.prepareStatement(
                "INSERT INTO registered_users (uuid, name, premium, salt, password_hash) VALUES (?, ?, ?, ?, ?)"
        );

        this.deleteUserStatement = this.mySQL.prepareStatement(
                "DELETE FROM registered_users WHERE name = ?"
        );
    }

    @Override
    public void loadUser(UUID uuid, Callback<RegisteredUser> callback) {
        try {
            this.loadUserStatement.setString(1, uuid.toString());
            this.mySQL.asyncQuery(this.loadUserStatement, userResult -> {
                try {
                    if (!userResult.next()) {
                        callback.accept(null);
                        return;
                    }

                    String name = userResult.getString("name");
                    boolean premium = userResult.getInt("premium") == 1;
                    String hashedPassword = userResult.getString("password_hash");
                    String salt = userResult.getString("salt");

                    callback.accept(new RegisteredUser(uuid, name, premium, salt, hashedPassword));
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void registerUser(RegisteredUser registeredUser) {
        try {
            this.createUserStatement.setString(1, registeredUser.getUuid().toString());
            this.createUserStatement.setString(2, registeredUser.getName());
            this.createUserStatement.setInt(3, registeredUser.isPremium() ? 1 : 0);
            this.createUserStatement.setString(4, registeredUser.getSalt());
            this.createUserStatement.setString(5, registeredUser.getHashedPassword());
            this.mySQL.asyncQueryUpdate(this.createUserStatement);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void resetUser(String userName) {
        try {
            this.deleteUserStatement.setString(1, userName);
            this.mySQL.asyncQueryUpdate(this.deleteUserStatement);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}

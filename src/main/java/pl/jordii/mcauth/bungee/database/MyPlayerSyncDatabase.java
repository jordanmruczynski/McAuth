package pl.jordii.mcauth.bungee.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import pl.jordii.mcauth.bungee.database.model.PlayerLocation;
import pl.jordii.mcauth.common.database.Callback;
import pl.jordii.mcauth.common.database.MySQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Singleton
public class MyPlayerSyncDatabase extends PlayerSyncDatabase {

    private final MySQL mySQL;
    private PreparedStatement playerLogoutTypeStatement;

    @Inject
    public MyPlayerSyncDatabase(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    @Override
    public void prepare() throws SQLException {
        this.playerLogoutTypeStatement = this.mySQL.prepareStatement(
                "SELECT logout_type, sector_logout_x, sector_logout_z FROM player_sync WHERE uuid = ?"
        );
    }

    @Override
    public void getPlayerLogoutType(String uuid, Callback<PlayerLocation> callback) {
        try {

            this.playerLogoutTypeStatement.setString(1, uuid);
            this.mySQL.asyncQuery(this.playerLogoutTypeStatement, result -> {
                try {
                    if (!result.next()) {
                        callback.accept(new PlayerLocation("channel", 0, 0));
                        return;
                    }

                    String logoutType = result.getString("logout_type");
                    double sectorLogoutX = result.getDouble("sector_logout_x");
                    double sectorLogoutZ = result.getDouble("sector_logout_z");
                    callback.accept(new PlayerLocation(logoutType, sectorLogoutX, sectorLogoutZ));
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            });

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

}

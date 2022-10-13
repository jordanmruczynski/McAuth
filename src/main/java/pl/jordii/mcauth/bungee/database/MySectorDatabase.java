package pl.jordii.mcauth.bungee.database;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.bungee.database.model.SimpleSector;
import pl.jordii.mcauth.common.database.MySQL;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

@Singleton
public class MySectorDatabase extends SectorDatabase {

    private final MySQL mySQL;
    private McAuthBungee plugin;

    private final List<SimpleSector> sectors = Lists.newArrayList();

    @Inject
    public MySectorDatabase(MySQL mySQL, McAuthBungee plugin) {
        this.mySQL = mySQL;
        this.plugin = plugin;
    }

    @Override
    public void prepare() throws SQLException {
        this.mySQL.asyncQuery("SELECT * FROM sectors", result -> {
            try {
                while (result.next()) {
                    int id = result.getInt("id");
                    int minX = result.getInt("minX");
                    int minZ = result.getInt("minZ");
                    int maxX = result.getInt("maxX");
                    int maxZ = result.getInt("maxZ");

                    sectors.add(new SimpleSector(id, minX, maxX, minZ, maxZ));
                }

                if (sectors.isEmpty()) {
                    plugin.getLogger().log(Level.SEVERE, "Cannot get sectors from database! Authentication system won't be fully functional as it could be, but it is not a problem for you.");
                }

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public List<SimpleSector> getSectors() {
        return this.sectors;
    }

}

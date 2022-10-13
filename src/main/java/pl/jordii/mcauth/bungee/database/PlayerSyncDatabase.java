package pl.jordii.mcauth.bungee.database;

import pl.jordii.mcauth.bungee.database.model.PlayerLocation;
import pl.jordii.mcauth.common.database.Callback;
import pl.jordii.mcauth.common.database.DataAccessObject;

public abstract class PlayerSyncDatabase implements DataAccessObject {

    public abstract void getPlayerLogoutType(String uuid, Callback<PlayerLocation> callback);

}

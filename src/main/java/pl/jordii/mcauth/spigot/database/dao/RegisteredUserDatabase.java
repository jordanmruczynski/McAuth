package pl.jordii.mcauth.spigot.database.dao;

import pl.jordii.mcauth.common.database.DataAccessObject;
import pl.jordii.mcauth.spigot.database.model.RegisteredUser;
import pl.jordii.mcauth.common.database.Callback;

import java.util.UUID;

public abstract class RegisteredUserDatabase implements DataAccessObject {

    public abstract void loadUser(UUID uuid, Callback<RegisteredUser> callback);

    public abstract void registerUser(RegisteredUser registeredUser);

    public abstract void resetUser(String userName);

}

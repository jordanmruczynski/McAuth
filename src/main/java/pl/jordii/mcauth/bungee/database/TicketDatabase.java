package pl.jordii.mcauth.bungee.database;

import pl.jordii.mcauth.bungee.database.model.Ticket;
import pl.jordii.mcauth.common.database.Callback;
import pl.jordii.mcauth.common.database.DataAccessObject;

import java.util.Collection;

public abstract class TicketDatabase implements DataAccessObject {
    public abstract void loadUser(String paramString, Callback<Ticket> callback);

    public abstract void registerUser(Ticket user);

    public abstract void resetUser(String name);

    public abstract void isExists(String paramString, Callback<Boolean> callback);

    public abstract void getUsers(Callback<Collection<Ticket>> tickets);

    public abstract void loadTickets();
}

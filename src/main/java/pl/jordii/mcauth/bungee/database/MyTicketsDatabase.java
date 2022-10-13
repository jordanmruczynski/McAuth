package pl.jordii.mcauth.bungee.database;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import pl.jordii.mcauth.bungee.database.model.Ticket;
import pl.jordii.mcauth.common.database.Callback;
import pl.jordii.mcauth.common.database.MySQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyTicketsDatabase extends TicketDatabase {
    private final MySQL mySQL;
    private PreparedStatement loadUserStatement;
    private PreparedStatement loadAllUsersStatement;
    private PreparedStatement createUserStatement;
    private PreparedStatement deleteUserStatement;

    private static final Map<String, Ticket> ticketCache = Maps.newHashMap();

    @Inject
    public MyTicketsDatabase(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    public void prepare() throws SQLException {
        this.mySQL.asyncQueryUpdate("CREATE TABLE IF NOT EXISTS ticket_users (name VARCHAR(40) NOT NULL UNIQUE PRIMARY KEY," +
                "create_date VARCHAR(32), " +
                "admin VARCHAR(32), " +
                "cooldown INT" +
                ")");

        this.loadUserStatement = this.mySQL.prepareStatement("SELECT name FROM ticket_users WHERE name = ?");

        this.loadAllUsersStatement = this.mySQL.prepareStatement("SELECT * FROM ticket_users");

        this.createUserStatement = this.mySQL.prepareStatement("INSERT INTO ticket_users (name, create_date, admin, cooldown) VALUES (?,?,?,?)");

        this.deleteUserStatement = this.mySQL.prepareStatement("DELETE FROM ticket_users WHERE name = ?");
    }

    public void loadUser(String name, Callback<Ticket> callback) {
        if (ticketCache.containsKey(name)) {
            callback.accept(ticketCache.get(name));
            return;
        }
        try {
            this.loadUserStatement.setString(1, name);
            this.mySQL.asyncQuery(this.loadUserStatement, userResult -> {
                try {
                    if (!userResult.next()) {
                        callback.accept(null);
                        return;
                    }
                    String createDate = userResult.getString("create_date");
                    String admin = userResult.getString("admin");
                    int cooldown = userResult.getInt("cooldown");
                    callback.accept(new Ticket(name, createDate, admin, cooldown));
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void isExists(String name, Callback<Boolean> callback) {
        if (ticketCache.containsKey(name)) {
            callback.accept(true);
        } else if (!ticketCache.containsKey(name)) {
            callback.accept(false);
        } else {
            try {
                this.loadUserStatement.setString(1, name);
                this.mySQL.asyncQuery(this.loadUserStatement, userResult -> {
                    try {
                        if (userResult.next()) {
                            callback.accept(true);
                        } else {
                            callback.accept(false);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerUser(Ticket ticket) {
        try {
            this.createUserStatement.setString(1, ticket.getName());
            this.createUserStatement.setString(2, ticket.getCreateDate());
            this.createUserStatement.setString(3, ticket.getAdmin());
            this.createUserStatement.setInt(4, ticket.getCooldown());
            this.mySQL.asyncQueryUpdate(this.createUserStatement);
            ticketCache.put(ticket.getName(), ticket);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void resetUser(String userName) {
        try {
            this.deleteUserStatement.setString(1, userName);
            this.mySQL.asyncQueryUpdate(this.deleteUserStatement);
            ticketCache.remove(userName);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void getUsers(Callback<Collection<Ticket>> tickets) {
        if (!ticketCache.isEmpty()) {
            tickets.accept(ticketCache.values());
            return;
        }
        this.mySQL.asyncQuery(loadAllUsersStatement, result -> {
            try {
                Collection<Ticket> output = Sets.newHashSet();
                while (result.next()) {
                    String name = result.getString("name");
                    String createDate = result.getString("create_date");
                    String admin = result.getString("admin");
                    int cooldown = result.getInt("cooldown");
                    Ticket ticket = new Ticket(name, createDate, admin, cooldown);
                    output.add(ticket);
                    ticketCache.put(name, ticket);
                }
                tickets.accept(output);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void loadTickets() {
        this.getUsers(result -> {
            for (Ticket ticket : result) {
                ticketCache.put(ticket.getName(), ticket);
            }
        });
    }

}

package pl.jordii.mcauth.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import pl.jordii.mcauth.bungee.McAuthBungee;
import pl.jordii.mcauth.bungee.config.AuthMessagesBungee;
import pl.jordii.mcauth.bungee.config.AuthMessagesManager;
import pl.jordii.mcauth.bungee.database.TicketDatabase;
import pl.jordii.mcauth.bungee.database.model.Ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class McAuthCommand extends Command {

    public McAuthCommand() {
        super("mcauth", "mcauthbungee.ticketadd", new String[0]);
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("ticket")) {
                String nickname = args[2];
                if (args[1].equalsIgnoreCase("add")) {
                    McAuthBungee.getInstance(TicketDatabase.class).isExists(nickname, loadResult -> {
                        if (loadResult) {
                            sender.sendMessage(AuthMessagesManager.sendMessage(AuthMessagesBungee.TICKETUSEREXISTS));
                        } else {
                            LocalDateTime dateTime = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                            McAuthBungee.getInstance(TicketDatabase.class).registerUser(new Ticket(nickname, dateTime.format(formatter), sender.getName(), 0));
                            sender.sendMessage(new TextComponent(" "));
                            sender.sendMessage(AuthMessagesManager.sendMessage(AuthMessagesBungee.GIVETICKET));
                            sender.sendMessage(new TextComponent(" "));
                        }
                    });
                    return;
                }
                if (args[1].equalsIgnoreCase("remove")) {;
                    McAuthBungee.getInstance(TicketDatabase.class).isExists(nickname, loadResult -> {
                        if (!loadResult) {
                            sender.sendMessage(AuthMessagesManager.sendMessage(AuthMessagesBungee.TICKETUSERDOESNTEXISTS));
                        } else {
                            McAuthBungee.getInstance(TicketDatabase.class).resetUser(nickname);
                            sender.sendMessage(new TextComponent(" "));
                            sender.sendMessage(AuthMessagesManager.sendMessage(AuthMessagesBungee.REMOVETICKET));
                            sender.sendMessage(new TextComponent(" "));
                        }
                    });
                    return;
                }
                if (args[1].equalsIgnoreCase("check")) {
                    sender.sendMessage(new TextComponent(" "));
                    McAuthBungee.getInstance(TicketDatabase.class).isExists(nickname, user -> {
                        if (user) {
                            McAuthBungee.getInstance(TicketDatabase.class).loadUser(nickname, result -> {
                                sender.sendMessage(new TextComponent(" "));
                                sender.sendMessage(new TextComponent("§c" + nickname + "§c's ticket info:"));
                                sender.sendMessage(new TextComponent("§cAdded §6" + result.getCreateDate() + " §cby §6" + result.getAdmin()));
                                sender.sendMessage(new TextComponent(" "));
                            });
                        }
                    });
                    //sender.sendMessage(new TextComponent("§7This part of software is still under development.. §8~Jordii"));
                    //sender.sendMessage(new TextComponent(" "));
                }
            }
        } else {
            sender.sendMessage(new TextComponent(" "));
            sender.sendMessage(new TextComponent("§cCorrectly command usage:"));
            sender.sendMessage(new TextComponent("§c§o  -> /mcauth ticket <add/remove/check> <nick>"));
            sender.sendMessage(new TextComponent("§c< > are required §8| §c( ) are optional"));
            sender.sendMessage(new TextComponent(" "));
        }
    }
}


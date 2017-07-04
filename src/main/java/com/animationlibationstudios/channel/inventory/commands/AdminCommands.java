package com.animationlibationstudios.channel.inventory.commands;

import com.animationlibationstudios.channel.inventory.commands.utility.CommandArgumentParserUtil;
import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import com.animationlibationstudios.channel.inventory.persist.RoomStorePersister;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.message.MessageDecoration;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Process !!admin commands.
 */
@Service
public class AdminCommands implements CommandExecutor {

    @Autowired
    private RoomStorePersister storage;

    @Autowired
    private CommandArgumentParserUtil commandArgumentParserUtil;

    @Command(aliases = {"!!admin", "!!a"},
            description = "!!admin - With no parameters, tells you who the current room admin is.\n" +
                    "!!admin set - Set yourself as the admin. Only works if there is no current admin.\n" +
                    "!!admin set <user_mention> - Set 'user' as the room's admin.\n")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        Server server = message.getChannelReceiver().getServer();
        String serverName = server.getName();
        Channel channel = message.getChannelReceiver();
        Room room = RoomStore.DataStore.get(serverName, channel.getName());
        String returnMessage = String.format("There is no room associated with channel #%s.  To create one, type !!room add <name>", channel.getName());

        User requestor = message.getAuthor();
        User target = null;

        if (message.getMentions() == null || message.getMentions().isEmpty()) {
            // error
            returnMessage = "You must @mention a user to assign the inventory admin role to.";
        } else if (message.getMentions().size() > 1) {
            // error
            returnMessage = "You must @mention only one user to assign the inventory admin role to.";
        } else {
            target = message.getMentions().get(0);
        }

        // Start by loading the server file if we need to, and if we can.
        commandArgumentParserUtil.checkAndRead(serverName);

        if (room != null) {
            if (args.length > 0 && "set".equalsIgnoreCase(args[0])) {
                if (room.getRoomAdmin() == null) {
                    if (target == null) {
                        returnMessage = setRoomAdmin(
                                server,
                                room,
                                requestor,
                                requestor);
                    } else {
                        returnMessage = setRoomAdmin(
                                server,
                                room,
                                requestor,
                                target);
                    }
                } else {
                    if (room.getRoomAdmin().equals(requestor)) {
                        if (target == null) {
                            returnMessage = "You are already the room admin.";
                        } else {
                            returnMessage = setRoomAdmin(
                                    server,
                                    room,
                                    requestor,
                                    target);
                        }
                    }
                }
            } else {
                if (room.getRoomAdmin() == null) {
                    returnMessage = "There is no room admin set.  Use !!admin set to assign the room admin.";
                } else {
                    returnMessage = String.format("The current room admin is '%s'.",
                            room.getRoomAdmin().getName());
                }
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();

    }

    /**
     * Do the work of setting the room admin.
     *
     * @param server The server object that the room is in.
     * @param room The room object that the shop is in.
     * @param requestor The person who requested the change.
     * @param target The person to set as the new admin.
     * @return message string.
     */
    private String setRoomAdmin(Server server, Room room, User requestor, User target) {
        String returnMessage;
        room.setRoomAdmin(target);
        returnMessage = String.format(
                "%s is now the room admin.", target.getNickname(server));
        target.sendMessage(
            String.format("You have been set as the room inventory admin for the '%s' server's " +
                    "'%s' channel.  This means you will receive private messages for certain " +
                    "inventory events.", server.getName(), room.getChannel())
        );

        try {
            storage.writeServer(server.getName());
        } catch (IOException e) {
            returnMessage = String.format("Error writing %s data to file. \n %s",
                    server.getName(), e.getMessage());
        }
        return returnMessage;
    }
}

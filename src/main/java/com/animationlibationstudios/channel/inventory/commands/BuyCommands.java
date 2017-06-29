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
 * Process !!buy commands.
 */
@Service
public class BuyCommands implements CommandExecutor {

    @Autowired
    private RoomStorePersister storage;

    @Autowired
    private CommandArgumentParserUtil commandArgumentParserUtil;

    @Command(aliases = {"!!buy", "!!b"},
            description = "!!buy <item>\n")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        Server server = message.getChannelReceiver().getServer();
        String serverName = server.getName();
        Channel channel = message.getChannelReceiver();
        Room room = RoomStore.DataStore.get(serverName, channel.getName());
        String returnMessage = String.format("There is no room associated with channel #%s.  To create one, type !!room add <name>", channel.getName());

        User requestor = message.getAuthor();
        User target = null;

        // Start by loading the server file if we need to, and if we can.
        commandArgumentParserUtil.checkAndRead(serverName);

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();
    }
}

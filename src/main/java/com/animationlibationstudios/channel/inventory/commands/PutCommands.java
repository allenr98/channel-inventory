package com.animationlibationstudios.channel.inventory.commands;

import com.animationlibationstudios.channel.inventory.commands.utility.CommandArgumentParserUtil;
import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.model.enumeration.Preposition;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.message.MessageDecoration;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * Process !!put commands.
 */
public class PutCommands implements CommandExecutor {

    @Autowired
    CommandArgumentParserUtil commandArgumentParserUtil;

    @Command(aliases = {"!!put"},
            description = "!!put <item> - Add 'item' to the current room.\n" +
                    "!!put <item> -q # - Add # items to the current room.\n") // +
//                    "!!put <item1> (on, in, under, behind) <item2> - Put 'item1' in/on/under/behind 'item2'.\n" +
//                    "!!put <item> (on, in, under, behind) <item2> -q # - Put # 'item1's in/on/under/behind 'item2'.\n")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        Channel channel = message.getChannelReceiver();

        Room room = RoomStore.DataStore.get(server, channel.getName());
        String returnMessage = String.format("There is no room associated with channel #%s.  To create one, type !!room add <name>", channel.getName());

        if (room != null) {
            PutCommands.PutCmd putCmd = new PutCommands.PutCmd(args);

            if (args.length == 0) {
                returnMessage = "";
            } else {
                returnMessage = "Invalid command.  Type !!help look for assistance with look commands.";
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();

    }

    /**
     * Class representing the parsed command submitted.
     */
    private class PutCmd {
        String commandType;
        Preposition preposition;
        String item;

        PutCmd(String[] args) {
            // Parse out and validate the operation
            commandType = "invalid";
            preposition = null;

            if (args.length == 1) {
                // !!put <item> command.
                commandType = "item";
                item = args[0];
                preposition = null;
            } else if (args.length > 1) {
                // Since there is no command "!!put <preposition> item" because a put needs a target, if the first
                // argument is a preposition, it's probably an error.
                if (commandArgumentParserUtil.isPreposition(args[0])) {
                    // !!look <preposition> <item>
                    commandType = "preposition-item";
                    preposition = Preposition.valueOf(args[0].toUpperCase());

                    // assume the rest of the line is the item name
                    item = commandArgumentParserUtil.parseItemName(Arrays.copyOfRange(args,1,args.length));
                } else {
                    commandType = "item";
                    item = commandArgumentParserUtil.parseItemName(args);
                }
            }
        }
    }

}

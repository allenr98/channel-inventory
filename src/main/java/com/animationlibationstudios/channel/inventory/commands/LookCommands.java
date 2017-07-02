package com.animationlibationstudios.channel.inventory.commands;

import com.animationlibationstudios.channel.inventory.commands.utility.CommandArgumentParserUtil;
import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.model.Thing;
import com.animationlibationstudios.channel.inventory.model.enumeration.Preposition;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.message.MessageDecoration;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;

/**
 * Process !!look commands.
 */
@Service
public class LookCommands implements CommandExecutor {

    // TODO: Add code to include NPCs to the look commands.
    // TODO: Allow !!look item commands to recurse into sublists of things, or as an alternative,
    //       implement "!!look item (in, on, behind, under) item" form of the command.

    @Autowired
    private CommandArgumentParserUtil commandArgumentParserUtil;

    @Command(aliases = {"!!look"},
            description = "!!look - What do I see if I just stand in the room and look around?\n" +
                    "!!look <item> - Get a description of an item and a list of anything visible on top of it.\n")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        Channel channel = message.getChannelReceiver();

        // Start by loading the server file if we need to, and if we can.
        commandArgumentParserUtil.checkAndRead(server);

        Room room = RoomStore.DataStore.get(server, channel.getName());
        String returnMessage = String.format("There is no room associated with channel #%s.  To create one, type !!room add <name>", channel.getName());

        // TODO: how would one "look item" at a thing on, in, under, or behind another thing?

        if (room != null) {
            LookCmd lookCmd = new LookCmd(args);

            if (args.length == 0) {
                returnMessage = buildLookResponse(room, channel);
            } else if (lookCmd.commandType.equals("item")) {
                returnMessage = buildLookItemResponse(room, lookCmd.item); // buildLookItemResponseRecursively(room, lookCmd.item);
            } else {
                returnMessage = "Invalid command.  Type !!help look for assistance with look commands.";
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();
    }

    /**
     * Once it's been determined that the command received was "!!look [item]", build the response message.
     *
     * @param room The current room.
     * @param item the item name.
     * @return Response message.
     */
    private String buildLookItemResponse(Room room, String item) {
        String returnMessage = String.format("Looking at the %s:\n", item);

        // find that item
        Thing theThing = null;
        for (Thing thing: room.getThings()) {
            if (item.equalsIgnoreCase(thing.getName())) {
                theThing = thing;
                break;
            }
        }

        if (theThing == null) {
            returnMessage = String.format("There is no %s in this room.", item);
        } else {
            returnMessage += String.format("%s\n", theThing.getDescription());
        }
        return returnMessage;
    }

    /**
     * Once it's been determined that the command received was "!!look" with no other parameters, build the response
     * message.
     *
     * @param room The current room.
     * @return Response message.
     */
    private String buildLookResponse(Room room, Channel channel) {
        String returnMessage;

        // Embeds: https://anidiotsguide.gitbooks.io/discord-js-bot-guide/examples/using-embeds-in-messages.html
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(room.getName());
        embedBuilder.setColor(new Color(0xB63D32));
        embedBuilder.setDescription(room.getDescription());

        if (null == room.getThings() || room.getThings().isEmpty()) {
            returnMessage = String.format("Room '%s' has nothing in it.", room.getName());
        } else {
//            StringBuilder builder = new StringBuilder();
//            builder.append(String.format("**Room %s contains the following:**\n", room.getName()));

            StringBuilder itemText = new StringBuilder();
            for (Thing thing : room.getThings()) {
                String qty = thing.getQuantity() > 1 ? String.format(" (%d)", thing.getQuantity()) : "";
                String price = thing.getPrice().isEmpty() ? "" : String.format(" [%s]", thing.getPrice());
//                builder.append(String.format("- %s%s%s\n", thing.getName(), qty, price));
                itemText.append(String.format("- %s%s%s\n", thing.getName(), qty, price));
            }
            embedBuilder.addField("Items in Room:", itemText.toString(), false);
            returnMessage = null;
        }

        if (returnMessage == null) {
            channel.sendMessage(null, embedBuilder);
        }

        return returnMessage;
    }

    /**
     * Class representing the parsed command submitted.
     */
    private class LookCmd {
        String commandType;
        Preposition preposition;
        String item;

        LookCmd(String[] args) {
            // Parse out and validate the operation
            commandType = "invalid";
            preposition = null;

            if (args.length == 1) {
                // !!look <item> command.
                commandType = "item";
                item = args[0];
                preposition = null;
            } else if (args.length > 1) {
                commandType = "item";
                item = commandArgumentParserUtil.parseItemName(args);
            }
        }

        @Override
        public String toString() {
            return "LookCmd {" +
                        "commandType='" + commandType + '\'' +
                        "preposition='" + preposition.name().toLowerCase() + '\'' +
                        ", item='" + item + '\'' +
                    '}';
        }
    }
}

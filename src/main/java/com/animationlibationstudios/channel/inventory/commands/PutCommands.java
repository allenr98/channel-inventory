package com.animationlibationstudios.channel.inventory.commands;

import com.animationlibationstudios.channel.inventory.commands.utility.CommandArgumentParserUtil;
import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.model.Thing;
import com.animationlibationstudios.channel.inventory.model.enumeration.Preposition;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import com.animationlibationstudios.channel.inventory.persist.RoomStorePersister;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
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
 * Process !!put commands.
 */
@Service
public class PutCommands implements CommandExecutor {

    @Autowired
    private RoomStorePersister storage;

    @Autowired
    private CommandArgumentParserUtil commandArgumentParserUtil;

    @Command(aliases = {"!!put"},
            description = "!!put <item> - Add 'item' to the current room.\n" +
//                    "!!put <item1> (on, in, under, behind) <item2> - Put 'item1' in/on/under/behind 'item2'.\n" +
//                    "!!put <item> (on, in, under, behind) <item2> -q # - Put # 'item1's in/on/under/behind 'item2'.\n" +
                    "Arguments (must appear after the item name but otherwise can be in any order):\n" +
                    "  -q # - Add quantity # items to the current room (0 means remove all items).\n" +
                    "  -p <price> - Add price (where <price is a free-format string) to the item.\n" +
                    "  -d <description> - Add a brief description to the item.\n")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        Channel channel = message.getChannelReceiver();

        // Start by loading the server file if we need to, and if we can.
        commandArgumentParserUtil.checkAndRead(server);

        Room room = RoomStore.DataStore.get(server, channel.getName());
        String returnMessage = String.format("There is no room associated with channel #%s.  To create one, type !!room add <name>", channel.getName());

        if (room != null) {
            PutCmd putCmd = new PutCmd(args);

            if (putCmd.commandType.equals("item")) {
                Thing thing = new Thing();

                thing.setName(putCmd.item);
                thing.setQuantity(putCmd.quantity);
                thing.setDescription(putCmd.description);
                thing.setPrice(putCmd.price);

                returnMessage = putThing(server, room, thing);
            } else {
                if (putCmd.commandType.equals("invalidArgPlacement")) {
                    returnMessage = "Invalid command.  An item name must appear before any arguments (like '-d' or '-q').";
                } else if (putCmd.commandType.equals("invalidQty")) {
                    returnMessage = "Invalid quantity specified.  '-q' must be followed by a valid, non-negative " +
                            "integer number (0 will remove all of the specified item).";
                } else {
                    returnMessage = "Invalid command.  Type `!!help put` for assistance with look commands.";
                }
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();

    }

    /**
     *
     */
    private String putThing(String server, Room room, Thing thing) {
        String returnMessage;

        // First see if there are already things in the room.
        boolean found = false;
        boolean removed = false;
        int newQuantity = 0;
        for (Thing roomThing: room.getThings()) {
            if (roomThing.getName().equalsIgnoreCase(thing.getName())) {
                found = true;

                // Zero essentially means to remove all the things from the room. (Set quantity to 0.)
                if (thing.getQuantity() == 0) {
                    room.getThings().remove(roomThing);
                    removed = true;
                } else {
                    roomThing.setQuantity(roomThing.getQuantity() + thing.getQuantity());
                    newQuantity = roomThing.getQuantity();
                }

                break;
            }
        }

        if (!found) {
            room.getThings().add(thing);
            returnMessage = "Added a new '" + thing.getName() + "' to the room.";
        } else {
            if (removed) {
                returnMessage = String.format("Removed all %s(s) from the room.", thing.getName());
            } else {
                returnMessage = String.format("Added %d more %s(s) to the room for a new total of %d.",
                        thing.getQuantity(), thing.getName(), newQuantity);
            }
        }

        // Whenever we update the inventory data, write the contents to a file.
        try {
            storage.writeServer(server);
        } catch (IOException e) {
            returnMessage = String.format("Error occurred while attempting to write %s server contents to storage; message: %s",
                    server, e.getMessage());
        }

        return returnMessage;
    }

    /**
     * Class representing the parsed command submitted.
     */
    private class PutCmd {
        String commandType;
        Preposition preposition;
        String item;
        String description;
        int quantity;
        String price;

        PutCmd(String[] args) {
            // Parse out and validate the operation
            commandType = "invalid";
            preposition = null;

            if (args.length == 1) {
                // !!put <item> command.
                commandType = "item";
                item = args[0];
                preposition = null;
                description = commandArgumentParserUtil.parseArgument("-d", args);
            } else if (args.length > 1) {
                // If the first argument is "-q" or a" -p" it's an error - need to provide the item first.
                if ("-q".equalsIgnoreCase(args[0]) || "-p".equalsIgnoreCase(args[0]) || "-d".equalsIgnoreCase(args[0])) {
                    commandType = "invalidArgPlacement";
                } else {
                    commandType = "item";
                    item = commandArgumentParserUtil.parseItemName(args);
                    description = commandArgumentParserUtil.parseArgument("-d", args);
                    price = commandArgumentParserUtil.parseArgument("-p", args);
                }

                // Now check and see if we find a quantity parameter
                boolean hasAQuantity = false;
                int qty = 1;
                for (String arg: args) {
                    if ("-q".equalsIgnoreCase(arg)) {
                        hasAQuantity = true;
                    } else if (hasAQuantity) {
                        // the next item had better be a well-formatted, non-negative integer number
                        try {
                            qty = Integer.parseInt(arg);
                            if (qty < 0) {
                                throw new NumberFormatException();
                            }
                            break;
                        } catch (NumberFormatException e) {
                            commandType = "invalidQty";
                        }
                    }
                }
                quantity = qty;
            }
        }
    }
}

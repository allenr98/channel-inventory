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
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Process !!put commands.
 */
@Service
public class PutCommands implements CommandExecutor {

    @Autowired
    private CommandArgumentParserUtil commandArgumentParserUtil;

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

            if (putCmd.commandType.equals("item")) {
                Thing thing = new Thing();

                thing.setName(putCmd.item);
                thing.setQuantity(putCmd.quantity);
                thing.setDescription(putCmd.description);

                returnMessage = putThing(room, thing);
            } else {
                if (putCmd.commandType.equals("invalidQtyPlacement")) {
                    returnMessage = "Invalid command.  An item name must appear before '-q'.";
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
    private String putThing(Room room, Thing thing) {
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
            returnMessage = "Added a new '" + thing.getName() + "' to the room.";
        } else {
            if (removed) {
                returnMessage = String.format("Removed all %s(s) from the room.", thing.getName());
            } else {
                returnMessage = String.format("Added %d more %s(s) to the room for a new total of %d.",
                        thing.getQuantity(), thing.getName(), newQuantity);
            }
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

        PutCmd(String[] args) {
            // Parse out and validate the operation
            commandType = "invalid";
            preposition = null;

            if (args.length == 1) {
                // !!put <item> command.
                commandType = "item";
                item = args[0];
                preposition = null;
                description = commandArgumentParserUtil.parseDescription(args);
            } else if (args.length > 1) {
                // If the first argument is "-q" it's an error - need to provide the item first.
                if ("-q".equalsIgnoreCase(args[0])) {
                    commandType = "invalidQtyPlacement";
                } else {
                    commandType = "item";
                    item = commandArgumentParserUtil.parseItemName(args);
                    description = commandArgumentParserUtil.parseDescription(args);
                }

                // Now check and see if we find a quantity parameter
                boolean hasAQuantity = false;
                int qty = 1;
                for (String arg: args) {
                    if ("-q".equalsIgnoreCase(arg)) {
                        hasAQuantity = true;
                    } else if (hasAQuantity) {
                        // the next item had better be a well-formatted  non-negative integer number
                        try {
                            qty = Integer.parseInt(arg);
                            if (qty < 0) {
                                throw new NumberFormatException();
                            }
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

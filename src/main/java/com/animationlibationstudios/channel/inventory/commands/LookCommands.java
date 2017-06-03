package com.animationlibationstudios.channel.inventory.commands;

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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Process !!look commands.  Heads up: an item name that contains one of the valid prepositions will not work properly.
 * For example, !!look Under Garment will expect that you want to look under the item "Garment."  This can be resolved
 * by...well...spelling it correctly, like "Undergarment".
 */
@Service
public class LookCommands implements CommandExecutor {

    @Command(aliases = {"!!look"},
            description = "!!look - What do I see if I just stand in the room and look around?\n" +
                    "!!look <item> - Get a description of an item and a list of anything visible on top of it.\n" +
                    "!!look (on, in, under, behind) <item> - See what's on, in, under, or behind the item.")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        Channel channel = message.getChannelReceiver();

        Room room = RoomStore.DataStore.get(server, channel.getName());
        String returnMessage = String.format("There is no room associated with channel #%s.  To create one, type !!room add <name>", channel.getName());

        // TODO: how would one "look item" at a thing on, in, under, or behind another thing?

        if (room != null) {
            LookCmd lookCmd = new LookCmd(args);

            if (args.length == 0) {
                returnMessage = buildLookResponse(room);
            } else if (lookCmd.commandType.equals("item")) {
                returnMessage = buildLookItemResponse(room, lookCmd.item);
            } else if (lookCmd.commandType.equals("preposition-item")) {
                returnMessage = buildLookPrepositionItemResponse(room, lookCmd);
            } else {
                returnMessage = "Invalid command.  Type !!help look for assistance with look commands.";
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();
    }

    /**
     * The most complex of the Look commands, this one needs the whole content of the command object to figure out
     * what to do.  Build a response message for the command format, "!!look [preposition] [item]".
     *
     * @param room The current room.
     * @param lookCmd The full command as received (after parsing).
     * @return Response message.
     */
    private String buildLookPrepositionItemResponse(Room room, LookCmd lookCmd) {
        String returnMessage = String.format("Looking %s the %s:\n", lookCmd.preposition, lookCmd.item);

        // find that item
        Thing theThing = null;
        for (Thing thing: room.getThings()) {
            if (lookCmd.item.equalsIgnoreCase(thing.getName())) {
                theThing = thing;
                break;
            }
        }

        if (theThing == null) {
            returnMessage = String.format("There is no %s to look %s in this room.", lookCmd.item, lookCmd.preposition.name().toLowerCase());
        } else {
            // List the things on the item being looked at
            StringBuilder builder = new StringBuilder();
            boolean foundThings = false;
            List<Thing> thingList = null;

            switch(lookCmd.preposition) {
                case ON:
                    thingList = theThing.getThingsOn();
                    break;
                case IN:
                    thingList = theThing.getThingsIn();
                    break;
                case UNDER:
                    thingList = theThing.getThingsUnder();
                    break;
                case BEHIND:
                    thingList = theThing.getThingsUnder();
                    break;
            }

            for (Thing thing: thingList) {
                if (!foundThings) {
                    builder.append(String.format("%s the %s you see:\n",
                            StringUtils.capitalize(lookCmd.preposition.name().toLowerCase()),
                            theThing.getName()));
                    foundThings = true;
                }
                builder.append(String.format("- %s\n", thing.getName()));
            }

            if (!foundThings) {
                returnMessage += String.format("There is nothing %s the %s.", lookCmd.preposition.name().toLowerCase(), theThing.getName());
            } else {
                returnMessage += builder.toString();
            }
        }
        return returnMessage;
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

            // Now list the things on the item being looked at
            StringBuilder builder = new StringBuilder();
            boolean foundThingsOn = false;
            for (Thing thing: theThing.getThingsOn()) {
                if (!foundThingsOn) {
                    builder.append(String.format("On top of the %s you see:\n", theThing.getName()));
                    foundThingsOn = true;
                }
                String qty = theThing.getQuantity() > 1 ? String.format(" (%d)", theThing.getQuantity()) : "";
                builder.append(String.format("- %s%s\n", thing.getName(), qty));
            }
            if (!foundThingsOn) {
                returnMessage += String.format("There is nothing on top of the %s.", theThing.getName());
            } else {
                returnMessage += builder.toString();
            }
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
    private String buildLookResponse(Room room) {
        String returnMessage;

        if (null == room.getThings() || room.getThings().isEmpty()) {
            returnMessage = String.format("Room '%s' has nothing in it.", room.getName());
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("**Room %s contains the following:**\n", room.getName()));

            for (Thing thing : room.getThings()) {
                String qty = thing.getQuantity() > 1 ? String.format(" (%d)", thing.getQuantity()) : "";
                builder.append(String.format("- %s%s\n", thing.getName(), qty));
            }

            returnMessage = builder.toString();
        }
        return returnMessage;
    }

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
                // This could mean many things - it could be a preposition and an item, or it could be no preposition
                // and a multi-word item name, or it could be a preposition and a multi-word item name.  We'll have to
                // deal with all possibilities.
                if (isPreposition(args[0])) {
                    // !!look <preposition> <item>
                    commandType = "preposition-item";
                    preposition = Preposition.valueOf(args[0].toUpperCase());

                    // assume the rest of the line is the item name
                    item = parseItemName(Arrays.copyOfRange(args,1,args.length));
                } else {
                    commandType = "item";
                    item = parseItemName(args);
                }
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

    /**
     * Check if the word passed in is in the preposition list.
     *
     * @param value The word to check.
     * @return True if it's a preposition.
     */
    private boolean isPreposition(String value) {
        boolean result;

        try {
            Preposition.valueOf(value.toUpperCase());
            result = true;
        } catch (IllegalArgumentException e) {
            result = false;
        }

        return result;
    }

    /**
     * Iterate through the words array and stop if we get to a preposition.
     *
     * @param words - array of strings from the command argument list.
     * @return String
     */
    private String parseItemName(String[] words) {
        StringBuilder builder = new StringBuilder();
        String space = "";
        for (String word: words) {
            if (isPreposition(word)) { break; }
            builder.append(space).append(word);
            if ("".equals(space)) { space = " "; };
        }

        return builder.toString();
    }
}

package com.animationlibationstudios.channel.inventory.commands;

import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.model.Thing;
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

import java.util.List;

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
        // TODO: pull prepositions into an enum so instead of "if-elseif-elsif-else" we can just use a switch statement.
        // TODO: pull interior code blocks out into methods to make the code more readable.
        // TODO: need to tweak the command builder to handle multi-word items (similar to how descriptions were handled in RoomCommands.
        // TODO: figure out why !!look Dust Bunny says "There is no Bunny to look invalid in this room."

        if (room != null) {
            LookCmd lookCmd = new LookCmd(args);

            if (args.length == 0) {
                if (null == room.getThings() || room.getThings().isEmpty()) {
                    returnMessage = String.format("Room '%s' has nothing in it.", room.getName());
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append(String.format("**Room %s contains the following:**\n", room.getName()));

                    for (Thing thing : room.getThings()) {
                        builder.append(String.format("\t%s\n", thing.getName()));
                    }

                    returnMessage = builder.toString();
                }
            } else if (lookCmd.commandType.equals("item")) {
                returnMessage = String.format("Looking at the %s:\n", lookCmd.item);

                // find that item
                Thing theThing = null;
                for (Thing thing: room.getThings()) {
                    if (lookCmd.item.equalsIgnoreCase(thing.getName())) {
                        theThing = thing;
                        break;
                    }
                }

                if (theThing == null) {
                    returnMessage = String.format("There is no %s in this room.", lookCmd.item);
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
                        builder.append(String.format("- %s\n", thing.getName()));
                    }
                    if (!foundThingsOn) {
                        returnMessage += String.format("There is nothing on top of the %s.", theThing.getName());
                    } else {
                        returnMessage += builder.toString();
                    }
                }
            } else if (lookCmd.commandType.equals("preposition-item")) {
                returnMessage = String.format("Looking %s the %s:\n", lookCmd.preposition, lookCmd.item);

                // find that item
                Thing theThing = null;
                for (Thing thing: room.getThings()) {
                    if (lookCmd.item.equalsIgnoreCase(thing.getName())) {
                        theThing = thing;
                        break;
                    }
                }

                if (theThing == null) {
                    returnMessage = String.format("There is no %s to look %s in this room.", lookCmd.item, lookCmd.preposition);
                } else {
                    // List the things on the item being looked at
                    StringBuilder builder = new StringBuilder();
                    boolean foundThings = false;
                    List<Thing> thingList = null;
                    if (lookCmd.preposition.equalsIgnoreCase("on")) {
                        thingList = theThing.getThingsOn();
                    } else if (lookCmd.preposition.equalsIgnoreCase("in")) {
                        thingList = theThing.getThingsIn();
                    } else if (lookCmd.preposition.equalsIgnoreCase("under")) {
                        thingList = theThing.getThingsUnder();
                    } else /* behind */ {
                        thingList = theThing.getThingsBehind();
                    }
                    for (Thing thing: thingList) {
                        if (!foundThings) {
                            builder.append(String.format("%s the %s you see:\n",
                                    StringUtils.capitalize(lookCmd.preposition),
                                    theThing.getName()));
                            foundThings = true;
                        }
                        builder.append(String.format("- %s\n", thing.getName()));
                    }
                    if (!foundThings) {
                        returnMessage += String.format("There is nothing on top of the %s.", theThing.getName());
                    } else {
                        returnMessage += builder.toString();
                    }
                }
            } else {
                returnMessage = "Invalid command.  Type !!help look for assistance with look commands.";
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();
    }


    private class LookCmd {
        String commandType;
        String preposition;
        String item;

        private final String[] validPrepositions = {"on", "in", "under", "behind"};

        LookCmd(String[] args) {
            // Parse out and validate the operation
            commandType = "invalid";
            preposition = "invalid";

            if (args.length == 1) {
                // !!look <item> command.
                commandType = "item";
                item = args[0];
                preposition = null;
            } else if (args.length == 2) {
                // !!look <preposition> <item> command.
                commandType = "preposition-item";
                for (String c : validPrepositions) {
                    if (c.equalsIgnoreCase(args[0])) {
                        preposition = c;
                        break;
                    }

                    item = args[1];
                }
            }
        }

        @Override
        public String toString() {
            return "LookCmd {" +
                        "commandType='" + commandType + '\'' +
                        "preposition='" + preposition + '\'' +
                        ", item='" + item + '\'' +
                    '}';
        }
    }
}

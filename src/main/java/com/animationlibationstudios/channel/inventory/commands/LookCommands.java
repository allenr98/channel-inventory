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
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Process !!look commands.  Heads up: an item name that contains one of the valid prepositions will not work properly.
 * For example, !!look Under Garment will expect that you want to look under the item "Garment."  This can be resolved
 * by...well...spelling it correctly, like "Undergarment".
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
 // don't do this yet    "!!look (on, in, under, behind) <item> - See what's on, in, under, or behind the item.")
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
                returnMessage = buildLookResponse(room);
            } else if (lookCmd.commandType.equals("item")) {
                returnMessage = buildLookItemResponse(room, lookCmd.item); // buildLookItemResponseRecursively(room, lookCmd.item);
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

// Skip the preposition stuff for now.
//            // Now list the things on the item being looked at
//            StringBuilder builder = new StringBuilder();
//            boolean foundThingsOn = false;
//            for (Thing thing: theThing.getThingsOn()) {
//                if (!foundThingsOn) {
//                    builder.append(String.format("On top of the %s you see:\n", theThing.getName()));
//                    foundThingsOn = true;
//                }
//                String qty = theThing.getQuantity() > 1 ? String.format(" (%d)", theThing.getQuantity()) : "";
//                builder.append(String.format("- %s%s\n", thing.getName(), qty));
//            }
//            if (!foundThingsOn) {
//                returnMessage += String.format("There is nothing on top of the %s.", theThing.getName());
//            } else {
//                returnMessage += builder.toString();
//            }
        }
        return returnMessage;
    }

    private String buildLookItemResponseRecursively(Room room, String item) {
        // find that item(s)
        List<FoundThing> listOfThings = findAllTheThingsInTheRoom(item, room);
        String returnMessage = String.format("Looking at the %s:\n", item);

        if (listOfThings == null || listOfThings.isEmpty()) {
            returnMessage = String.format("There is no %s in this room.", item);
        } else {
            if (listOfThings.size() == 1) {
                returnMessage = String.format("Looking at the %s:\n", listOfThings.get(0).toString());
                returnMessage += String.format("%s\n", listOfThings.get(0).thing.getDescription());
            } else {
                StringBuilder builder = new StringBuilder(String.format("There are %d things in this room with that name!\n", listOfThings.size()));
                for (FoundThing thing : listOfThings) {
                    builder.append(String.format("\t- %s\n", thing.toString()));
                }
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
                String price = thing.getPrice().isEmpty() ? "" : String.format(" [%s]", thing.getPrice());
                builder.append(String.format("- %s%s%s\n", thing.getName(), qty, price));
            }

            returnMessage = builder.toString();
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
                // This could mean many things - it could be a preposition and an item, or it could be no preposition
                // and a multi-word item name, or it could be a preposition and a multi-word item name.  We'll have to
                // deal with all possibilities.

                /**
                 * Kludgey code warning: for now we're not allowing prepositions; assume if the args length is > 1,
                 * that's a multi word item description.
                 */
//                if (commandArgumentParserUtil.isPreposition(args[0])) {
//                    // !!look <preposition> <item>
//                    commandType = "preposition-item";
//                    preposition = Preposition.valueOf(args[0].toUpperCase());
//
//                    // assume the rest of the line is the item name
//                    item = commandArgumentParserUtil.parseItemName(Arrays.copyOfRange(args,1,args.length));
//                } else {
                    commandType = "item";
                    item = commandArgumentParserUtil.parseItemName(args);
//                }
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
     * When searching for things, we'll build a list of matching things we found and where we found
     * them.  If the location is null, the thing in question is just in the room.
     * If thing is found in a child of a child of a child, etc., the location can get quite long - like
     * "in the leather pouch in the small wooden box in the jewelry case in the safe behind the oil painting"
     */
    private class FoundThing {
        private Thing thing;
        private String location;

        FoundThing(Thing thing, String location) {
            this.thing = thing;
            this.location = location;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(thing.getName());
            if (!location.isEmpty()) {
                builder.append(String.format(" (%s)", location)).toString();
            }
            return builder.toString();
        }
    }

    /**
     * Look for a named thing in other things - all the other things.
     *
     * @param name The name of the thing to find.
     * @param location Where we've looked before (builds a string of prepositional phrases describing where the thing is located).
     * @param thing The thing we're looking to see if there are named things in/behind/under/on.
     * @return A list of things found in the thing.
     */
    private List<FoundThing> findTheThingsInTheThing(String name, String location, Thing thing) {
        List<FoundThing> result = new LinkedList<FoundThing>();

        // IN
        for (Thing thingInTheThing: thing.getThingsIn()) {
            String newLocation = String.format("in the %s %s ", thing.getName(), location);

            if (thingInTheThing.getThingsIn() != null && !thingInTheThing.getThingsIn().isEmpty()) {
                result.addAll(findTheThingsInTheThing(name, newLocation, thingInTheThing));
            }
            if (thingInTheThing.getName().equalsIgnoreCase(name)) {
                result.add(new FoundThing(thingInTheThing, newLocation));
            }
        }

        // ON
        for (Thing thingOnTheThing: thing.getThingsOn()) {
            String newLocation = String.format("on the %s %s ", thing.getName(), location);

            if (thingOnTheThing.getThingsOn() != null && !thingOnTheThing.getThingsOn().isEmpty()) {
                result.addAll(findTheThingsInTheThing(name, newLocation, thingOnTheThing));
            }
            if (thingOnTheThing.getName().equalsIgnoreCase(name)) {
                result.add(new FoundThing(thingOnTheThing, newLocation));
            }
        }

        // UNDER
        for (Thing thingUnderTheThing: thing.getThingsUnder()) {
            String newLocation = String.format("under the %s %s ", thing.getName(), location);

            if (thingUnderTheThing.getThingsOn() != null && !thingUnderTheThing.getThingsOn().isEmpty()) {
                result.addAll(findTheThingsInTheThing(name, newLocation, thingUnderTheThing));
            }
            if (thingUnderTheThing.getName().equalsIgnoreCase(name)) {
                result.add(new FoundThing(thingUnderTheThing, newLocation));
            }
        }

        // BEHIND
        for (Thing thingBehindTheThing: thing.getThingsBehind()) {
            String newLocation = String.format("behind the %s %s ", thing.getName(), location);

            if (thingBehindTheThing.getThingsOn() != null && !thingBehindTheThing.getThingsOn().isEmpty()) {
                result.addAll(findTheThingsInTheThing(name, newLocation, thingBehindTheThing));
            }
            if (thingBehindTheThing.getName().equalsIgnoreCase(name)) {
                result.add(new FoundThing(thingBehindTheThing, newLocation));
            }
        }

        return result;
    }

    /**
     * Given a name (as from a !!look command), search the entire inventory of the room for something that
     * matches.  Return a list of matches along with where it was (e.g. "in the cupboard").
     *
     * @param name The name of the thing to find.
     * @param room The room in which to look.
     * @return A list of all the places it was found.
     */
    private List<FoundThing> findAllTheThingsInTheRoom(String name, Room room) {
        List<FoundThing> result = new LinkedList<FoundThing>();
        String theThingIsInTheRoom = "";

        // In the room
        for (Thing roomThing: room.getThings()) {
            if (name.equalsIgnoreCase(roomThing.getName())) {
                result.add(new FoundThing(roomThing, theThingIsInTheRoom));
            }
            result.addAll(findTheThingsInTheThing(name, theThingIsInTheRoom, roomThing));
        }

        return result;
    }
}

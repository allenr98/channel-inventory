/*
 * Copyright (C) 2016 Bastian Oppermann
 * 
 * This file is part of my Javacord Discord bot.
 *
 * This bot is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser general Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package com.animationlibationstudios.channel.inventory.commands;

import com.animationlibationstudios.channel.inventory.commands.utility.CommandArgumentParserUtil;
import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.model.Thing;
import com.animationlibationstudios.channel.inventory.model.enumeration.RoomOperations;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import com.animationlibationstudios.channel.inventory.persist.RoomStorePersister;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.message.MessageDecoration;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;

/**
 * The room command.
 */
@Service
public class RoomCommands implements CommandExecutor {

    @Autowired
    private RoomStorePersister storage;

    @Autowired
    private CommandArgumentParserUtil commandArgumentParserUtil;

    @Command(aliases = {"!!room", "!!rm"},
            description = "!!room - What room am I in?\n" +
                    "!!room describe (desc, d) - get the room description.\n" +
                    "!!room describe -d <description> - replace the room's description.\n" +
                    "!!room remove (rem, r) - remove the room associated with the current channel.\n" +
                    "                           **Warning:** this will also remove all the room's inventory!\n" +
                    "!!room add <name> -d <description> - add the room with the specified name and description.")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        Channel channel = message.getChannelReceiver();

        // Start by loading the server file if we need to, and if we can.
        commandArgumentParserUtil.checkAndRead(server);

        Room room = RoomStore.DataStore.get(server, channel.getName());
        String returnMessage, defaultMessage = String.format("There is no room associated with channel #%s.  To create one, type !!room add <name>", channel.getName());

        if (args.length == 0) {
            returnMessage = buildRoomResponse(room, defaultMessage);
        } else {
            RoomCmd cmd = new RoomCmd(args);

            if (cmd.operation == RoomOperations.DESCRIBE
                    || cmd.operation == RoomOperations.DESC
                    || cmd.operation == RoomOperations.D) {
                returnMessage = buildRoomDescribeResponse(server, room, cmd, defaultMessage);
            } else if (cmd.operation == RoomOperations.REMOVE
                    || cmd.operation == RoomOperations.REM
                    || cmd.operation == RoomOperations.R) {
                returnMessage = buildRoomRemoveResponse(server, room, defaultMessage);
            } else if (cmd.operation == RoomOperations.ADD
                        || cmd.operation == RoomOperations.A) {
                returnMessage = buildRoomAddResponse(server, channel, room, cmd, args.length, defaultMessage);
            } else {
                returnMessage = "Invalid command.  Type !!help room for assistance with room commands.";
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();
    }

    /**
     * The command is "!!room remove", build the response.
     *
     * @param server The current server.
     * @param channel The current channel.
     * @param room The current room.
     * @param cmd The received command.
     * @param argCount The number of arguments in the argument array.
     * @param defaultMessage The message to return if no message can be built.
     * @return The response message.
     */
    private String buildRoomAddResponse(String server, Channel channel, Room room, RoomCmd cmd, int argCount, String defaultMessage) {
        String returnMessage = defaultMessage;
        if (room != null) {
            returnMessage = String.format(
                    "There is already a room named '%s' associated with this channel.  To remove it and " +
                        "everything in it, type !!room delete",
                    room.getName());
        } else {
            room = new Room();
            room.setChannel(channel.getName());
            room.setName(cmd.roomName);
            room.setThings(new LinkedList<Thing>());

            if (argCount >= 3) {
                room.setDescription(cmd.description);
            } else {
                room.setDescription("This room is nondescript.");
            }
            RoomStore.DataStore.putRoom(server, room);

            try {
                storage.writeServer(server);
                returnMessage = String.format("Room %s successfully added.", room.getName());
            } catch (IOException e) {
                returnMessage = String.format("Error writing %s data to file. \n %s", server, e.getMessage());
            }
        }
        return returnMessage;
    }

    /**
     * The command is "!!room remove", build the response.
     *
     * @param server The current server.
     * @param room The current room.
     * @param defaultMessage The message to return if no message can be built.
     * @return The response message.
     */
    private String buildRoomRemoveResponse(String server, Room room, String defaultMessage) {
        String returnMessage = defaultMessage;
        if (room != null) {
            RoomStore.DataStore.deleteRoom(server, room);
            try {
                storage.writeServer(server);
                returnMessage = String.format("Room: %s deleted.", room.getName());
            } catch (IOException e) {
                returnMessage = String.format("Error writing %s data to file. \n %s", server, e.getMessage());
            }
        }
        return returnMessage;
    }

    /**
     * Once it's been determined that the command received was "!!room describe", build the response message.
     *
     * @param room
     * @param cmd
     * @return
     */
    private String buildRoomDescribeResponse(String server, Room room, RoomCmd cmd, String defaultMessage) {
        String returnMessage = defaultMessage;
        boolean success = true;

        if (room != null) {
            if (cmd.description != null && !cmd.description.isEmpty()) {
                // we're replacing, not just retrieving.
                room.setDescription(cmd.description);

                // We changed the description, so let's persist it.
                try {
                    storage.writeServer(server);
                } catch (IOException e) {
                    returnMessage = String.format("Error writing %s data to file. \n %s", server, e.getMessage());
                    success = false;
                }
            }

            if (success) {
                returnMessage = String.format("Room: %s\nDescription:\n%s", room.getName(), room.getDescription());
            }
        }
        return returnMessage;
    }

    /**
     * The command was a simple "!!room", build the response.
     *
     * @param room The current room.
     * @param defaultMessage The message to return if no message can be built.
     * @return The response message.
     */
    private String buildRoomResponse(Room room, String defaultMessage) {
        String returnMessage = defaultMessage;
        if (room != null) {
            returnMessage = "Room: " + room.getName();
        }
        return returnMessage;
    }

    @Command(aliases = {"!!cmd"}, description = "!!cmd - echo back the command")
    public String echoBack(DiscordAPI api, String command, String[] args, Message message) {
        RoomCmd cmd = new RoomCmd(args);

        return new MessageBuilder().appendDecoration(cmd.toString(), MessageDecoration.CODE_LONG).toString();
    }

    private class RoomCmd {
        RoomOperations operation;
        String roomName;
        String description;

        RoomCmd(String[] args) {
            // Parse out and validate the operation
            operation = null;

            if (isOperation(args[0])) {
                operation = RoomOperations.valueOf(args[0]);

                // The next parameter, if there is one, should be the room name, but we want to allow it to be more than
                // one word with spaces.  Let's grab all the next words until we hit the end of the line, or a "-d" to
                // indicate we're about to switch to the description.
                boolean buildingRoomName = true;
                if (args.length > 1) {
                    StringBuilder roomSb = null;
                    StringBuilder descSb = null;
                    for (int i = 1; i < args.length; i++) {
                        String token = args[i];

                        if ("-d".equalsIgnoreCase(token)) {
                            buildingRoomName = false;
                        } else {
                            if (buildingRoomName) {
                                if (roomSb == null) {
                                    roomSb = new StringBuilder(token);
                                } else {
                                    roomSb.append(" ").append(token);
                                }
                            } else {
                                if (descSb == null) {
                                    descSb = new StringBuilder(token);
                                } else {
                                    descSb.append(" ").append(token);
                                }
                            }
                        }
                    }

                    if (roomSb != null) {
                        roomName = roomSb.toString();
                    }

                    if (descSb != null) {
                        description = descSb.toString();
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "RoomCmd {" +
                    "operation='" + operation.name().toLowerCase() + '\'' +
                    ", roomName='" + roomName + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    /**
     * Check if the word passed in is in the room operation list.
     *
     * @param value The word to check.
     * @return True if it's a valid operation.
     */
    private boolean isOperation(String value) {
        boolean result;

        try {
            RoomOperations.valueOf(value.toUpperCase());
            result = true;
        } catch (IllegalArgumentException e) {
            result = false;
        }

        return result;
    }

}

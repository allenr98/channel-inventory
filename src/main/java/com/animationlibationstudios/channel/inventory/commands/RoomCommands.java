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

import java.util.LinkedList;

/**
 * The chuck command.
 */
public class RoomCommands implements CommandExecutor {
    @Command(aliases = {"inv!room", "inv!rm"},
            description = "inv!room - What room am I in?\n" +
                    "inv!room describe (desc, d) - get the room description.\n" +
                    "inv!room describe -d <description> - replace the room's description.\n" +
                    "inv!room remove (rem, r) - remove the room associated with the current channel.\n" +
                    "                           **Warning:** this will also remove all the room's inventory!\n" +
                    "inv!room add <name> -d <description> - add the room with the specified name and description.")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        Channel channel = message.getChannelReceiver();

        Room room = RoomStore.DataStore.get(server, channel.getName());
        String returnMessage = String.format("There is no room associated with channel #%s.  To create one, type inv!addRoom <name>", channel.getName());

        if (args.length == 0) {
            if (room != null) {
                returnMessage = "Room: " + room.getName();
            }
        } else {
            RoomCmd cmd = new RoomCmd(args);

            if (cmd.operation.equalsIgnoreCase("describe")
                    || cmd.operation.equalsIgnoreCase("desc")
                    || cmd.operation.equalsIgnoreCase("d")) {
                if (room != null) {
                    if (cmd.description != null && !cmd.description.isEmpty()) {
                        // we're replacing, not just retrieving.
                        room.setDescription(cmd.description);
                    }

                    returnMessage = String.format("Room: %s\nDescription:\n%s", room.getName(), room.getDescription());
                }
            } else if (cmd.operation.equalsIgnoreCase("remove")
                    || cmd.operation.equalsIgnoreCase("rem")
                    || cmd.operation.equalsIgnoreCase("r")) {
                if (room != null) {
                    RoomStore.DataStore.deleteRoom(server, room);
                    returnMessage = String.format("Room: %s deleted.", room.getName());
                }
            } else {
                if (cmd.operation.equalsIgnoreCase("add")
                        || cmd.operation.equalsIgnoreCase("a")) {
                    if (room != null) {
                        returnMessage = String.format(
                                "There is already a room named '%s' associated with this channel.  To remove it and " +
                                    "everything in it, type inv!room delete",
                                room.getName());
                    } else {
                        room = new Room();
                        room.setChannel(channel.getName());
                        room.setName(cmd.roomName);
                        room.setThings(new LinkedList<Thing>());

                        if (args.length >= 3) {
                            room.setDescription(cmd.description);
                        } else {
                            room.setDescription("This room is nondescript.");
                        }
                        RoomStore.DataStore.putRoom(server, room);
                        returnMessage = String.format("Room %s successfully added.", room.getName());
                    }
                }
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();
    }

    @Command(aliases = {"inv!cmd"}, description = "inv!cmd - echo back the command")
    public String echoBack(DiscordAPI api, String command, String[] args, Message message) {
        RoomCmd cmd = new RoomCmd(args);

        return new MessageBuilder().appendDecoration(cmd.toString(), MessageDecoration.CODE_LONG).toString();
    }

    private class RoomCmd {
        String operation;
        String roomName;
        String description;

        private final String[] validCommands = {"add", "a", "remove", "rem", "r", "describe", "desc", "d"};

        RoomCmd(String[] args) {
            // Parse out and validate the operation
            operation = "invalid";

            for (String c: validCommands) {
                if (c.equalsIgnoreCase(args[0])) {
                    operation = c;
                    break;
                }
            }

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
                                descSb.append(" ").append(token);      }
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

        @Override
        public String toString() {
            return "RoomCmd {" +
                    "operation='" + operation + '\'' +
                    ", roomName='" + roomName + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}

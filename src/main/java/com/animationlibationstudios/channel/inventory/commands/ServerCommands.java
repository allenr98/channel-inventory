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
import com.animationlibationstudios.channel.inventory.persist.LocalFileRoomStorePersister;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import com.animationlibationstudios.channel.inventory.persist.RoomStorePersister;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.message.MessageDecoration;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

/**
 * The chuck command.
 */
@Service
public class ServerCommands implements CommandExecutor {

    @Autowired
    private RoomStorePersister storage;

    @PostConstruct
    public void checkAndSetPersister() {
        if (null == storage) {
            storage = new LocalFileRoomStorePersister();
        }
    }

    @Command(aliases = {"!!read"},
            description = "!!read - read server contents from storage. Everything added since the last inv!server write will be lost!")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        String returnMessage = String.format("Contents of server %s successfully read from storage.", server);

        try {
            HashMap<String, Room> serverContents = new HashMap<>();
            serverContents.putAll(storage.readServer(server));
            putServerContentsInRoomStore(server, serverContents);
        } catch (IOException e) {
            returnMessage = String.format("%s occurred while attempting to read %s server contents from storage; message: %s",
                    e.getClass().getName(), server, e.getMessage());
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();
    }

    @Command(aliases = {"!!save"},
            description = "!!save - write the server contents to storage; at some point in the future, this " +
                    "will happen automagically.")
    public String writeCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        String returnMessage = String.format("Contents of server %s successfully written to storage.", server);

        try {
            storage.writeServer(server);
        } catch (IOException e) {
            returnMessage = String.format("%s occurred while attempting to write %s server contents to storage; message: %s",
                    e.getClass().getName(), server, e.getMessage());
        }

        return new MessageBuilder().appendDecoration(returnMessage, MessageDecoration.CODE_LONG).toString();
    }

    private void putServerContentsInRoomStore(String server, HashMap<String, Room> contents) {
        if (null != server && !server.isEmpty() && null != contents && !contents.isEmpty()) {
            RoomStore.DataStore.putServer(server);
            for (String channel: contents.keySet()) {
                RoomStore.DataStore.putRoom(server, contents.get(channel));
            }
        }
    }
}

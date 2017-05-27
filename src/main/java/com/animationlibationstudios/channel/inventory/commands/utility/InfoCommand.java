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
package com.animationlibationstudios.channel.inventory.commands.utility;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;
import de.btobastian.javacord.entities.message.MessageDecoration;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * The info command.
 */
@Service
public class InfoCommand implements CommandExecutor {

    private static final String ROBBIEWAN_ID = "306101364176715777";

    private final long startTime = System.currentTimeMillis();

    @Command(aliases = {"!!info"}, description = "Shows information about the bot")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        MessageBuilder msgBuilder = new MessageBuilder();
        msgBuilder.appendDecoration("General information", MessageDecoration.BOLD);
        appendAuthor(msgBuilder, message);
        msgBuilder
                .appendNewLine().append("• Library: Javacord")
//                .appendNewLine().append("• GitHub: <https://github.com/BtoBastian/JavacordBot>")
                .appendNewLine().append("• Servers: " + api.getServers().size());
        appendUsers(msgBuilder, api);
        appendUptime(msgBuilder);
        return msgBuilder.toString();
    }

    /**
     * Appends the author of the bot.
     *
     * @param msgBuilder The message builder.
     * @param message The message.
     */
    private void appendAuthor(MessageBuilder msgBuilder, Message message) {
        msgBuilder.appendNewLine().append("• Author: ");
        if (!message.isPrivateMessage()
                && message.getChannelReceiver().getServer().getMemberById(ROBBIEWAN_ID) != null) {
            msgBuilder.appendUser(message.getChannelReceiver().getServer().getMemberById(ROBBIEWAN_ID));
        } else {
            msgBuilder.append("Master.Robbiewan");
        }
    }

    /**
     * Appends the amount of users.
     *
     * @param msgBuilder The message builder.
     * @param api The discord api.
     */
    private void appendUsers(MessageBuilder msgBuilder, DiscordAPI api) {
        int amount = 0;
        for (Server server : api.getServers()) {
            amount += server.getMemberCount();
        }
        msgBuilder.appendNewLine().append("• Users: " + amount)
                .append(" (cached: ").append(String.valueOf(api.getUsers().size())).append(")");
    }

    /**
     * Appends the uptime of the bot.
     *
     * @param msgBuilder The message builder.
     */
    private void appendUptime(MessageBuilder msgBuilder) {
        long millis = System.currentTimeMillis() - startTime;
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        msgBuilder
                .appendNewLine()
                .append("• Uptime: " )
                .append(days + " Days " + hours + " Hours " + minutes + " Minutes " + seconds + " Seconds");
    }
}

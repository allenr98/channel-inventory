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
                .appendNewLine().append("• Version: 0.1.1 (beta)")
                .appendNewLine().append("• Library: Javacord")
                .appendNewLine().append("• Servers: " + api.getServers().size())
                .appendNewLine().append("• Website: http://masterrobbiewan.wix.com/quartermaster")
                .appendNewLine().append("• GitHub: https://github.com/allenr98/channel-inventory")
        ;
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

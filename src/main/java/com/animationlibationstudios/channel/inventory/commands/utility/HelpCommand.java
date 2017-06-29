package com.animationlibationstudios.channel.inventory.commands.utility;

import com.animationlibationstudios.channel.inventory.commands.ApiCommandExecutor;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandHandler;
import org.springframework.stereotype.Service;

import java.awt.*;

/**
 * The help command.
 */
@Service
public class HelpCommand implements ApiCommandExecutor {

    private CommandHandler commandHandler;

    @Override
    public void setCommandHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Command(aliases = {"!!help", "!!commands"}, description = "Shows this page")
    public void onHelpCommand(Message message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("=== Commands ===");
        embed.setFooter("Powered by Javacord");
        embed.setColor(Color.GREEN);

        for (CommandHandler.SimpleCommand simpleCommand : commandHandler.getCommands()) {
            if (!simpleCommand.getCommandAnnotation().showInHelpPage()) {
                continue; // skip command
            }
            StringBuilder command = new StringBuilder();
            if (!simpleCommand.getCommandAnnotation().requiresMention()) {
                // the default prefix only works if the command does not require a mention
                command.append(commandHandler.getDefaultPrefix());
            }
            String usage = simpleCommand.getCommandAnnotation().usage();
            if (usage.isEmpty()) { // no usage provided, using the first alias
                usage = simpleCommand.getCommandAnnotation().aliases()[0];
            }
            command.append(usage);
            String description = simpleCommand.getCommandAnnotation().description();

            embed.addField(command.toString(), description, false);
        }
        message.reply("", embed);
    }

}

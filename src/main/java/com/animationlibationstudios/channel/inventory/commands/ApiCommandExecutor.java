package com.animationlibationstudios.channel.inventory.commands;

import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;

public interface ApiCommandExecutor extends CommandExecutor {

    void setCommandHandler(CommandHandler commandHandler);
}

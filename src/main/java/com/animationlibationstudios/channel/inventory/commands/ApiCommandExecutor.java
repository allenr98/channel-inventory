package com.animationlibationstudios.channel.inventory.commands;

import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;

/**
 * Any command processor classes that require a command handler should implement this interface.
 */
public interface ApiCommandExecutor extends CommandExecutor {

    void setCommandHandler(CommandHandler commandHandler);
}

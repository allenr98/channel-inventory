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

@Service
public class LookCommands implements CommandExecutor {

    @Command(aliases = {"!!look"},
            description = "!!look - What do I see if I just stand in the room and look around?")
    public String onCommand(DiscordAPI api, String command, String[] args, Message message) {
        String server = message.getChannelReceiver().getServer().getName();
        Channel channel = message.getChannelReceiver();

        Room room = RoomStore.DataStore.get(server, channel.getName());
        String returnMessage = String.format("There is no room associated with channel #%s.  To create one, type !!room add <name>", channel.getName());

        if (room != null) {
            if (null == room.getThings() || room.getThings().isEmpty()) {
                returnMessage = String.format("Room '%s' has nothing in it.", room.getName());
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append(String.format("**Room %s contains the following:**\n", room.getName()));

                for (Thing thing: room.getThings()) {
                    builder.append(String.format("\t%s\n", thing.getName()));
                }

                returnMessage = builder.toString();
            }
        }

        return new MessageBuilder().appendDecoration(returnMessage, null).toString();
    }
}

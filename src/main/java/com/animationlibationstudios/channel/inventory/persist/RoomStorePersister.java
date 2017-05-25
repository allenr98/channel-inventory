package com.animationlibationstudios.channel.inventory.persist;

import com.animationlibationstudios.channel.inventory.model.Room;

import java.io.IOException;
import java.util.Map;

/**
 * Utility class for outputting the contents of a specified server and reading it back in.
 */
public interface RoomStorePersister {

    /**
     * If there's a file along the default (or specified) file path that corresponds to the specified server name,
     * read it into a map of rooms and return the result.
     *
     * @param serverName Name of the Discord server to be read.
     * @return Map keyed by channel names containing the room object associated with the channel.  Only the channel/room
     *         entries for the specified server will be returned.
     * @throws IOException if something goes wrong with the file read.
     */
    Map<String, Room> readServer(String serverName) throws IOException;

    /**
     * Writes a json file for the specified server name.  If one already exists, it will be overwritten.
     *
     * @param serverName Name of the Discord server to be written.
     * @throws IOException if something goes wrong with the file write.
     */
    void writeServer(String serverName) throws IOException;
}

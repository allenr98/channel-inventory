package com.animationlibationstudios.channel.inventory.persist;

import com.animationlibationstudios.channel.inventory.model.Room;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Data storage for the Rooms
 */
public enum RoomStore {
    DataStore;

    // Everything in every server we know about.  The HashMap cells are rooms.
    private HashMap<String, HashMap<String, Room>> serverRooms;

    /**
     * Could have made this a Spring bean, I suppose, but chose to make it a singleton instead.
     */
    RoomStore() {
        this.serverRooms = new HashMap<>();
    }

    /**
     * Check if the specified server (name) is in the map.
     *
     * @param server Server name.
     * @return TRUE if the specified server name is a key in the map.
     */
    public boolean hasServer(String server) {
        return serverRooms.containsKey(server);
    }

    /**
     * Put a server in the map.
     *
     * @param server Server name.
     */
    public void putServer(String server) {
        if (!hasServer(server)) {
            serverRooms.put(server, new HashMap<>());
        }
    }

    /**
     * Put a room in the server.
     *
     * @param server Server name
     * @param room A populated Room object to put in the server.
     */
    public void putRoom(String server, Room room) {
        if (!hasServer(server)) {
            putServer(server);
        }

        serverRooms.get(server).put(room.getChannel(), room);
    }

    /**
     * Getter for the room store map.
     *
     * @param server Server name
     * @return The map belonging to the server.
     */
    public HashMap<String, Room> get(String server) {
        if (serverRooms.get(server) != null) {
            return serverRooms.get(server);
        }
        return null;
    }

    /**
     * Getter for a specified room by server and channel (remember: one room = one channel).
     *
     * @param server Server name
     * @param channel Channel name
     * @return A room object for the specified server and channel.
     */
    public Room get(String server, String channel) {
        if (serverRooms.get(server) != null) {
            return serverRooms.get(server).get(channel);
        }
        return null;
    }

    /**
     * Delete the server from the map.  This can be undone by issuing a !!read command.
     *
     * @param server Server name.
     */
    public void deleteServer(String server) {
        if (hasServer(server)) {
            serverRooms.remove(server);
        }
    }

    /**
     * Delete the specified room.
     *
     * @param server Server name.
     * @param room Populated room object to remove.  All that really needs to be populated, though, is room.channel.
     */
    public void deleteRoom(String server, Room room) {
        if (serverRooms.get(server) != null && serverRooms.get(server).get(room.getChannel()) != null) {
            serverRooms.get(server).remove(room.getChannel());
        }
    }

    /**
     * Convert the whole map to Json for persisting as a file, mostly for debugging.
     *
     * @return A JSON string representing the serverRooms hashmap.
     */
    public String asJson() {
        return new Gson().toJson(serverRooms);
    }
}

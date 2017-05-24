package com.animationlibationstudios.channel.inventory.persist;

import com.animationlibationstudios.channel.inventory.model.Room;

import java.util.HashMap;

/**
 * Data storage for the Rooms
 */
public enum RoomStore {
    DataStore;

    // We'll worry about the persistence layer later.  For now it'll just all be in memory.
    private HashMap<String, HashMap<String, Room>> serverRooms;

    RoomStore() {
        this.serverRooms = new HashMap<>();
    }

    public void putServer(String server) {
        if (!serverRooms.containsKey(server)) {
            serverRooms.put(server, new HashMap<>());
        }
    }

    public void putRoom(String server, Room room) {
        if (!serverRooms.containsKey(server)) {
            putServer(server);
        }

        serverRooms.get(server).put(room.getChannel(), room);
    }

    public Room get(String server, String channel) {
        if (serverRooms.get(server) != null) {
            return serverRooms.get(server).get(channel);
        }
        return null;
    }

    public void deleteRoom(String server, Room room) {
        if (serverRooms.get(server) != null && serverRooms.get(server).get(room.getChannel()) != null) {
            serverRooms.get(server).remove(room.getChannel());
        }
    }
}

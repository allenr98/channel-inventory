package com.animationlibationstudios.channel.inventory.persist;

import com.animationlibationstudios.channel.inventory.model.Room;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.json.JsonReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * This class takes an object instance and writes is as a JSON file to DropBox.
 */
public class DropboxRoomStorePersister implements RoomStorePersister {
    @Override
    public Map<String, Room> readServer(String serverName) throws IOException {
        return null;
    }

    @Override
    public void writeServer(String serverName) throws IOException {
        try {
            DbxAuthInfo auth = DbxAuthInfo.Reader.readFromFile("dbx.props");
        } catch (JsonReader.FileLoadException e) {
            // Do exception stuff
        }
    }
}

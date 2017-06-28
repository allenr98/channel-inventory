package com.animationlibationstudios.channel.inventory.persist;

import com.animationlibationstudios.channel.inventory.model.Room;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This class takes an object instance and writes is as a JSON file to the file system.
 */
@Repository
public class LocalFileRoomStorePersister implements RoomStorePersister {

    private static final String defaultFilePath = "inventory-bot/";

    // When we instantiate the object, let's make sure the target directory exists.
    public LocalFileRoomStorePersister() {
        File folder = new File(defaultFilePath);

        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    @Override
    public Map<String, Room> readServer(String serverName) throws IOException {
        HashMap<String, Room> returnValue;
        String fileName = defaultFilePath + serverName + ".json";
        File file = new File(fileName);

        if (!file.exists()) {
            throw new FileNotFoundException(fileName);
        } else {
            Reader in = new BufferedReader(new FileReader(file));

            Type type = new TypeToken<HashMap<String, Room>>(){}.getType();
            returnValue = new Gson().fromJson(in, type);
        }
        return returnValue;
    }

    @Override
    public void writeServer(String serverName) throws IOException {
        Map<String, Room> serverContents = RoomStore.DataStore.get(serverName);

        if (null != serverContents && !serverContents.isEmpty()) {
            String fileName = defaultFilePath + serverName + ".json";
            File file = new File(fileName);
            Writer out = new BufferedWriter(new FileWriter(file));

            // This will completely overwrite the previous contents of "file".
            out.write(new Gson().toJson(serverContents));
            out.close();
        }
    }
}

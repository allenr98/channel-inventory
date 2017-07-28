package com.animationlibationstudios.channel.inventory.persist;

import com.animationlibationstudios.channel.inventory.Application;
import com.animationlibationstudios.channel.inventory.model.Room;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.btobastian.javacord.utils.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.inject.Qualifier;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * This class takes an object instance and writes is as a JSON file to the file system.
 */
@Repository
@Profile("default")
public class LocalFileRoomStorePersister implements RoomStorePersister {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LoggerUtil.getLogger(Application.class);

    private static final String defaultFilePath = "inventory-bot/";

    // When we instantiate the object, let's make sure the target directory exists.
    @PostConstruct
    public void init() {
        File folder = new File(defaultFilePath);

        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    @Override
    public Map<String, Room> readServer(String serverName) throws IOException {
        HashMap<String, Room> returnValue = null;
        String fileName = defaultFilePath + serverName + ".json";
        File file = new File(fileName);

        if (!file.exists()) {
            throw new FileNotFoundException(fileName);
        } else {
            try (Reader fileReader = new BufferedReader(new FileReader(file))){
                Type type = new TypeToken<HashMap<String, Room>>() {
                }.getType();
                returnValue = new Gson().fromJson(fileReader, type);
            } catch (JsonIOException readError) {
                logger.error("File read error trying to load file " + fileName, readError);
            } catch (JsonSyntaxException jsonError) {
                logger.error("JSON parse error trying to load file " + fileName, jsonError);
            }
        }
        return returnValue;
    }

    @Override
    public void writeServer(String serverName) throws IOException {
        Map<String, Room> serverContents = RoomStore.DataStore.get(serverName);

        if (null != serverContents && !serverContents.isEmpty()) {
            String fileName = defaultFilePath + serverName + ".json";
            File file = new File(fileName);
            try (Writer out = new BufferedWriter(new FileWriter(file))) {
                // This will completely overwrite the previous contents of "file".
                out.write(new Gson().toJson(serverContents));
            } catch (IOException e) {
                logger.error("File write error trying to save file " + fileName, e);
            }
        }
    }
}

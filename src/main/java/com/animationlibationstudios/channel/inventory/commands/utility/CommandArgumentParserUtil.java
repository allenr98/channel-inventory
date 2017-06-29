package com.animationlibationstudios.channel.inventory.commands.utility;

import com.animationlibationstudios.channel.inventory.model.Room;
import com.animationlibationstudios.channel.inventory.model.enumeration.Preposition;
import com.animationlibationstudios.channel.inventory.persist.RoomStore;
import com.animationlibationstudios.channel.inventory.persist.RoomStorePersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * Helpful methods for parsing commands.
 */
@Component
public class CommandArgumentParserUtil {

    @Autowired
    private RoomStorePersister storage;

    // oo operation...do nothing.
    private void noop() {}

    /**
     * Call on any interaction with a particular server.  If the server has already been loaded from the datastore, it
     * will do nothing.  If it hasn't, it will attempt to load it.
     *
     * @param server
     * @throws IOException
     */
    public void checkAndRead(String server) {
        if (!RoomStore.DataStore.hasServer(server)) {
            try {
                HashMap<String, Room> serverContents = new HashMap<>();
                serverContents.putAll(storage.readServer(server));
                if (null != server && !server.isEmpty() && !serverContents.isEmpty()) {
                    RoomStore.DataStore.putServer(server);
                    for (String channel: serverContents.keySet()) {
                        RoomStore.DataStore.putRoom(server, serverContents.get(channel));
                    }
                }
            } catch (IOException e) {
                // The server doesn't have a file.  Not to worry, it'll get created the first time we write something
                noop();
            }
        }
    }

    /**
     * Check if the word passed in is in the preposition list.
     *
     * @param value The word to check.
     * @return True if it's a preposition.
     */
    public boolean isPreposition(String value) {
        boolean result;

        try {
            Preposition.valueOf(value.toUpperCase());
            result = true;
        } catch (IllegalArgumentException e) {
            result = false;
        }

        return result;
    }

    /**
     * Iterate through the words array and stop if we get to a preposition.
     *
     * @param words - array of strings from the command argument list.
     * @return String
     */
    public String parseItemName(String[] words) {
        StringBuilder builder = new StringBuilder();
        String space = "";
        for (String word: words) {
// don't care about prepositions for now...            if (isPreposition(word)) { break; }
            if ("-q".equalsIgnoreCase(word)) { break; } // break if we hit a quantity arg
            if ("-d".equalsIgnoreCase(word)) { break; } // break if we hit a description arg
            if ("-p".equalsIgnoreCase(word)) { break; } // break if we hit a description arg
            builder.append(space).append(word);
            if ("".equals(space)) { space = " "; }
        }

        return builder.toString();
    }

    public int parseQuantity(String[] words) throws NumberFormatException {
        // Now check and see if we find a quantity parameter
        boolean hasAQuantity = false;
        int qty = 1;
        for (String arg: words) {
            if ("-q".equalsIgnoreCase(arg)) {
                hasAQuantity = true;
            } else if (hasAQuantity) {
                // the next item had better be a well-formatted, non-negative integer number
                qty = Integer.parseInt(arg);
                break;
            }
        }
        // will return 1 if there's no quantity specified.
        return qty;
    }

    /**
     * Iterate through the words array and start building when we find a "-d" and stop if we get to a "-q"
     *
     * @param words - array of strings from the command argument list.
     * @return String
     */
    public String parseArgument(String argument, String[] words) {
        StringBuilder builder = new StringBuilder();
        String space = "";
        boolean start = false;
        for (String word: words) {
// don't care about prepositions for now...            if (isPreposition(word)) { break; }
            // If we've started collecting description words and we hit a "-q" then we're done; break.  Otherwise just
            // keep skipping past.
            if ("-q".equalsIgnoreCase(word) || "-p".equalsIgnoreCase(word) || "-p".equalsIgnoreCase(word)) {
                if (start) {
                    break;
                }
            }
            if (argument.equalsIgnoreCase(word)) {
                start = true;
            } else if (start) {
                builder.append(space).append(word);
                if ("".equals(space)) {
                    space = " ";
                }
            }
        }

        return builder.toString();
    }
}

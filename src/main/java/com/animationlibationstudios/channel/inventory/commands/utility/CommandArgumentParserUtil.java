package com.animationlibationstudios.channel.inventory.commands.utility;

import com.animationlibationstudios.channel.inventory.model.enumeration.Preposition;
import org.springframework.stereotype.Service;

/**
 * Helpful methods for parsing commands.
 */
@Service
public class CommandArgumentParserUtil {

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
            if (isPreposition(word)) { break; }
            builder.append(space).append(word);
            if ("".equals(space)) { space = " "; };
        }

        return builder.toString();
    }

}

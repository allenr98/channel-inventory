package com.animationlibationstudios.channel.inventory.commands.utility;

import com.animationlibationstudios.channel.inventory.model.enumeration.Preposition;
import org.springframework.stereotype.Component;

/**
 * Helpful methods for parsing commands.
 */
@Component
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
// don't care about prepositions for now...            if (isPreposition(word)) { break; }
            if ("-q".equalsIgnoreCase(word)) { break; } // break if we hit a quantity arg
            if ("-d".equalsIgnoreCase(word)) { break; } // break if we hit a description arg
            builder.append(space).append(word);
            if ("".equals(space)) { space = " "; }
        }

        return builder.toString();
    }

    /**
     * Iterate through the words array and start building when we find a "-d" and stop if we get to a "-q"
     *
     * @param words - array of strings from the command argument list.
     * @return String
     */
    public String parseDescription(String[] words) {
        StringBuilder builder = new StringBuilder();
        String space = "";
        boolean start = false;
        for (String word: words) {
// don't care about prepositions for now...            if (isPreposition(word)) { break; }
            // If we've started collecting description words and we hit a "-q" then we're done; break.  Otherwise just
            // keep skipping past.
            if ("-q".equalsIgnoreCase(word)) {
                if (start) {
                    break;
                }
            }
            if ("-d".equalsIgnoreCase(word)) {
                // if we hit a second "-d" after we've started, just quit and ignore it.
                if (start) {
                    break;
                }

                start = true;
            }

            if (start) {
                builder.append(space).append(word);
                if ("".equals(space)) {
                    space = " ";
                }
            }
        }

        return builder.toString();
    }

}

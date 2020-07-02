package lev;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedInputStream;

/**
 * A helper object that manages searching a stream for targets.<br><br>
 * To use an LStringSearcher:<br>
 * 1) Create one with the targets desired.<br>
 * 2) read single ints from your stream and feed it to the next() functions<br>
 * 3) If a non-empty string is returned, then it is the target that was found.
 * @author Justin Swanson
 */
public class LStringSearcher {

    String[] targets;
    int[] found;
    Boolean mark;

    /**
     * Creates an LStringSearcher object that will look for the given target strings.
     * @param targets_ Strings to look for.
     */
    public LStringSearcher(String... targets_) {
        targets = targets_;
        found = new int[targets.length];
    }

    /**
     * Inform the searcher of the next input, and receive feedback if something was found.
     * @param in The next input
     * @return Target if found, empty string if not.
     */
    public String next(int in) {
        return next((char) in);
    }

    /**
     * Inform the searcher of the next input, and receive feedback if something was found.
     * @param in The next input
     * @return Target if found, empty string if not.
     */
    public String next(char in) {
        for (int i = 0; i < found.length; i++) {
            if (in == targets[i].charAt(found[i])) {
                found[i]++;
                if (found[i] == targets[i].length()) {
                    return targets[i];
                }
            } else if (in == targets[i].charAt(0)) {
                found[i] = 1;
            } else {
                found[i] = 0;
            }
        }

        return "";
    }

    /**
     * Specialized next function that marks the stream unless a portion of a target is 
     * found, which allows you to reset and get to the stream at the start of the target
     * once one is found.
     * @param in The next input
     * @param input Stream to mark.
     * @return Target if found, empty string if not.
     */
    public String next(int in, BufferedInputStream input) {
        return next((char) in, input);
    }

    /**
     * Specialized next function that marks the stream unless a portion of a target is 
     * found, which allows you to reset and get to the stream at the start of the target
     * once one is found.
     * @param in The next input
     * @param input Stream to mark.
     * @return Target if found, empty string if not.
     */
    public String next(char in, BufferedInputStream input) {
        mark = true;
        for (int i = 0; i < found.length; i++) {
            if (in == targets[i].charAt(found[i])) {
                found[i]++;
                if (found[i] == targets[i].length()) {
                    return targets[i];
                }
                mark = false;
            } else if (in == targets[i].charAt(0)) {
                found[i] = 1;
            } else {
                found[i] = 0;
            }
        }
        if (mark) {
            input.mark(25);
        }

        return "";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.BufferedInputStream;

/**
 *
 * @author Justin Swanson
 */
public class LByteSearcher {
    
    byte[][] targets;
    int[] found;
    Boolean mark;

    /**
     * Creates a searcher object that will look for the given target bytes.
     * @param targets_ Strings to look for.
     */
    public LByteSearcher(byte[]... targets_) {
        init(targets_);
    }
    
    /**
     * Creates a search object that will look for the target int.
     * @param target
     */
    public LByteSearcher(int target) {
	byte[] targ = { (byte) target };
	init(targ);
    }
    
    final void init(byte[]... targets_) {
	targets = targets_;
        found = new int[targets.length];
    }

    /**
     * Inform the searcher of the next input, and receive feedback if something was found.
     * @param in The next input
     * @return Target if found, byte[] of size 0 if not.
     */
    public byte[] next(int in) {
        return next((char) in);
    }

    /**
     * Inform the searcher of the next input, and receive feedback if something was found.
     * @param in The next input
     * @return Target if found, byte[] of size 0 if not.
     */
    public byte[] next(char in) {
        for (int i = 0; i < found.length; i++) {
            if (in == targets[i][found[i]]) {
                found[i]++;
                if (found[i] == targets[i].length) {
                    return targets[i];
                }
            } else if (in == targets[i][0]) {
                found[i] = 1;
            } else {
                found[i] = 0;
            }
        }

        return new byte[0];
    }

    /**
     * Specialized next function that marks the stream unless a portion of a target is 
     * found, which allows you to reset and get to the stream at the start of the target
     * once one is found.
     * @param in The next input
     * @param input Stream to mark.
     * @return Target if found, byte[] of size 0 if not.
     */
    public byte[] next(int in, BufferedInputStream input) {
        return next((char) in, input);
    }

    /**
     * Specialized next function that marks the stream unless a portion of a target is 
     * found, which allows you to reset and get to the stream at the start of the target
     * once one is found.
     * @param in The next input
     * @param input Stream to mark.
     * @return Target if found, byte[] of size 0 if not.
     */
    public byte[] next(char in, BufferedInputStream input) {
        mark = true;
        for (int i = 0; i < found.length; i++) {
            if (in == targets[i][found[i]]) {
                found[i]++;
                if (found[i] == targets[i].length) {
                    return targets[i];
                }
                mark = false;
            } else if (in == targets[i][0]) {
                found[i] = 1;
            } else {
                found[i] = 0;
            }
        }
        if (mark) {
            input.mark(25);
        }

        return new byte[0];
    }
}

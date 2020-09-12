/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.util.zip.Inflater;

/**
 *
 * @author Justin Swanson
 */
public class LGlobal {

    static Inflater inflater;

    /**
     *
     * @return A singleton inflater.
     */
    public static Inflater getInflater() {
	if (inflater == null) {
	    inflater = new Inflater();
	}
	return inflater;
    }

}

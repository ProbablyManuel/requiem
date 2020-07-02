/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingWorker;

/**
 *
 * @param <T>
 * @param <E>
 * @author Justin Swanson
 */
public abstract class LSwingWorker<T, E> extends SwingWorker<T, E> {

    final static Map<Class, LSwingWorker> workers = new HashMap<>();

    /**
     *
     * @param singleton
     */
    public LSwingWorker(boolean singleton) {
	if (singleton && workers.containsKey(getClass())) {
	    LSwingWorker current = workers.get(getClass());
	    if (!current.isCancelled() && current.isDone()) {
		current.cancel(true);
	    }
	}
	workers.put(getClass(), this);
    }
}

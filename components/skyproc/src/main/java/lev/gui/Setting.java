/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * A class representing a saveable value.
 * @param <T> Type of data being saved.
 * @author Justin Swanson
 */
public abstract class Setting<T> {

    /**
     *
     */
    protected T data;
    /**
     *
     */
    protected String title;
    /**
     *
     */
    protected LUserSetting<T> tie;
    /**
     *
     */
    protected Boolean[] extraFlags;

    /**
     *
     * @param title_
     * @param data_
     * @param extraFlags
     */
    public Setting(String title_, T data_, Boolean[] extraFlags) {
	this(title_, extraFlags);
	setTo(data_);
    }

    /**
     *
     * @param title_
     * @param extraFlags
     */
    public Setting(String title_, Boolean[] extraFlags) {
        title = title_;
        this.extraFlags = extraFlags;
    }

    /**
     *
     * @return
     */
    public T get() {
        return data;
    }

    /**
     *
     * @return Returns the value as a boolean.  Could fail.
     */
    public Boolean getBool() {
        return (Boolean)data;
    }

    /**
     *
     * @return
     */
    public Color getColor() {
	return (Color) data;
    }

    /**
     *
     * @return
     */
    public Enum getEnum() {
	return (Enum) data;
    }

    /**
     *
     * @return
     */
    public Float getFloat() {
	return (Float) data;
    }

    /**
     *
     * @return
     */
    public Double getDouble() {
	return (Double) data;
    }

    /**
     *
     * @return Returns the value as an int.  Could fail.
     */
    public Integer getInt() {
        return (Integer)data;
    }

    /**
     *
     * @return Returns the value as a Set of Strings.  Could fail.
     */
    public ArrayList<String> getStrings() {
	return (ArrayList<String>) data;
    }

    /**
     *
     * @return Returns the value as a string.  Could fail.
     */
    public String getStr() {
        return data.toString();
    }

    /**
     *
     * @return
     */
    public Object getData() {
	return data;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    void tie(LUserSetting c) {
        tie = c;
    }

    /**
     * Updates the setting to its GUI tie's value
     */
    public void set() {
        if (tie != null) {
            setTo(tie.getValue());
        }
    }

    /**
     *
     * @param input
     */
    public final void setTo(T input) {
        data = input;
    }

    /**
     *
     * @param b
     * @throws java.io.IOException
     */
    public void write(BufferedWriter b) throws java.io.IOException {
        b.write(title + ": " + toString() + "\n");
    }

    /**
     *
     * @param input
     * @throws java.io.IOException
     */
    public void readSetting(String input) throws java.io.IOException {
        parse(input.trim());
    }

    /**
     *
     * @param in
     */
    public abstract void parse (String in);

    /**
     *
     * @return
     */
    public Boolean isEmpty() {
	return data == null;
    }

    /**
     *
     * @return
     */
    public abstract Setting<T> copyOf ();

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Setting<T> other = (Setting<T>) obj;
	if (!Objects.equals(this.data, other.data)) {
	    return false;
	}
	if (!Objects.equals(this.title, other.title)) {
	    return false;
	}
	if (!Objects.equals(this.extraFlags, other.extraFlags)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 73 * hash + Objects.hashCode(this.data);
	hash = 73 * hash + Objects.hashCode(this.title);
	hash = 73 * hash + Objects.hashCode(this.extraFlags);
	return hash;
    }

}
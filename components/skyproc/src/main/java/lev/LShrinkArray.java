/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A special array with artificial min/max bounds. This allows smaller parts of
 * the array to be passed around without actually copying the data to a new
 * smaller array. It also provides extract functions which bump the lower limit
 * of the ShrinkArray's scope, so that a following extract function can be
 * called without having to worry about offset indexing.
 *
 * @author Justin Swanson
 */
public class LShrinkArray extends LImport {

    private ByteBuffer buffer;

    /**
     * Wraps bytes in a ShrinkArray.
     *
     * @param in
     */
    public LShrinkArray(final byte[] in) {
	buffer = ByteBuffer.wrap(in);
    }

    /**
     * Wraps ByteBuffer in a ShrinkArray.
     *
     * @param array_
     */
    public LShrinkArray(final ByteBuffer array_) {
	buffer = array_.duplicate();
    }

    /**
     * Creates a new ShrinkArray based on the same underlying array, starting at
     * the same beginning index of the rhs LImport, but with an upper limit
     * of high.
     *
     * @param rhs LImport to copy bounds from.
     * @param high New upper limit to give to the ShrinkArray.
     */
    public LShrinkArray(final LImport rhs, final int high) {
	if (rhs.getClass() == getClass()) {
	    LShrinkArray rhss = (LShrinkArray) rhs;
	    buffer = rhss.buffer.slice();
	} else {
	    buffer = ByteBuffer.wrap(rhs.getBytes(0, high));
	}
	buffer.limit(high);
    }

    /**
     *
     * @param rhs
     */
    public LShrinkArray(final LShrinkArray rhs) {
	this(rhs, rhs.available());
    }

    /**
     * Loads in a file as raw data and wraps it in a ShrinkArray.
     *
     * @param f File to load in.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public LShrinkArray(final File f) throws FileNotFoundException, IOException {
	LInChannel in = new LInChannel(f);
	buffer = ByteBuffer.wrap(in.extract(0, in.available()));
	in.close();
    }

    /**
     *
     * @return True if ShrinkArray has no bytes left to extract.
     */
    @Override
    public Boolean isDone() {
	return !buffer.hasRemaining();
    }

    /**
     *
     * @return Amount of bytes left in the bounds of the ShrinkArray.
     */
    @Override
    public final int available() {
	return buffer.remaining();
    }

    /**
     * Bumps the lower bound up to "skip" that many bytes.
     *
     * @param skip Bytes to skip over.
     */
    @Override
    public final void skip(final int skip) {
	buffer.position(buffer.position() + skip);
    }

    /**
     * Bumps the lower bound back to include bytes previously read/skipped or
     * outside the current bounds.
     *
     * @param amount Amount to jump back.
     */
    @Override
    public void jumpBack(final int amount) {
	skip(-amount);
    }

    /**
     * Extracts bytes until the delemiter is read.<br> Bumps the lower bound up
     * so that following extracts do not extract the same data.
     *
     * @param delimiter Integer at which to stop reading.
     * @return Bytes read excluding the signaling delimiter.
     */
    @Override
    public byte[] extractUntil(int delimiter) {
	int counter = 1;
	byte[] array = buffer.array();
	for (int i = buffer.arrayOffset() + buffer.position(); i < buffer.limit() + buffer.arrayOffset(); i++) {
	    if (array[i] != delimiter) {
		counter++;
	    } else {
		byte[] out = extract(counter - 1);
		skip(1);
		return out;
	    }
	}
	return new byte[0];
    }

    /**
     *
     * @param delimiter
     * @return
     */
    public byte[] extractUntil(String delimiter) {
	return extractUntil(Ln.toByteArray(delimiter));
    }

    /**
     * Extracts specified number of bytes. <br> Bumps the lower bound up so that
     * following extracts do not extract the same data.
     *
     * @param amount Amount to read.
     * @return byte array.
     */
    @Override
    public byte[] extract(int amount) {
	byte[] bytes = new byte[amount];
	buffer.get(bytes);
	return bytes;
    }

    @Override
    public void pos(long pos) {
	buffer.position((int)pos);
    }

    @Override
    public long pos() {
	return buffer.position();
    }

    @Override
    public void close() {
	buffer.clear();
    }

    @Override
    public String toString() {
	long pos = pos();
	buffer.position(0);
	int[] bytes = Ln.toIntArray(getAllBytes());
	buffer.position((int)pos);
	int count = 0;
	String out = "";
	for (int i = 0 ; i < bytes.length && i < 1000 ; i++) {

	    out += Ln.printHex(bytes[i]);

	    if (i + 1 == pos) {
		out += ">";
	    } else if (i == pos) {
		out += "<";
	    } else {
		out += " ";
	    }

	    // New line
	    count++;
	    if (count == 16) {
		count = 0;
		out += "\n";
	    }
	}
	return out;
    }
}

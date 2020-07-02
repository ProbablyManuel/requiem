/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Justin Swanson
 */
public class LOutChannel extends LExport {

    FileOutputStream out;
    FileChannel channel;
    Stack<LengthPair> lengthStackTracker = new Stack<>();
    ArrayList<LengthPair> posQueueTracker = new ArrayList<>();

    class LengthPair {

	long pos;
	int size;

	LengthPair(long posi, int sizei) {
	    pos = posi;
	    size = sizei;
	}
    }

    /**
     *
     * @param path Path to open a channel to.
     * @throws FileNotFoundException
     */
    public LOutChannel(final String path) throws FileNotFoundException {
	super(path);
    }

    /**
     *
     * @param f File to open a channel to.
     * @throws FileNotFoundException
     */
    public LOutChannel(final File f) throws FileNotFoundException {
	super(f);
    }

    @Override
    public void openOutput(String path) throws FileNotFoundException {
	out = new FileOutputStream(path);
	channel = out.getChannel();
    }

    @Override
    public void write(byte[] array) throws IOException {
	out.write(array);
    }

    /**
     *
     * @param b
     * @throws IOException
     */
    @Override
    public void write(byte b) throws IOException {
	out.write(b);
    }

    @Override
    public void close() throws IOException {
	out.close();
	channel.close();
    }

    /**
     * A stack-based system where you can declare an amount of bytes to represent the length of some upcoming data.
     * Later on you can closeLength() which will update the bytes to contain the size of the data in-between.
     * @param size Number of bytes to write/reserve for the length information.
     * @throws IOException
     */
    public void markLength(int size) throws IOException {
	lengthStackTracker.push(new LengthPair(pos(), size));
	writeZeros(size);
    }

    /**
     * Used in conjunction with markLength(int).  Updates the most recent
     * markLength(int) reserved bytes to contain the length of data in between them
     * and the current position.
     * @throws IOException
     */
    public void closeLength() throws IOException {
	if (!lengthStackTracker.isEmpty()) {
	    LengthPair last = lengthStackTracker.pop();
	    long curPos = pos();
	    pos(last.pos);
	    write((int) (curPos - last.pos) - last.size, last.size);
	    pos(curPos);
	}
    }

    /**
     * A queue-based system where you can declare an amount of bytes to represent the position of some upcoming data.
     * Late on you can fillPosMarker() which will update the bytes to contain the position the channel is currently at.
     * @param size Number of bytes to write/reserve for the position information.
     * @throws IOException
     */
    public void setPosMarker(int size) throws IOException {
	posQueueTracker.add(new LengthPair(pos(), size));
	writeZeros(size);
    }

    /**
     * Used in conjunction with setPosMarker(int).  Updates the next-in-queue
     * setPosMarker(int) reserved bytes to contain the current position of the channel.
     * @throws IOException
     */
    public void fillPosMarker() throws IOException {
	if (!posQueueTracker.isEmpty()) {
	    LengthPair last = posQueueTracker.remove(0);
	    long curPos = pos();
	    pos(last.pos);
	    write((int)curPos, last.size);
	    pos(curPos);
	}
    }

    /**
     *
     * @param set
     * @throws IOException
     */
    public void pos(long set) throws IOException {
	channel.position(set);
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public long pos() throws IOException {
	return channel.position();
    }
}

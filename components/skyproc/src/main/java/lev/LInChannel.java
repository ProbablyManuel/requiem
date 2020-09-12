package lev;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A FileChannel setup that supports easy extraction/getting of information.
 *
 * @author Justin Swanson
 */
public class LInChannel extends LImport {

    /**
     *
     */
    protected FileInputStream iStream;
    /**
     *
     */
    protected FileChannel iChannel;
    /**
     *
     */
    protected long end;

    /**
     *
     */
    public LInChannel() {
    }

    /**
     *
     * @param path Path to open a channel to.
     */
    public LInChannel(final String path) {
	openFile(path);
    }

    /**
     *
     * @param f File to open a channel to.
     */
    public LInChannel(final File f) {
	openFile(f);
    }

    /**
     *
     * @param rhs
     * @param allocation
     */
    public LInChannel(LInChannel rhs, long allocation) {
	slice(rhs, allocation);
    }

    /**
     *
     * @param rhs
     * @param allocation
     */
    protected void slice(LInChannel rhs, long allocation) {
	LInChannel fc = (LInChannel) rhs;
	iStream = fc.iStream;
	iChannel = fc.iChannel;
	end = pos() + allocation;
    }

    /**
     *
     * @param path Path to open a channel to.
     */
    public void openFile(final String path) {
	try {
	    iStream = new FileInputStream(path);
	    iChannel = iStream.getChannel();
	    end = iChannel.size();
	} catch (IOException ex) {
	    Logger.getLogger(LInChannel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     *
     * @param f File to open a channel to.
     */
    public void openFile(final File f) {
	openFile(f.getPath());
    }

    /**
     * Reads in a byte and moves forward one position in the file.
     *
     * @return The next int in the file.
     */
    @Override
    public int read(){
	try {
	    return iStream.read();
	} catch (IOException ex) {
	    Logger.getLogger(LInChannel.class.getName()).log(Level.SEVERE, null, ex);
	}
	return -1;
    }

    /**
     * Reads in the desired bytes and wraps them in a ByteBuffer.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return ByteBuffer containing read bytes.
     */
    public ByteBuffer extractByteBuffer(int skip, int read) {
	super.skip(skip);
	ByteBuffer buf = ByteBuffer.allocate(read);
	try {
	    iChannel.read(buf);
	} catch (IOException ex) {
	    Logger.getLogger(LInChannel.class.getName()).log(Level.SEVERE, null, ex);
	}
	buf.flip();
	return buf;
    }

    /**
     *
     * @param pos Position to move to.
     */
    @Override
    public void pos(long pos) {
	try {
	    iChannel.position(pos);
	} catch (IOException ex) {
	    Logger.getLogger(LInChannel.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     *
     * @return Current position.
     */
    @Override
    public long pos() {
	try {
	    return iChannel.position();
	} catch (IOException ex) {
	    Logger.getLogger(LInChannel.class.getName()).log(Level.SEVERE, null, ex);
	}
	return -1;
    }

    /**
     * Closes streams.
     *
     */
    @Override
    final public void close() {
	if (iStream != null) {
	    try {
		iStream.close();
		iChannel.close();
	    } catch (IOException ex) {
		Logger.getLogger(LInChannel.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    /**
     *
     * @return Bytes left to read in the file.
     */
    @Override
    final public int available() {
	return (int) (end - pos());
    }

    /**
     *
     * @return
     */
    @Override
    public Boolean isDone() {
	return pos() == end;
    }

    /**
     *
     * @param amount
     * @return
     */
    @Override
    public byte[] extract(int amount) {
	ByteBuffer allocate = ByteBuffer.allocate(amount);
	try {
	    iChannel.read(allocate);
	} catch (IOException ex) {
	    Logger.getLogger(LInChannel.class.getName()).log(Level.SEVERE, null, ex);
	}
	return allocate.array();
    }

    @Override
    public byte[] extractUntil(int delimiter) {
	int counter = 1;
	while (!isDone()) {
	    if (read() != delimiter) {
		counter++;
	    } else {
		jumpBack(counter);
		byte[] out = extract(counter - 1);
		skip(1);
		return out;
	    }
	}
	return new byte[0];
    }

    /**
     *
     * @param position
     * @param count
     * @param target
     * @return
     * @throws IOException
     */
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
	return iChannel.transferTo(position, count, target);
    }
}

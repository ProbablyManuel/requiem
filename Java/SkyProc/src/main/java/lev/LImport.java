/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 *
 * @author Justin Swanson
 */
public abstract class LImport {

    /**
     * Reads in one integer and moves the position up one
     *
     * @return
     */
    public int read() {
	return extractInt(1);
    }

    /**
     * Reads in the desired bytes and converts them to a long (little endian
     * assumed).
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return Long representation of read bytes
     */
    final public long extractLong(final int skip, final int read) {
	return Ln.arrayToLong(extract(skip, read));
    }

    /**
     * Reads in the desired bytes and converts them to a string.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return String representation of read bytes
     */
    final public String extractString(final int skip, final int read) {
	skip(skip);
	return extractString(read);
    }

    /**
     * Extracts the specified number of bytes, and returns the string
     * representation.<br> Bumps the lower bound up so that following extracts
     * do not extract the same data.
     *
     * @param amount Amount to read.
     * @return String representation of the bytes read.
     */
    public String extractString(int amount) {
	return Ln.arrayToString(extract(amount));
    }

    /**
     * Reads in the desired bytes and converts them to a int (little endian
     * assumed).
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return Integer representation of read bytes
     */
    final public int extractInt(final int skip, final int read) {
	return Ln.arrayToInt(extract(skip, read));
    }

    /**
     *
     * @param read
     * @return
     */
    final public int extractInt(final int read) {
	return extractInt(0, read);
    }
    
    /**
     * Reads in the desired bytes and converts them to a int (little endian
     * assumed). Sign extends if less than 4 bytes were read and the number
     * read is negative.
     *
     * @param read
     * @return
     */
    final public int extractIntSigned(final int read) {
        int value = extractInt(0, read);
        if (read < 4) {
            boolean isNeg = (value & (0x80 << (read-1))) != 0;
            if (isNeg) {
                for (int i = read; i < 4; i++) {
                    int high = 0xFF << i * 8;
                    value |= high;
                }
            }
        }
        return value;
    }

    /**
     * Reads in the desired bytes and converts them to an int array.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return
     */
    final public int[] extractInts(final int skip, final int read) {
	return Ln.toIntArray(extract(skip, read));
    }

    /**
     * Extracts specified number of ints. <br> Bumps the lower bound up so that
     * following extracts do not extract the same data.
     *
     * @param amount Amount to read.
     * @return int array containing desired offset.
     */
    public int[] extractInts(int amount) {
	return Ln.toIntArray(extract(amount));
    }

    /**
     * Skips an amount of bytes, reads in amount of bytes and converts them to
     * integers.<br> Does NOT move position.
     *
     * @param skip
     * @param read
     * @return
     */
    final public int[] getInts(final int skip, final int read) {
	int[] out = extractInts(skip, read);
	jumpBack(skip + read);
	return out;
    }

    /**
     * Extracts 4 bytes and returns their float representation<br> Bumps the
     * lower bound up so that following extracts do not extract the same data.
     *
     * @return Float represented by the next 4 bytes.
     */
    public float extractFloat() {
	return Float.intBitsToFloat(extractInt(0, 4));
    }

    /**
     *
     * @param amount
     * @return
     */
    public boolean extractBool(int amount) {
	if (extract(amount)[0] == 0) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Returns the remaining contents as a byte array, and adjusts bounds to be
     * empty.
     *
     * @return The remaining contents as a byte array.
     */
    public byte[] extractAllBytes() {
	return extract(available());
    }

    /**
     * Reads in characters until a null byte (0) is read, and converts them to a
     * string.
     *
     * @return Next string in the file.
     */
    public String extractString() {
	return Ln.arrayToString(extractUntil(0));
    }

    /**
     * Extracts the remaining data and returns it as a string.
     * @return
     */
    public String extractAllString() {
	return Ln.arrayToString(extract(0, available()));
    }

    /**
     * Reads in a line until a newline character is found. (Byte of 10, or bytes
     * 13 -> 10)
     *
     * @return
     */
    public String extractLine() {
	byte[] read1 = {10};
	byte[] read2 = {13, 10};
	return Ln.arrayToString(extractUntil(read2, read1));
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter char to stop reading at.
     * @param bufsize Buffer size to hold readings.
     * @return Byte array containing read data without delimiter.
     */
    public byte[] extractUntil(char delimiter, int bufsize) {
	return extractUntil((int) delimiter, bufsize);
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter char to stop reading at.
     * @return Byte array containing read data without delimiter.
     */
    public byte[] extractUntil(char delimiter) {
	return extractUntil((int) delimiter);
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter int to stop reading at.
     * @return Byte array containing read data without delimiter.
     */
    public byte[] extractUntil(int delimiter) {
	byte[] delimiterB = {(byte) delimiter};
	return extractUntil(delimiterB);
    }

    /**
     * Skips an amount of bytes, reads in amount of bytes and converts them to
     * integers.<br> Moves position forward.
     *
     * @param skip
     * @param read
     * @return
     */
    public byte[] extract(final int skip, final int read) {
	skip(skip);
	return extract(read);
    }

    /**
     * Reads in amount of bytes and converts them to integers.<br> Moves
     * position forward.
     *
     * @param amount
     * @return
     */
    public abstract byte[] extract(final int amount);

    /**
     * Gets specified number of bytes and converts them to a string. Does not
     * adjust bounds.
     *
     * @param amount
     * @return
     */
    public String getString(int amount) {
	String out = extractString(amount);
	jumpBack(amount);
	return out;
    }

    /**
     * Gets specified number of bytes after skipping the desired amount and
     * converts them to a string. Does not adjust bounds.
     *
     * @param skip
     * @param amount
     * @return
     */
    public String getString(int skip, int amount) {
	return Ln.arrayToString(getInts(skip, amount));
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter Byte to stop reading at.
     * @param bufsize Buffer size to hold readings.
     * @return Byte array containing read data without delimiter.
     */
    public byte[] extractUntil(int delimiter, int bufsize) {
	byte[] buffer = new byte[bufsize];
	int counter = 0;
	int in;
	while (available() > 0 && (in = read()) != delimiter) {
	    buffer[counter++] = (byte) in;
	}
	byte[] out = new byte[counter];
	System.arraycopy(buffer, 0, out, 0, counter);
	return out;
    }

    /**
     * Reads in bytes until any of the delimiters are read. Returns the first
     * delimiter found, so parameter order matters.
     *
     * @param delimiters Byte arrays of patterns to stop reading at.
     * @return Byte array containing read data without delimiter.
     */
    public byte[] extractUntil(byte[]... delimiters) {
	ArrayList<Byte> buffer = new ArrayList<>(50);
	LByteSearcher search = new LByteSearcher(delimiters);
	int in;
	byte[] stop = new byte[0];
	while (available() > 0 && stop.length == 0) {
	    in = read();
	    stop = search.next(in);
	    if (stop.length == 0) {
		buffer.add((byte) in);
	    }
	}
	byte[] out = new byte[buffer.size() - (stop.length - 1)];
	for (int i = 0; i < buffer.size() - (stop.length - 1) && i < buffer.size(); i++) {
	    out[i] = buffer.get(i);
	}
	return out;
    }

    /**
     * Gets specified number of bytes after skipping the desired amount. Does
     * not adjust bounds.
     *
     * @param skip Amount to skip.
     * @param amount Amount to read.
     * @return byte array containing desired offset.
     */
    public byte[] getBytes(int skip, int amount) {
	byte[] out = extract(skip, amount);
	jumpBack(skip + amount);
	return out;
    }

    /**
     * Returns the remaining contents as a byte array, but does NOT adjust the
     * bounds.
     *
     * @return The remaining contents as a byte array.
     */
    public byte[] getAllBytes() {
	return getBytes(0, available());
    }

    /**
     * Bumps the position the desired offset.
     *
     * @param offset Desired offset.
     */
    public void skip(final int offset) {
	if (offset != 0) {
	    pos(pos() + offset);
	}
    }

    /**
     * Moves position back an amount of bytes.
     *
     * @param amount
     */
    public void jumpBack(int amount) {
	skip(-amount);
    }

    /**
     *
     * @param pos
     */
    public abstract void pos(long pos);

    /**
     *
     * @return @throws IOException
     */
    public abstract long pos();

    /**
     * Uses an LStringSearcher to read file contents until one of the targets is
     * found.
     *
     * @param targets
     * @return The target found, or the empty string if none found.
     */
    final public String scanTo(String... targets) {
	LStringSearcher search = new LStringSearcher(targets);
	String result;
	int inputInt = read();

	while (inputInt != -1) {
	    if (!(result = search.next(inputInt)).equals("")) {
		return result;
	    }

	    inputInt = read();
	}

	return "";
    }

    /**
     *
     */
    public abstract void close();

    /**
     *
     * @return @throws IOException
     */
    public abstract int available();

    /**
     *
     * @return
     */
    public Boolean isDone() {
	return available() == 0;
    }

    /**
     * Assumes the contents of the ShrinkArray is raw zipped data in its
     * entirety, and nothing else. It then unzips that data in the ShrinkArray.
     *
     * @return new ShrinkArray with uncompressed data.
     * @throws DataFormatException
     */
    public LShrinkArray correctForCompression() throws DataFormatException {

	int uncompressedSize = Ln.arrayToInt(extractInts(4));

	byte[] compressedByteData = getAllBytes();

	//Uncompress
	Inflater decompresser = LGlobal.getInflater();
	decompresser.setInput(compressedByteData, 0, available());
	byte[] uncompressedByteData = new byte[uncompressedSize];
	decompresser.inflate(uncompressedByteData);
	decompresser.reset();

	return new LShrinkArray(ByteBuffer.wrap(uncompressedByteData));
    }
}

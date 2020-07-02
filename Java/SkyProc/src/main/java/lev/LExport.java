/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.*;
import java.nio.ByteBuffer;

/**
 *
 * @author Justin Swanson
 */
public abstract class LExport {

    /**
     *
     * @param file File to open an exporter to.
     * @throws FileNotFoundException
     */
    public LExport (File file) throws FileNotFoundException {
	this(file.getPath());
    }

    /**
     *
     * @param path Path to open an exporter to.
     * @throws FileNotFoundException
     */
    public LExport(String path) throws FileNotFoundException {
	Ln.makeDirs(path);
        openOutput(path);
    }

    /**
     *
     * @param path Path to open a channel to.
     * @throws FileNotFoundException
     */
    public abstract void openOutput(String path) throws FileNotFoundException;

    /**
     * Writes a byte array to the file.
     * @param array
     * @throws IOException
     */
    public abstract void write(byte[] array) throws IOException;

    /**
     * Writes a byte array to the file.
     * @param array
     * @param size Minimum byte size.  Output will be appended with zeros until size is met.
     * @throws IOException
     */
    public void write(byte[] array, int size) throws IOException {
        write(array);
        if (size - array.length > 0) {
            writeZeros(size - array.length);
        }
    }

    /**
     *
     * @param size
     * @throws IOException
     */
    public void writeZeros(int size) throws IOException {
	write(new byte[size]);
    }

    /**
     * Writes an integer to the file.  Can span multiple bytes if the integer is large.
     * @param input Integer to output.
     * @param size Minimum byte size.  Output will be appended with zeros until size is met.
     * @throws java.io.IOException
     */
    public void write(int input, int size) throws IOException {
        write(Ln.toByteArray(input, size, size));
    }

    /**
     *
     * @param input
     * @throws IOException
     */
    public void write(int input) throws IOException {
	write(Ln.toByteArray(input));
    }

    /**
     *
     * @param b
     * @throws IOException
     */
    public abstract void write(byte b) throws IOException;

    /**
     * Writes a string to the output (no null terminator).
     * @param input
     * @throws java.io.IOException
     */
    public void write(String input) throws java.io.IOException {
        write(input, 0);
    }

    /**
     * Writes a string to the output (no null terminator).
     * @param input
     * @param size Minimum byte size.  Output will be appended with zeros until size is met.
     * @throws IOException
     */
    public void write(String input, int size) throws IOException {
        write(Ln.toByteArray(input), size);
    }

    /**
     *
     * @param input Boolean to write
     * @param size Number of bytes to write
     * @throws IOException
     */
    public void write(boolean input, int size) throws IOException {
	if (input) {
	    write(1,size);
	} else {
	    write(0,size);
	}
    }

    /**
     * Writes a float to the file.
     * @param input
     * @throws IOException
     */
    public void write(float input) throws IOException {
        ByteBuffer out = ByteBuffer.allocate(4);
        out.putInt(Integer.reverseBytes(Float.floatToIntBits(input)));
        write(out.array(), 4);
    }

    /**
     * Flushes buffer and closes output stream.
     * @throws IOException
     */
    public abstract void close() throws IOException;
}

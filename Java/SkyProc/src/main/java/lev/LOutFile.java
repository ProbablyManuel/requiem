/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.*;

/**
 * A BufferedOutputStream with easy to use write functions.
 *
 * @author Justin Swanson
 */
public class LOutFile extends LExport {

    private BufferedOutputStream output;

    /**
     *
     * @param file File to open an exporter to.
     * @throws FileNotFoundException
     */
    public LOutFile(File file) throws FileNotFoundException {
	super(file);
    }

    /**
     *
     * @param path Path to open an exporter to.
     * @throws FileNotFoundException
     */
    public LOutFile(String path) throws FileNotFoundException {
	super(path);
    }

    /**
     *
     * @param b
     * @throws IOException
     */
    @Override
    public void write(byte b) throws IOException {
	output.write(b);
    }

    @Override
    public void write(byte[] array) throws IOException {
	output.write(array);
    }

    @Override
    public void openOutput(String path) throws FileNotFoundException {
	Ln.makeDirs(path);
	output = new BufferedOutputStream(new FileOutputStream(path));
    }

    @Override
    public void close() throws IOException  {
        output.close();
    }
}
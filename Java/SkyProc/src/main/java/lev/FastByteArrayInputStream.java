/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.InputStream;

/**
 * ByteArrayInputStream implementation that does not synchronize methods.
 */
public class FastByteArrayInputStream extends InputStream {
    /**
     * Our byte buffer
     */
    protected byte[] buf = null;

    /**
     * Number of bytes that we can read from the buffer
     */
    protected int count = 0;

    /**
     * Number of bytes that have been read from the buffer
     */
    protected int pos = 0;

    /**
     * 
     * @param buf
     * @param count
     */
    public FastByteArrayInputStream(byte[] buf, int count) {
        this.buf = buf;
        this.count = count;
    }

    /**
     * 
     * @return
     */
    @Override
    public final int available() {
        return count - pos;
    }

    /**
     * 
     * @return
     */
    @Override
    public final int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    /**
     * 
     * @param b
     * @param off
     * @param len
     * @return
     */
    @Override
    public final int read(byte[] b, int off, int len) {
        if (pos >= count)
            return -1;

        if ((pos + len) > count)
            len = (count - pos);

        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    /**
     * 
     * @param n
     * @return
     */
    @Override
    public final long skip(long n) {
        if ((pos + n) > count)
            n = count - pos;
        if (n < 0)
            return 0;
        pos += n;
        return n;
    }

}
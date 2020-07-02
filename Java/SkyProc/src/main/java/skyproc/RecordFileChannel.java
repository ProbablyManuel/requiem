/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.nio.ByteBuffer;
import lev.LInChannel;

/**
 *
 * @author Justin Swanson
 */
class RecordFileChannel extends LInChannel {

    long pos;

    public RecordFileChannel (String str) {
	super(str);
	pos = 0;
    }

    public RecordFileChannel (LInChannel fc, int allocation) {
	super();
	pos = fc.pos();
	slice(fc, allocation);
    }

    public RecordFileChannel (RecordFileChannel rfc, int allocation) {
	super();
	pos = rfc.pos;
	slice(rfc, allocation);
    }

    @Override
    public void openFile(String path) {
	super.openFile(path);
	try {
	    pos = iChannel.position();
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}
    }

    @Override
    public byte[] extract(int amount) {
	pos(pos);
	byte[] out = super.extract(amount);
	pos += amount;
	return out;
    }

    @Override
    public Boolean isDone() {
	return pos == end;
    }

    @Override
    public void pos(long pos) {
	super.pos(pos);
	this.pos = pos;
    }

    @Override
    public long pos() {
	return pos;
    }

    @Override
    public ByteBuffer extractByteBuffer(int skip, int read) {
	ByteBuffer out = super.extractByteBuffer(skip, read);
	pos += skip + read;
	return out;
    }

    @Override
    public int read() {
	pos(pos);
	int out = super.read();
	pos++;
	return out;
    }
}

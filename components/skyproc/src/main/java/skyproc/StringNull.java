/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import lev.LOutFile;

/**
 *
 * @author Justin Swanson
 */
class StringNull extends StringNonNull {

    public StringNull(String in) {
        super(in);
    }

    public StringNull() {
    }

    @Override
    void export(ModExporter out) throws IOException {
        super.export(out);
        out.write(0, 1);
    }

    @Override
    int getContentLength(ModExporter out) {
        return super.getContentLength(out) + 1;
    }

    @Override
    Record getNew() {
        return new StringNull();
    }


}

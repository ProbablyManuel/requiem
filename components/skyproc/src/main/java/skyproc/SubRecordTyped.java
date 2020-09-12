/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 *
 * @param <T> 
 * @author Justin Swanson
 */
abstract class SubRecordTyped<T> extends SubRecord<T> {

    ArrayList<String> types;

    SubRecordTyped(String t) {
	types = Record.getTypeList(t);
    }

    SubRecordTyped(ArrayList<String> t) {
	types = t;
    }

    @Override
    ArrayList<String> getTypes() {
	return types;
    }

}

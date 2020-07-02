/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LFlags;
import lev.Ln;
import skyproc.Condition.Operator;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class ConditionBase extends SubRecord {

    Operator operator = Operator.EqualTo;
    LFlags flags = new LFlags(1);
    byte[] fluff = new byte[3];
    FormID comparisonValueForm = new FormID();
    float comparisonValueFloat = 0;
    byte[] padding = new byte[2];
    ConditionOption option;

    ConditionBase() {
	super();
    }

    @Override
    void export(ModExporter out) throws IOException {
	super.export(out);
	//Flags and Operator
	int operatorInt = operator.ordinal();
	operatorInt *= 32;
	LFlags tmp = new LFlags(Ln.toByteArray(operatorInt, 1));
	for (int i = 0; i < 5; i++) {
	    tmp.set(i, flags.get(i));
	}
	out.write(tmp.export(), 1);
	out.write(fluff, 3);

	//Value
	if (get(Condition.CondFlag.UseGlobal)) {
	    // This FormID is flipped, so it's an odd export.
	    comparisonValueForm.adjustMasterIndex(out.getExportMod());
	    out.write(comparisonValueForm.getInternal(true));
	} else {
	    out.write(comparisonValueFloat);
	}

	//Function
	out.write(option.index, 2);
	out.write(padding, 2);

	option.export(out);
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	//Flags and Operator
	flags.set(in.extract(1));
	int operatorInt = flags.export()[0];
	operatorInt = operatorInt & 0xE0; // Mask unrelated bits
	operatorInt /= 32; // Shift bits left 5
	operator = Condition.Operator.values()[operatorInt];
	fluff = in.extract(3);

	//Value
	if (get(Condition.CondFlag.UseGlobal)) {
	    // Use public set here, because for some reason, this FormID is flipped
	    comparisonValueForm = new FormID();
	    comparisonValueForm.set(Ln.reverse(in.extract(4)));
	    comparisonValueForm.standardize(srcMod);
	} else {
	    comparisonValueFloat = in.extractFloat();
	}

	//Function
	option = ConditionOption.getOption(in.extractInt(2));
	padding = in.extract(2);

	if (SPGlobal.logMods){
	    logMod(srcMod, "", "New Condition.  Function: " + option.script.toString() + ", index: " + option.index);
	    logMod(srcMod, "", "  Operator: " + operator + ", flags: " + flags + " useGlobal: " + get(Condition.CondFlag.UseGlobal));
	    logMod(srcMod, "", "  Comparison Val: " + comparisonValueForm + "|" + comparisonValueFloat);
	}

	option.parseData(in, srcMod);

    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>(5);
	if (comparisonValueForm != null) {
	    out.add(comparisonValueForm);
	}
	out.addAll(option.allFormIDs());
	return out;
    }

    @Override
    SubRecord getNew(String type) {
	return new ConditionBase();
    }

    @Override
    boolean isValid() {
	return true;
    }

    @Override
    int getContentLength(ModExporter out) {
	return 32;
    }

    public boolean get(Condition.CondFlag flag) {
	return flags.get(flag.value);
    }

    public void set(Condition.CondFlag flag, boolean on) {
	flags.set(flag.value, on);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("CTDA");
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ConditionBase other = (ConditionBase) obj;
	if (this.operator != other.operator) {
	    return false;
	}
	if (!Objects.equals(this.flags, other.flags)) {
	    return false;
	}
	if (!Arrays.equals(this.fluff, other.fluff)) {
	    return false;
	}
	if (!Objects.equals(this.comparisonValueForm, other.comparisonValueForm)) {
	    return false;
	}
	if (Float.floatToIntBits(this.comparisonValueFloat) != Float.floatToIntBits(other.comparisonValueFloat)) {
	    return false;
	}
	if (!Arrays.equals(this.padding, other.padding)) {
	    return false;
	}
	if (!option.equals(other.option)) {  
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 97 * hash + (this.operator != null ? this.operator.hashCode() : 0);
	hash = 97 * hash + Objects.hashCode(this.flags);
	hash = 97 * hash + Arrays.hashCode(this.fluff);
	hash = 97 * hash + Objects.hashCode(this.comparisonValueForm);
	hash = 97 * hash + Float.floatToIntBits(this.comparisonValueFloat);
	hash = 97 * hash + Arrays.hashCode(this.padding);
	hash = 97 * hash + Objects.hashCode(this.option);
	return hash;
    }

    @Override
    boolean subRecordEquals(SubRecord subRecord) {
	return equals(subRecord);
    }
}

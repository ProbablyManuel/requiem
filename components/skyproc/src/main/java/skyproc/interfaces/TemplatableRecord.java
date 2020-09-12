package skyproc.interfaces;

import skyproc.FormID;

public interface TemplatableRecord {

    abstract public FormID getTemplate();

    abstract public boolean isTemplated();
}

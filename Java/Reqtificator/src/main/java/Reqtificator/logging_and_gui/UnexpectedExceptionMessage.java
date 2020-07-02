package Reqtificator.logging_and_gui;

import Reqtificator.components.Component;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.Message;
import skyproc.MajorRecord;

public class UnexpectedExceptionMessage implements Message {

    private final static String newLine = System.getProperty("line.separator");

    private final Throwable cause;
    private final MajorRecord source;
    private final ThreadContext.ContextStack context;

    public UnexpectedExceptionMessage(MajorRecord source,
                                      ThreadContext.ContextStack context,
                                      Throwable cause) {
        this.cause = cause;
        this.source = source;
        this.context = context;
    }

    @Override
    public String getFormattedMessage() {
        return formatStack();
    }

    @Override
    public String getFormat() {
        return "";
    }

    @Override
    public Object[] getParameters() {
        return context.toArray();
    }

    @Override
    public Throwable getThrowable() {
        return cause;
    }

    private String formatStack() {
        StringBuilder sb = new StringBuilder(
                "unexpected exception encountered, record stack:");
        sb.append(newLine).append("1. ").
                append(Component.formatRecord(source)).append(newLine);
        int i = 2;
        for (String record : context) {
            sb.append(i++).append(". ").append(record).append(newLine);
        }
        return sb.toString();
    }
}

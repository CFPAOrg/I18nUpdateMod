package org.cfpa.i18nupdatemod.command;

public class POJOResult {
    private int count;
    private int accepted;
    private int skipped;
    private boolean result;
    private int not_found;
    private int total;

    public int getCount() {
        return count;
    }

    public int getAccepted() {
        return accepted;
    }

    public int getSkipped() {
        return skipped;
    }

    public boolean isResult() {
        return result;
    }

    public int getNot_found() {
        return not_found;
    }

    public int getTotal() {
        return total;
    }
}

module skyproc {
    requires transitive java.desktop;
    requires transitive java.logging;
    requires transitive java.prefs;

    exports skyproc;
    exports lev;
    exports skyproc.gui;
    exports lev.gui;
    exports skyproc.interfaces;
    exports skyproc.exceptions;
    exports skyproc.genenums;
}
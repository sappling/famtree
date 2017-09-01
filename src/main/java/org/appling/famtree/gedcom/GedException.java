package org.appling.famtree.gedcom;

/**
 * Created by sappling on 8/15/2017.
 */
public class GedException extends Exception {
    public GedException(String message) {
        super(message);
    }

    public GedException(String message, Throwable cause) {
        super(message, cause);
    }
}

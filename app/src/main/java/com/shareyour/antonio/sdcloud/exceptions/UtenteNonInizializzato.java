package com.shareyour.antonio.sdcloud.exceptions;

/**
 * Created by antonio on 14/06/15.
 */
public class UtenteNonInizializzato extends Exception {

    private static final String MESSAGE = "Utente non inizializzato";

    public UtenteNonInizializzato() {
        super(MESSAGE);
    }
}

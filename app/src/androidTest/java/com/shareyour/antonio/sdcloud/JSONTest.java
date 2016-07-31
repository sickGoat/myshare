package com.shareyour.antonio.sdcloud;

import android.test.AndroidTestCase;

import com.appspot.id.app.clientAPI.model.UtenteProxy;
import com.shareyour.antonio.sdcloud.exceptions.UtenteNonInizializzato;
import com.shareyour.antonio.sdcloud.utilities.UtenteSession;
import com.shareyour.antonio.sdcloud.utilities.UtenteSessionManager;
import com.shareyour.antonio.sdcloud.utilities.UtenteSessionSerializer;

import java.io.IOException;

/**
 * Created by antonio on 14/06/15.
 */
public class JSONTest extends AndroidTestCase {

    public void testSerializeData() throws IOException, UtenteNonInizializzato {
        UtenteSession utenteSession = new UtenteSession();
        UtenteProxy utente = new UtenteProxy();
        utente.setId(new Long(1));
        utenteSession.setmUtente(utente);
        UtenteSessionSerializer serializer = new UtenteSessionSerializer(getContext(),"utentesession.json");
        serializer.saveSession(utenteSession);
        UtenteSession session = serializer.getSession();
        assertEquals(session.getmUtente().getId(),utente.getId());
    }
}

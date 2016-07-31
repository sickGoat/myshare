package com.shareyour.antonio.sdcloud.helpers;

import android.content.Context;
import android.util.Log;

import com.appspot.sd_app_970.clientAPI.model.AmbitoProxy;
import com.appspot.sd_app_970.clientAPI.model.CommentoProxy;
import com.appspot.sd_app_970.clientAPI.model.PostProxy;
import com.appspot.sd_app_970.clientAPI.model.UtenteProxy;
import com.shareyour.antonio.sdcloud.constants.JSONConstants;
import com.shareyour.antonio.sdcloud.utilities.HelperClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by antonio on 26/06/15.
 */
public class Serializer {

    private static final String FILE_NAME_UTENTE = "utente.json";

    private static final String FILE_NAME_AMBITI = "iscrizioni1.json";

    public static ArrayList<AmbitoProxy> loadIscrizioni(Context appContext){

        ArrayList<AmbitoProxy> ambiti = new ArrayList<>();
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(appContext.openFileInput(FILE_NAME_AMBITI)));
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null )
                jsonStringBuilder.append(line);

            JSONArray ambitiArray = new JSONArray(jsonStringBuilder.toString());
            for( int i = 0 ; i < ambitiArray.length() ; i++ ){
                JSONObject ambitoJson = (JSONObject) ambitiArray.get(i);
                AmbitoProxy ambito = new AmbitoProxy();
                ambito.setId(ambitoJson.getLong(JSONConstants.JSON_AMBITO_ID));
                ambito.setCategoriaId(ambitoJson.getLong(JSONConstants.JSON_AMBITO_CATEGORIA_ID));
                ambito.setTesto(ambitoJson.getString(JSONConstants.JSON_AMBITO_TESTO));
                ambito.setCategoriaTesto(ambitoJson.getString(JSONConstants.JSON_AMBITO_CATEGORIA_TESTO));
                ambiti.add(ambito);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            if( reader != null )
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return ambiti;
    }

    public static void saveIscrizioni(ArrayList<AmbitoProxy> ambiti,Context context){
        JSONArray ambitiArray = new JSONArray();
        try {
            for( AmbitoProxy ambito : ambiti ) {
                JSONObject ambitoJson = new JSONObject();
                ambitoJson.put(JSONConstants.JSON_AMBITO_ID, ambito.getId());
                ambitoJson.put(JSONConstants.JSON_AMBITO_CATEGORIA_ID,ambito.getCategoriaId());
                ambitoJson.put(JSONConstants.JSON_AMBITO_TESTO,ambito.getTesto());
                ambitoJson.put(JSONConstants.JSON_AMBITO_CATEGORIA_TESTO,ambito.getCategoriaTesto());

                ambitiArray.put(ambitoJson);
            }
        } catch (JSONException e) {
                e.printStackTrace();
        }

        Writer writer = null;
        try{
            OutputStream stream = context.getApplicationContext().openFileOutput(FILE_NAME_AMBITI, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(stream);
            if( ambitiArray.length() > 0 )
                writer.write(ambitiArray.toString());
            else
                writer.write("");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer != null ) try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static UtenteProxy loadUtente(Context appContext){
        UtenteProxy utente = new UtenteProxy();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(appContext.openFileInput(FILE_NAME_UTENTE)));
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null )
                jsonStringBuilder.append(line);

            JSONObject utenteJson = new JSONObject(jsonStringBuilder.toString());
            utente.setId(utenteJson.getLong(JSONConstants.JSON_UTENTE_ID));
            utente.setNome(utenteJson.getString(JSONConstants.JSON_UTENTE_NOME));
            utente.setCognome(utenteJson.getString(JSONConstants.JSON_UTENTE_COGNOME));
            utente.setEmail(utenteJson.getString(JSONConstants.JSON_UTENTE_EMAIL));
            utente.setAccessToken(utenteJson.getString(JSONConstants.JSON_UTENTE_ACCESSTOKEN));
            //utente.setDataNascita(utenteJson.get)
            utente.setSesso(utenteJson.getBoolean(JSONConstants.JSON_UTENTE_SESSO));

        } catch (Exception e) {
            utente = null;
        }
        return utente;
    }

    public static void saveUtente(UtenteProxy utente,Context context){
        JSONObject utenteObj = new JSONObject();
        if( utente != null ) {
            try {
                utenteObj.put(JSONConstants.JSON_UTENTE_ID, utente.getId());
                utenteObj.put(JSONConstants.JSON_UTENTE_NOME, utente.getNome());
                utenteObj.put(JSONConstants.JSON_UTENTE_COGNOME, utente.getCognome());
                utenteObj.put(JSONConstants.JSON_UTENTE_EMAIL, utente.getEmail());
                utenteObj.put(JSONConstants.JSON_UTENTE_ACCESSTOKEN, utente.getAccessToken());
                utenteObj.put(JSONConstants.JSON_UTENTE_DATAN, HelperClass.formatDate(utente.getDataNascita()));
                utenteObj.put(JSONConstants.JSON_UTENTE_SESSO, utente.getSesso());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Writer writer = null;
        try{
            OutputStream stream = context.openFileOutput(FILE_NAME_UTENTE, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(stream);
            writer.write(utenteObj.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if( writer != null )
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static String serializeAmbito(AmbitoProxy ambito){
        JSONObject obj = new JSONObject();
        try {
            obj.put(JSONConstants.JSON_AMBITO_ID,ambito.getId());
            obj.put(JSONConstants.JSON_AMBITO_TESTO,ambito.getTesto());
            obj.put(JSONConstants.JSON_AMBITO_CATEGORIA_ID,ambito.getCategoriaId());
            obj.put(JSONConstants.JSON_AMBITO_CATEGORIA_TESTO,ambito.getCategoriaTesto());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public static AmbitoProxy deserializeAmbito(String ambitoJSON){
        AmbitoProxy ambito = new AmbitoProxy();
        try {
            JSONObject obj = new JSONObject(ambitoJSON);
            ambito.setId(obj.getLong(JSONConstants.JSON_AMBITO_ID));
            ambito.setTesto(obj.getString(JSONConstants.JSON_AMBITO_TESTO));
            ambito.setCategoriaId(obj.getLong(JSONConstants.JSON_AMBITO_CATEGORIA_ID));
            ambito.setCategoriaTesto(obj.getString(JSONConstants.JSON_AMBITO_CATEGORIA_TESTO));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ambito;
    }

    public static String serializePost(PostProxy post){
        JSONObject postObj = new JSONObject();
        try {
            postObj.put(JSONConstants.JSON_POST_ID,post.getId());
            postObj.put(JSONConstants.JSON_POST_AMBITOID,post.getIdAmbito());
            postObj.put(JSONConstants.JSON_POST_NOMEUTENTE, post.getNomeUtente());
            postObj.put(JSONConstants.JSON_POST_COGNOMEUTENTE, post.getCognomeUtente());
            postObj.put(JSONConstants.JSON_POST_UTENTEID, post.getIdUtente());
            postObj.put(JSONConstants.JSON_POST_TESTO, post.getTesto());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return postObj.toString();
    }

    public static PostProxy deserializePost(String postJson){
        PostProxy post = new PostProxy();
        try {
            JSONObject postObj = new JSONObject(postJson);
            post.setId(postObj.getLong(JSONConstants.JSON_POST_ID));
            post.setTesto(postObj.getString(JSONConstants.JSON_POST_TESTO));
            post.setIdAmbito(postObj.getLong(JSONConstants.JSON_POST_AMBITOID));
            post.setIdUtente(postObj.getLong(JSONConstants.JSON_POST_UTENTEID));
            post.setNomeUtente(postObj.getString(JSONConstants.JSON_POST_NOMEUTENTE));
            post.setCognomeUtente(postObj.getString(JSONConstants.JSON_POST_COGNOMEUTENTE));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return post;
    }

    public static String serializeCommento(CommentoProxy commento){
        JSONObject obj = new JSONObject();
        try {
            obj.put(JSONConstants.JSON_COMMENTO_ID, commento.getId());
            obj.put(JSONConstants.JSON_COMMENTO_POSTID, commento.getIdPost());
            obj.put(JSONConstants.JSON_COMMENTO_COGNOMEUTENTE, commento.getNomeUtente());
            obj.put(JSONConstants.JSON_COMMENTO_COGNOMEUTENTE, commento.getCognomeUtente());
            obj.put(JSONConstants.JSON_COMMENTO_UTENTEID, commento.getIdUtente());
            obj.put(JSONConstants.JSON_COMMENTO_TESTO, commento.getTesto());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    public static CommentoProxy deserializeCommento(String commentoJson){
        CommentoProxy commento = new CommentoProxy();
        try {
            JSONObject obj = new JSONObject(commentoJson);
            commento.setId(obj.getLong(JSONConstants.JSON_COMMENTO_ID));
            commento.setCognomeUtente(obj.getString(JSONConstants.JSON_COMMENTO_COGNOMEUTENTE));
            commento.setNomeUtente(obj.getString(JSONConstants.JSON_COMMENTO_NOMEUTENTE));
            commento.setIdUtente(obj.getLong(JSONConstants.JSON_COMMENTO_UTENTEID));
            commento.setIdPost(obj.getLong(JSONConstants.JSON_COMMENTO_POSTID));
            commento.setTesto(obj.getString(JSONConstants.JSON_COMMENTO_TESTO));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return commento;
    }


}

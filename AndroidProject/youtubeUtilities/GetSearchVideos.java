package jb.dam2.discover.youtubeUtilities;

import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jb.dam2.discover.activities.ArtistProfileActivity;
import jb.dam2.discover.activities.ShareActivity;

/*
    JB:GetSearchVideos Es una clase que gestiona la busqueda de terminos con la API de Youtube
*/
public class GetSearchVideos {

    private String query;
    private YouTube youtube;
    //JB: La clave API es privada. Otorgada por el panel de desarrollador de Google
    private final String apiKey = 
    private static final long NUMBER_OF_VIDEOS_RETURNED = 15;
    private ShareActivity shareActivity;
    private ArtistProfileActivity artistProfileActivity;

    public GetSearchVideos(String query, ShareActivity shareActivity) {
        this.query = query;
        this.shareActivity = shareActivity;
        artistProfileActivity = null;
    }

    public GetSearchVideos(String query, ArtistProfileActivity artistProfileActivity) {
        this.query = query;
        this.shareActivity = null;
        this.artistProfileActivity = artistProfileActivity;
    }

    public GetSearchVideos(String videoId) {
        this.query = videoId;
    }

    /*
        JB:getSearchResults Realiza la busqueda de videos segun el termino con el que se definio la llamada
    */
    public void getSearchResults() {
        try {

            //JB: Ejecucion de busqueda
            YouTube.Search.List search = getSearch();

            //Definicion de resultados maximos
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            //JB: Recoleccion de resultados
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

            //JB: Si la lista no esta vacia, realiza la siguiente accion
            if (searchResultList != null) {
                videoSearchResult(searchResultList.iterator(), query);
            }
        } catch (GoogleJsonResponseException e) {
            //Log.d("pretty", "1. There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
        } catch (IOException e) {
            //Log.d("pretty", "2. There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            //Log.d("pretty", "3. " + t.toString());
        }
    }

    /*
       JB:videoSearchResult() Realiza la busqueda de videos segun el termino con el que se definio la llamada
    */
    private void videoSearchResult(Iterator<SearchResult> iteratorSearchResults, String query) {
        ArrayList<String> videoIds = new ArrayList<>();
        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            //JB: Se confirma que el resultado es un video
            if (rId.getKind().equals("youtube#video")) {
                videoIds.add(rId.getVideoId());
            }
        }
        //JB: Depende de la actividad se definen los videos a mostrar en cada actividad
        if(shareActivity!=null){
            shareActivity.setmVideoIds(videoIds);
        }else if(artistProfileActivity!=null){
            artistProfileActivity.setVideoIds(videoIds);
        }
    }

    /*
       JB:getTitleQuery() Establece el titulo de cada video
    */
    public String getTitleQuery() {
        String titulo = "";
        try {
            YouTube.Search.List search = getSearch();

            search.setMaxResults(Long.valueOf(1));

            //JB: Llamada a la API
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

            if (searchResultList != null) {
                titulo = searchForTitle(searchResultList.iterator());
            }

        } catch (GoogleJsonResponseException e) {
            Log.d("pretty", "1. There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            Log.d("pretty", "2. There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            Log.d("pretty", "3. " + t.toString());
        }
        return titulo;
    }

    /*
       JB:searchForTitle() Recoge en titulo de cada video y lo devuelve
    */
    private String searchForTitle(Iterator<SearchResult> iteratorSearchResults) {
        String title = "";
        while (iteratorSearchResults.hasNext()) {
            SearchResult singleVideo = iteratorSearchResults.next();
            title = singleVideo.getSnippet().getTitle();
        }
        return title;
    }

    /*
       JB:getSearch() Realiza la busqueda utilizando la API de Youtube
    */
    private YouTube.Search.List getSearch() {
        YouTube.Search.List search = null;
        try {
            JsonFactory jsonFactory = new JacksonFactory();

            /*
            JB: Este objeto se usa para hacer peticiones a la API de youtube.
            El ultimo argumento es necesario, pero ya que no necesitamos nada inicializado cuando la peticion
            http es inicializada, sobreescribimos la interfaz y escribimos una funcion sin objetivo
             */
            youtube = new YouTube.Builder(new NetHttpTransport(), jsonFactory, request -> {
            }).setApplicationName("discover").build();

            //JB: Se define la peticion de la API de Youtube
            search = youtube.search().list("id,snippet");

            //JB: Seteo de las claves de desarrollador Set your developer
            search.setKey(apiKey);
            search.setQ(query);

            //JB: Restriccion del tipo de resultado
            search.setType("video");

            //JB: Para aumentar eficiencia, se recogen solo los campos necesarios
            search.setFields("items(id/kind,id/videoId,snippet/title)");

        } catch (GoogleJsonResponseException e) {
            Log.d("pretty", "1. There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            Log.d("pretty", "2. There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            Log.d("pretty", "3. " + t.toString());
        }

        return search;
    }

}

package com.codecool.micro_services.video_service.vimeo_service;


import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;



public class VimeoAPIService {
    private static final Logger logger = LoggerFactory.getLogger(VimeoAPIService.class);
    private static final String API_URL = "https://api.vimeo.com/videos";
    private static final String AUTH_TOKEN = "Bearer 79c255eb2e3d0145bb91a26c824662d8";

    private static VimeoAPIService INSTANCE;

    public static VimeoAPIService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VimeoAPIService();
        }
        return INSTANCE;
    }

    /**
     * Gets videos from vimeo by the search expression as iframe Json
     *
     * @param searchExpression - if not {@link StringUtils#isEmpty(Object)} acts as a filter.
     * @return - JSON received from the API as it is.
     * @throws IOException
     * @throws URISyntaxException
     */
    public String getVideoFromVimeo(String searchExpression) throws IOException, URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(API_URL);
        uriBuilder.addParameter("page", "1");  //optional
        uriBuilder.addParameter("per_page", "1"); //optional
        uriBuilder.addParameter("query", searchExpression);
        uriBuilder.addParameter("sort", "relevant");
        uriBuilder.addParameter("direction", "asc"); //optional
        logger.debug("URI prepared for request: {}", uriBuilder.toString());

        String response = execute(uriBuilder.build());
        logger.debug("HTTP response received: {}", response);

        String embedCode = getLinkFromJSON(response);
        logger.debug("Link from Vimeo response JSON: {}", embedCode);

        return embedCode;
    }


    /**
     * Executes the actual GET request against the given URI
     *
     * @param uri - obj containing path and params.
     * @return
     * @throws IOException
     */
    private String execute(URI uri) throws IOException {
        return Request.Get(uri)
                .addHeader("Authorization", AUTH_TOKEN)
                .execute()
                .returnContent()
                .asString();
    }

    /**
     * Gets embed Json from API's response
     *
     * @param jsonString - Json received from the API as it is.
     * @return
     * @throws IOException
     */
    private String getLinkFromJSON(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        JSONArray dataArray = (JSONArray) json.get("data");
        JSONObject video = (JSONObject) dataArray.get(0);
        JSONObject embed = (JSONObject) video.get("embed");
        return embed.get("html").toString();
    }


}


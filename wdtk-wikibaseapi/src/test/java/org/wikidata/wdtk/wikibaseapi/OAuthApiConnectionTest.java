package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2020 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class OAuthApiConnectionTest {

    private static final String CONSUMER_KEY = "consumer_key";
    private static final String CONSUMER_SECRET = "consumer_secret";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_SECRET = "access_secret";

    private static MockWebServer server;
    private OAuthApiConnection connection;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String NOT_LOGGED_IN_SERIALIZED = "{\"baseUrl\":\"" + server.url("/w/api.php") + "\"," +
            "\"consumerKey\":null," +
            "\"consumerSecret\":null," +
            "\"accessToken\":null," +
            "\"accessSecret\":null," +
            "\"username\":\"\"," +
            "\"loggedIn\":false," +
            "\"tokens\":{}," +
            "\"connectTimeout\":-1," +
            "\"readTimeout\":-1}";
    private final String LOGGED_IN_SERIALIZED = "{\"baseUrl\":\"" + server.url("/w/api.php") + "\"," +
            "\"consumerKey\":\"consumer_key\"," +
            "\"consumerSecret\":\"consumer_secret\"," +
            "\"accessToken\":\"access_token\"," +
            "\"accessSecret\":\"access_secret\"," +
            "\"username\":\"foo\"," +
            "\"loggedIn\":true," +
            "\"tokens\":{}," +
            "\"connectTimeout\":-1," +
            "\"readTimeout\":-1," +
            "\"customUserAgent\":\"customAgent\"}";

    @BeforeClass
    public static void init() throws IOException {
        Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String requestBody = request.getBody().readUtf8();
                Map<String, String> params = parseFormBody(requestBody);

                if (isGetEntitiesRequest(params)) {
                    return new MockResponse()
                            .addHeader("Content-Type", "application/json; charset=utf-8")
                            .setBody("{\"entities\":{\"Q8\":{\"pageid\":134,\"ns\":0,\"title\":\"Q8\",\"lastrevid\":1174289176,\"modified\":\"2020-05-05T12:39:07Z\",\"type\":\"item\",\"id\":\"Q8\",\"labels\":{\"fr\":{\"language\":\"fr\",\"value\":\"bonheur\"}},\"descriptions\":{\"fr\":{\"language\":\"fr\",\"value\":\"état émotionnel\"}},\"aliases\":{\"fr\":[{\"language\":\"fr\",\"value\":\":)\"},{\"language\":\"fr\",\"value\":\"\uD83D\uDE04\"},{\"language\":\"fr\",\"value\":\"\uD83D\uDE03\"}]},\"sitelinks\":{\"enwiki\":{\"site\":\"enwiki\",\"title\":\"Happiness\",\"badges\":[]}}}},\"success\":1}");
                }

                if (isUserInfoRequest(params)) {
                    return new MockResponse()
                            .addHeader("Content-Type", "application/json; charset=utf-8")
                            .setBody("{\"batchcomplete\":\"\",\"query\":{\"userinfo\":{\"id\":2333,\"name\":\"foo\"}}}");
                }

                return new MockResponse().setResponseCode(404);
            }
        };

        server = new MockWebServer();
        server.setDispatcher(dispatcher);
        server.start();
    }

    @AfterClass
    public static void finish() throws IOException {
        server.shutdown();
    }

    @Before
    public void setUp() {
        connection = new OAuthApiConnection(server.url("/w/api.php").toString(),
                CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_SECRET);
        connection.setCustomUserAgent("customAgent");
    }

    @Test
    public void testFetchOnlineData() throws IOException, MediaWikiApiErrorException, InterruptedException {
        WikibaseDataFetcher wbdf = new WikibaseDataFetcher(connection, Datamodel.SITE_WIKIDATA);
        wbdf.getFilter().setSiteLinkFilter(Collections.singleton("enwiki"));
        wbdf.getFilter().setLanguageFilter(Collections.singleton("fr"));
        wbdf.getFilter().setPropertyFilter(
                Collections.<PropertyIdValue>emptySet());
        EntityDocument q8 = wbdf.getEntityDocument("Q8");
        String result = "";
        if (q8 instanceof ItemDocument) {
            result = "The French label for entity Q8 is "
                    + ((ItemDocument) q8).getLabels().get("fr").getText()
                    + "\nand its English Wikipedia page has the title "
                    + ((ItemDocument) q8).getSiteLinks().get("enwiki")
                    .getPageTitle() + ".";
        }
        assertEquals("The French label for entity Q8 is bonheur\n" +
                "and its English Wikipedia page has the title Happiness.", result);
    }

    @Test
    public void testLogout() throws IOException, InterruptedException {
        assertTrue(connection.isLoggedIn());
        assertEquals("foo", connection.getCurrentUser());

        connection.logout();
        assertEquals("", connection.getCurrentUser());
        assertFalse(connection.isLoggedIn());
        assertEquals("", connection.getCurrentUser());

        RecordedRequest request = server.takeRequest();
        assertNotNull(request.getHeader("Authorization"));
    }

    @Test
    public void testSerialize() throws IOException, LoginFailedException {
        String jsonSerialization = mapper.writeValueAsString(connection);
        assertEquals(LOGGED_IN_SERIALIZED, jsonSerialization);
    }

    @Test
    public void testDeserialize() throws IOException {
        OAuthApiConnection newConnection = mapper.readValue(LOGGED_IN_SERIALIZED, OAuthApiConnection.class);
        assertTrue(newConnection.isLoggedIn());
        assertEquals(CONSUMER_KEY, newConnection.getConsumerKey());
        assertEquals(CONSUMER_SECRET, newConnection.getConsumerSecret());
        assertEquals(ACCESS_TOKEN, newConnection.getAccessToken());
        assertEquals(ACCESS_SECRET, newConnection.getAccessSecret());
        assertEquals(server.url("/w/api.php").toString(), newConnection.getApiBaseUrl());
        assertEquals("foo", newConnection.getCurrentUser());
        assertEquals(-1, connection.getConnectTimeout());
        assertEquals(-1, connection.getReadTimeout());
        assertTrue(connection.getTokens().isEmpty());
    }

    @Test
    public void testDeserializeNotLogin() throws IOException {
        OAuthApiConnection connection = mapper.readValue(NOT_LOGGED_IN_SERIALIZED, OAuthApiConnection.class);
        assertFalse(connection.isLoggedIn());
        assertNull(CONSUMER_KEY, connection.getConsumerKey());
        assertNull(CONSUMER_SECRET, connection.getConsumerSecret());
        assertNull(ACCESS_TOKEN, connection.getAccessToken());
        assertNull(ACCESS_SECRET, connection.getAccessSecret());
        assertEquals(server.url("/w/api.php").toString(), connection.getApiBaseUrl());
        assertEquals(-1, connection.getConnectTimeout());
        assertEquals(-1, connection.getReadTimeout());
        assertTrue(connection.getTokens().isEmpty());
    }

    private static Map<String, String> parseFormBody(String body) {
        Map<String, String> params = new HashMap<>();
        if (body == null || body.isEmpty()) {
            return params;
        }
        for (String pair : body.split("&")) {
            int idx = pair.indexOf('=');
            String key = idx >= 0 ? pair.substring(0, idx) : pair;
            String value = idx >= 0 ? pair.substring(idx + 1) : "";
            params.put(
                    URLDecoder.decode(key, StandardCharsets.UTF_8),
                    URLDecoder.decode(value, StandardCharsets.UTF_8)
            );
        }
        return params;
    }

    private static boolean isGetEntitiesRequest(Map<String, String> params) {
        return "wbgetentities".equals(params.get("action"))
                && "Q8".equals(params.get("ids"))
                && "enwiki".equals(params.get("sitefilter"))
                && "fr".equals(params.get("languages"))
                && "info|datatype|labels|aliases|descriptions|sitelinks".equals(params.get("props"))
                && "user".equals(params.get("assert"))
                && "json".equals(params.get("format"));
    }

    private static boolean isUserInfoRequest(Map<String, String> params) {
        return "query".equals(params.get("action"))
                && "userinfo".equals(params.get("meta"))
                && "user".equals(params.get("assert"))
                && "json".equals(params.get("format"));
    }
}

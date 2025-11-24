package org.wikidata.wdtk.examples;

/*-
 * #%L
 * Wikidata Toolkit Examples
 * %%
 * Copyright (C) 2014 - 2025 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

/**
 * Example class for retrieving multiple values from a specific property of a Wikidata item.
 * This is the Java equivalent of the TypeScript getMultipleValues endpoint.
 *
 * @author nahomaraya
 */
public final class TimelineExample {

    /** Wikidata data fetcher instance. */
    private final WikibaseDataFetcher fetcher;
    /** Executor service for parallel processing. */
    private final ExecutorService executorService;

    /** Property ID for image (P18). */
    private static final String PROP_IMAGE = "P18";
    /** Property ID for coordinates (P625). */
    private static final String PROP_COORDINATES = "P625";
    /** Property ID for location (P276). */
    private static final String PROP_LOCATION = "P276";
    /** Property ID for country (P17). */
    private static final String PROP_COUNTRY = "P17";
    /** Property ID for located in administrative territorial entity (P131). */
    private static final String PROP_LOCATED_IN = "P131";

    /** Property ID for point in time (P585). */
    private static final String PROP_POINT_IN_TIME = "P585";
    /** Property ID for start time (P580). */
    private static final String PROP_START_TIME = "P580";
    /** Property ID for end time (P582). */
    private static final String PROP_END_TIME = "P582";
    /** Property ID for birthdate (P569). */
    private static final String PROP_BIRTH_DATE = "P569";
    /** Property ID for death date (P570). */
    private static final String PROP_DEATH_DATE = "P570";
    /** Property ID for inception (P571). */
    private static final String PROP_INCEPTION = "P571";
    /** Property ID for publication date (P577). */
    private static final String PROP_PUBLICATION_DATE = "P577";

    /** Number of threads for parallel processing. */
    private static final int THREAD_POOL_SIZE = 5;

    /**
     * Constructor for TimetrailExample.
     */
    public TimelineExample() {
        this.fetcher = WikibaseDataFetcher.getWikidataDataFetcher();
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    /**
     * Shuts down the executor service.
     * Call this method when done using the TimetrailExample instance.
     */
    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * Main method to demonstrate usage.
     *
     * @param args command line arguments
     * @throws Exception if an error occurs
     */
    public static void main(final String[] args) throws Exception {
        TimelineExample example = new TimelineExample();
        try {
            // Example: Get all significant events (P793) of French Revolution (Q6534)
            String itemId = "Q6534";
            String propertyId = "P793";

            List<ValueDetails> results =
                    example.getValuesFromProperty(itemId, propertyId);

            System.out.println("Results for " + itemId + " - " + propertyId + ":");
            System.out.println("--------------------------------------------------");
            for (ValueDetails detail : results) {
                System.out.println(detail);
            }
        } catch (Exception e) {
            Logger.getLogger("Exception Occurred", String.valueOf(e));
        } finally {
            example.shutdown();
        }
    }

    /**
     * Gets all values from a specific property of an item.
     * This is the core logic equivalent to the TypeScript getValuesFromProperty.
     * Uses CompletableFuture to fetch value details in parallel.
     *
     * @param itemId The QID of the item (e.g., "Q361")
     * @param propertyId The PID of the property (e.g., "P828")
     * @return List of ValueDetails objects
     * @throws Exception if an error occurs during fetching
     */
    private List<ValueDetails> getValuesFromProperty(final String itemId,
                                                     final String propertyId)
            throws Exception {

        List<ValueDetails> results = new ArrayList<>();

        // Fetch the item document
        EntityDocument entity = fetcher.getEntityDocument(itemId);
        if (!(entity instanceof ItemDocument)) {
            System.out.println("Entity is not an item: " + itemId);
            return results;
        }

        ItemDocument item = (ItemDocument) entity;

        // Find the statement group for the specified property
        StatementGroup statementGroup = null;
        for (StatementGroup sg : item.getStatementGroups()) {
            if (sg.getProperty().getId().equals(propertyId)) {
                statementGroup = sg;
                break;
            }
        }

        if (statementGroup == null) {
            System.out.println("Property " + propertyId
                    + " not found on item " + itemId);
            return results;
        }

        // Collect all value QIDs first
        List<String> valueQIDs = new ArrayList<>();
        for (Statement statement : statementGroup.getStatements()) {
            Value mainValue = getValue(statement.getClaim().getMainSnak());

            if (mainValue instanceof ItemIdValue) {
                ItemIdValue itemValue = (ItemIdValue) mainValue;
                String valueQID = itemValue.getId();
                valueQIDs.add(valueQID);
            }
        }

        // Create CompletableFutures for parallel fetching
        List<CompletableFuture<ValueDetails>> futures = valueQIDs.stream()
                .map(valueQID -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return fetchValueDetails(valueQID);
                    } catch (Exception e) {
                        System.err.println("Error fetching details for "
                                + valueQID + ": " + e.getMessage());
                        return null;
                    }
                }, executorService))
                .collect(Collectors.toList());

        // Wait for all futures to complete and collect results
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        // Block until all are complete
        allOf.join();

        // Collect the results, filtering out nulls
        results = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Sort by date if available (ascending: oldest first)
        Collections.sort(results);

        return results;
    }

    /**
     * Fetches detailed information about a value (item).
     * This mirrors the TypeScript getValueDetails logic.
     *
     * @param qid The QID of the value to fetch
     * @return ValueDetails object with name, description, location, date, image
     * @throws MediaWikiApiErrorException if API error occurs
     * @throws IOException if I/O error occurs
     */
    private ValueDetails fetchValueDetails(final String qid)
            throws MediaWikiApiErrorException, IOException {
        EntityDocument entity = fetcher.getEntityDocument(qid);
        if (!(entity instanceof ItemDocument)) {
            return null;
        }

        ItemDocument item = (ItemDocument) entity;

        String name = item.findLabel("en");
        String description = item.findDescription("en");

        // Extract date
        String dateStr = extractDateAsString(item);

        // Extract location
        LocationInfo location = extractLocation(item);

        // Extract image
        String imageName = extractImage(item);

        return new ValueDetails(
                qid,
                name != null ? name : qid,
                description,
                dateStr,
                location,
                imageName
        );
    }

    /**
     * Extracts a date from an item's statements and returns it as ISO string.
     * Checks multiple date-related properties in priority order.
     *
     * @param item The ItemDocument to extract date from
     * @return ISO 8601 formatted date string or null if no date found
     */
    private String extractDateAsString(final ItemDocument item) {
        String[] dateProperties = {
                PROP_POINT_IN_TIME,
                PROP_START_TIME,
                PROP_INCEPTION,
                PROP_PUBLICATION_DATE,
                PROP_BIRTH_DATE,
                PROP_END_TIME,
                PROP_DEATH_DATE
        };

        for (String propId : dateProperties) {
            for (StatementGroup sg : item.getStatementGroups()) {
                if (sg.getProperty().getId().equals(propId)) {
                    for (Statement stmt : sg.getStatements()) {
                        Value v = getValue(stmt.getClaim().getMainSnak());
                        if (v instanceof TimeValue) {
                            TimeValue tv = (TimeValue) v;
                            return formatTimeValue(tv);
                        }

                        // Also check qualifiers for time values
                        TimeValue qualifierTime =
                                extractTimeQualifier(stmt);
                        if (qualifierTime != null) {
                            return formatTimeValue(qualifierTime);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Formats a TimeValue to ISO 8601 string format.
     *
     * @param tv The TimeValue to format
     * @return ISO 8601 formatted date string like "1853-01-01T00:00:00.000Z"
     */
    private String formatTimeValue(final TimeValue tv) {
        if (tv == null) {
            return null;
        }

        long year = tv.getYear();
        byte month = tv.getMonth();
        byte day = tv.getDay();

        // Handle default values (0 means unknown)
        if (month == 0) {
            month = 1;
        }
        if (day == 0) {
            day = 1;
        }

        return String.format("%04d-%02d-%02dT00:00:00.000Z", year, month, day);
    }

    /**
     * Extracts location information from an item.
     *
     * @param item The ItemDocument to extract location from
     * @return LocationInfo with locationName, latitude, and longitude or null
     * @throws MediaWikiApiErrorException if API error occurs
     * @throws IOException if I/O error occurs
     */
    private LocationInfo extractLocation(final ItemDocument item)
            throws MediaWikiApiErrorException, IOException {
        // Try multiple location-related properties
        String[] locationProperties = {
                PROP_LOCATION,
                PROP_COUNTRY,
                PROP_LOCATED_IN
        };

        for (String propId : locationProperties) {
            for (StatementGroup sg : item.getStatementGroups()) {
                if (sg.getProperty().getId().equals(propId)) {
                    for (Statement stmt : sg.getStatements()) {
                        Value v = getValue(stmt.getClaim().getMainSnak());
                        if (v instanceof ItemIdValue) {
                            ItemIdValue locationItem = (ItemIdValue) v;
                            String locationQID = locationItem.getId();

                            // Try to get coordinates for this location
                            CoordinateInfo coords =
                                    getCoordinatesForLocation(locationQID);

                            // Get location name
                            String locationName = getItemLabel(locationQID);

                            return new LocationInfo(
                                    locationName != null
                                            ? locationName : locationQID,
                                    coords != null ? coords.getLatitude() : "",
                                    coords != null ? coords.getLongitude() : ""
                            );
                        }
                    }
                }
            }
        }

        // Also check if the item itself has coordinates
        CoordinateInfo directCoords =
                getCoordinatesForLocation(item.getEntityId().getId());
        if (directCoords != null) {
            return new LocationInfo(
                    item.findLabel("en"),
                    directCoords.getLatitude(),
                    directCoords.getLongitude()
            );
        }

        return null;
    }

    /**
     * Gets coordinates (P625) for a given location item.
     *
     * @param locationQID The QID of the location
     * @return CoordinateInfo with latitude and longitude or null
     * @throws MediaWikiApiErrorException if API error occurs
     * @throws IOException if I/O error occurs
     */
    private CoordinateInfo getCoordinatesForLocation(final String locationQID)
            throws MediaWikiApiErrorException, IOException {
        EntityDocument locationEntity = fetcher.getEntityDocument(locationQID);
        if (!(locationEntity instanceof ItemDocument)) {
            return null;
        }

        ItemDocument locationItem = (ItemDocument) locationEntity;

        for (StatementGroup sg : locationItem.getStatementGroups()) {
            if (sg.getProperty().getId().equals(PROP_COORDINATES)) {
                for (Statement stmt : sg.getStatements()) {
                    Value v = getValue(stmt.getClaim().getMainSnak());
                    if (v instanceof GlobeCoordinatesValue) {
                        GlobeCoordinatesValue coords =
                                (GlobeCoordinatesValue) v;
                        return new CoordinateInfo(
                                String.valueOf(coords.getLatitude()),
                                String.valueOf(coords.getLongitude())
                        );
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the label for an item.
     *
     * @param qid The QID of the item
     * @return The English label or null if not found
     * @throws MediaWikiApiErrorException if API error occurs
     * @throws IOException if I/O error occurs
     */
    private String getItemLabel(final String qid)
            throws MediaWikiApiErrorException, IOException {
        EntityDocument entity = fetcher.getEntityDocument(qid);
        if (entity instanceof ItemDocument) {
            return ((ItemDocument) entity).findLabel("en");
        }
        return null;
    }

    /**
     * Extracts image filename (P18) from an item.
     *
     * @param item The ItemDocument to extract image from
     * @return The Commons filename or null
     */
    private String extractImage(final ItemDocument item) {
        for (StatementGroup sg : item.getStatementGroups()) {
            if (sg.getProperty().getId().equals(PROP_IMAGE)) {
                for (Statement stmt : sg.getStatements()) {
                    Value v = getValue(stmt.getClaim().getMainSnak());
                    if (v instanceof StringValue) {
                        StringValue imageValue = (StringValue) v;
                        return imageValue.getString();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Helper to extract a TimeValue from a specific qualifier property.
     *
     * @param statement The statement to search
     * @return TimeValue if found, null otherwise
     */
    private static TimeValue extractTimeQualifier(final Statement statement) {
        for (SnakGroup qg : statement.getClaim().getQualifiers()) {
            if (qg.getProperty().getId().equals(TimelineExample.PROP_POINT_IN_TIME)) {
                for (Snak s : qg.getSnaks()) {
                    Value v = getValue(s);
                    if (v instanceof TimeValue) {
                        return (TimeValue) v;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Helper to get the Value from a Snak if it is a ValueSnak.
     *
     * @param snak The snak to extract value from
     * @return The Value or null
     */
    private static Value getValue(final Snak snak) {
        if (snak instanceof ValueSnak) {
            return ((ValueSnak) snak).getValue();
        }
        return null;
    }

    /**
     * Inner class to represent coordinate information.
     */
    static class CoordinateInfo {
        /** Latitude value. */
        private final String latitude;
        /** Longitude value. */
        private final String longitude;

        /**
         * Constructor for CoordinateInfo.
         *
         * @param lat Latitude value
         * @param lon Longitude value
         */
        CoordinateInfo(final String lat, final String lon) {
            this.latitude = lat;
            this.longitude = lon;
        }

        /**
         * Gets the latitude.
         *
         * @return latitude value
         */
        public String getLatitude() {
            return latitude;
        }

        /**
         * Gets the longitude.
         *
         * @return longitude value
         */
        public String getLongitude() {
            return longitude;
        }
    }

    /**
     * Inner class to represent location information.
     */
    static class LocationInfo {
        /** Location name. */
        private final String locationName;
        /** Latitude value. */
        private final String latitude;
        /** Longitude value. */
        private final String longitude;

        /**
         * Constructor for LocationInfo.
         *
         * @param locName Location name
         * @param lat Latitude value
         * @param lon Longitude value
         */
        LocationInfo(final String locName, final String lat, final String lon) {
            this.locationName = locName;
            this.latitude = lat;
            this.longitude = lon;
        }

        @Override
        public String toString() {
            if (latitude.isEmpty() && longitude.isEmpty()) {
                return locationName;
            }
            return String.format("%s (%.6s, %.6s)",
                    locationName, latitude, longitude);
        }
    }

    /**
     * Inner class to represent value details.
     * Equivalent to ValueDetailsResult interface in TypeScript.
     */
    static class ValueDetails implements Comparable<ValueDetails> {
        /** Item ID. */
        private final String id;
        /** Item name. */
        private final String name;
        /** Item description. */
        private final String description;
        /** ISO date string. */
        private final String date;
        /** Location information. */
        private final LocationInfo location;
        /** Image filename. */
        private final String image;

        /**
         * Constructor for ValueDetails.
         *
         * @param itemId Item ID
         * @param itemName Item name
         * @param desc Description
         * @param dateStr ISO date string
         * @param loc Location information
         * @param img Image filename
         */
        ValueDetails(final String itemId, final String itemName,
                     final String desc, final String dateStr,
                     final LocationInfo loc, final String img) {
            this.id = itemId;
            this.name = itemName;
            this.description = desc;
            this.date = dateStr;
            this.location = loc;
            this.image = img;
        }

        @Override
        public int compareTo(@NotNull final ValueDetails other) {
            // Sort by date (ascending: oldest first)
            if (this.date == null && other.date == null) {
                return 0;
            }
            if (this.date == null) {
                return 1;
            }
            if (other.date == null) {
                return -1;
            }

            return this.date.compareTo(other.date);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[%s] %s", id, name));

            if (date != null) {
                sb.append(String.format("%n  Date: %s", date));
            }

            if (location != null) {
                sb.append(String.format("%n  Location: %s", location));
            }

            if (image != null) {
                sb.append(String.format("%n  Image: %s", image));
            }

            if (description != null && !description.isEmpty()) {
                sb.append(String.format("%n  Description: %s", description));
            }

            return sb.toString();
        }
    }
}

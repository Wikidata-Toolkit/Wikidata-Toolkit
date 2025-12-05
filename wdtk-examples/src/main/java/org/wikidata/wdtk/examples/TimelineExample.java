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
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

/**
 * Example class that demonstrates how to extract and order property values
 * by their dates. The curernt example is  all significant events (P793) of
 * the French Revolution and ordering them chronologically.
 *
 * @author nahomaraya
 */
public final class TimelineExample {

    /** Property ID for point in time (P585). */
    private static final String PROP_POINT_IN_TIME = "P585";

    /**
     * Private constructor to prevent instantiation.
     */
    private TimelineExample() {
        // Utility class
    }

    /**
     * Main method to demonstrate usage.
     *
     * @param args command line arguments
     * @throws MediaWikiApiErrorException if API error occurs
     * @throws IOException if I/O error occurs
     */
    public static void main(final String[] args)
            throws MediaWikiApiErrorException, IOException {
        String itemId = "Q6534"; //French Revolution
        String propertyId = "P793"; //significant event

        System.out.println("Building timeline for " + itemId
                + " - property " + propertyId);
        List<TimelineEvent> timeline = buildTimeline(itemId, propertyId);

        for (TimelineEvent event : timeline) {
            System.out.println(event);
        }
    }

    /**
     * Builds a timeline by fetching all values of a property and ordering
     * them by the date found in each value item.
     *
     * @param itemId The QID of the item (e.g., "Q6534")
     * @param propertyId The PID of the property (e.g., "P793")
     * @return List of TimelineEvent objects sorted by date
     * @throws MediaWikiApiErrorException if API error occurs
     * @throws IOException if I/O error occurs
     * @throws IllegalArgumentException if entity is not an item or property not found
     */
    public static List<TimelineEvent> buildTimeline(final String itemId,
                                                    final String propertyId)
            throws MediaWikiApiErrorException, IOException {

        WikibaseDataFetcher fetcher = WikibaseDataFetcher.getWikidataDataFetcher();
        EntityDocument entity = fetcher.getEntityDocument(itemId);
        if (!(entity instanceof ItemDocument)) {
            throw new IllegalArgumentException("Entity is not an item: " + itemId);
        }

        ItemDocument item = (ItemDocument) entity;
        StatementGroup statementGroup = findStatementGroup(item, propertyId);
        if (statementGroup == null) {
            throw new IllegalArgumentException("Property " + propertyId
                    + " not found on item " + itemId);
        }

        List<String> valueQIDs = new ArrayList<>();
        for (Statement statement : statementGroup.getStatements()) {
            Value mainValue = getValue(statement.getClaim().getMainSnak());
            if (mainValue instanceof ItemIdValue) {
                ItemIdValue itemValue = (ItemIdValue) mainValue;
                valueQIDs.add(itemValue.getId());
            }
        }


        Map<String, EntityDocument> valueEntities =
                fetcher.getEntityDocuments(valueQIDs);

        List<TimelineEvent> events = new ArrayList<>();
        for (String valueQID : valueQIDs) {
            EntityDocument valueEntity = valueEntities.get(valueQID);
            if (valueEntity instanceof ItemDocument) {
                ItemDocument valueItem = (ItemDocument) valueEntity;
                String label = valueItem.findLabel("en");
                TimeValue dateValue = extractDateFromItem(valueItem);
                if (dateValue != null) {
                    events.add(new TimelineEvent(
                            valueQID,
                            label != null ? label : valueQID,
                            dateValue
                    ));
                }
            }
        }

        Collections.sort(events);
        return events;
    }

    /**
     * Extracts a date from an item by looking for time-valued properties.
     * Checks P585 (point in time) as the primary date property.
     *
     * @param item The ItemDocument to extract date from
     * @return TimeValue if found, null otherwise
     */
    private static TimeValue extractDateFromItem(final ItemDocument item) {
        // Look for P585 (point in time)
        for (StatementGroup sg : item.getStatementGroups()) {
            if (sg.getProperty().getId().equals(PROP_POINT_IN_TIME)) {
                for (Statement stmt : sg.getStatements()) {
                    Value v = getValue(stmt.getClaim().getMainSnak());
                    if (v instanceof TimeValue) {
                        return (TimeValue) v;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a statement group for a given property ID.
     *
     * @param item The ItemDocument to search
     * @param propertyId The property ID to find
     * @return StatementGroup if found, null otherwise
     */
    private static StatementGroup findStatementGroup(final ItemDocument item,
                                                     final String propertyId) {
        for (StatementGroup sg : item.getStatementGroups()) {
            if (sg.getProperty().getId().equals(propertyId)) {
                return sg;
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
     * Inner class to represent a timeline event.
     */
    static class TimelineEvent implements Comparable<TimelineEvent> {
        /** Property ID. */
        private final String id;
        /** Property label. */
        private final String label;
        /** Event time. */
        private final TimeValue time;

        /**
         * Constructor for TimelineEvent.
         *
         * @param eventId Event ID
         * @param eventLabel Event label
         * @param eventTime Event time
         */
        TimelineEvent(final String eventId, final String eventLabel,
                      final TimeValue eventTime) {
            this.id = eventId;
            this.label = eventLabel;
            this.time = eventTime;
        }

        /**
         * Gets the date string in ISO format.
         *
         * @return ISO formatted date string
         */
        public String getDateString() {
            byte month = time.getMonth();
            byte day = time.getDay();
            if (month == 0) {
                month = 1;
            }
            if (day == 0) {
                day = 1;
            }

            return String.format("%04d-%02d-%02d",
                    time.getYear(), month, day);
        }

        @Override
        public int compareTo(final TimelineEvent other) {
            if (this.time.getYear() != other.time.getYear()) {
                return Long.compare(this.time.getYear(), other.time.getYear());
            }
            if (this.time.getMonth() != other.time.getMonth()) {
                return Byte.compare(this.time.getMonth(),
                        other.time.getMonth());
            }
            return Byte.compare(this.time.getDay(), other.time.getDay());
        }

        @Override
        public String toString() {
            return String.format("[%s] %s (%s)", getDateString(), label, id);
        }
    }
}

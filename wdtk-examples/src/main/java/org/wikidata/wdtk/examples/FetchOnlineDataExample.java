package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

/**
 * This example illustrates simple data fetching from the Wikidata Action API.
 * It uses a {@link WikibaseDataFetcher} to establish a connection to the
 * Wikidata API directly and fetches details about several different
 * {@link EntityDocument}'s and {@link WbSearchEntitiesResult}'s.
 * 
 * This example does not download any dump file
 * 
 * <ul>
 * <li>Fetches a single and multiple Entities at a time using the unique QID
 * (Wikimedia identifier)
 * <li>Fetches using filters for a single entity to reduce the volume of data
 * returned
 * <li>Fetches a single and multiple Entities at a time using the title
 * <li>Searches for entities matching "Douglas Adams" from the "fr" Wiki
 * </ul>
 *
 * Options are provided to output results directly to a logfile using the static
 * attributes at the top of the class. Simply toggle the
 * {@code OUTPUT_RAW_RESULTS_DATA} to true and configure the desired output path
 * and filename {@code OUTPUT_FILE_NAME}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class FetchOnlineDataExample {

	/**
	 * Directory (destination) and filename for optional raw results data, should be
	 * a .log or .txt file
	 */
	static final String OUTPUT_FILE_NAME = "fetch-online-data-example.log";
	static final boolean OUTPUT_RAW_RESULTS_DATA = false;

	/**
	 * Main method that demonstrates various ways to fetch data from Wikidata
	 * using the WikibaseDataFetcher. This example shows how to:
	 * <ul>
	 * <li>Fetch a single entity by its ID</li>
	 * <li>Fetch multiple entities at once</li>
	 * <li>Use filters to limit the data returned</li>
	 * <li>Fetch entities by title from Wikipedia</li>
	 * <li>Search for entities by name</li>
	 * </ul>
	 * 
	 * @param args command line arguments (unused)
	 * @throws MediaWikiApiErrorException if there is an error with the MediaWiki API
	 * @throws IOException if there is an I/O error during data fetching
	 */
	public static void main(String[] args) throws MediaWikiApiErrorException, IOException {
		ExampleHelpers.configureLogging();
		printDocumentation();

		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(
				BasicApiConnection.getWikidataApiConnection(),
				Datamodel.SITE_WIKIDATA);

		/**
		 * Fetch a single EntityDocument from Wikidata using its entityId
		 */
		System.out.println("*** Fetching data for one entity:");
		EntityDocument q42 = wbdf.getEntityDocument("L1259271");
		System.out.println(q42);

		if (q42 instanceof ItemDocument) {
			System.out.println("The English name for entity Q42 is "
					+ ((ItemDocument) q42).getLabels().get("en").getText());
			if (OUTPUT_RAW_RESULTS_DATA) {
				writeFinalResults(q42);
			}
		}

		/**
		 * Fetch multiple Entity Documents from Wikidata using their entitiyId values.
		 */
		System.out.println("*** Fetching data for several entities:");
		Map<String, EntityDocument> results = wbdf.getEntityDocuments("Q80",
				"P31");
		// Keys of this map are Qids, but we only use the values here:
		for (EntityDocument ed : results.values()) {
			System.out.println("Successfully retrieved data for "
					+ ed.getEntityId().getId());
			if (OUTPUT_RAW_RESULTS_DATA) {
				writeFinalResults(ed);
			}
		}

		/**
		 * Fetch single Entity Document using filters to limit the scope of data
		 * returned
		 */
		System.out
				.println("*** Fetching data using filters to reduce data volume:");
		// Only site links from English Wikipedia:
		wbdf.getFilter().setSiteLinkFilter(Collections.singleton("enwiki"));
		// Only labels in French:
		wbdf.getFilter().setLanguageFilter(Collections.singleton("fr"));
		// No statements at all:
		wbdf.getFilter().setPropertyFilter(Collections.emptySet());
		EntityDocument q8 = wbdf.getEntityDocument("Q8");
		if (q8 instanceof ItemDocument) {
			System.out.println("The French label for entity Q8 is "
					+ ((ItemDocument) q8).getLabels().get("fr").getText()
					+ "\nand its English Wikipedia page has the title "
					+ ((ItemDocument) q8).getSiteLinks().get("enwiki")
							.getPageTitle()
					+ ".");
			if (OUTPUT_RAW_RESULTS_DATA) {
				writeFinalResults(q8);
			}
		}

		/**
		 * Fetch single Entity Document from "enwiki" with the title: "Terry Pratchett"
		 */
		System.out.println("*** Fetching data based on page title:");
		EntityDocument edPratchett = wbdf.getEntityDocumentByTitle("enwiki",
				"Terry Pratchett");
		System.out.println("The Qid of Terry Pratchett is "
				+ edPratchett.getEntityId().getId());
		if (OUTPUT_RAW_RESULTS_DATA) {
			writeFinalResults(edPratchett);
		}

		/**
		 * Fetch multiple Entity Documents from "enwiki" with the title(s): "Wikidata",
		 * "Wikipedia"
		 */
		System.out.println("*** Fetching data based on several page titles:");
		results = wbdf.getEntityDocumentsByTitle("enwiki", "Wikidata",
				"Wikipedia");
		// In this case, keys are titles rather than Qids
		for (Entry<String, EntityDocument> entry : results.entrySet()) {
			System.out
					.println("Successfully retrieved data for page entitled \""
							+ entry.getKey() + "\": "
							+ entry.getValue().getEntityId().getId());
			if (OUTPUT_RAW_RESULTS_DATA) {
				writeFinalResults(entry.getValue());
			}
		}

		/**
		 * Search "fr" Wikidata for entities matching "Douglas Adams"
		 */
		System.out.println("** Doing search on Wikidata:");
		for (WbSearchEntitiesResult result : wbdf.searchEntities("Douglas Adams", "fr")) {
			System.out.println("Found " + result.getEntityId() + " with label " + result.getLabel());
			if (OUTPUT_RAW_RESULTS_DATA) {
				writeFinalResults(result);
			}
		}

		System.out.println("*** Done.");
	}

	/**
	 * Prints the raw {@link EntityDocument} results to the configured file.
	 * 
	 * @param document the entity document to print
	 */
	private static void writeFinalResults(EntityDocument document) {
		try (PrintStream out = new PrintStream(ExampleHelpers.openExampleFileOuputStream(OUTPUT_FILE_NAME))) {
			out.println(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the raw {@link WbSearchEntitiesResult} results to the configured file.
	 * 
	 * @param result the entity document to print
	 */
	private static void writeFinalResults(WbSearchEntitiesResult result) {
		try (PrintStream out = new PrintStream(ExampleHelpers.openExampleFileOuputStream(OUTPUT_FILE_NAME))) {
			out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: FetchOnlineDataExample");
		System.out.println("*** ");
		System.out
				.println("*** This program fetches individual data using the wikidata.org API.");
		System.out.println("*** It does not download any dump files.");
		System.out
				.println("********************************************************************");
	}

}

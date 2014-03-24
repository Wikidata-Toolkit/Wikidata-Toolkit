package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.testing.MockStringContentFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

/**
 * The test setup uses several files containing JSON. These files are read by
 * the org.json-parser into sample objects to be converted.
 * 
 * @author Fredo Erxleben
 * 
 */
public class JsonConverterTest {

	private static final String SAMPLE_FILES_BASE_PATH = "/testSamples/";
	private static final String BASE_IRI = "test";

	private JsonConverter unitUnderTest;
	private DataObjectFactory factory;

	@Before
	public void setUp() {
		this.factory = new DataObjectFactoryImpl();
		this.unitUnderTest = new JsonConverter(BASE_IRI,
				new DataObjectFactoryImpl());
	}

	@Test
	public void testEmptyProperty() throws JSONException, IOException {
		PropertyDocument propertyDocument = getPropertyDocumentFromResource(
				"EmptyProperty.json", "P1");

		PropertyIdValue propertyId = this.factory.getPropertyIdValue("P1",
				BASE_IRI);
		DatatypeIdValue datatypeId = this.factory
				.getDatatypeIdValue(DatatypeIdValue.DT_GLOBE_COORDINATES);
		PropertyDocument emptyPropertyDocument = this.factory
				.getPropertyDocument(propertyId,
						Collections.<MonolingualTextValue> emptyList(),
						Collections.<MonolingualTextValue> emptyList(),
						Collections.<MonolingualTextValue> emptyList(),
						datatypeId);

		assertEquals(propertyDocument, emptyPropertyDocument);
	}

	@Test
	public void testEmptyItem() throws JSONException, IOException {
		ItemDocument itemDocument = getItemDocumentFromResource(
				"EmptyItem.json", "Q1");

		ItemIdValue itemIdValue = this.factory.getItemIdValue("Q1", BASE_IRI);
		Map<String, SiteLink> siteLinks = new HashMap<>();
		ItemDocument emptyItemDocument = this.factory.getItemDocument(
				itemIdValue, Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), siteLinks);

		assertEquals(itemDocument, emptyItemDocument);
	}

	@Test
	public void testBasicItem() throws JSONException, IOException {
		ItemDocument basicItemDocument = this.createBasicItemDocument();
		ItemDocument itemDocument = getItemDocumentFromResource(
				"BasicItem.json", "Q1");

		assertEquals(itemDocument.getEntityId(),
				basicItemDocument.getEntityId());
		assertEquals(itemDocument.getItemId(), basicItemDocument.getItemId());
		assertEquals(itemDocument.getDescriptions(),
				basicItemDocument.getDescriptions());
		assertEquals(itemDocument.getAliases(), basicItemDocument.getAliases());
		assertEquals(itemDocument.getLabels(), basicItemDocument.getLabels());
		assertEquals(itemDocument.getSiteLinks(),
				basicItemDocument.getSiteLinks());
		assertEquals(itemDocument.getStatementGroups(),
				basicItemDocument.getStatementGroups());

		assertEquals(itemDocument, basicItemDocument);
	}

	private ItemDocument createBasicItemDocument() {

		ItemIdValue itemIdValue = this.factory.getItemIdValue("Q1", BASE_IRI);

		List<MonolingualTextValue> labels = new LinkedList<>();
		labels.add(this.factory.getMonolingualTextValue("test", "en"));

		List<MonolingualTextValue> descriptions = new LinkedList<>();
		descriptions.add(this.factory.getMonolingualTextValue("this is a test",
				"en"));

		List<MonolingualTextValue> aliases = new LinkedList<>();
		aliases.add(this.factory.getMonolingualTextValue("TEST", "en"));
		aliases.add(this.factory.getMonolingualTextValue("Test", "en"));

		List<StatementGroup> statementGroups = new LinkedList<>();
		List<Statement> statements = new LinkedList<>();

		PropertyIdValue propertyId = this.factory.getPropertyIdValue("P1",
				BASE_IRI);
		Value value = this.factory.getItemIdValue("Q1", BASE_IRI);
		Snak mainSnak = factory.getValueSnak(propertyId, value);
		List<? extends Snak> qualifiers = new LinkedList<>();
		Claim claim = this.factory.getClaim(itemIdValue, mainSnak, qualifiers);

		List<? extends Reference> references = new LinkedList<>();
		StatementRank rank = StatementRank.NORMAL;
		String statementId = "foo";
		statements.add(this.factory.getStatement(claim, references, rank,
				statementId));

		statementGroups.add(this.factory.getStatementGroup(statements));

		Map<String, SiteLink> siteLinks = new HashMap<>();
		List<String> badges = new LinkedList<>();
		String siteKey = "enwiki";
		String title = "test";
		siteLinks.put("enwiki",
				this.factory.getSiteLink(title, siteKey, "", badges));

		ItemDocument document = this.factory.getItemDocument(itemIdValue,
				labels, descriptions, aliases, statementGroups, siteLinks);
		return document;
	}

	@Test
	public void testRealItems() throws JSONException, IOException {
		getItemDocumentFromResource("Chicago.json", "Q1");
		getItemDocumentFromResource("Haaften.json", "Q1");
		getItemDocumentFromResource("Tours.json", "Q1");
		getItemDocumentFromResource("JohnPaulII.json", "Q1");
		getItemDocumentFromResource("Wernigerode.json", "Q1");
		// FIXME this does not test anything (copied from earlier test file)
	}

	@Test
	public void testClaims() throws JSONException, IOException {
		getItemDocumentFromResource("GlobalCoordinates.json", "Q1");
		getItemDocumentFromResource("StatementRanks.json", "Q1");
		getItemDocumentFromResource("SnakTypes.json", "Q1");
		// FIXME this does not test anything (copied from earlier test file)
	}

	@Test
	public void testDifferentNotations() throws JSONException, IOException {
		getItemDocumentFromResource("DifferentNotations.json", "Q1");
		getItemDocumentFromResource("StringEntityItem.json", "Q1");
		getPropertyDocumentFromResource("StringEntityProperty.json", "P1");
		// FIXME this does not test anything (copied from earlier test file)
	}

	@Test(expected = JSONException.class)
	public void testPropertyDocumentLacksDatatype() throws JSONException,
			IOException {
		getPropertyDocumentFromResource("NoEntityDocument.json", "P1");
	}

	@Test(expected = JSONException.class)
	public void testItemDocumentWithErrors() throws JSONException, IOException {
		getItemDocumentFromResource("MiscErrors.json", "Q1");
	}

	@Test
	public void testUniverse() throws JSONException, IOException {
		getItemDocumentFromResource("Universe.json", "Q1");
		// FIXME this does not test anything (copied from earlier test file)
	}
	@Test
  public void testJacksonDemo() throws Exception {
    DemoItem readValue = new ObjectMapper().readValue(
        Resources.getResource("testSamples/BasicItem.json"), DemoItem.class);

    // XXX List equals
    assertEquals(ImmutableList.of(new DemoEntity("item"), new DemoEntity(Integer.valueOf(1)))
        .toString(), readValue.entities.toString());
  }

  // XXX do not ignore
  @JsonIgnoreProperties({ "label", "description", "aliases", "links", "claims" })
  private static class DemoItem {
    private final ImmutableList<DemoEntity> entities;

    @JsonCreator
    private DemoItem(@JsonProperty("entity") List<DemoEntity> entities) {
      this.entities = ImmutableList.copyOf(entities);
    }
  }

  static class DemoEntity {

    private final Object key;

    @JsonCreator
    private DemoEntity(Object key) {
      this.key = key;
    }

    @Override
    public int hashCode() {
      return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return key.equals(obj);
    }

    @Override
    public String toString() {
      return key.toString() + " " + key.getClass().getCanonicalName();
    }

  }

	/**
	 * Applies the JSON converter to the JSON stored in the given resource to
	 * return an ItemDocument.
	 * 
	 * @param fileName
	 *            the file name only, no path information
	 * @param itemId
	 *            the string id of the item
	 * @throws IOException
	 * @throws JSONException
	 * @return the ItemDocument
	 */
	private ItemDocument getItemDocumentFromResource(String fileName,
			String itemId) throws IOException, JSONException {
		JSONObject jsonObject = getJsonObjectForResource(fileName);
		return this.unitUnderTest.convertToItemDocument(jsonObject, itemId);
	}

	/**
	 * Applies the JSON converter to the JSON stored in the given resource to
	 * return a PropertyDocument.
	 * 
	 * @param fileName
	 *            the file name only, no path information. The file is supposed
	 *            to be in the "resources/testSamples/"-directory.
	 * @param propertyId
	 *            the string id of the property
	 * @return the PropertyDocument
	 * @throws JSONException
	 * @throws IOException
	 */
	private PropertyDocument getPropertyDocumentFromResource(String fileName,
			String propertyId) throws IOException, JSONException {
		JSONObject jsonObject = getJsonObjectForResource(fileName);
		return this.unitUnderTest.convertToPropertyDocument(jsonObject,
				propertyId);
	}

	/**
	 * Returns a JSON object for the JSON stored in the given resource.
	 * 
	 * @param resourceName
	 *            a file name without any path information
	 * @return the JSONObject
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject getJsonObjectForResource(String resourceName)
			throws IOException, JSONException {
		URL resourceUrl = this.getClass().getResource(
				JsonConverterTest.SAMPLE_FILES_BASE_PATH + resourceName);
		String jsonString = MockStringContentFactory
				.getStringFromUrl(resourceUrl);
		return new JSONObject(jsonString);
	}

}

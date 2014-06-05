package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.rdf.RdfConverter;
import org.wikidata.wdtk.rdf.TestObjectFactory;

public class RdfConverterTest {

	ByteArrayOutputStream out;

	RdfWriter rdfWriter;
	RdfConverter rdfConverter;

	SitesImpl sites;

	ValueFactory rdfFactory = ValueFactoryImpl.getInstance();
	Resource resource = rdfFactory.createURI("http://test.org/");

	final TestObjectFactory objectFactory = new TestObjectFactory();
	final DataObjectFactory dataObjectFactory = new DataObjectFactoryImpl();

	@Before
	public void setUp() throws Exception {
		this.out = new ByteArrayOutputStream();
		this.rdfWriter = new RdfWriter(RDFFormat.N3, out);
		this.sites = new SitesImpl();
		this.rdfConverter = new RdfConverter(this.rdfWriter, this.sites);
		this.rdfWriter.start();
	}

	@Test
	public void testWriteItemDocument() throws RDFHandlerException,
			IOException, RDFParseException {
		ItemDocument document = this.objectFactory.createItemDocument();
		this.rdfConverter.writeItemDocument(document);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("ItemDocument.rdf")));
	}

	@Test
	public void testWritePropertyDocument() throws RDFHandlerException,
			RDFParseException, IOException {
		PropertyDocument document = this.objectFactory
				.createEmptyPropertyDocument();
		this.rdfConverter.writePropertyDocument(document);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("EmptyPropertyDocument.rdf")));
	}

	@Test
	public void testWriteBasicDeclarations() throws RDFHandlerException,
			RDFParseException, IOException {
		this.rdfConverter.writeBasicDeclarations();
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("BasicDeclarations.rdf")));

	}

	@Test
	public void testWriteNamespaceDeclarations() throws RDFHandlerException,
			RDFParseException, IOException {
		this.rdfConverter.writeNamespaceDeclarations();
		this.rdfWriter.finish();
		assertEquals(this.out.toString(),
				RdfTestHelpers.getResourceFromFile("Namespaces.rdf"));
	}

	@Test
	public void testWriteSiteLinks() throws RDFHandlerException, IOException,
			RDFParseException {
		this.sites.setSiteInformation("enwiki", "wikipedia", "en", "mediawiki",
				"http://en.wikipedia.org/w/$1",
				"http://en.wikipedia.org/wiki/$1");
		this.sites.setSiteInformation("dewiki", "wikipedia", "de", "mediawiki",
				"http://de.wikipedia.org/w/$1",
				"http://de.wikipedia.org/wiki/$1");
		Map<String, SiteLink> siteLinks = objectFactory.createSiteLinks();
		this.rdfConverter.writeSiteLinks(this.resource, siteLinks);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("SiteLinks.rdf")));

	}

	private ItemDocument createTestItemDocument() {
		ItemIdValue itemValue = dataObjectFactory.getItemIdValue("Q100",
				"http://www.wikidata.org/");
		ItemIdValue value1 = dataObjectFactory.getItemIdValue("Q10",
				"http://www.wikidata.org/");
		ItemIdValue value2 = dataObjectFactory.getItemIdValue("Q11",
				"http://www.wikidata.org/");
		PropertyIdValue propertyIdValueP31 = dataObjectFactory
				.getPropertyIdValue("P31", "http://www.wikidata.org/");
		PropertyIdValue propertyIdValueP279 = dataObjectFactory
				.getPropertyIdValue("P279", "http://www.wikidata.org/");
		// Statement InstaceOf - P31
		ValueSnak mainSnak1 = dataObjectFactory.getValueSnak(
				propertyIdValueP31, value1);
		Claim claim1 = dataObjectFactory.getClaim(itemValue, mainSnak1,
				Collections.<SnakGroup> emptyList());
		Statement statement1 = dataObjectFactory.getStatement(claim1,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"10103");
		List<Statement> statementList1 = new ArrayList<Statement>();
		statementList1.add(statement1);
		StatementGroup statementGroup1 = this.dataObjectFactory
				.getStatementGroup(statementList1);
		// Statement SubclassOf - P279
		ValueSnak mainSnak2 = dataObjectFactory.getValueSnak(
				propertyIdValueP279, value2);
		Claim claim2 = dataObjectFactory.getClaim(itemValue, mainSnak2,
				Collections.<SnakGroup> emptyList());
		Statement statement2 = dataObjectFactory.getStatement(claim2,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"10104");
		List<Statement> statementList2 = new ArrayList<Statement>();
		statementList2.add(statement2);
		StatementGroup statementGroup2 = this.dataObjectFactory
				.getStatementGroup(statementList2);

		List<StatementGroup> statementGroups = new ArrayList<StatementGroup>();
		statementGroups.add(statementGroup1);
		statementGroups.add(statementGroup2);
		return dataObjectFactory.getItemDocument(itemValue,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, Collections.<String, SiteLink> emptyMap());
	}

	@Test
	public void testWriteInstanceOfStatements() throws RDFHandlerException {
		this.rdfConverter.tasks = RdfSerializer.TASK_INSTANCE_OF;
		ItemDocument document = createTestItemDocument();
		this.rdfConverter.writeInstanceOfStatements(resource, document);
		this.rdfWriter.finish();
		assertEquals(out.toString(),
				"\n<http://test.org/> a <http://www.wikidata.org/Q10> .\n");
	}

	@Test
	public void testWriteSubclassOfStatements() throws RDFHandlerException {
		ItemDocument document = createTestItemDocument();
		this.rdfConverter.writeSubclassOfStatements(resource, document);
		this.rdfWriter.finish();
		assertEquals(
				out.toString(),
				"\n<http://test.org/> <http://www.w3.org/2000/01/rdf-schema#subClassOf> <http://www.wikidata.org/Q11> .\n");
	}

	@Test
	public void testTaxonomy() throws RDFHandlerException, RDFParseException,
			IOException {
		ItemDocument document = createTestItemDocument();
		rdfConverter.tasks = RdfSerializer.TASK_TAXONOMY
				| RdfSerializer.TASK_ITEMS;
		rdfConverter.writeItemDocument(document);
		this.rdfWriter.finish();
		Model model = RdfTestHelpers.parseRdf(out.toString());
		assertEquals(model, RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("taxonomy.rdf")));
	}

	@Test
	public void testWriteSimpleStatements() throws RDFHandlerException {
		ItemDocument document = createTestItemDocument();
		this.rdfConverter.writeSimpleStatements(resource, document);
		this.rdfWriter.finish();
		assertEquals(
				out.toString(),
				"\n<http://test.org/> <http://www.wikidata.org/P31c> <http://www.wikidata.org/Q10> ;\n	<http://www.wikidata.org/P279c> <http://www.wikidata.org/Q11> .\n");
	}

	@After
	public void clear() throws RDFHandlerException, IOException {
		this.out.close();
	}

}

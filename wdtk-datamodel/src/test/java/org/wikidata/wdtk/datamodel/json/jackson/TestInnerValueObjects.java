package org.wikidata.wdtk.datamodel.json.jackson;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerEntityId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerMonolingualText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * This class tests the inner objects lying behind the …ValueImpl-classes.
 *
 * @author Fredo Erxleben
 *
 */
public class TestInnerValueObjects {

	private JacksonInnerEntityId testEntityId;
	private JacksonInnerMonolingualText testMonolingualText;

	@Before
	public void setupTestEntityIds() {
		this.testEntityId = new JacksonInnerEntityId("Q1");
	}

	@Before
	public void setupTestMonolingualText() {
		this.testMonolingualText = new JacksonInnerMonolingualText("en",
				"foobar");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntityIdConstructor() {
		JacksonInnerEntityId testId = new JacksonInnerEntityId("W1");
		testId.getStringId(); // should fail
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntityIdSetterLegacy() {
		JacksonInnerEntityId testId = new JacksonInnerEntityId();
		testId.setNumericId(1);
		testId.setJsonEntityType("wrongType"); // should fail
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEntityIdSetter() {
		JacksonInnerEntityId testId = new JacksonInnerEntityId();
		testId.setStringId("W1"); // should fail
	}

	@Test
	public void testEntityIdMethods() {
		assertEquals("Q1", this.testEntityId.getStringId());
		assertEquals(this.testEntityId.getNumericId(), 1);

		// test equals
		assertEquals(this.testEntityId, new JacksonInnerEntityId("Q1"));
		assertEquals(this.testEntityId, this.testEntityId);
		assertFalse(this.testEntityId.equals(new Object()));
		assertFalse(this.testEntityId
				.equals(new JacksonInnerEntityId("Q2")));

	}

	@Test
	public void testMonolingualTextMethods() {
		assertEquals(this.testMonolingualText.getLanguage(), "en");
		assertEquals(this.testMonolingualText.getText(), "foobar");

		// test equals
		assertEquals(this.testMonolingualText, new JacksonInnerMonolingualText(
				"en", "foobar"));
		assertEquals(this.testMonolingualText, this.testMonolingualText);
		assertFalse(this.testMonolingualText.equals(new Object()));
		assertFalse(this.testMonolingualText
				.equals(new JacksonInnerMonolingualText("en", "barfoo")));
		assertFalse(this.testMonolingualText
				.equals(new JacksonInnerMonolingualText("de", "foobar")));

	}
}

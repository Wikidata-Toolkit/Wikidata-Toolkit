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
package org.wikidata.wdtk.datamodel.helpers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

public class StatementUpdateBuilderTest {

	static final EntityIdValue JOHN = Datamodel.makeWikidataItemIdValue("Q1");
	static final EntityIdValue RITA = Datamodel.makeWikidataItemIdValue("Q2");
	static final PropertyIdValue HAIR = Datamodel.makeWikidataPropertyIdValue("P1");
	static final PropertyIdValue EYES = Datamodel.makeWikidataPropertyIdValue("P2");
	static final PropertyIdValue SHIRT = Datamodel.makeWikidataPropertyIdValue("P3");
	static final PropertyIdValue TROUSERS = Datamodel.makeWikidataPropertyIdValue("P4");
	static final StringValue BROWN = Datamodel.makeStringValue("brown");
	static final StringValue SILVER = Datamodel.makeStringValue("silver");
	static final StringValue BLUE = Datamodel.makeStringValue("blue");
	static final Statement NOBODY_HAS_BROWN_HAIR = StatementBuilder
			.forSubjectAndProperty(ItemIdValue.NULL, HAIR)
			.withValue(BROWN)
			.build();
	static final Statement NOBODY_ALREADY_HAS_BROWN_HAIR = NOBODY_HAS_BROWN_HAIR.withStatementId("ID1");
	static final Statement JOHN_HAS_BROWN_HAIR = StatementBuilder
			.forSubjectAndProperty(JOHN, HAIR)
			.withValue(BROWN)
			.build();
	static final Statement JOHN_ALREADY_HAS_BROWN_HAIR = JOHN_HAS_BROWN_HAIR.withStatementId("ID2");
	static final Statement RITA_HAS_BROWN_HAIR = StatementBuilder
			.forSubjectAndProperty(RITA, HAIR)
			.withValue(BROWN)
			.build();
	static final Statement RITA_ALREADY_HAS_BROWN_HAIR = RITA_HAS_BROWN_HAIR.withStatementId("ID3");
	static final Statement JOHN_HAS_BROWN_EYES = StatementBuilder
			.forSubjectAndProperty(JOHN, EYES)
			.withValue(BROWN)
			.build();
	static final Statement JOHN_ALREADY_HAS_BROWN_EYES = JOHN_HAS_BROWN_EYES.withStatementId("ID4");
	static final Statement JOHN_HAS_SILVER_HAIR = StatementBuilder
			.forSubjectAndProperty(JOHN, HAIR)
			.withValue(SILVER)
			.build();
	static final Statement JOHN_ALREADY_HAS_SILVER_HAIR = JOHN_HAS_SILVER_HAIR.withStatementId("ID5");
	static final Statement JOHN_HAS_BLUE_SHIRT = StatementBuilder
			.forSubjectAndProperty(JOHN, SHIRT)
			.withValue(BLUE)
			.build();
	static final Statement JOHN_ALREADY_HAS_BLUE_SHIRT = JOHN_HAS_BLUE_SHIRT.withStatementId("ID6");
	static final Statement JOHN_HAS_BROWN_TROUSERS = StatementBuilder
			.forSubjectAndProperty(JOHN, TROUSERS)
			.withValue(BROWN)
			.build();
	static final Statement JOHN_ALREADY_HAS_BROWN_TROUSERS = JOHN_HAS_BROWN_TROUSERS.withStatementId("ID7");
	static final Statement JOHN_HAS_BLUE_TROUSERS = StatementBuilder
			.forSubjectAndProperty(JOHN, TROUSERS)
			.withValue(BLUE)
			.build();
	static final Statement JOHN_HAS_BLUE_EYES = StatementBuilder
			.forSubjectAndProperty(JOHN, EYES)
			.withValue(BLUE)
			.build();
	static final Statement JOHN_ALREADY_HAS_BLUE_EYES = JOHN_HAS_BLUE_EYES.withStatementId("ID8");

	@Test
	public void testCreate() {
		StatementUpdate update = StatementUpdateBuilder.create().build();
		assertThat(update.getAddedStatements(), is(empty()));
		assertThat(update.getReplacedStatements(), is(anEmptyMap()));
		assertThat(update.getRemovedStatements(), is(empty()));
	}

	@Test
	public void testForStatements() {
		assertThrows(NullPointerException.class, () -> StatementUpdateBuilder.forStatements(null));
		assertThrows(NullPointerException.class,
				() -> StatementUpdateBuilder.forStatements(Arrays.asList(JOHN_ALREADY_HAS_BROWN_HAIR, null)));
		// no statement subject
		assertThrows(IllegalArgumentException.class,
				() -> StatementUpdateBuilder.forStatements(Arrays.asList(NOBODY_ALREADY_HAS_BROWN_HAIR)));
		// no statement ID
		assertThrows(IllegalArgumentException.class,
				() -> StatementUpdateBuilder.forStatements(Arrays.asList(JOHN_HAS_BROWN_HAIR)));
		// duplicate statement ID
		assertThrows(IllegalArgumentException.class, () -> StatementUpdateBuilder
				.forStatements(Arrays.asList(JOHN_ALREADY_HAS_BROWN_HAIR, JOHN_ALREADY_HAS_BROWN_HAIR)));
		// inconsistent statement subject
		assertThrows(IllegalArgumentException.class, () -> StatementUpdateBuilder
				.forStatements(Arrays.asList(JOHN_ALREADY_HAS_BROWN_HAIR, RITA_ALREADY_HAS_BROWN_HAIR)));
		// no base statements
		StatementUpdateBuilder.forStatements(Collections.emptyList());
		StatementUpdate update = StatementUpdateBuilder
				.forStatements(Arrays.asList(JOHN_ALREADY_HAS_BROWN_HAIR, JOHN_ALREADY_HAS_BROWN_EYES))
				.build();
		assertThat(update.getAddedStatements(), is(empty()));
		assertThat(update.getReplacedStatements(), is(anEmptyMap()));
		assertThat(update.getRemovedStatements(), is(empty()));
	}

	@Test
	public void testForStatementGroups() {
		assertThrows(NullPointerException.class, () -> StatementUpdateBuilder.forStatementGroups(null));
		StatementGroup johnAlreadyHasBrownAndSilverHair = Datamodel.makeStatementGroup(
				Arrays.asList(JOHN_ALREADY_HAS_BROWN_HAIR, JOHN_ALREADY_HAS_SILVER_HAIR));
		assertThrows(IllegalArgumentException.class,
				() -> StatementUpdateBuilder.forStatementGroups(Arrays.asList(johnAlreadyHasBrownAndSilverHair, null)));
		// no statement groups
		StatementUpdateBuilder.forStatementGroups(Collections.emptyList());
		StatementUpdate update = StatementUpdateBuilder
				.forStatementGroups(Arrays.asList(johnAlreadyHasBrownAndSilverHair))
				.build();
		assertThat(update.getAddedStatements(), is(empty()));
		assertThat(update.getReplacedStatements(), is(anEmptyMap()));
		assertThat(update.getRemovedStatements(), is(empty()));
	}

	@Test
	public void testBlindAddition() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.addStatement(null));
		builder.addStatement(JOHN_HAS_BROWN_HAIR); // simple case
		builder.addStatement(JOHN_HAS_BROWN_HAIR); // duplicates allowed
		builder.addStatement(JOHN_ALREADY_HAS_BROWN_EYES); // strip ID
		// inconsistent subject
		assertThrows(IllegalArgumentException.class, () -> builder.addStatement(RITA_HAS_BROWN_HAIR));
		StatementUpdate update = builder.build();
		assertEquals(update.getAddedStatements(),
				Arrays.asList(JOHN_HAS_BROWN_HAIR, JOHN_HAS_BROWN_HAIR, JOHN_HAS_BROWN_EYES));
	}

	@Test
	public void testBlindReplacement() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.replaceStatement(null));
		builder.removeStatement(JOHN_ALREADY_HAS_BROWN_EYES.getStatementId());
		builder.replaceStatement(JOHN_ALREADY_HAS_BROWN_HAIR); // simple case
		builder.replaceStatement(JOHN_ALREADY_HAS_BROWN_EYES); // previously removed
		builder.replaceStatement(JOHN_ALREADY_HAS_BROWN_HAIR); // replace twice
		// inconsistent subject
		assertThrows(IllegalArgumentException.class, () -> builder.replaceStatement(RITA_ALREADY_HAS_BROWN_HAIR));
		// no statement ID
		assertThrows(IllegalArgumentException.class, () -> builder.replaceStatement(JOHN_HAS_SILVER_HAIR));
		StatementUpdate update = builder.build();
		assertThat(update.getRemovedStatements(), is(empty()));
		assertThat(
				update.getReplacedStatements().values(),
				containsInAnyOrder(JOHN_ALREADY_HAS_BROWN_HAIR, JOHN_ALREADY_HAS_BROWN_EYES));
	}

	@Test
	public void testBlindRemoval() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.removeStatement(null));
		assertThrows(IllegalArgumentException.class, () -> builder.removeStatement(""));
		builder.replaceStatement(JOHN_ALREADY_HAS_BROWN_EYES);
		builder.removeStatement(JOHN_ALREADY_HAS_BROWN_HAIR.getStatementId()); // simple case
		builder.removeStatement(JOHN_ALREADY_HAS_BROWN_EYES.getStatementId()); // previously replaced
		StatementUpdate update = builder.build();
		assertThat(update.getReplacedStatements(), is(anEmptyMap()));
		assertThat(update.getRemovedStatements(), containsInAnyOrder(
				JOHN_ALREADY_HAS_BROWN_HAIR.getStatementId(),
				JOHN_ALREADY_HAS_BROWN_EYES.getStatementId()));
	}

	@Test
	public void testBaseAddition() {
		StatementUpdateBuilder builder = StatementUpdateBuilder
				.forStatements(Arrays.asList(JOHN_ALREADY_HAS_BROWN_HAIR));
		// inconsistent subject
		assertThrows(IllegalArgumentException.class, () -> builder.addStatement(RITA_HAS_BROWN_HAIR));
		builder.addStatement(JOHN_HAS_BROWN_EYES); // simple case
		builder.addStatement(JOHN_ALREADY_HAS_BROWN_HAIR); // duplicating existing statements is allowed
		StatementUpdate update = builder.build();
		assertEquals(update.getAddedStatements(), Arrays.asList(JOHN_HAS_BROWN_EYES, JOHN_HAS_BROWN_HAIR));
	}

	@Test
	public void testBaseReplacement() {
		StatementUpdateBuilder builder = StatementUpdateBuilder.forStatements(Arrays.asList(
				JOHN_ALREADY_HAS_BROWN_HAIR,
				JOHN_ALREADY_HAS_BROWN_EYES,
				JOHN_ALREADY_HAS_BLUE_SHIRT,
				JOHN_ALREADY_HAS_BROWN_TROUSERS));
		builder.removeStatement(JOHN_ALREADY_HAS_BROWN_EYES.getStatementId());
		Statement johnChangesBrownTrousersToBlueTrousers = JOHN_HAS_BLUE_TROUSERS
				.withStatementId(JOHN_ALREADY_HAS_BROWN_TROUSERS.getStatementId());
		builder.replaceStatement(johnChangesBrownTrousersToBlueTrousers);
		// inconsistent subject
		assertThrows(IllegalArgumentException.class, () -> builder.replaceStatement(
				RITA_ALREADY_HAS_BROWN_HAIR.withStatementId(JOHN_ALREADY_HAS_BROWN_EYES.getStatementId())));
		// unknown ID
		assertThrows(IllegalArgumentException.class,
				() -> builder.replaceStatement(JOHN_ALREADY_HAS_BROWN_HAIR.withStatementId("ID999")));
		Statement johnChangesBrownHairToSilverHair = JOHN_HAS_SILVER_HAIR
				.withStatementId(JOHN_ALREADY_HAS_BROWN_HAIR.getStatementId());
		builder.replaceStatement(johnChangesBrownHairToSilverHair); // simple case
		builder.replaceStatement(JOHN_ALREADY_HAS_BLUE_SHIRT); // no change
		builder.replaceStatement(JOHN_ALREADY_HAS_BROWN_EYES); // restore deleted
		builder.replaceStatement(JOHN_ALREADY_HAS_BROWN_TROUSERS); // restore replaced
		StatementUpdate update = builder.build();
		assertThat(update.getRemovedStatements(), is(empty()));
		assertThat(update.getReplacedStatements().values(), containsInAnyOrder(johnChangesBrownHairToSilverHair));
	}

	@Test
	public void testBaseRemoval() {
		StatementUpdateBuilder builder = StatementUpdateBuilder
				.forStatements(Arrays.asList(JOHN_ALREADY_HAS_BROWN_HAIR));
		assertThrows(IllegalArgumentException.class, () -> builder.removeStatement("ID999")); // unknown ID
		builder.removeStatement(JOHN_ALREADY_HAS_BROWN_HAIR.getStatementId()); // simple case
		StatementUpdate update = builder.build();
		assertThat(update.getRemovedStatements(), containsInAnyOrder(JOHN_ALREADY_HAS_BROWN_HAIR.getStatementId()));
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> StatementUpdateBuilder.create().apply(null));
		StatementUpdate update = StatementUpdateBuilder.create()
				.addStatement(JOHN_HAS_BROWN_EYES) // prior addition
				.replaceStatement(JOHN_ALREADY_HAS_SILVER_HAIR) // prior replacement
				.removeStatement(JOHN_ALREADY_HAS_BLUE_SHIRT.getStatementId()) // prior removal
				.apply(StatementUpdateBuilder.create()
						.addStatement(JOHN_HAS_BROWN_TROUSERS) // another addition
						.replaceStatement(JOHN_ALREADY_HAS_BROWN_HAIR) // another replacement
						.removeStatement(JOHN_ALREADY_HAS_BLUE_EYES.getStatementId()) // another removal
						.build())
				.build();
		assertEquals(update.getAddedStatements(), Arrays.asList(JOHN_HAS_BROWN_EYES, JOHN_HAS_BROWN_TROUSERS));
		assertThat(update.getReplacedStatements().values(),
				containsInAnyOrder(JOHN_ALREADY_HAS_SILVER_HAIR, JOHN_ALREADY_HAS_BROWN_HAIR));
		assertThat(update.getRemovedStatements(), containsInAnyOrder(
				JOHN_ALREADY_HAS_BLUE_SHIRT.getStatementId(),
				JOHN_ALREADY_HAS_BLUE_EYES.getStatementId()));
	}

}

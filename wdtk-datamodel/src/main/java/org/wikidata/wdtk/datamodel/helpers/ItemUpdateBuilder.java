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

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Builder for incremental construction of {@link ItemUpdate} objects.
 */
public class ItemUpdateBuilder extends TermedStatementDocumentUpdateBuilder {

	/**
	 * Initializes new builder object for constructing update of item entity with
	 * given ID.
	 * 
	 * @param itemId
	 *            ID of the item entity that is to be updated
	 * @throws NullPointerException
	 *             if {@code itemId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code itemId} is not a valid ID
	 */
	private ItemUpdateBuilder(ItemIdValue itemId) {
		super(itemId);
	}

	/**
	 * Initializes new builder object for constructing update of given base item
	 * entity revision.
	 * 
	 * @param revision
	 *            base item entity revision to be updated
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} does not have valid ID
	 */
	private ItemUpdateBuilder(ItemDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of item entity with given
	 * ID.
	 * 
	 * @param itemId
	 *            ID of the item entity that is to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code itemId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code itemId} is not valid
	 */
	public static ItemUpdateBuilder forEntityId(ItemIdValue itemId) {
		return new ItemUpdateBuilder(itemId);
	}

	/**
	 * Creates new builder object for constructing update of given base item entity
	 * revision. Provided item document might not represent the latest revision of
	 * the item entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param revision
	 *            base item entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} does not have valid ID
	 */
	public static ItemUpdateBuilder forBaseRevision(ItemDocument revision) {
		return new ItemUpdateBuilder(revision);
	}

	@Override
	ItemIdValue getEntityId() {
		return (ItemIdValue) super.getEntityId();
	}

	@Override
	ItemDocument getBaseRevision() {
		return (ItemDocument) super.getBaseRevision();
	}

	@Override
	public ItemUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	@Override
	public ItemUpdateBuilder updateLabels(TermUpdate update) {
		super.updateLabels(update);
		return this;
	}

	@Override
	public ItemUpdateBuilder updateDescriptions(TermUpdate update) {
		super.updateDescriptions(update);
		return this;
	}

	@Override
	public ItemUpdate build() {
		return factory.getItemUpdate(getEntityId(), getBaseRevision(), labels, descriptions, statements);
	}

}

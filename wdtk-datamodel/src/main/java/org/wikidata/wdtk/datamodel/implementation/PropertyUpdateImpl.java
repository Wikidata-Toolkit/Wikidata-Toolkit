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
package org.wikidata.wdtk.datamodel.implementation;

import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Jackson implementation of {@link PropertyUpdate}.
 */
public class PropertyUpdateImpl extends TermedStatementDocumentUpdateImpl implements PropertyUpdate {

	/**
	 * Initializes new property update.
	 * 
	 * @param entityId
	 *            ID of the property entity that is to be updated
	 * @param revision
	 *            base property entity revision to be updated or {@code null} if not
	 *            available
	 * @param labels
	 *            changes in entity labels or {@code null} for no change
	 * @param descriptions
	 *            changes in entity descriptions or {@code null} for no change
	 * @param aliases
	 *            changes in entity aliases, possibly empty
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public PropertyUpdateImpl(
			PropertyIdValue entityId,
			PropertyDocument revision,
			TermUpdate labels,
			TermUpdate descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			StatementUpdate statements) {
		super(entityId, revision, labels, descriptions, aliases, statements);
	}

	@JsonIgnore
	@Override
	public PropertyIdValue getEntityId() {
		return (PropertyIdValue) super.getEntityId();
	}

	@JsonIgnore
	@Override
	public PropertyDocument getBaseRevision() {
		return (PropertyDocument) super.getBaseRevision();
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return getLabels().isEmpty() && getDescriptions().isEmpty() && getStatements().isEmpty();
	}

}

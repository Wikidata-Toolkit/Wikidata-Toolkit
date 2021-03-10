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

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

/**
 * Jackson implementation of {@link FormUpdate}.
 */
public class FormUpdateImpl extends StatementUpdateImpl implements FormUpdate {

	private final Map<String, MonolingualTextValue> modifiedRepresentations;
	private final Set<String> removedRepresentations;
	private final Set<ItemIdValue> grammaticalFeatures;

	/**
	 * Initializes new form update.
	 * 
	 * @param entityId
	 *            ID of the form that is to be updated
	 * @param document
	 *            form revision to be updated or {@code null} if not available
	 * @param modifiedRepresentations
	 *            added or changed form representations
	 * @param removedRepresentations
	 *            language codes of removed form representations
	 * @param grammaticalFeatures
	 *            new grammatical features of the form or {@code null} for no change
	 * @param addedStatements
	 *            added statements
	 * @param replacedStatements
	 *            replaced statements
	 * @param removedStatements
	 *            IDs of removed statements
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	protected FormUpdateImpl(
			FormIdValue entityId,
			FormDocument document,
			Collection<MonolingualTextValue> modifiedRepresentations,
			Collection<String> removedRepresentations,
			Collection<ItemIdValue> grammaticalFeatures,
			Collection<Statement> addedStatements,
			Collection<Statement> replacedStatements,
			Collection<String> removedStatements) {
		super(entityId, document, addedStatements, replacedStatements, removedStatements);
		this.modifiedRepresentations = Collections.unmodifiableMap(
				modifiedRepresentations.stream().collect(toMap(r -> r.getLanguageCode(), r -> r)));
		this.removedRepresentations = Collections.unmodifiableSet(new HashSet<>(removedRepresentations));
		this.grammaticalFeatures = grammaticalFeatures != null
				? Collections.unmodifiableSet(new HashSet<>(grammaticalFeatures))
				: null;
	}

	@Override
	public FormIdValue getEntityId() {
		return (FormIdValue) super.getEntityId();
	}

	@Override
	public FormDocument getCurrentDocument() {
		return (FormDocument) super.getCurrentDocument();
	}

	@Override
	public Map<String, MonolingualTextValue> getModifiedRepresentations() {
		return modifiedRepresentations;
	}

	@Override
	public Set<String> getRemovedRepresentations() {
		return removedRepresentations;
	}

	@Override
	public Optional<Set<ItemIdValue>> getGrammaticalFeatures() {
		return Optional.ofNullable(grammaticalFeatures);
	}

}

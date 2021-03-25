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
package org.wikidata.wdtk.datamodel.interfaces;

import java.util.Optional;
import java.util.Set;

/**
 * Collection of changes that can be applied to form entity.
 */
public interface FormUpdate extends StatementDocumentUpdate {

	@Override
	FormIdValue getEntityId();

	@Override
	FormDocument getBaseRevision();

	/**
	 * Returns changes in form representations.
	 * 
	 * @return update of form representations, possibly empty
	 */
	TermUpdate getRepresentations();

	/**
	 * Returns new grammatical features of the form assigned in this update. If
	 * grammatical features are not changing in this update, this method returns
	 * {@link Optional#empty()}. If grammatical features are being removed without
	 * replacement, this method returns an empty set.
	 * 
	 * @return new grammatical features or {@link Optional#empty()} if grammatical
	 *         features do not change
	 */
	Optional<Set<ItemIdValue>> getGrammaticalFeatures();

}

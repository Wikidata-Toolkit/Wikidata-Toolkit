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

import java.util.Map;
import java.util.Set;

/**
 * Collection of changes that can be applied to an entity that has labels.
 */
public interface LabeledUpdate extends EntityUpdate {

	@Override
	LabeledDocument getCurrentDocument();

	/**
	 * Returns labels added or modified in this update. Existing labels are
	 * preserved if their language code is not listed here.
	 * 
	 * @return added or modified labels indexed by language code
	 */
	Map<String, MonolingualTextValue> getModifiedLabels();

	/**
	 * Returns language codes of labels removed in this update.
	 * 
	 * @return language codes of removed labels
	 */
	Set<String> getRemovedLabels();

}

package org.wikidata.wdtk.datamodel.interfaces;

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

/**
 * Interface for classes which serialize {@link EntityDocument} objects.
 * 
 * @author Michael Günther
 * 
 */
public interface EntityDocumentsSerializer extends EntityDocumentProcessor {

	/**
	 * Initializes the serializer and writes the header (if any) to the output.
	 */
	void start();

	/**
	 * Writes the footer (if any) on the output and closes the output stream.
	 */
	void close();

}

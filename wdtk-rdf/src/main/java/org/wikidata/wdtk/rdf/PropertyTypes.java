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

import java.io.IOException;
import java.io.OutputStream;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

/**
 * Interface for retrieving the primitive types of Wikibase properties.
 * 
 * @author Michael Günther
 * 
 */
public interface PropertyTypes {

	/**
	 * Returns the IRI of the primitive type of an {@link PropertyIdValue}.
	 * 
	 * @param propertyIdValue
	 * @return
	 */
	public String getPropertyType(PropertyIdValue propertyIdValue);

	/**
	 * Sets datatypeIri an IRI of the primitive type of an Property for
	 * {@link PropertyIdValue}.
	 * 
	 * @param propertyIdValue
	 * @param datatypeIri
	 */
	public void setPropertyType(PropertyIdValue propertyIdValue,
			String datatypeIri);

	/**
	 * Returns the IRI of the primitive Type of an Property for
	 * {@link EnitityIdValue} objects.
	 * 
	 * @param propertyIdValue
	 * @param value
	 * @return
	 */
	public String setPropertyTypeFromEntityIdValue(
			PropertyIdValue propertyIdValue, EntityIdValue value);

	/**
	 * Returns the IRI of the primitive Type of an Property for
	 * {@link GlobeCoordinatesValue} objects.
	 * 
	 * @param propertyIdValue
	 * @param value
	 * @return
	 */
	public String setPropertyTypeFromGlobeCoordinatesValue(
			PropertyIdValue propertyIdValue, GlobeCoordinatesValue value);

	/**
	 * Returns the IRI of the primitive Type of an Property for
	 * {@link QuantityValue} objects.
	 * 
	 * @param propertyIdValue
	 * @param value
	 * @return
	 */
	public String setPropertyTypeFromQuantityValue(
			PropertyIdValue propertyIdValue, QuantityValue value);

	/**
	 * Returns the IRI of the primitive Type of an Property for
	 * {@link StringValue} objects.
	 * 
	 * @param propertyIdValue
	 * @param value
	 * @return
	 */
	public String setPropertyTypeFromStringValue(
			PropertyIdValue propertyIdValue, StringValue value);

	/**
	 * Returns the IRI of the primitive Type of an Property for
	 * {@link TimeValue} objects.
	 * 
	 * @param propertyIdValue
	 * @param value
	 * @return
	 */
	public String setPropertyTypeFromTimeValue(PropertyIdValue propertyIdValue,
			TimeValue value);

	/**
	 * Gives out a list of all known Properties as a static java map to the
	 * {@link OutputStream} out.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void getPropertyList(OutputStream out) throws IOException;

}

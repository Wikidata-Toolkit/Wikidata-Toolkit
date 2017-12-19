package org.wikidata.wdtk.dumpfiles.constraint.model;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateConstant;

/**
 * This models a property constraint that says that a property should contain a
 * well-formed filename of a file available on Wikimedia Commons.
 * <p>
 * For example, <i>flag image (P41)</i> is a picture to connect to the picture
 * of an item's in namespace 'File'.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintCommonsLink implements Constraint {

	final PropertyIdValue constrainedProperty;
	final String namespace;

	/**
	 * Constructs a new {@link ConstraintCommonsLink}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param namespace
	 *            namespace
	 */
	public ConstraintCommonsLink(PropertyIdValue constrainedProperty,
			String namespace) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.namespace = namespace;

	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns the namespace.
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ConstraintCommonsLink)) {
			return false;
		}
		ConstraintCommonsLink other = (ConstraintCommonsLink) obj;
		return this.constrainedProperty.equals(other.constrainedProperty)
				&& this.namespace.equals(other.namespace);
	}

	@Override
	public int hashCode() {
		return this.constrainedProperty.hashCode()
				+ (0x1F * this.namespace.hashCode());
	}

	@Override
	public String getTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append(TemplateConstant.OPENING_BRACES);
		sb.append("Constraint:Commons link");
		sb.append(TemplateConstant.VERTICAL_BAR);
		sb.append("namespace");
		sb.append(TemplateConstant.EQUALS_SIGN);
		sb.append(this.namespace);
		sb.append(TemplateConstant.CLOSING_BRACES);
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.constrainedProperty.getId() + " " + getTemplate();
	}

}

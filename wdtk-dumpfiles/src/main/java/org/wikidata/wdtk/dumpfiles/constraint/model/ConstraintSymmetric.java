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
 * This models a property constraint that says that a property is symmetric.
 * <p>
 * For example, <i>spouse (P26)</i> is a symmetric property, since if <i>a</i>
 * is a spouse of <i>b</i>, <i>b</i> is a spouse of <i>a</i>.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintSymmetric implements Constraint {

	final PropertyIdValue constrainedProperty;

	/**
	 * Constructs a new {@link ConstraintSymmetric}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 */
	public ConstraintSymmetric(PropertyIdValue constrainedProperty) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
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
		if (!(obj instanceof ConstraintSymmetric)) {
			return false;
		}
		ConstraintSymmetric other = (ConstraintSymmetric) obj;
		return this.constrainedProperty.equals(other.constrainedProperty);
	}

	@Override
	public int hashCode() {
		return this.constrainedProperty.hashCode();
	}

	@Override
	public String getTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append(TemplateConstant.OPENING_BRACES);
		sb.append("Constraint:Symmetric");
		sb.append(TemplateConstant.CLOSING_BRACES);
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.constrainedProperty.getId() + " " + getTemplate();
	}

}


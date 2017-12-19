package org.wikidata.wdtk.dumpfiles.constraint.builder;

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

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintTargetRequiredClaim;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;

/**
 * An object of this class is a builder of a 'Value type' constraint. This is a
 * particular case of a 'Target required claim' constraint.
 *
 * @author Julian Mendez
 *
 */
class ConstraintValueTypeBuilder implements ConstraintBuilder {

	/**
	 * Constructs a new builder.
	 */
	public ConstraintValueTypeBuilder() {
	}

	@Override
	public ConstraintTargetRequiredClaim parse(
			PropertyIdValue constrainedProperty, Template template) {
		ConstraintTargetRequiredClaim ret = null;
		String classStr = template.getValue(ConstraintBuilderConstant.P_CLASS);
		String relationStr = template
				.getValue(ConstraintBuilderConstant.P_RELATION);
		if ((constrainedProperty != null) && (classStr != null)
				&& (relationStr != null)) {
			DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
			ItemIdValue classId = factory.getItemIdValue(
					ConstraintMainBuilder.firstLetterToUpperCase(classStr),
					ConstraintMainBuilder.PREFIX_WIKIDATA);
			if (relationStr.equals(ConstraintBuilderConstant.V_INSTANCE)) {
				ret = new ConstraintTargetRequiredClaim(constrainedProperty,
						ConstraintMainBuilder.PROPERTY_INSTANCE_OF, classId);
			} else if (relationStr.equals(ConstraintBuilderConstant.V_SUBCLASS)) {
				ret = new ConstraintTargetRequiredClaim(constrainedProperty,
						ConstraintMainBuilder.PROPERTY_SUBCLASS_OF, classId);
			}
		}
		return ret;
	}

}

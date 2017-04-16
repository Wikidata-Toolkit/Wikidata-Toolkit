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

import org.junit.Assert;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Test class for {@link ConstraintSingleValue}
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintSingleValueTest {

	@Test
	public void testParameters() {
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue("P36");
		ConstraintSingleValue constraint = new ConstraintSingleValue(
				constrainedProperty);
		Assert.assertEquals(constrainedProperty,
				constraint.getConstrainedProperty());
	}

	@Test
	public void testToStringAndVisit() {
		String propertyName = "P36";
		String templateStr = "{{Constraint:Single value}}";
		String string = propertyName + " " + templateStr;
		PropertyIdValue constrainedProperty = ConstraintTestHelper
				.getPropertyIdValue(propertyName);
		ConstraintSingleValue constraint = new ConstraintSingleValue(
				constrainedProperty);
		Assert.assertEquals(templateStr, constraint.getTemplate());
		Assert.assertEquals(string, constraint.toString());

		ConstraintTestHelper.testVisit(constraint);
	}

	@Test
	public void testEquals() {
		PropertyIdValue constrainedProperty0 = ConstraintTestHelper
				.getPropertyIdValue("P36");
		PropertyIdValue constrainedProperty1 = ConstraintTestHelper
				.getPropertyIdValue("P38");

		ConstraintTestHelper.testEquals(new ConstraintSingleValue(
				constrainedProperty0), new ConstraintSingleValue(
				constrainedProperty0), new ConstraintSingleValue(
				constrainedProperty1));
	}

}


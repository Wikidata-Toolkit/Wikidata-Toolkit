package org.wikidata.wdtk.dumpfiles.constraint.renderer;

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

import java.util.List;

import org.openrdf.model.Resource;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.format.RendererFormat;
import org.wikidata.wdtk.dumpfiles.constraint.model.Constraint;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintOneOf;

/**
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintOneOfRenderer implements ConstraintRenderer {

	final RendererFormat f;

	public ConstraintOneOfRenderer(RendererFormat rendererFormat) {
		this.f = rendererFormat;
	}

	@Override
	public void renderConstraint(Constraint c) {
		if (c instanceof ConstraintOneOf) {
			render((ConstraintOneOf) c);
		}
	}

	public void render(ConstraintOneOf c) {
		if (c.getTypeOfConstraint().equals(
				ConstraintOneOf.TypeOfConstraint.ITEM)) {
			renderItems(c.getConstrainedProperty(), c.getItemValues());
		} else if (c.getTypeOfConstraint().equals(
				ConstraintOneOf.TypeOfConstraint.QUANTITY)) {
			renderQuantities(c.getConstrainedProperty(), c.getQuantityValues());
		} else if (c.getTypeOfConstraint().equals(
				ConstraintOneOf.TypeOfConstraint.STRING)) {
			renderStrings(c.getConstrainedProperty(), c.getStringValues());
		}
	}

	public void renderItems(PropertyIdValue p, List<ItemIdValue> values) {
		if ((p == null) || (values == null)) {
			return;
		}
		this.f.addDeclarationObjectProperty(this.f.getPs(p));
		this.f.addInverseFunctionalObjectProperty(this.f.getPs(p));

		this.f.addDeclarationObjectProperty(this.f.getPv(p));

		Resource resource = (values.size() == 1) ? //
		this.f.getObjectOneOf(ConstraintItemRenderer.getAndDeclareItem(this.f,
				values.iterator().next())) //
				: this.f.getObjectOneOf(ConstraintItemRenderer
						.getListAndDeclareItems(this.f, values));

		this.f.addObjectPropertyRange(this.f.getPv(p), resource);
	}

	public void renderQuantities(PropertyIdValue p, List<Integer> values) {
		if ((p == null) || (values == null)) {
			return;
		}
		this.f.addDeclarationObjectProperty(this.f.getPs(p));
		this.f.addInverseFunctionalObjectProperty(this.f.getPs(p));

		this.f.addDeclarationDatatypeProperty(this.f.getPv(p));

		Resource resource = (values.size() == 1) ? //
		this.f.getDataOneOf(values.iterator().next()) //
				: this.f.getDataOneOfInt(values);

		this.f.addDataPropertyRange(this.f.getPv(p), resource);
	}

	public void renderStrings(PropertyIdValue p, List<String> values) {
		if ((p == null) || (values == null)) {
			return;
		}
		this.f.addDeclarationObjectProperty(this.f.getPs(p));
		this.f.addInverseFunctionalObjectProperty(this.f.getPs(p));

		this.f.addDeclarationDatatypeProperty(this.f.getPv(p));

		Resource resource = (values.size() == 1) ? //
		this.f.getDataOneOf(values.iterator().next()) //
				: this.f.getDataOneOfStr(values);

		this.f.addDataPropertyRange(this.f.getPv(p), resource);
	}

}

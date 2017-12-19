package org.wikidata.wdtk.dumpfiles.constraint.processor;

/*
 * #%L
 * Wikidata Toolkit Examples
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.constraint.builder.ConstraintMainBuilder;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateParser;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateScanner;

/**
 * 
 * @author Julian Mendez
 * 
 */
class PropertyTalkTemplateMwRevisionProcessor implements MwRevisionProcessor {

	public static final int PROPERTY_TALK_NS = 121;
	public static final String COLON = ":";
	public static final String SLASH = "/";

	final TemplateScanner templateScanner = new TemplateScanner();
	final TemplateParser templateParser = new TemplateParser();
	final Map<PropertyIdValue, List<Template>> map = new HashMap<PropertyIdValue, List<Template>>();
	String propertyTalkPrefix = "";

	public PropertyTalkTemplateMwRevisionProcessor() {
	}

	@Override
	public void startRevisionProcessing(String siteName, String baseUrl,
			Map<Integer, String> namespaces) {
		this.propertyTalkPrefix = namespaces.get(PROPERTY_TALK_NS) + COLON;
	}

	@Override
	public void processRevision(MwRevision mwRevision) {
		String title = mwRevision.getPrefixedTitle();
		if (title.startsWith(this.propertyTalkPrefix)
				&& (title.indexOf(SLASH) == -1)) {
			String propertyName = title.substring(this.propertyTalkPrefix
					.length());
			String text = mwRevision.getText();
			List<String> listOfTemplateStr = this.templateScanner
					.getTemplates(text);
			List<Template> listOfTemplates = new ArrayList<Template>();
			for (String templateStr : listOfTemplateStr) {
				listOfTemplates.add(this.templateParser.parse(templateStr));
			}

			DataObjectFactoryImpl factory = new DataObjectFactoryImpl();
			PropertyIdValue constrainedProperty = factory.getPropertyIdValue(
					propertyName, ConstraintMainBuilder.PREFIX_WIKIDATA);

			this.map.put(constrainedProperty, listOfTemplates);
		}
	}

	@Override
	public void finishRevisionProcessing() {
	}

	public Map<PropertyIdValue, List<Template>> getMap() {
		return Collections.unmodifiableMap(this.map);
	}

}

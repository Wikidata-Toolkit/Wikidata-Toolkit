package org.wikidata.wdtk.dumpfiles.constraint.format;

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
import java.io.Writer;

/**
 * This class throws an exception in every method overridden from the super
 * class. This is used for testing.
 * 
 * @author Julian Mendez
 * 
 */
class WriterWithExceptions extends Writer {

	public WriterWithExceptions() {
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		throw new IOException();
	}

	@Override
	public void flush() throws IOException {
		throw new IOException();
	}

	@Override
	public void close() throws IOException {
		throw new IOException();
	}

}

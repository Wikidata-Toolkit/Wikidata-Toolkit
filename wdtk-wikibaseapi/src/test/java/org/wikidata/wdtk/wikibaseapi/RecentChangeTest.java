package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class RecentChangeTest {
	@Test
	public void testEquals() {
		RecentChange rc1 = new RecentChange("a", new Date(), "b");
		RecentChange rc2 = new RecentChange("a", new Date(), "b");
		RecentChange rc3 = new RecentChange("c", new Date(), "b");
		assertTrue(rc1.equals(rc2));
		assertFalse(rc3.equals(rc2));
	}

	@Test
	public void testContainsSet() {
		RecentChange rc1 = new RecentChange("a", new Date(), "b");
		RecentChange rc2 = new RecentChange("a", new Date(), "b");
		Set<RecentChange> rcs = new TreeSet<>();
		rcs.add(rc1);
		assertTrue(rcs.contains(rc1));
		assertTrue(rcs.contains(rc2));
	}

	@Test
	public void testComparable() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		RecentChange rc1 = new RecentChange("",
				sdf.parse("31.12.2014"), "");
		RecentChange rc2 = new RecentChange("",
				sdf.parse("31.12.2014"), "");
		RecentChange rc3 = new RecentChange("",
				sdf.parse("01.01.2015"), "");
		assertEquals(rc1.compareTo(rc2), 0);
		assertEquals(rc2.compareTo(rc3), -1);
		assertEquals(rc3.compareTo(rc2), 1);
	}
}

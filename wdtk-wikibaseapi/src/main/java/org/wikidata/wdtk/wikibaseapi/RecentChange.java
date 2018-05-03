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


import java.util.Date;

/**
 * Simple class for saving recent changes
 * 
 * @author Markus Damm
 *
 */

class RecentChange implements Comparable<RecentChange> {

	/**
	 * author of the recent change
	 */
	private String author;

	/**
	 * property that was recently changed
	 */
	private String propertyName;

	/**
	 * date and time of the recent change
	 */
	private Date date;

	/**
	 * Constructor
	 * 
	 * @param propertyName
	 *                name of the changed property
	 * @param date
	 *                date of the recent change
	 * @param author
	 *                name of the author of the recent change
	 */
	RecentChange(String propertyName, Date date, String author) {
		this.propertyName = propertyName;
		this.date = date;
		this.author = author;
	}

	/**
	 * Returns the author of the recent change
	 * 
	 * @return name (if user is registered) or the ip adress (if user is
	 *         unregistered) of the author of the recent change
	 */
	String getAuthor() {
		return author;
	}

	/**
	 * Returns the name of the changed property
	 * 
	 * @return name of the recently changed property
	 */
	String getPropertyName() {
		return propertyName;
	}

	/**
	 * Returns the date of the recent change
	 * 
	 * @return date of the recent change
	 */
	Date getDate() {
		return date;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof RecentChange) {
			RecentChange o = (RecentChange) other;
			if (this.propertyName.equals(o.propertyName)
					&& (this.date.equals(o.date))
					&& (this.author.equals(o.author))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int compareTo(RecentChange other) {
		if (this.date.after(other.date)) {
			return 1;
		}
		if (this.date.before(other.date)) {
			return -1;
		}
		return 0;
	}
}

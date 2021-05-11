/*
 * #%L
 * Wikidata Toolkit Data Model
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

package org.wikidata.wdtk.datamodel.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QuantityValueImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final BigDecimal nv = new BigDecimal(
			"0.123456789012345678901234567890123456789");
	private final Optional<BigDecimal> lb = Optional.of(new BigDecimal(
			"0.123456789012345678901234567890123456788"));
	private final Optional<BigDecimal> ub = Optional.of(new BigDecimal(
			"0.123456789012345678901234567890123456790"));
	private final Optional<ItemIdValue> unitMeter = Optional.of(new ItemIdValueImpl("Q11573", "http://wikidata.org/entity/"));
	private final QuantityValue q1 = new QuantityValueImpl(nv, lb, ub, unitMeter);
	private final QuantityValue q2 = new QuantityValueImpl(nv, lb, ub, unitMeter);
	private final QuantityValue q3 = new QuantityValueImpl(nv, Optional.empty(), Optional.empty(), unitMeter);
	private final QuantityValue q4 = new QuantityValueImpl(nv, lb, ub, Optional.empty());
	private static String JSON_QUANTITY_VALUE = "{\"value\":{\"amount\":\"+0.123456789012345678901234567890123456789\",\"lowerBound\":\"+0.123456789012345678901234567890123456788\",\"upperBound\":\"+0.123456789012345678901234567890123456790\",\"unit\":\"http://wikidata.org/entity/Q11573\"},\"type\":\"quantity\"}";
	private static String JSON_UNBOUNDED_QUANTITY_VALUE = "{\"value\":{\"amount\":\"+0.123456789012345678901234567890123456789\",\"unit\":\"http://wikidata.org/entity/Q11573\"},\"type\":\"quantity\"}";
    private static String JSON_INVALID_UNIT_QUANTITY_VALUE = "{\"value\":{\"amount\":\"+0.123456789012345678901234567890123456789\",\"lowerBound\":\"+0.123456789012345678901234567890123456788\",\"upperBound\":\"+0.123456789012345678901234567890123456790\",\"unit\":\"foobar\"},\"type\":\"quantity\"}";
	
	@Test
	public void gettersWorking() {
		assertEquals(q1.getNumericValue(), nv);
		assertEquals(q1.getLowerBound(), lb.get());
		assertEquals(q1.getUpperBound(), ub.get());
		assertEquals(q1.getOptionalLowerBound(), lb);
		assertEquals(q1.getOptionalUpperBound(), ub);
	}

	@Test
	public void getUnitItemId() {
		assertEquals(new ItemIdValueImpl("Q11573", "http://wikidata.org/entity/"), q1.getUnitItemId());
	}

	@Test
	public void getUnitItemIdNoUnit() {
		assertNull(q4.getUnitItemId());
	}

	@Test
	public void equalityBasedOnContent() {
		BigDecimal nvplus = new BigDecimal(
				"0.1234567890123456789012345678901234567895");
		BigDecimal nvminus = new BigDecimal(
				"0.1234567890123456789012345678901234567885");
		QuantityValue q4 = new QuantityValueImpl(nvplus, lb, ub, unitMeter);
		QuantityValue q5 = new QuantityValueImpl(nv, Optional.of(nvminus), ub, unitMeter);
		QuantityValue q6 = new QuantityValueImpl(nv, lb, Optional.of(nvplus), unitMeter);
		QuantityValue q7 = new QuantityValueImpl(nv, lb, ub, Optional.empty());

		assertEquals(q1, q1);
		assertEquals(q1, q2);
		assertNotEquals(q1, q3);
		assertNotEquals(q1, q4);
		assertNotEquals(q1, q5);
		assertNotEquals(q1, q6);
		assertNotEquals(q1, q7);
		assertNotEquals(q1, null);
		assertNotEquals(q1, this);
	}
	
	@Test
	public void equalityBasedOnRepresentation() {
		BigDecimal amount1 = new BigDecimal("4.00");
		BigDecimal amount2 = new BigDecimal("4");
		assertNotEquals(amount1, amount2);
		QuantityValue quantity1 = new QuantityValueImpl(amount1, Optional.empty(), Optional.empty(), Optional.empty());
		QuantityValue quantity2 = new QuantityValueImpl(amount2, Optional.empty(), Optional.empty(), Optional.empty());
		assertNotEquals(quantity1, quantity2);
	}
	
	@Test
	public void faithfulJsonSerialization() {
		BigDecimal amount = new BigDecimal("4.00");
		QuantityValueImpl quantity = new QuantityValueImpl(amount, Optional.empty(), Optional.empty(), Optional.empty());
		assertEquals("+4.00", quantity.getValue().getAmountAsString());
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(q1.hashCode(), q2.hashCode());
	}

	@Test
	public void numValueNotNull() {
		assertThrows(NullPointerException.class, () -> new QuantityValueImpl(null, lb, ub, unitMeter));
	}

	@Test
	public void lowerBoundNotNull() {
		assertThrows(IllegalArgumentException.class, () -> new QuantityValueImpl(nv, Optional.empty(), ub, unitMeter));
	}

	@Test
	public void upperBoundNotNull() {
		assertThrows(IllegalArgumentException.class, () -> new QuantityValueImpl(nv, lb, Optional.empty(), unitMeter));
	}

	@Test
	public void unitNotNull() {
		assertThrows(NullPointerException.class, () -> new QuantityValueImpl(nv, lb.get(), ub.get(), (String) null));
	}

	@Test
	public void lowerBoundNotGreaterNumVal() {
		assertThrows(IllegalArgumentException.class, () -> new QuantityValueImpl(lb.get(), Optional.of(nv), ub, unitMeter));
	}

	@Test
	public void numValNotGreaterLowerBound() {
		assertThrows(IllegalArgumentException.class, () -> new QuantityValueImpl(ub.get(), lb, Optional.of(nv), unitMeter));
	}
	
	@Test
	public void testInvalidUnit() {
	    assertThrows(IllegalArgumentException.class, () -> new QuantityValueImpl(nv, lb.get(), ub.get(), "foobar"));
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_QUANTITY_VALUE, mapper.writeValueAsString(q1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(q1, mapper.readValue(JSON_QUANTITY_VALUE, ValueImpl.class));
	}

	@Test
	public void testUnboundedToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_UNBOUNDED_QUANTITY_VALUE, mapper.writeValueAsString(q3));
	}

	@Test
	public void testUnboundedToJava() throws IOException {
		assertEquals(q3, mapper.readValue(JSON_UNBOUNDED_QUANTITY_VALUE, ValueImpl.class));
	}
	
    @Test
    public void getUnitItemIdInvalidIri() throws JsonMappingException, JsonProcessingException {
        assertThrows(JsonMappingException.class, () -> mapper.readValue(JSON_INVALID_UNIT_QUANTITY_VALUE, ValueImpl.class));
    }
}

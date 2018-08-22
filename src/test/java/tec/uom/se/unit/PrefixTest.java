/*
 * Units of Measurement Implementation for Java SE
 * Copyright (c) 2005-2017, Jean-Marie Dautelle, Werner Keil, V2COM.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-363 nor the names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tec.uom.se.unit;

import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static tec.uom.se.unit.MetricPrefix.DECI;
import static tec.uom.se.unit.MetricPrefix.KILO;
import static tec.uom.se.unit.MetricPrefix.MEGA;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.GRAM;
import static tec.uom.se.unit.Units.KILOGRAM;
import static tec.uom.se.unit.Units.LITRE;

public class PrefixTest {
  @Test
  public void testKilo() {
    // TODO how to handle equals for units?
    // assertEquals(KILOGRAM.getSymbol(), KILO(GRAM).getSymbol());
    assertEquals(KILOGRAM.toString(), KILO(GRAM).toString());
  }

  @Test
  public void testMega() {
    Quantity<Mass> m1 = Quantities.getQuantity(1.0, MEGA(Units.GRAM));
    assertEquals(1d, m1.getValue());
    assertEquals("Mg", m1.getUnit().toString());
  }

  @Test
  public void testDeci() {
    Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
    assertEquals(1d, m1.getValue());
    assertEquals("l", m1.getUnit().toString());

    Quantity<Volume> m2 = m1.to(DECI(LITRE));
    assertEquals(10.0d, m2.getValue());
    assertEquals("dl", m2.getUnit().toString());
  }

  @Test
  public void testMilli() {
    Quantity<Mass> m1 = Quantities.getQuantity(1.0, MILLI(Units.GRAM));
    assertEquals(1d, m1.getValue());
    assertEquals("mg", m1.getUnit().toString());
  }

  @Test
  public void testMilli2() {
    Quantity<Volume> m1 = Quantities.getQuantity(10, MILLI(LITRE));
    assertEquals(10, m1.getValue());
    assertEquals("ml", m1.getUnit().toString());
  }

  @Test
  public void testMilli3() {
    Quantity<Volume> m1 = Quantities.getQuantity(1.0, LITRE);
    assertEquals(1d, m1.getValue());
    assertEquals("l", m1.getUnit().toString());

    Quantity<Volume> m2 = m1.to(MILLI(LITRE));
    assertEquals(1000.0d, m2.getValue());
    assertEquals("ml", m2.getUnit().toString());
  }

  @Test
  public void testMilli4() {
    Quantity<Volume> m1 = Quantities.getQuantity(1.0, MILLI(LITRE));
    assertEquals(1d, m1.getValue());
    assertEquals("ml", m1.getUnit().toString());

    Quantity<Volume> m2 = m1.to(LITRE);
    assertEquals(0.001d, m2.getValue());
    assertEquals("l", m2.getUnit().toString());
  }

  @Test
  public void testMicro2() {
    Quantity<Length> m1 = Quantities.getQuantity(1.0, Units.METRE);
    assertEquals(1d, m1.getValue());
    assertEquals("m", m1.getUnit().toString());

    Quantity<Length> m2 = m1.to(MICRO(Units.METRE));
    assertEquals(1000000.0d, m2.getValue());
    assertEquals("µm", m2.getUnit().toString());
  }

  @Test
  public void testNano() {
    Quantity<Mass> m1 = Quantities.getQuantity(1.0, Units.GRAM);
    assertEquals(1d, m1.getValue());
    assertEquals("g", m1.getUnit().toString());

    Quantity<Mass> m2 = m1.to(NANO(Units.GRAM));
    assertEquals(1000000000.0d, m2.getValue());
    assertEquals("ng", m2.getUnit().toString());
  }

  @Test
  public void testNano2() {
    Quantity<Length> m1 = Quantities.getQuantity(1.0, Units.METRE);
    assertEquals(1d, m1.getValue());
    assertEquals("m", m1.getUnit().toString());

    Quantity<Length> m2 = m1.to(NANO(Units.METRE));
    assertEquals(1000000000.0d, m2.getValue());
    assertEquals("nm", m2.getUnit().toString());
  }

  @Test
  public void testHashMapAccessingMap() {
    assertThat(LITRE.toString(), is("l"));
    assertThat(MILLI(LITRE).toString(), is("ml"));
    assertNotNull(MILLI(GRAM).toString(), is("mg"));
  }

  @Test
  public void testSingleOperation() {
    assertEquals(MICRO(GRAM), GRAM.divide(1000000));
  }

  @Test
  public void testNestedOperationsShouldBeSame() {
    Unit<Mass> m1 = MICRO(GRAM);
    Unit<Mass> m2 = GRAM.divide(1000).divide(1000);
    UnitConverter c1 = m1.getConverterTo(m2);
    List steps1 = c1.getConversionSteps();
    UnitConverter c2 = m2.getConverterTo(m1);
    List steps2 = c2.getConversionSteps();
    assertEquals(c1, c2);
    assertEquals(m1, m2);
  }

  @Test
  public void testNestedOperationsNotTheSame() {
    Unit<Mass> m1 = MICRO(GRAM);
    Unit<Mass> m2 = GRAM.divide(1000).divide(2000);
    UnitConverter c1 = m1.getConverterTo(m2);
    List steps1 = c1.getConversionSteps();
    UnitConverter c2 = m2.getConverterTo(m1);
    List steps2 = c2.getConversionSteps();
    assertNotEquals(c1, c2);
    assertNotEquals(m1, m2);
  }
}

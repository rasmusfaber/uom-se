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
package tec.uom.se.format;

import org.junit.Ignore;
import org.junit.Test;

import javax.measure.Unit;
import javax.measure.format.UnitFormat;

import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.MetricPrefix.KILO;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.METRE;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 *
 */
public class LocalFormatTest {

  @Test
  @Ignore
  // TODO LocalUnitFormat won't parse Compound Units, EBNF does, also see https://github.com/unitsofmeasurement/uom-se/issues/145
  public void testPrefixKm() {
    final UnitFormat format = LocalUnitFormat.getInstance();
    Unit<?> u = format.parse("km");
    assertEquals(KILO(METRE), u);
    assertEquals("km", u.toString());
  }

  @Test
  public void testFormatKm() {
    final UnitFormat format = LocalUnitFormat.getInstance();
    String s = format.format(KILO(METRE));
    assertEquals("km", s);
  }

  @Test
  public void testFormatMm() {
    final UnitFormat format = LocalUnitFormat.getInstance();
    String s = format.format(MILLI(METRE));
    assertEquals("mm", s);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testParseIrregularStringLocal() {
    final UnitFormat format = LocalUnitFormat.getInstance();
    Unit<?> u = format.parse("bl//^--1a");
    // System.out.println(u);
  }

}

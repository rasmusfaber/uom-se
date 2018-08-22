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

import tec.uom.lib.common.function.UnitConverterSupplier;
import tec.uom.se.AbstractConverter;
import tec.uom.se.AbstractUnit;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This class represents the units derived from other units using {@linkplain UnitConverter converters}.
 * </p>
 * <p>
 * <p>
 * Examples of transformed units:<code>
 * CELSIUS = KELVIN.shift(273.15);
 * FOOT = METRE.multiply(3048).divide(10000);
 * MILLISECOND = MILLI(SECOND);
 * </code>
 * </p>
 * <p>
 * <p>
 * Transformed units have no symbol. But like any other units, they may have labels attached to them (see
 * {@link javax.measure.format.UnitFormat#label(Unit, String) UnitFormat.label}
 * </p>
 * <p>
 * <p>
 * Instances of this class are created through the {@link AbstractUnit#transform} method.
 * </p>
 *
 * @param <Q>
 *          The type of the quantity measured by this unit.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.0.4, June 9, 2017
 * @since 1.0
 */
public final class TransformedUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> implements UnitConverterSupplier {

  /**
	 *
	 */
  private static final long serialVersionUID = 1L;

  /**
   * Holds the parent unit.
   */
  private final AbstractUnit<Q> parentUnit;

  /**
   * Holds the system unit.
   */
  private final Unit<Q> systemUnit;

  /**
   * Holds the converter to the parent unit.
   */
  private final UnitConverter converter;

  /**
   * Holds the symbol.
   */
  private String symbol;

  /**
   * Creates a transformed unit from the specified system unit. using the parent as symbol
   *
   * @param parentUnit
   *          the system unit from which this unit is derived.
   * @param converter
   *          the converter to the parent units.
   */
  public TransformedUnit(Unit<Q> parentUnit, UnitConverter unitConverter) {
    this(null, parentUnit, unitConverter);
  }

  /**
   * Creates a transformed unit from the specified parent unit.
   *
   * @param symbol
   *          the symbol to use with this transformed unit.
   * @param parentUnit
   *          the parent unit from which this unit is derived.
   * @param unitConverter
   *          the converter to the parent units.
   */
  public TransformedUnit(String symbol, Unit<Q> parentUnit, UnitConverter unitConverter) {
    this(symbol, parentUnit, parentUnit.getSystemUnit(), unitConverter);
  }

  /**
   * Creates a transformed unit from the specified parent and system unit. using the parent as symbol
   *
   * @param parentUnit
   *          the parent unit from which this unit is derived.
   * @param sysUnit
   *          the system unit which this unit is based on.
   * @param converter
   *          the converter to the parent units.
   */
  public TransformedUnit(String symbol, Unit<Q> parentUnit, Unit<Q> sysUnit, UnitConverter unitConverter) {
    if (parentUnit instanceof AbstractUnit) {
      final AbstractUnit<Q> abParent = (AbstractUnit<Q>) parentUnit;

      this.systemUnit = sysUnit;
      // if (!abParent.isSystemUnit()) {
      // throw new IllegalArgumentException("The parent unit: " + abParent
      // + " is not a system unit");
      // }
      this.parentUnit = abParent;
      this.converter = unitConverter;
      this.symbol = symbol;
      // see https://github.com/unitsofmeasurement/uom-se/issues/54
    } else {
      throw new IllegalArgumentException("The parent unit: " + parentUnit + " is not an abstract unit.");
    }
  }

  @Override
  public Dimension getDimension() {
    return parentUnit.getDimension();
  }

  @Override
  public UnitConverter getSystemConverter() {
    return parentUnit.getSystemConverter().concatenate(converter);
  }

  /**
   * Returns the converter to the parent unit.
   *
   * @return the converter to the parent unit.
   */
  @Override
  public UnitConverter getConverter() {
    return converter;
  }

  @Override
  protected Unit<Q> toSystemUnit() {
    return (systemUnit != null ? systemUnit : parentUnit.getSystemUnit());
  }

  @Override
  public Map<? extends Unit<?>, Integer> getBaseUnits() {
    return parentUnit.getBaseUnits();
  }

  @Override
  public AbstractUnit<Q> transform(UnitConverter operation) {
    UnitConverter newConverter = this.converter.concatenate(operation);
    return parentUnit.transform(newConverter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parentUnit, converter);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof TransformedUnit) {
      TransformedUnit<?> other = (TransformedUnit<?>) obj;
      return Objects.equals(parentUnit, other.parentUnit) && Objects.equals(converter, other.converter);
    }
    return false;
  }

  @Override
  public String getSymbol() {
    return symbol;
  }

  /**
   * Returns the parent unit for this unit. The parent unit is the untransformed unit from which this unit is derived.
   *
   * @return the untransformed unit from which this unit is derived.
   */
  public Unit<Q> getParentUnit() {
    return parentUnit;
  }
}

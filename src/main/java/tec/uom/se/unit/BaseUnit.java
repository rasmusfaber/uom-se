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

import tec.uom.se.AbstractConverter;
import tec.uom.se.AbstractUnit;
import tec.uom.se.quantity.QuantityDimension;

import javax.measure.Dimension;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;

import java.util.Map;

/**
 * <p>
 * This class represents the building blocks on top of which all others physical units are created. Base units are always unscaled SI units.
 * </p>
 * 
 * <p>
 * When using the {@link tec.uom.se.spi.StandardModel standard model}, all seven <b>SI</b> base units are dimensionally independent.
 * </p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/SI_base_unit"> Wikipedia: SI base unit</a>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.0, Jan 21, 2017
 */
public final class BaseUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

  /**
     * 
     */
  private static final long serialVersionUID = 1721629233768215930L;

  /**
   * Holds the symbol.
   */
  private final String symbol;

  /**
   * Holds the base unit dimension.
   */
  private final Dimension dimension;

  /**
   * Creates a base unit having the specified symbol and dimension.
   *
   * @param symbol
   *          the symbol of this base unit.
   */
  public BaseUnit(String symbol, Dimension dimension) {
    this.symbol = symbol;
    this.dimension = dimension;
  }

  /**
   * Creates a base unit having the specified symbol and dimension.
   *
   * @param symbol
   *          the symbol of this base unit.
   */
  public BaseUnit(String symbol) {
    this.symbol = symbol;
    this.dimension = QuantityDimension.NONE;
  }

  /**
   * Creates a base unit having the specified symbol and name.
   *
   * @param symbol
   *          the symbol of this base unit.
   * @param name
   *          the name of this base unit.
   * @throws IllegalArgumentException
   *           if the specified symbol is associated to a different unit.
   */
  public BaseUnit(String symbol, String name) {
    this(symbol);
    this.name = name;
  }

  @Override
  public String getSymbol() {
    return symbol;
  }

  @Override
  public AbstractUnit<Q> toSystemUnit() {
    return this;
  }

  @Override
  public UnitConverter getSystemConverter() throws UnsupportedOperationException {
    return AbstractConverter.IDENTITY;
  }

  @Override
  public Dimension getDimension() {
    return dimension;
  }

  @Override
  public final boolean equals(Object that) {
    if (this == that)
      return true;
    if (!(that instanceof BaseUnit))
      return false;
    BaseUnit<?> thatUnit = (BaseUnit<?>) that;
    return this.symbol.equals(thatUnit.symbol) && this.dimension.equals(thatUnit.dimension);
  }

  @Override
  public final int hashCode() {
    return symbol.hashCode();
  }

  @Override
  public Map<? extends AbstractUnit<Q>, Integer> getBaseUnits() {
    // TODO Shall we return null, empty list or what (e.g. Optional in SE
    // 8)?
    return null;
  }
}

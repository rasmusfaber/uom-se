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
package tec.uom.se.function;

import tec.uom.lib.common.function.ValueSupplier;
import tec.uom.se.AbstractConverter;

import javax.measure.UnitConverter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * <p>
 * This class represents a converter scaling by a fixed power of 10.
 * </p>
 *
 * @author <a href="mailto:rfaber@gmail.com">Rasmus Faber-Espensen</a>
 * @since 1.0.9
 */
public class PowerOfTenConverter extends AbstractConverter implements ValueSupplier<Double>, Supplier<Double>, DoubleSupplier {

  /**
	 *
	 */
  private static final long serialVersionUID = 8910167943266907093L;

  /**
   * Lookup tables for fast double conversion.
   */
  private static final double[] positiveFactors = { 1e0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8, 1e9, 1e10, 1e11, 1e12, 1e13, 1e14, 1e15, 1e16, 1e17,
      1e18, 1e19, 1e20, 1e21, 1e22, 1e23, 1e24 };
  private static final double[] negativeFactors = { 1e0, 1e-1, 1e-2, 1e-3, 1e-4, 1e-5, 1e-6, 1e-7, 1e-8, 1e-9, 1e-10, 1e-11, 1e-12, 1e-13, 1e-14,
      1e-15, 1e-16, 1e-17, 1e-18, 1e-19, 1e-20, 1e-21, 1e-22, 1e-23, 1e-24 };

  /**
   * Holds the scaling factor as a power of ten.
   */
  private final int powerOfTen;

  /**
   * Creates a power-of-ten converter with the specified scaling factor as a power of ten.
   *
   * @param powerOfTen
   *          the power of ten.
   * @throws IllegalArgumentException
   *           if <code>powerOfTen == 0</code>
   */
  public PowerOfTenConverter(int powerOfTen) {
    if (powerOfTen == 0) {
      throw new IllegalArgumentException("Would result in identity converter");
    }
    this.powerOfTen = powerOfTen;
  }

  /**
   * Convenience method equivalent to <code>new PowerOfTenConverter(powerOfTen)</code>
   *
   * @param powerOfTen
   *          the power of ten.
   * @throws IllegalArgumentException
   *           if <code>powerOfTen == 0</code>
   */
  public static PowerOfTenConverter of(int powerOfTen) {
    return new PowerOfTenConverter(powerOfTen);
  }

  /**
   * Returns the scaling factor as a power of ten.
   *
   * @return this scaling factor.
   */
  public int getPowerOfTen() {
    return powerOfTen;
  }

  @Override
  public double convert(double value) {
    return value * getDoubleFactor();
  }

  @Override
  public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
    return value.scaleByPowerOfTen(powerOfTen);
  }

  @Override
  public UnitConverter concatenate(UnitConverter converter) {
    if (converter instanceof RationalConverter) {
      return converter.concatenate(this);
    }
    if (!(converter instanceof PowerOfTenConverter)) {
      return super.concatenate(converter);
    }
    PowerOfTenConverter that = (PowerOfTenConverter) converter;
    int newPowerOfTen = this.powerOfTen + that.powerOfTen;
    if (newPowerOfTen == 0) {
      return IDENTITY;
    }
    return PowerOfTenConverter.of(newPowerOfTen);
  }

  @Override
  public PowerOfTenConverter inverse() {
    return PowerOfTenConverter.of(-powerOfTen);
  }

  @Override
  public final String toString() {
    return "PowerOfTenConverter(" + powerOfTen + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof PowerOfTenConverter) {
      PowerOfTenConverter that = (PowerOfTenConverter) obj;
      return this.powerOfTen == that.powerOfTen;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return powerOfTen;
  }

  @Override
  public boolean isLinear() {
    return true;
  }

  @Override
  public Double getValue() {
    return getDoubleFactor();
  }

  @Override
  public double getAsDouble() {
    return getDoubleFactor();
  }

  @Override
  public Double get() {
    return getDoubleFactor();
  }

  private double getDoubleFactor() {
    if (powerOfTen >= 0 && powerOfTen <= 24) {
      return positiveFactors[powerOfTen];
    }
    if (powerOfTen < 0 && powerOfTen >= -24) {
      return negativeFactors[-powerOfTen];
    }
    return Math.pow(10, powerOfTen);
  }
}

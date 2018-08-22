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
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * <p>
 * This class represents a converter multiplying numeric values by an exact scaling factor (represented as the quotient of two <code>BigInteger</code>
 * numbers).
 * </p>
 *
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.0, Oct 11, 2016
 * @since 1.0
 */
public final class RationalConverter extends AbstractConverter implements ValueSupplier<Double>, Supplier<Double>, DoubleSupplier {

  /**
	 *
	 */
  private static final long serialVersionUID = 3563384008357680074L;

  /**
   * Holds the converter dividend.
   */
  private final BigInteger dividend;

  /**
   * Holds the converter divisor (always positive).
   */
  private final BigInteger divisor;
  private static final BigInteger TEN = BigInteger.valueOf(10);

  private static final Map<BigInteger, Integer> BIG_INTEGER_INTEGER_MAP = new HashMap<>();

  static {
    // Double.MIN_VALUE is approx. 4.9e-324 and Double.MAX_VALUE is approx. 1.8e308
    for (int n = 0; n <= 32; n++) {
      BIG_INTEGER_INTEGER_MAP.put(TEN.pow(n), n);
    }
  }

  /**
   * Creates a rational converter with the specified dividend and divisor.
   *
   * @param dividend
   *          the dividend.
   * @param divisor
   *          the positive divisor.
   * @throws IllegalArgumentException
   *           if <code>divisor &lt;= 0</code>
   * @throws IllegalArgumentException
   *           if <code>dividend == divisor</code>
   */
  public RationalConverter(BigInteger dividend, BigInteger divisor) {
    if (divisor.compareTo(BigInteger.ZERO) <= 0)
      throw new IllegalArgumentException("Negative or zero divisor");
    if (dividend.equals(divisor))
      throw new IllegalArgumentException("Would result in identity converter");
    this.dividend = dividend; // Exact conversion
    if (divisor.compareTo(BigInteger.ONE) == 0) {
      this.divisor = BigInteger.ONE;
    } else {
      this.divisor = divisor; // Exact conversion.
    }
  }

  /**
   * Convenience method equivalent to <code>new RationalConverter(BigInteger.valueOf(dividend), BigInteger.valueOf
   * (divisor))</code>
   *
   * @param dividend
   *          the dividend.
   * @param divisor
   *          the positive divisor.
   * @throws IllegalArgumentException
   *           if <code>divisor &lt;= 0</code>
   * @throws IllegalArgumentException
   *           if <code>dividend == divisor</code>
   */
  public RationalConverter(long dividend, long divisor) {
    this(BigInteger.valueOf(dividend), BigInteger.valueOf(divisor));
  }

  /**
   * Convenience method equivalent to <code>new RationalConverter(dividend, divisor)</code>
   *
   * @param dividend
   *          the dividend.
   * @param divisor
   *          the positive divisor.
   * @throws IllegalArgumentException
   *           if <code>divisor &lt;= 0</code>
   * @throws IllegalArgumentException
   *           if <code>dividend == divisor</code>
   */
  public static RationalConverter of(BigInteger dividend, BigInteger divisor) {
    return new RationalConverter(dividend, divisor);
  }

  /**
   * Convenience method equivalent to <code>new RationalConverter(dividend, divisor)</code>
   *
   * @param dividend
   *          the dividend.
   * @param divisor
   *          the positive divisor.
   * @throws IllegalArgumentException
   *           if <code>divisor &lt;= 0</code>
   * @throws IllegalArgumentException
   *           if <code>dividend == divisor</code>
   */
  public static RationalConverter of(long dividend, long divisor) {
    return new RationalConverter(dividend, divisor);
  }

  /**
   * Convenience method equivalent to <code>new RationalConverter(BigDecimal.valueOf(dividend).toBigInteger(),
   * BigDecimal.valueOf(divisor).toBigInteger())</code>
   *
   * @param dividend
   *          the dividend.
   * @param divisor
   *          the positive divisor.
   * @throws IllegalArgumentException
   *           if <code>divisor &lt;= 0</code>
   * @throws IllegalArgumentException
   *           if <code>dividend == divisor</code>
   */
  public static RationalConverter of(double dividend, double divisor) {
    return new RationalConverter(BigDecimal.valueOf(dividend).toBigInteger(), BigDecimal.valueOf(divisor).toBigInteger());
  }

  /**
   * Returns the integer dividend for this rational converter.
   *
   * @return this converter dividend.
   */
  public BigInteger getDividend() {
    return dividend;
  }

  /**
   * Returns the integer (positive) divisor for this rational converter.
   *
   * @return this converter divisor.
   */
  public BigInteger getDivisor() {
    return divisor;
  }

  @Override
  public double convert(double value) {
    return value * toDouble(dividend) / toDouble(divisor);
  }

  // Optimization of BigInteger.doubleValue() (implementation too
  // inneficient).
  private static double toDouble(BigInteger integer) {
    return (integer.bitLength() < 64) ? integer.longValue() : integer.doubleValue();
  }

  @Override
  public BigDecimal convert(BigDecimal value, MathContext ctx) throws ArithmeticException {
    BigDecimal decimalDividend = new BigDecimal(dividend, 0);
    BigDecimal multiplied = value.multiply(decimalDividend, ctx);
    if (divisor == BigInteger.ONE) {
      return multiplied;
    }
    BigDecimal decimalDivisor = new BigDecimal(divisor, 0);
    return multiplied.divide(decimalDivisor, ctx);
  }

  @Override
  public UnitConverter concatenate(UnitConverter converter) {
    if (!(converter instanceof RationalConverter) && !(converter instanceof PowerOfTenConverter)) {
      return super.concatenate(converter);
    }
    BigInteger newDividend;
    BigInteger newDivisor;
    if (converter instanceof RationalConverter) {
      RationalConverter that = (RationalConverter) converter;
      newDividend = this.getDividend().multiply(that.getDividend());
      newDivisor = this.getDivisor().multiply(that.getDivisor());
    } else {
      PowerOfTenConverter that = (PowerOfTenConverter) converter;
      if (that.getPowerOfTen() > 0) {
        newDividend = this.getDividend().multiply(TEN.pow(that.getPowerOfTen()));
        newDivisor = this.getDivisor();
      } else {
        newDividend = this.getDividend();
        newDivisor = this.getDivisor().multiply(TEN.pow(-that.getPowerOfTen()));
      }
    }
    BigInteger gcd = newDividend.gcd(newDivisor);
    newDividend = newDividend.divide(gcd);
    newDivisor = newDivisor.divide(gcd);
    if (newDivisor.compareTo(BigInteger.ONE) == 0) {
      if (newDividend.compareTo(BigInteger.ONE) == 0) {
        return IDENTITY;
      }
      Integer powerOfTen = BIG_INTEGER_INTEGER_MAP.get(newDividend);
      if (powerOfTen != null) {
        return new PowerOfTenConverter(powerOfTen);
      }
    } else if (newDividend.compareTo(BigInteger.ONE) == 0) {
      Integer powerOfTen = BIG_INTEGER_INTEGER_MAP.get(newDivisor);
      if (powerOfTen != null) {
        return new PowerOfTenConverter(-powerOfTen);
      }
    }
    return new RationalConverter(newDividend, newDivisor);
  }

  @Override
  public RationalConverter inverse() {
    return dividend.signum() == -1 ? new RationalConverter(getDivisor().negate(), getDividend().negate()) : new RationalConverter(getDivisor(),
        getDividend());
  }

  @Override
  public final String toString() {
    return "RationalConverter(" + dividend + "," + divisor + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof RationalConverter) {

      RationalConverter that = (RationalConverter) obj;
      return Objects.equals(dividend, that.dividend) && Objects.equals(divisor, that.divisor);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(dividend, divisor);
  }

  @Override
  public boolean isLinear() {
    return true;
  }

  @Override
  public Double getValue() {
    return getAsDouble();
  }

  @Override
  public double getAsDouble() {
    return toDouble(dividend) / toDouble(divisor);
  }

  @Override
  public Double get() {
    return getValue();
  }
}

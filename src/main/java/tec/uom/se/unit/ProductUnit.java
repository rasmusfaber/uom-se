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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * This class represents units formed by the product of rational powers of existing physical units.
 * </p>
 * <p>
 * <p>
 * This class maintains the canonical form of this product (simplest form after factorization). For example: <code>METRE.pow(2).divide(METRE)</code>
 * returns <code>METRE</code>.
 * </p>
 *
 * @param <Q>
 *          The type of the quantity measured by this unit.
 * @author <a href="mailto:jean-marie@dautelle.com">Jean-Marie Dautelle</a>
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @version 1.0.3, August 20, 2017
 * @since 1.0
 */
public final class ProductUnit<Q extends Quantity<Q>> extends AbstractUnit<Q> {

  /**
	 *
	 */
  private static final long serialVersionUID = 962983585531030093L;

  /**
   * Holds the units composing this product unit.
   */
  private final Element[] elements;

  /**
   * Holds the symbol for this unit.
   */
  private final String symbol;

  /**
   * DefaultQuantityFactory constructor (used solely to create <code>ONE</code> instance).
   */
  public ProductUnit() {
    this.symbol = "";
    elements = new Element[0];
  }

  /**
   * Copy constructor (allows for parameterization of product units).
   *
   * @param productUnit
   *          the product unit source.
   * @throws ClassCastException
   *           if the specified unit is not a product unit.
   */
  public ProductUnit(Unit<?> productUnit) {
    this.symbol = productUnit.getSymbol();
    this.elements = ((ProductUnit<?>) productUnit).elements;
  }

  /**
   * Product unit constructor.
   *
   * @param elements
   *          the product elements.
   */
  private ProductUnit(Element[] elements) {
    this.elements = elements;
    this.symbol = elements[0].getUnit().getSymbol(); // FIXME this should
    // contain ALL
    // elements
  }

  /**
   * Returns the product of the specified units.
   *
   * @param left
   *          the left unit operand.
   * @param right
   *          the right unit operand.
   * @return <code>left * right</code>
   */
  public static AbstractUnit<?> getProductInstance(Unit<?> left, Unit<?> right) {
    return getProductInstance(left, right, AbstractConverter.IDENTITY);
  }

  private static AbstractUnit<?> getProductInstance(Unit<?> left, Unit<?> right, UnitConverter converter) {
    if (left instanceof TransformedUnit) {
      TransformedUnit<?> transformedUnit = (TransformedUnit<?>) left;
      if (transformedUnit.getParentUnit() instanceof ProductUnit) {
        return getProductInstance(transformedUnit.getParentUnit(), right, converter.concatenate(transformedUnit.getConverter()));
      }
    }
    if (right instanceof TransformedUnit) {
      TransformedUnit<?> transformedUnit = (TransformedUnit<?>) right;
      if (transformedUnit.getParentUnit() instanceof ProductUnit) {
        return getProductInstance(left, transformedUnit.getParentUnit(), converter.concatenate(transformedUnit.getConverter()));
      }
    }
    Element[] leftElems = getElements(left);
    Element[] rightElems = getElements(right);
    AbstractUnit<?> productUnit = getInstance(leftElems, rightElems);
    if (converter.isIdentity()) {
      return productUnit;
    } else {
      return new TransformedUnit(productUnit, converter);
    }
  }

  private static Element[] getElements(Unit<?> unit) {
    if (unit instanceof ProductUnit<?>) {
      return ((ProductUnit<?>) unit).elements;
    }
    return new Element[] { new Element(unit, 1, 1) };

  }

  /**
   * Returns the quotient of the specified units.
   *
   * @param left
   *          the dividend unit operand.
   * @param right
   *          the divisor unit operand.
   * @return <code>dividend / divisor</code>
   */
  public static AbstractUnit<?> getQuotientInstance(Unit<?> left, Unit<?> right) {
    Element[] leftElems = getElements(left);
    Element[] rightElems = inverseElements(getElements(right));
    return getInstance(leftElems, rightElems);
  }

  private static Element[] inverseElements(Element[] elems) {
    Element[] res = new Element[elems.length];
    for (int i = 0; i < elems.length; i++) {
      res[i] = new Element(elems[i].unit, -elems[i].pow, elems[i].root);
    }
    return res;
  }

  /**
   * Returns the product unit corresponding to the specified root of the specified unit.
   *
   * @param unit
   *          the unit.
   * @param n
   *          the root's order (n &gt; 0).
   * @return <code>unit^(1/nn)</code>
   * @throws ArithmeticException
   *           if <code>n == 0</code>.
   */
  public static AbstractUnit<?> getRootInstance(Unit<?> unit, int n) {
    Element[] unitElems;
    if (unit instanceof ProductUnit<?>) {
      Element[] elems = ((ProductUnit<?>) unit).elements;
      unitElems = new Element[elems.length];
      for (int i = 0; i < elems.length; i++) {
        int gcd = gcd(Math.abs(elems[i].pow), elems[i].root * n);
        unitElems[i] = new Element(elems[i].unit, elems[i].pow / gcd, elems[i].root * n / gcd);
      }
    } else
      unitElems = new Element[] { new Element(unit, 1, n) };
    return getInstance(unitElems, new Element[0]);
  }

  /**
   * Returns the product unit corresponding to this unit raised to the specified exponent.
   *
   * @param unit
   *          the unit.
   * @param nn
   *          the exponent (nn &gt; 0).
   * @return <code>unit^n</code>
   */
  public static AbstractUnit<?> getPowInstance(Unit<?> unit, int n) {
    Element[] unitElems;
    if (unit instanceof ProductUnit<?>) {
      Element[] elems = ((ProductUnit<?>) unit).elements;
      unitElems = new Element[elems.length];
      for (int i = 0; i < elems.length; i++) {
        int gcd = gcd(Math.abs(elems[i].pow * n), elems[i].root);
        unitElems[i] = new Element(elems[i].unit, elems[i].pow * n / gcd, elems[i].root / gcd);
      }
    } else
      unitElems = new Element[] { new Element(unit, n, 1) };
    return getInstance(unitElems, new Element[0]);
  }

  /**
   * Returns the number of unit elements in this product.
   *
   * @return the number of unit elements.
   */
  public int getUnitCount() {
    return elements.length;
  }

  /**
   * Returns the unit element at the specified position.
   *
   * @param index
   *          the index of the unit element to return.
   * @return the unit element at the specified position.
   * @throws IndexOutOfBoundsException
   *           if index is out of range <code>(index &lt; 0 || index &gt;= getUnitCount())
   *                                   </code>.
   */
  public Unit<?> getUnit(int index) {
    return elements[index].getUnit();
  }

  /**
   * Returns the power exponent of the unit element at the specified position.
   *
   * @param index
   *          the index of the unit element.
   * @return the unit power exponent at the specified position.
   * @throws IndexOutOfBoundsException
   *           if index is out of range <code>(index &lt; 0 || index &gt;= getUnitCount())
   *                                   </code>.
   */
  public int getUnitPow(int index) {
    return elements[index].getPow();
  }

  /**
   * Returns the root exponent of the unit element at the specified position.
   *
   * @param index
   *          the index of the unit element.
   * @return the unit root exponent at the specified position.
   * @throws IndexOutOfBoundsException
   *           if index is out of range <code>(index &lt; 0 || index &gt;= getUnitCount())
   *                                   </code>.
   */
  public int getUnitRoot(int index) {
    return elements[index].getRoot();
  }

  @Override
  public Map<Unit<?>, Integer> getBaseUnits() {
    final Map<Unit<?>, Integer> units = new HashMap<>(); // Diamond (Java7+)
    for (int i = 0; i < getUnitCount(); i++) {
      units.put(getUnit(i), getUnitPow(i));
    }
    return units;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ProductUnit) {
      ProductUnit other = (ProductUnit) obj;
      return Arrays.equals(elements, other.elements);
    }
    if (obj instanceof Unit<?>) {
      // A wrapper ProductUnit is equal to the unit it wraps
      return elements.length == 1 && elements[0].pow == elements[0].root && obj.equals(elements[0].unit);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int res = 0;
    for (Element element : elements) {
      res += element.hashCode();
    }
    return res;
  }

  @SuppressWarnings("unchecked")
  @Override
  public AbstractUnit<Q> toSystemUnit() {
    Unit<?> systemUnit = AbstractUnit.ONE;
    for (Element element : elements) {
      Unit<?> unit = element.unit.getSystemUnit();
      unit = unit.pow(element.pow);
      unit = unit.root(element.root);
      systemUnit = systemUnit.multiply(unit);
    }
    return (AbstractUnit<Q>) systemUnit;
  }

  @Override
  public boolean isSystemUnit() {
    for (Element element : elements) {
      if (!(element.unit instanceof AbstractUnit<?> && ((AbstractUnit<?>) element.unit).isSystemUnit())) {
        return super.isSystemUnit();
      }
    }
    return true;
  }

  @Override
  public UnitConverter getSystemConverter() {
    UnitConverter converter = AbstractConverter.IDENTITY;
    for (Element e : elements) {
      if (e.unit instanceof AbstractUnit) {
        UnitConverter cvtr = ((AbstractUnit) e.unit).getSystemConverter();
        if (!(cvtr.isLinear()))
          throw new UnsupportedOperationException(e.unit + " is non-linear, cannot convert");
        if (e.root != 1)
          throw new UnsupportedOperationException(e.unit + " holds a base unit with fractional exponent");
        int pow = e.pow;
        if (pow < 0) { // Negative power.
          pow = -pow;
          cvtr = cvtr.inverse();
        }
        for (int j = 0; j < pow; j++) {
          converter = converter.concatenate(cvtr);
        }
      }
    }
    return converter;
  }

  @Override
  public Dimension getDimension() {
    Dimension dimension = QuantityDimension.NONE;
    for (int i = 0; i < this.getUnitCount(); i++) {
      Unit<?> unit = this.getUnit(i);
      if (this.elements != null && unit.getDimension() != null) {
        Dimension d = unit.getDimension().pow(this.getUnitPow(i)).root(this.getUnitRoot(i));
        dimension = dimension.multiply(d);
      }
    }
    return dimension;
  }

  /**
   * Returns the unit defined from the product of the specified elements.
   *
   * @param leftElems
   *          left multiplicand elements.
   * @param rightElems
   *          right multiplicand elements.
   * @return the corresponding unit.
   */
  @SuppressWarnings("rawtypes")
  private static AbstractUnit<?> getInstance(Element[] leftElems, Element[] rightElems) {

    // Merges left elements with right elements.
    Element[] result = new Element[leftElems.length + rightElems.length];
    int resultIndex = 0;
    for (Element leftElem : leftElems) {
      Unit<?> unit = leftElem.unit;
      int p1 = leftElem.pow;
      int r1 = leftElem.root;
      int p2 = 0;
      int r2 = 1;
      for (Element rightElem : rightElems) {
        if (unit.equals(rightElem.unit)) {
          p2 = rightElem.pow;
          r2 = rightElem.root;
          break; // No duplicate.
        }
      }
      int pow = (p1 * r2) + (p2 * r1);
      int root = r1 * r2;
      if (pow != 0) {
        int gcd = gcd(Math.abs(pow), root);
        result[resultIndex++] = new Element(unit, pow / gcd, root / gcd);
      }
    }

    // Appends remaining right elements not merged.
    for (Element rightElem : rightElems) {
      Unit<?> unit = rightElem.unit;
      boolean hasBeenMerged = false;
      for (Element leftElem : leftElems) {
        if (unit.equals(leftElem.unit)) {
          hasBeenMerged = true;
          break;
        }
      }
      if (!hasBeenMerged)
        result[resultIndex++] = rightElem;
    }

    // Returns or creates instance.
    if (resultIndex == 0)
      return AbstractUnit.ONE;
    else if ((resultIndex == 1) && (result[0].pow == result[0].root))
      return maybeWrap(result[0].unit);
    else {
      Element[] elems = new Element[resultIndex];
      System.arraycopy(result, 0, elems, 0, resultIndex);
      Arrays.sort(elems);
      return new ProductUnit(elems);
    }
  }

  /**
   * Wraps the given unit in a ProductUnit if it's not already an AbstractUnit.
   */
  private static <Q extends Quantity<Q>> AbstractUnit<Q> maybeWrap(Unit<Q> unit) {
    if (unit instanceof AbstractUnit<?>) {
      return (AbstractUnit<Q>) unit;
    } else {
      return new ProductUnit<Q>(unit);
    }
  }

  /**
   * Returns the greatest common divisor (Euclid's algorithm).
   *
   * @param m
   *          the first number.
   * @param nn
   *          the second number.
   * @return the greatest common divisor.
   */
  private static int gcd(int m, int n) {
    if (n == 0)
      return m;
    else
      return gcd(n, m % n);
  }

  /**
   * Inner product element represents a rational power of a single unit.
   */
  private final static class Element implements Serializable, Comparable<Element> {

    /**
		 *
		 */
    private static final long serialVersionUID = 452938412398890507L;

    /**
     * Holds the single unit.
     */
    private final Unit<?> unit;

    /**
     * Holds the power exponent.
     */
    private final int pow;

    /**
     * Holds the root exponent.
     */
    private final int root;

    /**
     * Structural constructor.
     *
     * @param unit
     *          the unit.
     * @param pow
     *          the power exponent.
     * @param root
     *          the root exponent.
     */
    private Element(Unit<?> unit, int pow, int root) {
      this.unit = unit;
      this.pow = pow;
      this.root = root;
    }

    /**
     * Returns this element's unit.
     *
     * @return the single unit.
     */
    public Unit<?> getUnit() {
      return unit;
    }

    /**
     * Returns the power exponent. The power exponent can be negative but is always different from zero.
     *
     * @return the power exponent of the single unit.
     */
    public int getPow() {
      return pow;
    }

    /**
     * Returns the root exponent. The root exponent is always greater than zero.
     *
     * @return the root exponent of the single unit.
     */
    public int getRoot() {
      return root;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      Element element = (Element) o;

      if (pow != element.pow) {
        return false;
      }
      return root == element.root && (unit != null ? unit.equals(element.unit) : element.unit == null);

    }

    /**
     * Arbitrary ordering, to be used only in sorting arrays for {@link ProductUnit#equals} and {@link ProductUnit#hashCode}.
     */
    @Override
    public int compareTo(Element other) {
      // hashCode() - other.hashCode() may overflow
      long ourHash = hashCode();
      long theirHash = other.hashCode();
      if (ourHash < theirHash) {
        return -1;
      } else if (ourHash == theirHash) {
        return 0;
      } else {
        return 1;
      }
    }

    @Override
    public int hashCode() {
      return Objects.hash(unit, ((double) pow) / root);
    }
  }

  @Override
  public String getSymbol() {
    return symbol;
  }
}

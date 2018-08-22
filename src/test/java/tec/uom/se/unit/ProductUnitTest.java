package tec.uom.se.unit;

import org.junit.Test;

import javax.measure.Unit;

import static org.junit.Assert.assertEquals;

public class ProductUnitTest {
  @Test
  public void mwhToMw() throws Exception {
    Unit<?> mw = MetricPrefix.MEGA(Units.WATT);
    Unit<?> mwh = MetricPrefix.MEGA(Units.WATT.multiply(Units.HOUR));
    assertEquals(mw, mwh.divide(Units.HOUR));
  }
}

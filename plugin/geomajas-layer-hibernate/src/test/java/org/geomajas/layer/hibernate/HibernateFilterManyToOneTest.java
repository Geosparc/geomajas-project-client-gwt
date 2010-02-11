/*
 * This file is part of Geomajas, a component framework for building
 * rich Internet applications (RIA) with sophisticated capabilities for the
 * display, analysis and management of geographic information.
 * It is a building block that allows developers to add maps
 * and other geographic data capabilities to their web applications.
 *
 * Copyright 2008-2010 Geosparc, http://www.geosparc.com, Belgium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geomajas.layer.hibernate;

import org.geomajas.layer.LayerException;
import org.geomajas.layer.hibernate.pojo.HibernateTestFeature;
import org.geomajas.layer.hibernate.pojo.HibernateTestManyToOne;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opengis.filter.Filter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * TestCase for Hibernate's ManyToOne association. All possible filters we can imagine on such an association must be
 * put to the test here.
 * 
 * @author Pieter De Graef
 */
public class HibernateFilterManyToOneTest extends AbstractHibernateLayerModelTest {

	static boolean initialised = false;

	@Before
	public void setUpTestDataWithinTransaction() throws LayerException {
		HibernateTestFeature f1 = HibernateTestFeature.getDefaultInstance1(null);
		f1.setManyToOne(HibernateTestManyToOne.getDefaultInstance1(null));
		layer.create(f1);
		HibernateTestFeature f2 = HibernateTestFeature.getDefaultInstance2(null);
		f2.setManyToOne(HibernateTestManyToOne.getDefaultInstance2(null));
		layer.create(f2);
		HibernateTestFeature f3 = HibernateTestFeature.getDefaultInstance3(null);
		f3.setManyToOne(HibernateTestManyToOne.getDefaultInstance3(null));
		layer.create(f3);
		HibernateTestFeature f4 = HibernateTestFeature.getDefaultInstance4(null);
		f4.setManyToOne(HibernateTestManyToOne.getDefaultInstance4(null));
		layer.create(f4);
	}

	@Test
	public void betweenFilterOnInteger() throws Exception {
		Filter filter = filterCreator.createBetweenFilter(ATTR__MANY_TO_ONE__DOT__INT, "50", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void betweenFilterOnFloat() throws Exception {
		Filter filter = filterCreator.createBetweenFilter(ATTR__MANY_TO_ONE__DOT__FLOAT, "50", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void betweenFilterOnDouble() throws Exception {
		Filter filter = filterCreator.createBetweenFilter(ATTR__MANY_TO_ONE__DOT__DOUBLE, "50", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void ltFilterOnInt() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__INT, "<", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void leFilterOnInt() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__INT, "<=", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void gtFilterOnInt() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__INT, ">", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void geFilterOnInt() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__INT, ">=", "300");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void eqFilterOnInt() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__INT, "==", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(1, t);
	}

	@Test
	public void neFilterOnInt() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__INT, "<>", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(3, t);
	}

	@Test
	public void ltFilterOnFloat() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__FLOAT, "<", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void leFilterOnFloat() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__FLOAT, "<=", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void gtFilterOnFloat() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__FLOAT, ">", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void geFilterOnFloat() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__FLOAT, ">=", "300");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void eqFilterOnFloat() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__FLOAT, "==", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(1, t);
	}

	@Test
	public void neFilterOnFloat() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__FLOAT, "<>", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(3, t);
	}

	@Test
	public void ltFilterOnDouble() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DOUBLE, "<", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void leFilterOnDouble() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DOUBLE, "<=", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void gtFilterOnDouble() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DOUBLE, ">", "250");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void geFilterOnDouble() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DOUBLE, ">=", "300");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void eqFilterOnDouble() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DOUBLE, "==", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(1, t);
	}

	@Test
	public void neFilterOnDouble() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DOUBLE, "<>", "200");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(3, t);
	}

	@Test
	public void ltFilterOnDate() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		date = format.parse("01/01/2008");
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DATE, "<", date);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void leFilterOnDate() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		date = format.parse("01/01/2007");
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DATE, "<=", date);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void gtFilterOnDate() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		date = format.parse("01/01/2007");
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DATE, ">", date);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void geFilterOnDate() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		date = format.parse("01/01/2007");
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DATE, ">=", date);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(3, t);
	}

	@Test
	public void eqFilterOnDate() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		date = format.parse("01/01/2008");
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DATE, "==", date);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(1, t);
	}

	@Test
	public void neFilterOnDate() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date;
		date = format.parse("01/01/2008");
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__DATE, "<>", date);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(3, t);
	}

	@Test
	public void eqFilterOnBoolean() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__BOOLEAN, "==", "true");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void neFilterOnBoolean() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__BOOLEAN, "<>", "true");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(2, t);
	}

	@Test
	public void eqFilterOnString() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__TEXT, "==", "manyToOne-1");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(1, t);
	}

	@Test
	public void neFilterOnString() throws Exception {
		Filter filter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__TEXT, "<>", "manyToOne-1");
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(3, t);
	}

	@Test
	public void likeFilter() throws Exception {
		Filter wildCardFilter = filterCreator.createLikeFilter(ATTR__MANY_TO_ONE__DOT__TEXT, "*-1");
		Iterator<?> it = layer.getElements(wildCardFilter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(1, t);

		Filter singleCharFilter = filterCreator.createLikeFilter(ATTR__MANY_TO_ONE__DOT__TEXT, "manyToOne-?");
		it = layer.getElements(singleCharFilter, 0, 0);
		t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(4, t);
	}

	@Test
	public void andFilter() throws Exception {
		Filter textFilter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__TEXT, "==", "manyToOne-1");
		Filter intFilter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__INT, "<", "250");
		Filter filter = filterCreator.createLogicFilter(textFilter, "and", intFilter);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(1, t);
	}

	@Test
	public void orFilter() throws Exception {
		Filter textFilter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__TEXT, "==", "manyToOne-1");
		Filter intFilter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__INT, ">", "250");
		Filter filter = filterCreator.createLogicFilter(textFilter, "or", intFilter);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(3, t);
	}

	@Test
	public void notFilter() throws Exception {
		Filter textFilter = filterCreator.createCompareFilter(ATTR__MANY_TO_ONE__DOT__TEXT, "==", "manyToOne-1");
		Filter filter = filterCreator.createLogicFilter(textFilter, "NOT", null);
		Iterator<?> it = layer.getElements(filter, 0, 0);
		int t = 0;
		while (it.hasNext()) {
			Assert.assertTrue("Returned object must be a HibernateTestFeature",
					it.next() instanceof HibernateTestFeature);
			t++;
		}
		Assert.assertEquals(3, t);
	}
}

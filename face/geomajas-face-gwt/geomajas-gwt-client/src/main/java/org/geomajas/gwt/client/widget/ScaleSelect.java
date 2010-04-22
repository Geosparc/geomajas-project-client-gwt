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

package org.geomajas.gwt.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.geomajas.global.Api;
import org.geomajas.gwt.client.i18n.I18nProvider;
import org.geomajas.gwt.client.map.MapView;
import org.geomajas.gwt.client.map.event.MapViewChangedEvent;
import org.geomajas.gwt.client.map.event.MapViewChangedHandler;

import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.CustomValidator;

/**
 * <p>
 * A drop down selection box for setting and displaying the current scale of a map. The displayed scale is a relative
 * scale and is expressed as an abstract number, as opposed to the MapView scale which is expressed in pixels per map
 * unit. An arbitrary scale can be filled in by the user. Implemented as a canvas so it can be added where needed.
 * </p>
 * <p>
 * This widget has the option to add new scale levels to the list, as the user types them (and presses 'Enter'). By
 * default this option is turned off. Turn it on using the <code>setUpdatingScaleList</code> method.
 * </p>
 * 
 * @author Jan De Moerloose
 * @since 1.6.0
 */
@Api
public class ScaleSelect extends Canvas implements KeyPressHandler, ChangedHandler, MapViewChangedHandler {

	private MapView mapView;

	private DynamicForm form;

	private ComboBoxItem scaleItem;

	private static NumberFormat DENOMINATOR_FORMAT = NumberFormat.getFormat("###,###");

	// needed for item
	private List<String> valueList;

	// bidirectional lookup
	private LinkedHashMap<Double, String> scaleToValue;

	private LinkedHashMap<String, Double> valueToScale;

	// pixel length in map units
	private double pixelLength;

	private boolean updatingScaleList;

	// -------------------------------------------------------------------------
	// Constructor:
	// -------------------------------------------------------------------------

	/**
	 * Constructs a ScaleSelect that acts on the specified map view.
	 * 
	 * @param mapView
	 *            the map view
	 * @param pixelLength
	 *            the pixel length in map units
	 * @since 1.6.0
	 */
	@Api
	public ScaleSelect(MapView mapView, double pixelLength) {
		this.mapView = mapView;
		this.pixelLength = pixelLength;
		setWidth100();
		setHeight100();
		init();
	}

	// -------------------------------------------------------------------------
	// Public methods:
	// -------------------------------------------------------------------------

	/**
	 * Set the specified relative scale values in the select item.
	 * 
	 * @param scales
	 *            array of relative scales (should be multiplied by pixelLength if in pixels/m)
	 * @since 1.6.0
	 */
	@Api
	public void setScales(Double... scales) {
		// sort decreasing
		Arrays.sort(scales, Collections.reverseOrder());
		// create lookup maps
		valueList = new ArrayList<String>();
		scaleToValue = new LinkedHashMap<Double, String>();
		valueToScale = new LinkedHashMap<String, Double>();
		for (int i = 0; i < scales.length; i++) {
			Double scale = scales[i];
			// eliminate doubles and null
			if (scale != null && !scaleToValue.containsKey(scale)) {
				String value = scaleToString(scale);
				valueList.add(value);
				scaleToValue.put(scale, value);
				valueToScale.put(value, scale);
			}
		}
		// set list
		scaleItem.setValueMap(valueList.toArray(new String[0]));
	}

	public MapView getMapView() {
		return mapView;
	}

	/**
	 * When typing custom scale levels in the select item, should these new scale levels be added to the list or not?
	 */
	public boolean isUpdatingScaleList() {
		return updatingScaleList;
	}

	/**
	 * When typing custom scale levels in the select item, should these new scale levels be added to the list or not?
	 * The default value is false, which means that the list of scales in the select item does not change.
	 * 
	 * @param updatingScaleList
	 *            Should the new scale be added?
	 */
	public void setUpdatingScaleList(boolean updatingScaleList) {
		this.updatingScaleList = updatingScaleList;
	}

	/**
	 * When the MapView changes, update the select item to the correct scale.
	 */
	public void onMapViewChanged(MapViewChangedEvent event) {
		if (!event.isSameScaleLevel() || scaleItem.getDisplayValue() == null || 
				"".equals(scaleItem.getDisplayValue())) {
			setDisplayScale(mapView.getCurrentScale() * pixelLength);
		}
	}

	/**
	 * Make sure that the scale in the scale select is applied on the map, when the user presses the 'Enter' key.
	 */
	public void onKeyPress(KeyPressEvent event) {
		String name = event.getKeyName();
		if (name.equalsIgnoreCase("enter")) {
			reorderValues();
		}
	}

	/**
	 * When the user selects a different scale, have the map zoom to it.
	 */
	public void onChanged(ChangedEvent event) {
		String value = (String) scaleItem.getValue();
		Double scale = valueToScale.get(value);
		if (scale != null) {
			mapView.setCurrentScale(scale / pixelLength, MapView.ZoomOption.LEVEL_CLOSEST);
		}
	}

	// -------------------------------------------------------------------------
	// Private methods:
	// -------------------------------------------------------------------------

	protected void setDisplayScale(double scale) {
		scaleItem.setValue(scaleToString(scale));
	}

	protected String scaleToString(double scale) {
		if (scale > 0 && scale < 1.0) {
			int denom = (int) Math.round(1. / scale);
			return "1 : " + DENOMINATOR_FORMAT.format(denom);
		} else if (scale >= 1.0) {
			int denom = (int) Math.round(scale);
			return DENOMINATOR_FORMAT.format(denom) + " : 1";
		} else {
			return "negative scale not allowed";
		}
	}

	protected Double stringToScale(String s) {
		String[] scale2 = s.split(":");
		if (scale2.length == 1) {
			return 1.0 / DENOMINATOR_FORMAT.parse(scale2[0].trim());
		} else {
			return DENOMINATOR_FORMAT.parse(scale2[0].trim()) / DENOMINATOR_FORMAT.parse(scale2[1].trim());
		}
	}

	private void init() {
		form = new DynamicForm();
		scaleItem = new ComboBoxItem();
		scaleItem.setTitle(I18nProvider.getToolbar().scaleSelect());
		scaleItem.setValidators(new ScaleValidator());
		scaleItem.setValidateOnChange(true);
		scaleItem.addKeyPressHandler(this);
		scaleItem.addChangedHandler(this);
		form.setFields(scaleItem);
		addChild(form);
		if (mapView.getResolutions() != null) {
			List<Double> scales = new ArrayList<Double>();
			for (Double resolution : mapView.getResolutions()) {
				scales.add(pixelLength / resolution);
			}
			setScales(scales.toArray(new Double[0]));
		}
		mapView.addMapViewChangedHandler(this);
	}

	private void reorderValues() {
		String value = (String) scaleItem.getValue();
		if (value != null) {
			Double scale = valueToScale.get(value);
			if (scale == null) {
				scale = stringToScale(value);
				if (updatingScaleList) {
					List<Double> newScales = new ArrayList<Double>(valueToScale.values());
					newScales.add(scale);
					setScales(newScales.toArray(new Double[0]));
				}
			}
			scaleItem.setValue(scaleToValue.get(scale));
			mapView.setCurrentScale(scale / pixelLength, MapView.ZoomOption.LEVEL_CLOSEST);
		}
	}

	/**
	 * Custom validation of user entered scale
	 */
	private class ScaleValidator extends CustomValidator {

		@Override
		protected boolean condition(Object value) {
			try {
				Double d = stringToScale((String) value);
				return d.doubleValue() >= 0;
			} catch (Throwable t) {
				return false;
			}
		}
	}
}

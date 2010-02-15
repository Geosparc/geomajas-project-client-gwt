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

package org.geomajas.gwt.client.samples;

import org.geomajas.gwt.client.controller.PanController;
import org.geomajas.gwt.client.samples.base.SamplePanel;
import org.geomajas.gwt.client.samples.i18n.I18nProvider;
import org.geomajas.gwt.client.widget.MapWidget;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * <p>
 * Sample that shows a map with an OpenStreetMap layer.
 * </p>
 * 
 * @author Pieter De Graef
 */
public class OpenStreetMapSample extends SamplePanel {

	private MapWidget map;

	public OpenStreetMapSample() {
		super("OSM", I18nProvider.getSampleMessages().osmTitle(), "[ISOMORPHIC]/geomajas/layer-raster.png");
	}

	public Canvas getViewPanel() {
		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();
		
		// Map with ID osmMap is defined in the XML configuration. (mapOsm.xml)
		map = new MapWidget("gwt-samples","osmMap");

		// Set a panning controller on the map:
		map.setController(new PanController(map));
		layout.addMember(map);

		// Specific to these samples; calls the MapWidget.initialize method when ready.
		registerMap(map);
		return layout;
	}

	public String getDescription() {
		return I18nProvider.getSampleMessages().osmDescription();
	}

	public String[] getConfigurationFiles() {
		return new String[] { "/org/geomajas/gwt/samples/mapwidget/layerOsm.xml",
				"/org/geomajas/gwt/samples/mapwidget/mapOsm.xml" };
	}
}

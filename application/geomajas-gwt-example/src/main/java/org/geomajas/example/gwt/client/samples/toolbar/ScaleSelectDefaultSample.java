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

package org.geomajas.example.gwt.client.samples.toolbar;

import org.geomajas.example.gwt.client.samples.base.SamplePanel;
import org.geomajas.example.gwt.client.samples.base.SamplePanelFactory;
import org.geomajas.gwt.client.controller.PanController;
import org.geomajas.gwt.client.map.event.MapModelEvent;
import org.geomajas.gwt.client.map.event.MapModelHandler;
import org.geomajas.example.gwt.client.samples.i18n.I18nProvider;
import org.geomajas.gwt.client.widget.MapWidget;
import org.geomajas.gwt.client.widget.ScaleSelect;
import org.geomajas.gwt.client.widget.Toolbar;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * <p>
 * Sample that shows a ScaleSelect using the default zoomlevels from the configuration.
 * </p>
 * 
 * @author Frank Wynants
 */
public class ScaleSelectDefaultSample extends SamplePanel {

	public static final String TITLE = "ScaleSelectDefault";

	public static final SamplePanelFactory FACTORY = new SamplePanelFactory() {

		public SamplePanel createPanel() {
			return new ScaleSelectDefaultSample();
		}
	};

	/**
	 * @return The viewPanel Canvas
	 */
	public Canvas getViewPanel() {
		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();

		final MapWidget map = new MapWidget("osmMapResolutions", "gwt-samples");

		// Set a panning controller on the map:
		map.setController(new PanController(map));

		final Toolbar toolbar = new Toolbar(map);
		// add a button in GWT code
		layout.addMember(toolbar);
		layout.addMember(map);

		// wait for the map to be loaded cause we need a correct map.getPixelPerUnit
		map.getMapModel().addMapModelHandler(new MapModelHandler() {

			public void onMapModelChange(MapModelEvent event) {
				ScaleSelect scaleSelect = new ScaleSelect(map.getMapModel().getMapView(), map.getPixelPerUnit());
				toolbar.addMember(scaleSelect);
			}
		});

		return layout;
	}

	public String getDescription() {
		return I18nProvider.getSampleMessages().scaleSelectDefaultDescription();
	}

	public String getSourceFileName() {
		return "classpath:org/geomajas/example/gwt/client/samples/toolbar/ScaleSelectDefaultSample.txt";
	}

	public String[] getConfigurationFiles() {
		return new String[] { "WEB-INF/layerOsm.xml",
				"WEB-INF/toolbar/mapResolutions.xml" };
	}

	public String ensureUserLoggedIn() {
		return "luc";
	}
}

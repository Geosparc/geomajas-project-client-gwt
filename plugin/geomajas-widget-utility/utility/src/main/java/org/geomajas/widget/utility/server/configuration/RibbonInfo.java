/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.widget.utility.server.configuration;

import java.util.List;

import org.geomajas.annotation.Api;
import org.geomajas.configuration.client.ClientWidgetInfo;

/**
 * Ribbon configuration information object. This object represents a tabbed ribbon, not a single bar.
 * 
 * @author Pieter De Graef
 * @since 1.0.0
 */
@Api(allMethods = true)
public class RibbonInfo implements ClientWidgetInfo {

	private static final long serialVersionUID = 100L;

	private String id;

	private List<RibbonBarInfo> tabs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<RibbonBarInfo> getTabs() {
		return tabs;
	}

	public void setTabs(List<RibbonBarInfo> tabs) {
		this.tabs = tabs;
	}
}
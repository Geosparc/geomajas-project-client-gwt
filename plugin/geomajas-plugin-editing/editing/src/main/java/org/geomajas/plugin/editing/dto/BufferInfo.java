/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2012 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.plugin.editing.dto;

/**
 * DTO object that holds  additional information needed to perform a buffer operation.
 * 
 * @author Jan De Moerloose
 */
public class BufferInfo {

	private double distance;

	/**
	 * Get distance to buffer.
	 *
	 * @return distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Set distance to buffer.
	 *
	 * @param distance distance
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

}

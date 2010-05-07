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
package org.geomajas.layer.osm;

import java.util.ArrayList;
import java.util.List;

import org.geomajas.configuration.RasterLayerInfo;
import org.geomajas.geometry.Bbox;
import org.geomajas.global.ExceptionCode;
import org.geomajas.global.GeomajasException;
import org.geomajas.layer.LayerException;
import org.geomajas.layer.RasterLayer;
import org.geomajas.layer.tile.RasterTile;
import org.geomajas.layer.tile.TileCode;
import org.geomajas.service.ConfigurationService;
import org.geomajas.service.DtoConverterService;
import org.geomajas.service.GeoService;
import org.geotools.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import javax.annotation.PostConstruct;

/**
 * Layer for displaying OpenStreetMap images.
 * 
 * @author Jan De Moerloose
 */
public class OsmLayer implements RasterLayer {

	protected List<OSMResolution> resolutions = new ArrayList<OSMResolution>();

	protected int tileWidth;

	protected int tileHeight;

	protected double maxWidth;

	protected double maxHeight;

	public static final double EARTH_RADIUS_IN_METERS = 6378137.0;

	public static final double EQUATOR_IN_METERS = 2 * Math.PI * EARTH_RADIUS_IN_METERS;

	private final Logger log = LoggerFactory.getLogger(OsmLayer.class);

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private DtoConverterService converterService;

	@Autowired
	private GeoService geoService;

	private RasterLayerInfo layerInfo;

	private CoordinateReferenceSystem crs;

	private String id;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public RasterLayerInfo getLayerInfo() {
		return layerInfo;
	}

	public CoordinateReferenceSystem getCrs() {
		return crs;
	}

	public Envelope getMaxBounds() {
		return converterService.toInternal(layerInfo.getMaxExtent());
	}

	public void setLayerInfo(RasterLayerInfo layerInfo) throws LayerException {
		this.layerInfo = layerInfo;
	}

	@PostConstruct
	private void postConstruct() throws Exception {
		if (null != layerInfo) {
			crs = geoService.getCrs("EPSG:900913"); // we overrule the declared crs, always use mercator/google
			tileWidth = layerInfo.getTileWidth();
			tileHeight = layerInfo.getTileHeight();
			maxWidth = layerInfo.getMaxExtent().getWidth();
			maxHeight = layerInfo.getMaxExtent().getHeight();
			this.calculatePredefinedResolutions();
		}
	}

	public List<RasterTile> paint(CoordinateReferenceSystem boundsCrs, Envelope bounds, double scale)
			throws GeomajasException {
		try {
			MathTransform layerToMap = geoService.findMathTransform(crs, boundsCrs);
			MathTransform mapToLayer = layerToMap.inverse();
			// TODO: if bounds width or height is 0, we run out of memory ?
			bounds = clipBounds(bounds);
			if (bounds.isNull()) {
				return new ArrayList<RasterTile>();
			}
			// find the center of the map in map coordinates (positive
			// y-axis)
			DirectPosition2D center = new DirectPosition2D(0.5 * (bounds.getMinX() + bounds.getMaxX()), 0.5 * (bounds
					.getMinY() + bounds.getMaxY()));

			double scaleRatio = calculateMapUnitPerGoogleMeter(mapToLayer, center);

			// find zoomlevel
			// scale in pix/m should just above the given scale so we have at
			// least
			// 1
			// screen pixel per google pixel ! (otherwise text unreadable)
			int tileLevel = getBestOSMLevelForScaleInPixPerMeter(scale * scaleRatio);

			// find the google indices for the center
			// google indices determine the row and column of the 256x256 image
			// in
			// the big google square for the given zoom zoomLevel
			// the resulting indices are floating point values as the center
			// is not coincident with an image corner !!!!
			Coordinate indicesCenter;
			indicesCenter = getOsmIndicesFromMap(mapToLayer, center, tileLevel);

			// Calculate the width in map units of the image that contains the
			// center
			Coordinate indicesUpperLeft = new Coordinate(Math.floor(indicesCenter.x), Math.floor(indicesCenter.y));
			Coordinate indicesLowerRight = new Coordinate(indicesUpperLeft.x + 1, indicesUpperLeft.y + 1);
			DirectPosition mapUpperLeft = getMapFromOsmIndices(layerToMap, indicesUpperLeft, tileLevel);
			DirectPosition mapLowerRight = getMapFromOsmIndices(layerToMap, indicesLowerRight, tileLevel);
			double width = mapLowerRight.getOrdinate(0) - mapUpperLeft.getOrdinate(0);

			// Calculate the position and indexes of the center image corner
			// in map space
			double xCenter = center.x - (indicesCenter.x - indicesUpperLeft.x) * width;
			double yCenter = center.y + (indicesCenter.y - indicesUpperLeft.y) * width;
			int iCenter = (int) indicesUpperLeft.x;
			int jCenter = (int) indicesUpperLeft.y;

			// Calculate the position and indexes of the upper left image corner
			// that just falls off the screen
			double xMin = xCenter;
			int iMin = iCenter;
			while (xMin > bounds.getMinX()) {
				xMin -= width;
				iMin--;
			}
			double yMax = yCenter;
			int jMin = jCenter;
			while (yMax < bounds.getMaxY()) {
				yMax += width;
				jMin--;
			}
			// Calculate the indexes of the lower right corner
			// that just falls off the screen
			double xMax = xCenter;
			int iMax = iCenter;
			while (xMax < bounds.getMaxX()) {
				xMax += width;
				iMax++;
			}
			double yMin = yCenter;
			int jMax = jCenter;
			while (yMin > bounds.getMinY()) {
				yMin -= width;
				jMax++;
			}
			Coordinate upperLeft = new Coordinate(xMin, yMax);

			// calculate the images
			List<RasterTile> result = new ArrayList<RasterTile>();
			int xScreenUpperLeft = (int) Math.round(upperLeft.x * scale);
			int yScreenUpperLeft = (int) Math.round(upperLeft.y * scale);
			int screenWidth = (int) Math.round(scale * width);
			for (int i = iMin; i < iMax; i++) {
				for (int j = jMin; j < jMax; j++) {
					// Using screen coordinates:
					int x = xScreenUpperLeft + (i - iMin) * screenWidth;
					int y = yScreenUpperLeft - (j - jMin) * screenWidth;

					RasterTile image = new RasterTile(new Bbox(x, -y, screenWidth, screenWidth), getId()
							+ "." + tileLevel + "." + i + "," + j);
					image.setCode(new TileCode(tileLevel, i, j));
					image.setUrl("http://tile.openstreetmap.org/" + tileLevel + "/" + i + "/" + j + ".png");
					log.debug("adding OSM image {}", image);
					result.add(image);
				}
			}
			return result;
		} catch (TransformException e) {
			throw new GeomajasException(e, ExceptionCode.RENDER_TRANSFORMATION_FAILED);
		}
	}

	private double calculateMapUnitPerGoogleMeter(MathTransform layerToGoogle, DirectPosition2D mapPosition) {
		try {
			DirectPosition p1 = layerToGoogle.transform(mapPosition, null);
			DirectPosition mapdx = new DirectPosition2D(mapPosition.x + 1, mapPosition.y);
			DirectPosition p2 = layerToGoogle.transform(mapdx, null);
			return 1. / (p2.getOrdinate(0) - p1.getOrdinate(0));
		} catch (MismatchedDimensionException e) {
			log.warn("calculateMapUnitPerGoogleMeter() : transformation failed", e);
			return 0.653;
		} catch (TransformException e) {
			log.warn("calculateMapUnitPerGoogleMeter() : transformation failed", e);
			return 0.653;
		}
	}

	private Envelope clipBounds(Envelope bounds) {
		return bounds.intersection(getMaxBounds());
	}

	private void calculatePredefinedResolutions() {
		for (int zoomLevel = 0; zoomLevel <= 17; zoomLevel++) {
			double resolution = (EQUATOR_IN_METERS) / (256 * Math.pow(2.0, zoomLevel));
			resolutions.add(new OSMResolution(resolution, zoomLevel));
		}
	}

	private int getBestOSMLevelForScaleInPixPerMeter(double scaleInPixPerMeter) {
		double screenResolution = 1.0 / scaleInPixPerMeter;
		if (screenResolution >= resolutions.get(0).getResolution()) {
			return resolutions.get(0).getZoomLevel();
		} else if (screenResolution <= resolutions.get(resolutions.size() - 1).getResolution()) {
			return resolutions.get(resolutions.size() - 1).getZoomLevel();
		} else {
			for (int i = 0; i < resolutions.size() - 1; i++) {
				OSMResolution upper = resolutions.get(i);
				OSMResolution lower = resolutions.get(i + 1);
				if (screenResolution <= upper.getResolution() && screenResolution >= lower.getResolution()) {
					if ((upper.getResolution() - screenResolution) > 2 * (screenResolution - lower.getResolution())) {
						return lower.getZoomLevel();
					} else {
						return upper.getZoomLevel();
					}
				}
			}
		}
		// should not occur !!!!
		return resolutions.get(resolutions.size() - 1).getZoomLevel();
	}

	private DirectPosition getMapFromOsmIndices(MathTransform googleToLayer, Coordinate indices, int zoomLevel)
			throws TransformException {
		double xMeter = EQUATOR_IN_METERS * indices.x / Math.pow(2.0, zoomLevel) - 0.5 * EQUATOR_IN_METERS;
		double yMeter = -EQUATOR_IN_METERS * indices.y / Math.pow(2.0, zoomLevel) + 0.5 * EQUATOR_IN_METERS;
		return googleToLayer.transform(new DirectPosition2D(xMeter, yMeter), null);
	}

	private Coordinate getOsmIndicesFromMap(MathTransform layerToGoogle, DirectPosition2D map, int zoomLevel)
			throws TransformException {
		DirectPosition google = layerToGoogle.transform(map, null);
		double xIndex = (google.getOrdinate(0) + 0.5 * EQUATOR_IN_METERS) * Math.pow(2.0, zoomLevel)
				/ (EQUATOR_IN_METERS);
		double yIndex = (-google.getOrdinate(1) + 0.5 * EQUATOR_IN_METERS) * Math.pow(2.0, zoomLevel)
				/ (EQUATOR_IN_METERS);
		return new Coordinate(xIndex, yIndex);
	}

	/**
	 * ???
	 */
	class OSMResolution {

		private double resolution;

		private int zoomLevel;

		public OSMResolution(double resolution, int zoomLevel) {
			this.resolution = resolution;
			this.zoomLevel = zoomLevel;
		}

		public int getZoomLevel() {
			return zoomLevel;
		}

		public double getResolution() {
			return resolution;
		}

	}
}

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

package org.geomajas.layer.shapeinmem;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.geomajas.configuration.VectorLayerInfo;
import org.geomajas.global.ExceptionCode;
import org.geomajas.global.GeomajasException;
import org.geomajas.layer.LayerException;
import org.geomajas.layer.VectorLayer;
import org.geomajas.layer.feature.FeatureModel;
import org.geomajas.layer.geotools.DataStoreFactory;
import org.geomajas.service.FilterService;
import org.geomajas.service.GeoService;
import org.geotools.data.DataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Layer model for handling shape files in memory.
 * 
 * @author check subversion
 */
public class ShapeInMemLayer extends FeatureSourceRetriever implements VectorLayer {

	private static final String CLASSPATH_URL_PROTOCOL = "classpath:";

	private Map<String, SimpleFeature> features = new HashMap<String, SimpleFeature>();

	private FeatureModel featureModel;

	private VectorLayerInfo layerInfo;

	@Autowired
	private FilterService filterCreator;

	@Autowired
	private GeoService geoService;

	private CoordinateReferenceSystem crs;

	private URL url;

	public CoordinateReferenceSystem getCrs() {
		return crs;
	}

	private void initCrs() throws LayerException {
		try {
			crs = CRS.decode(layerInfo.getCrs());
		} catch (NoSuchAuthorityCodeException e) {
			throw new LayerException(e, ExceptionCode.LAYER_CRS_UNKNOWN_AUTHORITY, layerInfo.getId(), getLayerInfo()
					.getCrs());
		} catch (FactoryException exception) {
			throw new LayerException(exception, ExceptionCode.LAYER_CRS_PROBLEMATIC, layerInfo.getId(), getLayerInfo()
					.getCrs());
		}
	}

	public void setLayerInfo(VectorLayerInfo layerInfo) throws LayerException {
		this.layerInfo = layerInfo;
		initCrs();
	}

	public VectorLayerInfo getLayerInfo() {
		return layerInfo;
	}

	public boolean isCreateCapable() {
		return true;
	}

	public boolean isUpdateCapable() {
		return true;
	}

	public boolean isDeleteCapable() {
		return true;
	}

	public void setUrl(String url) throws LayerException {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("url", url);
			DataStore store = DataStoreFactory.create(params);
			setDataStore(store);
		} catch (IOException ioe) {
			throw new LayerException(ExceptionCode.INVALID_SHAPE_FILE_URL, url);
		}
	}

	public void setDataStore(DataStore dataStore) throws LayerException {
		super.setDataStore(dataStore);
	}

	/**
	 * This implementation does not support the 'offset' and 'maxResultSize' parameters.
	 */
	public Iterator<?> getElements(Filter queryFilter, int offset, int maxResultSize) throws LayerException {
		Filter filter = convertFilter(queryFilter);
		List<SimpleFeature> filteredList = new ArrayList<SimpleFeature>();
		for (SimpleFeature feature : features.values()) {
			if (filter.evaluate(feature)) {
				filteredList.add(feature);
			}
		}
		return filteredList.iterator();
	}

	public Envelope getBounds() throws LayerException {
		return getBounds(Filter.INCLUDE);
	}

	private Filter convertFilter(Filter queryFilter) {
		if (queryFilter instanceof Id) {
			Iterator<?> iterator = ((Id) queryFilter).getIdentifiers().iterator();
			List<String> identifiers = new ArrayList<String>();
			while (iterator.hasNext()) {
				identifiers.add(getFeatureSourceName() + "." + iterator.next());
			}
			return filterCreator.createFidFilter(identifiers.toArray(new String[identifiers.size()]));
		}
		return queryFilter;
	}

	/**
	 * Retrieve the bounds of the specified features.
	 * 
	 * @return the bounds of the specified features
	 */
	public Envelope getBounds(Filter queryFilter) throws LayerException {
		try {
			Filter filter = convertFilter(queryFilter);
			FeatureCollection<SimpleFeatureType, SimpleFeature> fc = getFeatureSource().getFeatures(filter);
			return fc.getBounds();
		} catch (IOException ioe) {
			throw new LayerException(ioe, ExceptionCode.FEATURE_MODEL_PROBLEM);
		}
	}

	public FeatureModel getFeatureModel() {
		return featureModel;
	}

	public Object create(Object feature) throws LayerException {
		String id = featureModel.getId(feature);
		if (id != null && !features.containsKey(id)) {
			SimpleFeature realFeature = asFeature(feature);
			features.put(id, realFeature);
			return realFeature;
		}
		return null;
	}

	public Object read(String featureId) throws LayerException {
		return features.get(featureId);
	}

	public Object saveOrUpdate(Object feature) throws LayerException {
		if (read(getFeatureModel().getId(feature)) == null) {
			return create(feature);
		} else {
			// Nothing to do
			return feature;
		}
	}

	public void delete(String featureId) throws LayerException {
		features.remove(featureId);
	}

	public Iterator<?> getObjects(String attributeName, Filter filter) throws LayerException {
		return Collections.EMPTY_LIST.iterator();
	}

	// Private functions:
	@PostConstruct
	protected void initFeatures() throws LayerException {
		try {
			setFeatureSourceName(layerInfo.getFeatureInfo().getDataSourceName());
			featureModel = new ShapeInMemFeatureModel(getDataStore(), layerInfo.getFeatureInfo().getDataSourceName(),
					geoService.getSridFromCrs(layerInfo.getCrs()));
			FeatureCollection<SimpleFeatureType, SimpleFeature> col = getFeatureSource().getFeatures();
			FeatureIterator<SimpleFeature> iterator = col.features();
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				String id = featureModel.getId(feature);
				features.put(id, feature);
				int intId = Integer.parseInt(id);
				if (intId > nextId) {
					nextId = intId;
				}
			}
			col.close(iterator);
			((ShapeInMemFeatureModel) featureModel).setNextId(++nextId);
		} catch (NumberFormatException nfe) {
			throw new LayerException(nfe, ExceptionCode.FEATURE_MODEL_PROBLEM);
		} catch (MalformedURLException e) {
			throw new LayerException(ExceptionCode.INVALID_SHAPE_FILE_URL, url);
		} catch (IOException ioe) {
			throw new LayerException(ioe, ExceptionCode.CANNOT_CREATE_LAYER_MODEL, url);
		} catch (GeomajasException ge) {
			throw new LayerException(ge, ExceptionCode.CANNOT_CREATE_LAYER_MODEL, url);
		}
	}

}
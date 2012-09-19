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
package org.geomajas.plugin.deskmanager.service.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geomajas.command.configuration.GetMapConfigurationCommand;
import org.geomajas.configuration.client.ClientApplicationInfo;
import org.geomajas.configuration.client.ClientLayerInfo;
import org.geomajas.configuration.client.ClientMapInfo;
import org.geomajas.geometry.Bbox;
import org.geomajas.geometry.service.GeometryService;
import org.geomajas.global.GeomajasException;
import org.geomajas.layer.VectorLayerService;
import org.geomajas.plugin.deskmanager.client.gwt.geodesk.GeodeskLayout;
import org.geomajas.plugin.deskmanager.configuration.UserApplicationInfo;
import org.geomajas.plugin.deskmanager.domain.Blueprint;
import org.geomajas.plugin.deskmanager.domain.Geodesk;
import org.geomajas.plugin.deskmanager.security.DeskmanagerSecurityContext;
import org.geomajas.plugin.runtimeconfig.service.Rewirable;
import org.geomajas.security.SecurityContext;
import org.geomajas.service.ConfigurationService;
import org.geomajas.service.DtoConverterService;
import org.geomajas.service.GeoService;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kristof Heirwegh
 */
@Component
@Transactional(readOnly = true, rollbackFor = { Exception.class })
public class GeodeskConfigurationServiceImpl implements GeodeskConfigurationService, Rewirable {

	private final Logger log = LoggerFactory.getLogger(GeodeskConfigurationServiceImpl.class);

	@Autowired
	private BlueprintService blueprintService;

	@Autowired
	private Map<String, UserApplicationInfo> userApplications;

	@Autowired(required = false)
	private Map<String, ClientLayerInfo> layerMap = new LinkedHashMap<String, ClientLayerInfo>();

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private DtoConverterService convertorService;

	@Autowired
	private SecurityContext securityContext;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private GetMapConfigurationCommand mapConfigurationCommand;

	@Autowired
	private LayerModelService layerModelService;

	@Autowired
	private SessionFactory session;

	@Autowired
	private GeoService geoService;

	@Autowired
	private VectorLayerService layerService;

	// -------------------------------------------------

	private UserApplicationInfo getUserApplicationInfo(String key) {
		for (UserApplicationInfo uai : userApplications.values()) {
			if (uai.getKey().equals(key)) {
				return uai;
			}
		}
		return null;
	}

	public ClientApplicationInfo createGeodeskConfiguration(Geodesk geodesk, boolean includeMaps) {
		try {
			geodesk = (Geodesk) session.getCurrentSession().merge(geodesk);
			UserApplicationInfo userApplicationInfo = getUserApplicationInfo(geodesk.getBlueprint()
					.getUserApplicationKey());

			ClientApplicationInfo appInfo = clone(userApplicationInfo.getApplicationInfo());
			Blueprint blueprint = blueprintService.getBlueprintByIdInternal(geodesk.getBlueprint().getId());
			if (includeMaps) {
				ClientMapInfo mainMap = null;
				ClientMapInfo overviewMap = null;
				for (ClientMapInfo map : appInfo.getMaps()) {
					if (GeodeskLayout.MAPMAIN_ID.equals(map.getId())) {
						mainMap = map;
					}
					if (GeodeskLayout.MAPOVERVIEW_ID.equals(map.getId())) {
						overviewMap = map;
					}
				}

				if (!geodesk.getMainMapLayers().isEmpty()) {
					mainMap.getLayers().clear();
					mainMap.getLayers().addAll(addLayers(geodesk.getMainMapLayers()));
				} else if (!blueprint.getMainMapLayers().isEmpty()) {
					mainMap.getLayers().clear();
					mainMap.getLayers().addAll(addLayers(blueprint.getMainMapLayers()));
				}

				if (!geodesk.getOverviewMapLayers().isEmpty()) {
					overviewMap.getLayers().clear();
					overviewMap.getLayers().addAll(addLayers(geodesk.getOverviewMapLayers()));
				} else if (!blueprint.getOverviewMapLayers().isEmpty()) {
					overviewMap.getLayers().clear();
					overviewMap.getLayers().addAll(addLayers(blueprint.getOverviewMapLayers()));
				}

				// Set bounds
				if (geodesk.mustFilterByCreatorTerritory()) {
					try {
						Bbox bounds = GeometryService.getBounds(convertorService
								.toDto(geodesk.getOwner().getGeometry()));
						mainMap.setMaxBounds(bounds);
						mainMap.setInitialBounds(bounds);
						overviewMap.setMaxBounds(bounds);
						overviewMap.setInitialBounds(bounds);
					} catch (GeomajasException e) {
						log.warn("Unable to apply loket territory bounds, falling back to default. {}",
								e.getLocalizedMessage());
					}
				} else if (geodesk.mustFilterByUserTerritory()) {
					try {
						Bbox bounds = GeometryService.getBounds(convertorService
								.toDto(((DeskmanagerSecurityContext) securityContext).getTerritory().getGeometry()));
						mainMap.setMaxBounds(bounds);
						mainMap.setInitialBounds(bounds);
						overviewMap.setMaxBounds(bounds);
						overviewMap.setInitialBounds(bounds);
					} catch (GeomajasException e) {
						log.warn("Unable to apply loket territory bounds, falling back to default. {}",
								e.getLocalizedMessage());
					}
				}

				// Custom configurations for main map (order matters, overrides)
				mainMap.getWidgetInfo().putAll(userApplicationInfo.getMainMapWidgetInfos());
				mainMap.getWidgetInfo().putAll(blueprint.getMainMapClientWidgetInfos());
				mainMap.getWidgetInfo().putAll(geodesk.getMainMapClientWidgetInfos());

				overviewMap.getWidgetInfo().putAll(userApplicationInfo.getOverviewMapWidgetInfos());
				overviewMap.getWidgetInfo().putAll(blueprint.getOverviewMapClientWidgetInfos());
				overviewMap.getWidgetInfo().putAll(geodesk.getOverviewMapClientWidgetInfos());

			} else {
				appInfo.getMaps().clear(); // no maps here
			}

			// -- custom widget configurations
			appInfo.getWidgetInfo().putAll(userApplicationInfo.getApplicationWidgetInfos());
			appInfo.getWidgetInfo().putAll(blueprint.getApplicationClientWidgetInfos());
			appInfo.getWidgetInfo().putAll(geodesk.getApplicationClientWidgetInfos());

			return appInfo;

		} catch (Exception e) {
			log.warn("Fout bij aanmaken configuratie: " + e.getMessage());
			return null;
		}
	}

	private ClientApplicationInfo clone(ClientApplicationInfo cai) {
		return XmlConverterService.toClientApplicationInfo(XmlConverterService.toXml(cai));
	}

	private ClientLayerInfo clone(ClientLayerInfo cli) {
		return XmlConverterService.toClientLayerInfo(XmlConverterService.toXml(cli));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geomajas.plugin.deskmanager.common.server.service.LoketConfigurationService
	 * #createClonedConfiguration(org.geomajas.plugin.deskmanager.common .server.domain.configuration.Loket, boolean)
	 */
	public ClientApplicationInfo createClonedGeodeskConfiguration(Geodesk loket, boolean includeMaps) {
		ClientApplicationInfo loketConfig = createGeodeskConfiguration(loket, true);
		List<ClientMapInfo> cloned = new ArrayList<ClientMapInfo>();
		for (ClientMapInfo map : loketConfig.getMaps()) {
			cloned.add(mapConfigurationCommand.securityClone(map));
		}
		loketConfig.getMaps().clear();
		loketConfig.getMaps().addAll(cloned);

		return loketConfig;
	}

	private Set<ClientLayerInfo> addLayers(Set<org.geomajas.plugin.deskmanager.domain.Layer> layers) {
		Set<ClientLayerInfo> clientLayers = new HashSet<ClientLayerInfo>();
		for (org.geomajas.plugin.deskmanager.domain.Layer layer : layers) {
			if (layer.getClientLayerInfo() != null) {
				clientLayers.add(layer.getClientLayerInfo());
			} else {
				ClientLayerInfo cli = (ClientLayerInfo) applicationContext.getBean(layer.getLayerModel()
						.getClientLayerId());
				if (cli != null) {
					clientLayers.add(cli);
				} else {
					log.error("Unknown client layer info for " + layer.getLayerModel().getClientLayerId());
				}
			}
		}
		return clientLayers;
	}
}

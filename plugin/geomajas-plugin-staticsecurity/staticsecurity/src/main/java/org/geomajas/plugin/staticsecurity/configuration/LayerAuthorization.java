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

package org.geomajas.plugin.staticsecurity.configuration;

import org.geomajas.global.Api;
import org.geomajas.security.BaseAuthorization;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Layer authorization matching LayerAuthorizationInfo.
 *
 * @author Joachim Van der Auwera
 * @since 1.6.0
 */
@Api(allMethods = true)
public class LayerAuthorization implements BaseAuthorization {

	private LayerAuthorizationInfo info;

	LayerAuthorization(LayerAuthorizationInfo info) {
		this.info = info;
	}

	public String getId() {
		return "LayerAuthorizationInfo." + Integer.toString(info.hashCode());
	}

	public boolean isToolAuthorized(String toolId) {
		return check(toolId, info.getToolsInclude(), info.getToolsExclude());
	}

	public boolean isCommandAuthorized(String commandName) {
		return check(commandName, info.getCommandsInclude(), info.getCommandsExclude());
	}

	public boolean isLayerVisible(String layerId) {
		return check(layerId, info.getVisibleLayersInclude(), info.getVisibleLayersExclude());
	}

	public boolean isLayerUpdateAuthorized(String layerId) {
		return check(layerId, info.getUpdateAuthorizedLayersInclude(), info.getUpdateAuthorizedLayersExclude());
	}

	public boolean isLayerCreateAuthorized(String layerId) {
		return check(layerId, info.getCreateAuthorizedLayersInclude(), info.getCreateAuthorizedLayersExclude());
	}

	public boolean isLayerDeleteAuthorized(String layerId) {
		return check(layerId, info.getDeleteAuthorizedLayersInclude(), info.getDeleteAuthorizedLayersExclude());
	}

	protected boolean check(String id, List<String> includes, List<String> excludes) {
		return check(id, includes) && !check(id, excludes);
	}

	protected boolean check(String id, List<String> includes) {
		if (null != includes) {
			for (String check : includes) {
				if (check(id, check)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean check(String value, String regex) {
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(value).matches();
	}
}

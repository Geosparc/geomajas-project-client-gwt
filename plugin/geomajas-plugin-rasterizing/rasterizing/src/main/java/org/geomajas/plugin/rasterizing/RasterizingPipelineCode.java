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

package org.geomajas.plugin.rasterizing;

import org.geomajas.global.Api;

/**
 * Codes for the rasterizing pipeline.
 *
 * @author Joachim Van der Auwera
 * @since 1.0.0
 */
@Api(allMethods = true)
public interface RasterizingPipelineCode {
	String PIPELINE_RASTERIZING = "rasterizing.rasterize";

	String IMAGE_ID_KEY = "rasterizing.imageId"; // String
	String CONTAINER_KEY = "rasterizing.container"; // rasterizingContainer
}

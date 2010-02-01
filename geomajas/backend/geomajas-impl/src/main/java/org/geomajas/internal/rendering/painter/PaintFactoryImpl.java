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

package org.geomajas.internal.rendering.painter;

import org.geomajas.internal.application.tile.RasterTileJG;
import org.geomajas.internal.rendering.DefaultLayerPaintContext;
import org.geomajas.internal.rendering.image.TileImageCreatorImpl;
import org.geomajas.internal.rendering.painter.tile.RasterTilePainter;
import org.geomajas.layer.VectorLayer;
import org.geomajas.rendering.image.TileImageCreator;
import org.geomajas.rendering.painter.LayerPaintContext;
import org.geomajas.rendering.painter.PaintFactory;
import org.geomajas.rendering.painter.tile.TilePainter;
import org.geomajas.rendering.tile.InternalTile;
import org.geomajas.rendering.tile.TileCode;
import org.geomajas.rendering.tile.InternalUrlTile;
import org.geomajas.service.FilterService;
import org.geomajas.service.VectorLayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Allows creation of painter related objects.
 *
 * @author Joachim Van der Auwera
 */
@Component
public class PaintFactoryImpl implements PaintFactory {

	@Autowired
	private FilterService filterCreator;

	@Autowired
	private VectorLayerService layerService;

	public LayerPaintContext createLayerPaintContext(VectorLayer layer) {
		return new DefaultLayerPaintContext(layer);
	}

	public TileImageCreator createTileImageCreator(InternalTile tile, boolean transparent) {
		return new TileImageCreatorImpl(tile, transparent, filterCreator, layerService);
	}

	public TilePainter createRasterTilePainter(String layerId) {
		return new RasterTilePainter(layerId);
	}

	public InternalUrlTile createRasterTile(TileCode code, VectorLayer layer, double scale) {
		return new RasterTileJG(code, layer, scale);
	}
}

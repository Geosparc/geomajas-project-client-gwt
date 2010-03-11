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

package org.geomajas.gwt.client.controller.editing;

import org.geomajas.geometry.Coordinate;
import org.geomajas.gwt.client.action.menu.CancelEditingAction;
import org.geomajas.gwt.client.action.menu.InsertPointAction;
import org.geomajas.gwt.client.action.menu.RemovePointAction;
import org.geomajas.gwt.client.action.menu.SaveEditingAction;
import org.geomajas.gwt.client.action.menu.ToggleEditModeAction;
import org.geomajas.gwt.client.action.menu.ToggleGeometricInfoAction;
import org.geomajas.gwt.client.action.menu.ToggleSnappingAction;
import org.geomajas.gwt.client.action.menu.UndoOperationAction;
import org.geomajas.gwt.client.gfx.paintable.GfxGeometry;
import org.geomajas.gwt.client.gfx.style.ShapeStyle;
import org.geomajas.gwt.client.map.feature.Feature;
import org.geomajas.gwt.client.map.feature.FeatureTransaction;
import org.geomajas.gwt.client.map.feature.TransactionGeomIndex;
import org.geomajas.gwt.client.map.feature.TransactionGeomIndexUtil;
import org.geomajas.gwt.client.map.feature.operation.AddCoordinateOp;
import org.geomajas.gwt.client.map.feature.operation.FeatureOperation;
import org.geomajas.gwt.client.map.feature.operation.SetCoordinateOp;
import org.geomajas.gwt.client.spatial.geometry.LineString;
import org.geomajas.gwt.client.widget.MapWidget;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Event;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;

/**
 * Editing controller for LineString geometries.
 * 
 * @author Pieter De Graef
 */
public class LineStringEditController extends EditController {

	protected TransactionGeomIndex index;

	private String dragTargetId;

	private FeatureTransaction dragTransaction;

	private GfxGeometry tempLine;

	// -------------------------------------------------------------------------
	// Constructor:
	// -------------------------------------------------------------------------

	public LineStringEditController(MapWidget mapWidget, EditController parent) {
		super(mapWidget, parent);
	}

	// -------------------------------------------------------------------------
	// EditController implementation:
	// -------------------------------------------------------------------------

	public Menu getContextMenu() {
		if (menu == null) {
			menu = new Menu();
			menu.addItem(new UndoOperationAction(mapWidget, this));
			menu.addItem(new CancelEditingAction(mapWidget, (ParentEditController) parent));
			menu.addItem(new SaveEditingAction(mapWidget, (ParentEditController) parent));
			menu.addItem(new ToggleEditModeAction((ParentEditController) parent));
			menu.addItem(new MenuItemSeparator());
			menu.addItem(new InsertPointAction(mapWidget));
			menu.addItem(new RemovePointAction(mapWidget));
			menu.addItem(new MenuItemSeparator());
			menu.addItem(new ToggleGeometricInfoAction(this));
			menu.addItem(new ToggleSnappingAction(mapWidget.getMapModel(), this));
		}
		return menu;
	}

	public TransactionGeomIndex getGeometryIndex() {
		if (index == null) {
			index = new TransactionGeomIndex();
			index.setFeatureIndex(0);
			index.setCoordinateIndex(0);
		}
		return index;
	}

	public void setGeometryIndex(TransactionGeomIndex geometryIndex) {
	}

	public void cleanup() {
		removeTempLine();
	}

	// -------------------------------------------------------------------------
	// MapController implementation:
	// -------------------------------------------------------------------------

	public void onMouseDown(MouseDownEvent event) {
		FeatureTransaction featureTransaction = getFeatureTransaction();
		if (featureTransaction != null && parent.getEditMode() == EditMode.DRAG_MODE
				&& event.getNativeButton() != Event.BUTTON_RIGHT) {
			String targetId = getTargetId(event);
			if (TransactionGeomIndexUtil.isVertex(targetId)) {
				dragTargetId = targetId;
				dragTransaction = (FeatureTransaction) featureTransaction.clone();
				mapWidget.render(featureTransaction, "delete");
				mapWidget.render(dragTransaction, "all");
				createTempLine(featureTransaction, event);
			}
		}
	}

	public void onMouseMove(MouseMoveEvent event) {
		FeatureTransaction featureTransaction = getFeatureTransaction();
		if (featureTransaction != null && parent.getEditMode() == EditMode.DRAG_MODE && dragTargetId != null) {
			TransactionGeomIndex index = TransactionGeomIndexUtil.getIndex(dragTargetId);

			Feature feature = dragTransaction.getNewFeatures()[index.getFeatureIndex()];
			FeatureOperation op = new SetCoordinateOp(index, getWorldPosition(event));
			op.execute(feature);

			mapWidget.render(dragTransaction, "delete");
			mapWidget.render(dragTransaction, "all");
		} else if (featureTransaction != null && parent.getEditMode() == EditMode.INSERT_MODE) {
			updateTempLine(featureTransaction, event);
		}
	}

	public void onMouseUp(MouseUpEvent event) {
		if (event.getNativeButton() != Event.BUTTON_RIGHT) {
			FeatureTransaction featureTransaction = getFeatureTransaction();
			if (featureTransaction != null && parent.getEditMode() == EditMode.INSERT_MODE) {
				// The creation of a new point:
				FeatureOperation op = new AddCoordinateOp(getGeometryIndex(), getWorldPosition(event));
				featureTransaction.execute(op);
				mapWidget.render(featureTransaction, "delete");
				mapWidget.render(featureTransaction, "all");
				updateGeometricInfo();
			} else if (featureTransaction != null && parent.getEditMode() == EditMode.DRAG_MODE &&
					dragTargetId != null) {
				// Done dragging a point:
				TransactionGeomIndex index = TransactionGeomIndexUtil.getIndex(dragTargetId);
				// TODO: check validity
				FeatureOperation op = new SetCoordinateOp(index, getWorldPosition(event));
				featureTransaction.execute(op);
				if (dragTransaction != null) {
					mapWidget.render(dragTransaction, "delete");
					dragTransaction = null;
				}
				mapWidget.render(featureTransaction, "all");
				dragTargetId = null;
				removeTempLine();
				updateGeometricInfo();
			}
		}
	}

	// Getters and setters:

	public void setEditMode(EditMode editMode) {
		super.setEditMode(editMode);
		if (editMode == EditMode.DRAG_MODE) {
			removeTempLine();
		}
	}

	// Private methods:

	private void createTempLine(FeatureTransaction featureTransaction, MouseEvent<?> event) {
		if (featureTransaction.getNewFeatures() != null && featureTransaction.getNewFeatures().length > 0
				&& tempLine == null) {
			Coordinate position = getScreenPosition(event);
			LineString lineString = getGeometryIndex().getGeometry(featureTransaction).getGeometryFactory()
					.createLineString(new Coordinate[] { position, position });
			tempLine = new GfxGeometry("LineStringEditController.updateLine");
			tempLine.setGeometry(lineString);
			tempLine.setStyle(new ShapeStyle("#FFFFFF", 0, "#FF3322", 1, 1));
			mapWidget.render(tempLine, "all");
		}
	}

	private void updateTempLine(FeatureTransaction featureTransaction, MouseEvent<?> event) {
		if (featureTransaction.getNewFeatures() != null && featureTransaction.getNewFeatures().length > 0) {
			if (tempLine == null) {
				createTempLine(featureTransaction, event);
			}
			Coordinate[] coordinates = getGeometryIndex().getGeometry(featureTransaction).getCoordinates();
			if (coordinates != null && coordinates.length > 0) {
				Coordinate lastCoordinate = getTransformer().worldToView(coordinates[coordinates.length - 1]);
				LineString lineString = featureTransaction.getNewFeatures()[0].getGeometry().getGeometryFactory()
						.createLineString(new Coordinate[] { lastCoordinate, getScreenPosition(event) });
				tempLine.setGeometry(lineString);
				mapWidget.render(tempLine, "all");
			}
		}
	}

	private void removeTempLine() {
		if (tempLine != null) {
			mapWidget.render(tempLine, "delete");
			tempLine = null;
		}
	}
}

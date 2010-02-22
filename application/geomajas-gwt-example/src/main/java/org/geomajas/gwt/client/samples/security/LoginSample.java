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

package org.geomajas.gwt.client.samples.security;

import org.geomajas.gwt.client.samples.base.SamplePanel;
import org.geomajas.gwt.client.samples.base.SamplePanelFactory;
import org.geomajas.gwt.client.samples.i18n.I18nProvider;
import org.geomajas.plugin.springsecurity.client.Authentication;
import org.geomajas.plugin.springsecurity.client.LoginWindow;
import org.geomajas.plugin.springsecurity.client.LogoutButton;
import org.geomajas.plugin.springsecurity.client.event.LoginFailureEvent;
import org.geomajas.plugin.springsecurity.client.event.LoginHandler;
import org.geomajas.plugin.springsecurity.client.event.LoginSuccessEvent;
import org.geomajas.plugin.springsecurity.client.event.LogoutFailureEvent;
import org.geomajas.plugin.springsecurity.client.event.LogoutHandler;
import org.geomajas.plugin.springsecurity.client.event.LogoutSuccessEvent;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * <p>
 * Sample that shows a map with an OpenStreetMap layer.
 * </p>
 * 
 * @author Pieter De Graef
 */
public class LoginSample extends SamplePanel {

	public static final String LOGIN_TITLE = "Login";

	public static final SamplePanelFactory FACTORY = new SamplePanelFactory() {

		public SamplePanel createPanel() {
			return new LoginSample();
		}
	};

	public Canvas getViewPanel() {
		final VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();
		layout.setMembersMargin(20);

		// Create horizontal layout for the buttons:
		HLayout buttonLayout = new HLayout();
		buttonLayout.setMembersMargin(10);
		buttonLayout.setHeight(25);

		// Create a button that displays the login window on click:
		final IButton loginButton = new IButton("Log in");
		loginButton.setIcon("[ISOMORPHIC]/geomajas/silk/key.png");
		loginButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				LoginWindow loginWindow = new LoginWindow();
				loginWindow.setKeepInParentRect(true);
				layout.addMember(loginWindow);
			}
		});

		// Add a login handler that disables the login button on successful login:
		Authentication.getInstance().addLoginHandler(new LoginHandler() {

			public void onLoginFailure(LoginFailureEvent event) {
			}

			public void onLoginSuccess(LoginSuccessEvent event) {
				loginButton.setDisabled(true);
			}
		});

		// Create a log out button, and attach an event handler:
		LogoutButton logoutButton = new LogoutButton(new LogoutHandler() {

			// Show localized messages:
			public void onLogoutFailure(LogoutFailureEvent event) {
				SC.warn(I18nProvider.getSampleMessages().logoutFailure());
			}

			// Logout success - enable the login button again:
			public void onLogoutSuccess(LogoutSuccessEvent event) {
				SC.say(I18nProvider.getSampleMessages().logoutSuccess());
				loginButton.setDisabled(false);
			}
		});

		buttonLayout.addMember(loginButton);
		buttonLayout.addMember(logoutButton);
		layout.addMember(buttonLayout);
		return layout;
	}

	public String getDescription() {
		return I18nProvider.getSampleMessages().loginDescription();
	}

	public String getSourceFileName() {
		return "classpath:org/geomajas/gwt/client/samples/security/LoginSample.txt";
	}

	public String[] getConfigurationFiles() {
		return new String[] { "classpath:org/geomajas/gwt/samples/security/security.xml" };
	}

	public String ensureUserLoggedIn() {
		return null;
	}
}

/*******************************************************************************
 * Copyright (c) 2015 Pivotal Software, Inc. and others.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution 
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html). 
 *
 * Contributors:
 *     Pivotal Software, Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.flux.sample.service;

import org.eclipse.flux.client.FluxClient;
import org.eclipse.flux.client.MessageConnector;
import org.eclipse.flux.client.MessageConstants;
import org.eclipse.flux.client.config.SocketIOFluxConfig;

public class ServiceStartup {

	public static void main(String[] args) {
		// create the flux client
		FluxClient fluxClient = FluxClient.DEFAULT_INSTANCE;
		
		// the message connector is the main connection to flux messages
		// and can be implemented in various ways, here we use the SocketIO-based
		// implementation
		MessageConnector connector = new SocketIOFluxConfig("http://localhost:3000/",
				MessageConstants.SUPER_USER, null).connect(fluxClient);
		
		// then we connect to the channel of the specific user that this service
		// belongs to. In this case we connect to the channel of the super user
		// which gets everything (from all users)
		try {
			connector.connectToChannelSync(MessageConstants.SUPER_USER);
			new FilenameService().start(connector);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

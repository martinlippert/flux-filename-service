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

import org.eclipse.flux.client.MessageConnector;
import org.eclipse.flux.client.MessageHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FilenameService {

	private MessageConnector connector;

	public void start(MessageConnector connector) {
		this.connector = connector;

		// add a handler for resourceChanged messages
		// this is being executed each time a resourceChanged message arrives
		// on the channel this connector has joined
		connector.addMessageHandler(new MessageHandler("resourceChanged") {
			@Override
			public void handle(String type, JSONObject message) {
				try {
					resourceChanged(message);
				} catch (Exception e) {
				}
			}
		});
	}

	private void resourceChanged(JSONObject message) throws JSONException, Exception {
		System.out.println("RESOURCE CHANGED: " + message.toString());

		String resourceName = message.optString("resource");
		
		// check whether the name of the file ends with "Service.java"
		if (resourceName != null && resourceName.endsWith("Service.java")) {
			sendWarning(message, resourceName);
		}
	}

	private void sendWarning(JSONObject message, String resourceName) throws Exception {
		JSONObject warning = new JSONObject();
		
		// general information about the affected resource
		warning.put("username", message.getString("username"));
		warning.put("project", message.getString("project"));
		warning.put("resource", resourceName);

		// the problems that should appear in the editor
		JSONArray problems = new JSONArray();
		JSONObject marker = new JSONObject();
		marker.put("id", "markerID1");
		marker.put("description", "This class is a Service class written in Java - take care");
		marker.put("line", "1");
		marker.put("severity", "warning");
		marker.put("start", 0);
		marker.put("end", 0);
		problems.put(marker);

		warning.put("problems", problems);

		// send the warning message
		connector.send("liveMetadataChanged", warning);
	}
}

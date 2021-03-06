/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.roverrobot;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * Common constants which are used across this binding.
 *
 * @author Michael Vorburger.ch - Initial contribution
 */
public class RoverRobotBindingConstants {

    public static final String BINDING_ID = "roverrobot";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "rover");

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";

}

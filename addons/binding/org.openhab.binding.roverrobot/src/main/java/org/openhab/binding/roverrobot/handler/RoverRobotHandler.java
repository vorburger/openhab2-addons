/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.roverrobot.handler;

import static org.openhab.binding.roverrobot.RoverRobotBindingConstants.CHANNEL_1;

import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.vorburger.raspberry.mc33926.TwoMotorsProvider;
import ch.vorburger.raspberry.motors.TwoMotors;
import ch.vorburger.raspberry.turtle.AsyncTurtle;

/**
 * Handling Rover Robot commands.
 *
 * @author Michael Vorburger.ch - Initial contribution
 */
public class RoverRobotHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(RoverRobotHandler.class);

    private AsyncTurtle turtle;

    public RoverRobotHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_1)) {
            String commandString = command.toString(); // (StringType)
            switch (commandString) {
                case "FORWARD2":
                    turtle.forward(0.2);
                    break;
                case "BACK2":
                    turtle.backward(0.2);
                    break;
                case "LEFT45":
                    turtle.turnLeft(45);
                    break;
                case "RIGHT45":
                    turtle.turnRight(45);
                    break;
                default:
                    logger.warn("handleCommand() unknown command: " + commandString);
                    break;
            }
        }
    }

    @Override
    public void initialize() {
        logger.info("initialize() is about to set-up Raspberry Pi Rover GPIO....");
        updateStatus(ThingStatus.INITIALIZING);

        try {
            TwoMotors twoMotors = new TwoMotorsProvider().get();
            turtle = new AsyncTurtle(twoMotors);

            // Just a hard-coded quick first init test, to signal to the user we're up and ready
            turtle.turnLeft(10);
            turtle.turnRight(10);

            logger.info("initialize() successfully set-up robot turtle control! Ready now.");
            updateStatus(ThingStatus.ONLINE);

        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR, e.getMessage());
            logger.error("initialize() failed to set-up robot turtle control :(", e);
        }
    }

    @Override
    public void dispose() {
        // TODO clean release of GPIO .. (This needs a bit of work in ch.vorburger.raspberry.mc33926)
        turtle = null;
    }
}

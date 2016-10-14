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
import ch.vorburger.raspberry.turtle.QueueingTurtle;
import ch.vorburger.raspberry.turtle.Turtle;

/**
 * Handling Rover Robot commands.
 *
 * @author Michael Vorburger.ch - Initial contribution
 */
public class RoverRobotHandler extends BaseThingHandler {

    private Logger log = LoggerFactory.getLogger(RoverRobotHandler.class);

    private TwoMotors twoMotors;
    private Turtle qTurtle; // QueueingTurtle, best for rulez scripts
    private Turtle aTurtle; // AsyncTurtle, best for remote control UI

    public RoverRobotHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (!channelUID.getId().equals(CHANNEL_1)) {
            log.warn("handleCommand() on channelUID id {} instead {} (handling anyway)", channelUID.getId(), CHANNEL_1);
        }
        String commandString = command.toString(); // (StringType)
        switch (commandString) {
            // Commands for the QueueingTurtle, best for rulez scripts
            case "FORWARD2":
                qTurtle.forward(0.2);
                break;
            case "BACK2":
                qTurtle.backward(0.2);
                break;
            case "LEFT11":
                qTurtle.turnLeft(11.5);
                break;
            case "RIGHT11":
                qTurtle.turnRight(11.5);
                break;
            case "LEFT22":
                qTurtle.turnLeft(22.5);
                break;
            case "RIGHT22":
                qTurtle.turnRight(22.5);
                break;
            case "LEFT45":
                qTurtle.turnLeft(45);
                break;
            case "RIGHT45":
                qTurtle.turnRight(45);
                break;
            case "LEFT90":
                qTurtle.turnLeft(90);
                break;
            case "RIGHT90":
                qTurtle.turnRight(90);
                break;

            // Commands for the AsyncTurtle API, best for remote control UI
            case "AFORWARD4":
                aTurtle.forward(0.4);
                break;
            case "ABACK4":
                aTurtle.backward(0.4);
                break;
            case "ALEFT90":
                aTurtle.turnLeft(90);
                break;
            case "ARIGHT90":
                aTurtle.turnRight(90);
                break;
            case "AHALT":
                aTurtle.halt();
                break;

            case "REFRESH":
                // Ignore what appears to be some sort of well known (but, by us, un-used)
                // system command, which is frequently sent to us by openHAB core?
                break;
            default:
                log.warn("handleCommand() unknown command: " + commandString);
                break;
        }
    }

    @Override
    public void initialize() {
        log.info("initialize() is about to set-up Raspberry Pi Rover GPIO....");
        updateStatus(ThingStatus.INITIALIZING);

        try {
            twoMotors = new TwoMotorsProvider().get();
            qTurtle = new QueueingTurtle(twoMotors);
            aTurtle = new AsyncTurtle(twoMotors);

            // Just a hard-coded quick first init test, to signal to the user we're up and ready
            qTurtle.turnLeft(10);
            qTurtle.turnRight(10);

            log.info("initialize() successfully set-up robot turtle control! Ready now.");
            updateStatus(ThingStatus.ONLINE);

        } catch (Exception e) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR, e.getMessage());
            log.error("initialize() failed to set-up robot turtle control :(", e);
        }
    }

    @Override
    public void dispose() {
        log.info("dispose() halting Rover now....");

        qTurtle.halt();
        qTurtle = null;

        aTurtle.halt();
        aTurtle = null;

        twoMotors.disable();
        twoMotors = null;

        // TODO clean release of GPIO .. (This needs a bit of work in ch.vorburger.raspberry.mc33926)
    }
}

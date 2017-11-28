/*
 * Copyright (C) 2017 Axel Müller <axel.mueller@avanux.de>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package de.avanux.smartapplianceenabler.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.pi4j.io.gpio.*;
import de.avanux.smartapplianceenabler.appliance.ApplianceIdConsumer;
import de.avanux.smartapplianceenabler.log.ApplianceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
abstract public class GpioControllable implements ApplianceIdConsumer {
    private transient ApplianceLogger logger = new ApplianceLogger(LoggerFactory.getLogger(GpioControllable.class));
    @XmlAttribute
    private Integer gpio;
    @XmlAttribute
    private String pinPullResistance;
    private transient GpioController gpioController;
    private transient String applianceId;


    protected GpioController getGpioController() {
        return gpioController;
    }

    public void setGpioController(GpioController gpioController) {
        this.gpioController = gpioController;
    }

    protected Pin getGpio() {
       return RaspiPin.getPinByName("GPIO " + gpio);
    }
    
    protected PinPullResistance getPinPullResistance() {
        if(PinPullResistance.OFF.getName().equals(pinPullResistance)) {
            return PinPullResistance.OFF;
        }
        else if(PinPullResistance.PULL_DOWN.getName().equals(pinPullResistance)) {
            return PinPullResistance.PULL_DOWN;
        }
        else if(PinPullResistance.PULL_UP.getName().equals(pinPullResistance)) {
            return PinPullResistance.PULL_UP;
        }
        return null;
    }

    abstract public void start();

    public void stop() {
        GpioController gpioController = getGpioController();
        if(gpioController != null) {
            logger.info("Shutting down " + getGpio());
            gpioController.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
            gpioController.shutdown();
        }
        else {
            logGpioAccessDisabled(logger);
        }
    }

    protected void logGpioAccessDisabled(Logger logger) {
        logger.warn("Configured for " + getGpio()+ ", but GPIO access disabled.");
    }

    @Override
    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
        this.logger.setApplianceId(applianceId);
    }

    protected String getApplianceId() {
        return applianceId;
    }
}
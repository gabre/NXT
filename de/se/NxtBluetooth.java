/*
 * NXCBluetoothLogger
 * Copyright (C) 2012 Max Leuthaeuser
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.se;

import javax.bluetooth.*;
import java.util.LinkedList;
import java.util.List;

public class NxtBluetooth implements DiscoveryListener {
    private List<RemoteDevice> devices;
    private RemoteDevice brick;
    private String url;
    private Logger logger;

    public NxtBluetooth(Logger aLogger) {
        devices = new LinkedList<RemoteDevice>();
        logger = aLogger;
    }

    public void deviceDiscovered(RemoteDevice rd, DeviceClass dc) {
        devices.add(rd);
        logger.line(rd.getBluetoothAddress());
    }

    public void inquiryCompleted(int status) {
        logger.line("Inquiry completed");

        synchronized (this) {
            try {
                this.notifyAll();
            } catch (Exception ignored) {
            }
        }
    }

    public void servicesDiscovered(int transId, ServiceRecord[] rec) {
        // TODO what happens if there are more than one SPP service on the brick?
        url = rec[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
        // TODO printing all services found is only for debugging
        for (ServiceRecord s : rec) {
            String url = s.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

            if (url == null) continue;

            DataElement serviceName = s.getAttributeValue(0x0100);
            if (serviceName != null)
                logger.line("Service " + serviceName.getValue() + " found " + url);
            else
                logger.line("Service found " + url);
        }
    }

    public void serviceSearchCompleted(int transId, int respCode) {
        logger.line("Service search completed");
        synchronized (this) {
            try {
                this.notifyAll();
            } catch (Exception ignored) {
            }
        }
    }

    public String getURL() {
        return url;
    }

    public void find(String nxtName) throws Exception {
        try {
            DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
            LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);
            logger.line("Searching devices...");
            agent.startInquiry(DiscoveryAgent.GIAC, this);
            synchronized (this) {
                try {
                    this.wait();
                } catch (Exception ignored) {
                }
            }

            logger.line("Names...");
            for (RemoteDevice device : devices) {
                String name = "?";
                try {
                    name = device.getFriendlyName(false);
                    logger.line(name);
                } catch (java.io.IOException e) {
                    logger.line("No name found. ( " + e.getMessage() + ")");
                }
                if (name.equals(nxtName))
                    brick = device;
            }

            UUID serviceUUID = new UUID(0x1101);    // We will find SPP services only
            UUID[] uuids = new UUID[]{serviceUUID};
            int attrs[] = new int[]{0x0100};

            url = null;
            if (brick != null) {
                logger.line("Searching services...");
                agent.searchServices(attrs, uuids, brick, this);
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (Exception ignored) { }
                }
                if (url == null)
                    throw new Exception("NXT SPP service not found");
            } else
                throw new Exception("NXT not found");
        } catch (BluetoothStateException e) {
            logger.line(e.toString());
            throw new Exception("Bluetooth error");
        }
    }
}
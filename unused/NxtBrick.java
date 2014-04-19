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

package unused;

import javax.microedition.io.Connector;

import java.util.Observable;

import javax.microedition.io.StreamConnection;

import de.se.Logger;
import de.se.NxtBluetooth;
import de.se.NxtMessage;
import de.se.NxtBrickBluetoothThread;
import de.se.NxtMessage.Type;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class NxtBrick extends Observable {
    private NxtBrickBluetoothThread reader;
    private StreamConnection connection;
    private OutputStream outputstream;
    private Logger logger;

    public NxtBrick(final Logger aLogger) {
        logger = aLogger;
    }

    public void connect(final String nxtName) throws Exception {
        NxtBluetooth nxtBluetooth = new NxtBluetooth(logger);

        String url;
        try {
            nxtBluetooth.find(nxtName);
            url = nxtBluetooth.getURL();
            logger.line(url);
        } catch (java.io.IOException e) {
            logger.line(e.getMessage());
            throw new Exception("No NXT found");
        }

        try {
            connection = (StreamConnection) Connector.open(url);

            outputstream = connection.openOutputStream();
            InputStream inputstream = connection.openInputStream();

            reader = null; //new NxtReaderThread(logger, inputstream, outputstream);
            reader.run();
        } catch (java.io.IOException e) {
            logger.line(e.getMessage());
            throw new Exception("Connection failed to " + url);
        }
    }

    public void disconnect() {
        reader.terminate();
        reader = null;
        connection = null;
    }


    public void send(int mailbox, String s) {
        NxtMessage msg = new NxtMessage(NxtMessage.Type.INCOMING, mailbox, s, false);
        try {
            outputstream.write(msg.pack());
            outputstream.flush();
        } catch (java.io.IOException e) {
            logger.line(e.getMessage());
        }
    }
}
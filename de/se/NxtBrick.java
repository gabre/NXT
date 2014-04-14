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

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class NxtBrick {
    private NxtReaderThread reader;
    private StreamConnection con;
    private OutputStream os;
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
            con = (StreamConnection) Connector.open(url);

            os = con.openOutputStream();
            InputStream is = con.openInputStream();

            reader = new NxtReaderThread(logger, is, os);
            reader.start();
        } catch (java.io.IOException e) {
            logger.line(e.getMessage());
            throw new Exception("Connection failed to " + url);
        }
    }

    public void disconnect() {
        reader.terminate();
        reader = null;
        con = null;
    }

    public int avail() {
        return reader.getInAvail();
    }

    public List<NxtMessage> messages() {
        return reader.getMessages();
    }
    
    public NxtMessage getOldestMessage() {
        return reader.getFirstMessage();
    }    

    public void send(int mailbox, String s) {
        NxtMessage msg = new NxtMessage(NxtMessage.Type.INCOMING, mailbox, s, false);
        try {
            os.write(msg.pack());
            os.flush();
        } catch (java.io.IOException e) {
            logger.line(e.getMessage());
        }
    }
}
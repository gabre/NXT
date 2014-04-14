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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class NxtReaderThread extends Thread {
    private InputStream is;
    private OutputStream os;
    private List<NxtMessage> queue;
    private boolean terminated;
    private Logger logger;

    public NxtReaderThread(Logger aLogger, InputStream inputStream, OutputStream outputStream) {
        is = inputStream;
        os = outputStream;
        logger = aLogger;
    }

    public synchronized List<NxtMessage> getMessages() {
        List<NxtMessage> messages = queue;
        queue.clear();
        return messages;
    }

    public synchronized int getInAvail() {
        return queue.size();
    }

    public void run() {
        queue = new LinkedList<NxtMessage>();

        try {
            int state = 0;
            byte[] prolog = new byte[2];
            int size = 0;
            terminated = false;

            while (!terminated) {
                // looks awkward
                try {
                    sleep(1);
                } catch (java.lang.InterruptedException ignored) { }

                if (state == 0) {
                    if (is.available() < 2) continue;

                    is.read(prolog, 0, 2);
                    size = prolog[1] * 256 + prolog[0];
                    state = 1;
                } else {
                    if (is.available() < size) continue;

                    byte[] cmd = new byte[size];
                    is.read(cmd, 0, size);
                    state = 0;

                    try {
                        NxtMessage message = new NxtMessage(cmd);

                        synchronized (this) {
                            queue.add(message);
                            logger.line(message.toString());
                        }

                        if (message.getResponseNeeded())
                            os.write(NxtMessage.RESPONSE_MSG);

                    } catch (NxtMessage.UnknownMessage ignored) { }
                }
            }
        } catch (IOException ignored) { }
    }

    synchronized public void terminate() {
        terminated = true;
    }
}

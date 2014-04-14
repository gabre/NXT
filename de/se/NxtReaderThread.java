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
    private static final int sizeReadingState = 0;
	private static final int valueReadingState = 1;
	private InputStream inputstream;
    private OutputStream outputstream;
    private List<NxtMessage> queue;
    private boolean terminated;
    private Logger logger;

    public NxtReaderThread(Logger aLogger, InputStream inputStream, OutputStream outputStream) {
        inputstream = inputStream;
        outputstream = outputStream;
        logger = aLogger;
    }

    public synchronized List<NxtMessage> getMessages() {
        List<NxtMessage> messages = queue;
        queue.clear();
        return messages;
    }
    
    public synchronized NxtMessage getFirstMessage() {
        return queue.remove(0);
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
                sleepIfPossible();
                if (tooFewDataAvailable()) continue;

                if (state == sizeReadingState) {
                    inputstream.read(prolog, 0, 2);
                    size = prolog[1] * 256 + prolog[0];
                    state = valueReadingState;
                } else {
                	
                    byte[] incomingMessage = new byte[size];
                    inputstream.read(incomingMessage, 0, size);
                    state = 0;

                    try {
                        NxtMessage message = new NxtMessage(incomingMessage);

                        synchronized (this) {
                            queue.add(message);
                            logger.line(message.toString());
                        }

                        if (message.getResponseNeeded()) {
                            outputstream.write(NxtMessage.RESPONSE_MSG);
                        }

                    } catch (NxtMessage.UnknownMessage ignored) { }
                }
            }
        } catch (IOException ignored) { }
    }

	private boolean tooFewDataAvailable() throws IOException {
		return inputstream.available() < 2;
	}

	private void sleepIfPossible() {
		try {
		    sleep(1);
		} catch (java.lang.InterruptedException ignored) { }
	}

    synchronized public void terminate() {
        terminated = true;
    }
}

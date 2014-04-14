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

import de.se.Logger;
import de.se.NxtBrick;

public class NxtConsole {
    private NxtBrick nxtBrick;
    private Logger logger;

    public NxtConsole(Logger logger) {
        this.logger = logger;
        nxtBrick = new NxtBrick(logger);
        this.logger.line("Started");
    }

    public void start(final String nxtName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    logger.line("Connecting...");
                    nxtBrick.connect(nxtName);
                    logger.line("Connected");
                } catch (Exception e) {
                    logger.line(e.getMessage());
                    logger.line("Try again");
                }
            }
        }.start();
    }
}

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

public class NxtMessage {
    public enum Type {
        INCOMING,
        REQUEST
    }
    final static public byte[] RESPONSE_MSG = new byte[]{0x02, 0x09, 0x00};
    private Type type;
    private int mailbox;
    private String message;
    private boolean responseNeeded;

    public static class UnknownMessage extends Exception {
    }

    boolean getResponseNeeded() {
        return responseNeeded;
    }

    public NxtMessage(final Type type, final int mailbox, final String message, final boolean responseNeeded) {
        this.type = type;
        this.mailbox = mailbox;
        this.message = message;
        this.responseNeeded = responseNeeded;
    }

    public NxtMessage(byte[] cmd) throws UnknownMessage {
        if ((cmd[0] & 0x7F) == 0 && cmd[1] == 0x09) {
            type = Type.INCOMING;
            mailbox = (int) cmd[3];
            message = new String(cmd, 4, (int) cmd[3] - 1);
            responseNeeded = cmd[0] == 0x00;
        } else if ((cmd[0] & 0x7F) == 0x02 && cmd[1] == 0x13) {
            type = Type.REQUEST;
            message = "";
            mailbox = (int) cmd[2];
        } else
            throw new UnknownMessage();
    }

    @Override
    public String toString() {
        return (type == Type.INCOMING ? "INCOMING" : "REQUEST") +
                (responseNeeded ? " (R)" : "") + ": " +
                mailbox + ", " +
                "'" + message + "'" + ", Length: " +
                message.length();
    }

    public byte[] pack() {
        byte[] cmd = new byte[80];
        cmd[2] = (byte) 0x80;
        cmd[3] = (byte) 0x09;
        cmd[4] = (byte) mailbox;
        String msg = message + "\0";
        cmd[5] = (byte) (msg.length() & 0xff);
        for (int i = 0; i < msg.length(); i++) {
            cmd[6 + i] = (byte) msg.charAt(i);
        }
        cmd[0] = (byte) ((cmd.length - 2) & 0xFF);
        cmd[1] = (byte) ((cmd.length - 2) >> 8);

        return cmd;
    }
}

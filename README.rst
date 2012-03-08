################################################################################
                    THE NXCBluetoothLogger REPOSITORY
################################################################################

This document describes the NXCBluetoothLogger, how to build and use it.

Part I. Usage
--------------------------------------------------------------------------------                            

1. Connect the NXT via Bluetooth with your computer.
2. Place the compiled files from ``nxt/`` on your NXT brick. Start them.
3. Start the client on your computer: ``java -jar NXCBluetoothLogger.jar NXTName``.
Replace ``NXTName`` with the name of your brick.

Part II. Building NXCBluetoothLogger with Maven
--------------------------------------------------------------------------------

Maven is used to manage the dependencies and it will build a runnable jar executable with Java.

- install Maven (http://maven.apache.org/)
- run ``mvn package``, the resulting jar is placed in ``builds/``

^^^^^^^^^^^^^^^^^^^^^^^^
REQUIREMENTS FOR NXCBluetoothLogger:
^^^^^^^^^^^^^^^^^^^^^^^^
The following is assumed to be installed on the build machine:

- A Java runtime environment (JRE) or SDK 1.6 or above.
- Maven

The following is assumed to be installed on the machines you want to run NXCBluetoothLogger:

- Client side: A Java runtime environment (JRE)
- NXT Mindstorm: NXC Firmware 1.28 or above (1.31 recommended)
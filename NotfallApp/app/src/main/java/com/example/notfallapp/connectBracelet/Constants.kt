package com.example.notfallapp.connectBracelet

import java.util.*

public class Constants {
    companion object {
       /* // Client Characteristic UUID Values to set for notification.
        var CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        // To enable the notification value
        val ENABLE_NOTIFICATION_VALUE = byteArrayOf(0x01.toByte(), 0x00)

        // To disable the notification value
        val DISABLE_NOTIFICATION_VALUE = byteArrayOf(0x00.toByte(), 0x00)

        // VSN Simple Service to listen the key press,fall detect and acknowledge and cancel the event.
        val SERVICE_VSN_SIMPLE_SERVICE =
            UUID.fromString("fffffff0-02f7-4000-b000-000000000000") // 0xFFF0

        // Characteristic UUID for key press and fall detect event.
        val CHAR_KEY_PRESS =
            UUID.fromString("fffffff4-00f7-4000-b000-000000000000") // 0xFFF4

        // Characteristic UUID for acknowledge the data received and cancel the key press / fall detect event.
        val ACK_DETECT =
            UUID.fromString("fffffff3-00f7-4000-b000-000000000000") // 0xFFF3

        // Value need to write the acknowledge data received.
        val RECEIVED_ACK = byteArrayOf(0x01.toByte())

        // Value need to write to cancel the key press / fall detect.
        val CANCEL_ACK = byteArrayOf(0x00.toByte())

        //Characteristic UUID to secure the puck and restrict to respond to other APP.
        val CHAR_APP_VERIFICATION =
            UUID.fromString("fffffff5-00f7-4000-b000-000000000000") //0xFFF5

        // New Value need to write with in 30 seconds of connection event occurred.
        val NEW_APP_VERIFICATION_VALUE = byteArrayOf(
            0x80.toByte(),
            0xBE.toByte(),
            0xF5.toByte(),
            0xAC.toByte(),
            0xFF.toByte()
        )
        val CHAR_DETECTION_CONFIG =
            UUID.fromString("fffffff2-00f7-4000-b000-000000000000") // 0xFFF2

        val CHAR_DETECTION_NOTIFY =
            UUID.fromString("fffffff4-00f7-4000-b000-000000000000") // 0xFFF4

        val ENABLE_KEY_DETECTION_VALUE = byteArrayOf(0x01.toByte())

        // To read the battery information form the Battery information service.
        val SERVICE_BATTERY_LEVEL =
            UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")

        // Characteristic to read the battery status value.
        val CHAR_BATTERY_LEVEL =
            UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")*/

        // Client Characteristic UUID Values to set for notification.
        var CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        // To enable the notification value
        val ENABLE_NOTIFICATION_VALUE = byteArrayOf(0x01.toByte(), 0x00)

        // To disable the notification value
        val DISABLE_NOTIFICATION_VALUE = byteArrayOf(0x00.toByte(), 0x00)

        // VSN Simple Service to listen the key press,fall detect and acknowledge and cancel the event.
        val SERVICE_VSN_SIMPLE_SERVICE =
            UUID.fromString("fffffff0-00f7-4000-b000-000000000000") // 0xFFF0

        // Characteristic UUID for key press and fall detect event.
        val CHAR_KEY_PRESS =
            UUID.fromString("fffffff4-00f7-4000-b000-000000000000") // 0xFFF4

        // Characteristic UUID for acknowledge the data received and cancel the key press / fall detect event.
        val ACK_DETECT =
            UUID.fromString("fffffff3-00f7-4000-b000-000000000000") // 0xFFF3

        // Value need to write the acknowledge data received.
        val RECEIVED_ACK = byteArrayOf(0x01.toByte())

        // Value need to write to cancel the key press / fall detect.
        val CANCEL_ACK = byteArrayOf(0x00.toByte())

        //Characteristic UUID to secure the puck and restrict to respond to other APP.
        val CHAR_APP_VERIFICATION =
            UUID.fromString("fffffff5-00f7-4000-b000-000000000000") //0xFFF5

        // New Value need to write with in 30 seconds of connection event occurred.
        val NEW_APP_VERIFICATION_VALUE = byteArrayOf(
            0x80.toByte(),
            0xBE.toByte(),
            0xF5.toByte(),
            0xAC.toByte(),
            0xFF.toByte()
        )
        val CHAR_DETECTION_CONFIG =
            UUID.fromString("fffffff2-00f7-4000-b000-000000000000") // 0xFFF2

        val CHAR_DETECTION_NOTIFY =
            UUID.fromString("fffffff4-00f7-4000-b000-000000000000") // 0xFFF4

        val ENABLE_KEY_DETECTION_VALUE = byteArrayOf(0x01.toByte())

        // To read the battery information form the Battery information service.
        val SERVICE_BATTERY_LEVEL =
            UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")

        // Characteristic to read the battery status value.
        val CHAR_BATTERY_LEVEL =
            UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
    }
}
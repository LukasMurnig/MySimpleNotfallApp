package com.example.notfallapp.bll

import java.util.*

/**
 * Class for our Alerts which we get from the Server
 */
class Alert constructor(
    var id: Long,
    /*var date: Date, change to String, because problem by converting*/
    var date: String,
    var type: Byte,
    var state: Byte,
    var clientId: UUID,
    var helperId: UUID?,
    var deviceId: UUID?,
    var triggeringPositionLatitude: Double?,
    var triggeringPositionLongitude: Double?,
    var triggeringPositionTime: String?, /*Date, change to String, because problem by converting*/
    var canBeForwarded: Boolean) {
}
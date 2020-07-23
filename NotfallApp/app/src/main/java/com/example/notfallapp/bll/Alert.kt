package com.example.notfallapp.bll

import java.util.*

class Alert constructor(
    var id: Long,
    var date: Date,
    var type: Byte,
    var state: Byte,
    var clientId: UUID,
    var helperId: UUID?,
    var deviceId: UUID?,
    var triggeringPositionLatitude: Double?,
    var triggeringPositionLongitude: Double?,
    var triggeringPositionTime: Date?,
    var canBeForwarded: Boolean) {

    /*
    HelperId UUID (nullable) Id of the helping user
    CreatedBy UUID (nullable)
    CreationDate DateTime (nullable)
    ModificationDate DateTime (nullable)
    ModifiedBy UUID (nullable)
    DateAcceted DateTime (nullable) date and time when alert was accepted
    DateClosed DateTime (nullable) date and time when alert was closed o
    DeviceId UUID (nullable) Id of the triggering device
    CanBeAccepted boolean (nullable) true, if the alert can be accepted by the user,
    false otherwise
    CanBeClosed boolean (nullable) true, if the alert can be closed by the user,
    false otherwise
    CanBeInvalidated boolean (nullable) true, if the alert can be invalidated by the
    user, false otherwise
    LastBattery int (nullable) device battery charge level in % (0 to 100)
    when the alert was closed
    TriggeringBattery int (nullable) device battery charge level in % (0 to 100)
    when the alert was created
    TriggeringPositionLatitude double (nullable) Latitude (WGS84) of position triggering
    position (position when alert was started)
    TriggeringPositionLongitude double (nullable) Longitude (WGS84) of position triggering
    position
    TriggeringPositionTime DateTime (nullable) Timestamp of the triggering position
    CanBeForwarded Boolean True, if alert can be forwarded*/
}
package com.example.notfallapp.bll

import java.util.*

class AlertLog constructor(
    var id: Long?,
    var alertId: Long?,
    var date: String,
    var logType: Int,
    var userId: UUID?,
    var message: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AlertLog

        if (id != other.id) return false
        if (alertId != other.alertId) return false
        if (date != other.date) return false
        if (logType != other.logType) return false
        if (userId != other.userId) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (alertId?.hashCode() ?: 0)
        result = 31 * result + date.hashCode()
        result = 31 * result + logType
        result = 31 * result + (userId?.hashCode() ?: 0)
        result = 31 * result + (message?.hashCode() ?: 0)
        return result
    }
}
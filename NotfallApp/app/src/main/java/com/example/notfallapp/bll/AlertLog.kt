package com.example.notfallapp.bll

import java.util.*

class AlertLog constructor(
    var id: Long,
    var alertId: Long,
    var date: String,
    var logType: Int,
    var userId: UUID?,
    var message: String?
) {
}
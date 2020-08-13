package com.example.notfallapp.bll

import java.util.*

/**
 * Class for the Members of Alerting Chain from the Server
 */
class AlertingChainMember constructor(
    var alertingChainId: UUID,
    var helperId: UUID,
    var rank: Int,
    var active: Boolean,
    var contact: Boolean,
    var helperForename: String?,
    var helperSurname: String?,
    var phoneNumber: String?,
    var email: String?
) {
}
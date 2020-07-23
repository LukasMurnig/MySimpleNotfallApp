package com.example.notfallapp.bll

import java.util.*

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
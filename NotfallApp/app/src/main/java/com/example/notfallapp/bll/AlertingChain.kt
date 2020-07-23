package com.example.notfallapp.bll

import java.util.*

class AlertingChain constructor(
    var id: UUID,
    var userId: UUID,
    var name: String?,
    var description: String?,
    var helpers: Array<AlertingChainMember>?
) {
}
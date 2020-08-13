package com.example.notfallapp.bll

import java.util.*

/**
 * Class for the Alerting Chain we get from the server.
 */
class AlertingChain constructor(
    var id: UUID,
    var userId: UUID,
    var name: String?,
    var description: String?,
    var helpers: Array<AlertingChainMember>?
) {
}
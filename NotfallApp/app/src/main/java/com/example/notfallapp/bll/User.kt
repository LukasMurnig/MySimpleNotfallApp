package com.example.notfallapp.bll

import java.util.*

class User constructor(
    var id: UUID,
    var foreignId: String?,
    var title: String?,
    var forename: String,
    var surname: String,
    var username: String,
    var active: Boolean,
    var role: String,
    var gender: Int,
    var photoSet: Boolean,
    var birthDay: Date?,
    var emailAddress: String?,
    var phoneFixed: String?,
    var orgUnit: Int,
    var language: String?,
    var timezone: String?
)
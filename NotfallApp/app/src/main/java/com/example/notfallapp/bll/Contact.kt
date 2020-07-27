package com.example.notfallapp.bll

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

/*
Anrede (Herr, Frau) => entspricht Geschlecht/Gender in der API
Nachname
Vorname
TelNr (bestehend aus Ländervorwahl und restlicher Telefon Nummer - List der möglichen Ländervorwahlen vom Server).
Email
Sprache (List der möglichen Werte vom Server)
Zeitzone (List der möglichen Werte vom Server)
Aktiv (boolean, ob aktiv in der Alarmierungskette)
Benachrichtigungen: Auswählbar in welcher Form der Kontakt benachrichtigt werden soll (Per Anruf, SMS oder Email)

Optional: Photo (jpg oder png)
Optional: Adresse: Straße, Hausnummer, Postleitzahl, Ort, Land (List der möglichen Länder vom Server)
*/

@Entity(tableName = "Contact")
class Contact constructor(
    var id: Long?,
    var forename: String,
    var surname: String,
    /*var username: String,*/
    var active: Boolean,
    var role: String,
    var gender: Int,
    var photoSet: Boolean,
    /*var birthDay: java.sql.Date?,*/
    var e_mail: String,
    var phoneFixed: String,
    var language: String?,
    var timezone: String?,
    var messageType: String,
    var priority: Int,
    var pathToImage: String?,
    var street: String?,
    var houseNumber: String?,
    var postAreaCode: Int?,
    var place: String?,
    var country: String?
    ) {

    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String = e_mail
}
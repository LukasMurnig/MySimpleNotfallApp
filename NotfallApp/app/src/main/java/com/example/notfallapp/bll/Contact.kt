package com.example.notfallapp.bll

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
class Contact constructor(var firstname: String, var lastname: String, var e_mail: String, var number: String, var priority: Int, var pathToImage: String, var active: Boolean) {
    @PrimaryKey
    @ColumnInfo(name = "email")
    var email: String = e_mail
}
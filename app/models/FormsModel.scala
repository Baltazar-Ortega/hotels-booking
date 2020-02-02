package models

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._

case class Reservation(name: String,
                        email: String,
                        phone: String,
                        amountAdults: Int,
                        amountOthers: Int,
                        spa: Int,
                        gym: Int,
                        inDate: java.util.Date,
                        outDate: java.util.Date,
                        floor: Int,
                        fcfm: Boolean,
                        total: Double
                        )

object Reservation {
    val form: Form[Reservation] = Form(
        mapping(
            "name"-> nonEmptyText,
            "email"-> email,
            "phone"-> text,
            "amountAdults"-> number,
            "amountOthers"-> number,
            "spa"-> number,
            "gym"-> number,
            "inDate"-> date,
            "outDate"-> date,
            "floor"-> number,
            "fcfm"-> boolean,
            "total" -> of(doubleFormat)
        )(Reservation.apply)(Reservation.unapply)
    )
}
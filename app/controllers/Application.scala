package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import play.api.db._
import scala.collection.mutable.ArrayBuffer

object Application extends Controller {
  def index = Action {
    Ok(views.html.index(null))
  }
  def hotel1 = Action {
    Ok(views.html.hotelForm("Silver Hotel - Vallarta")(1))
  }
  def hotel2 = Action {
    Ok(views.html.hotelForm("Temptation Hotel - Cancun")(2))
  }
  def hotel3 = Action {
    Ok(views.html.hotelForm("Hyatt Ziva - Los Cabos")(3))
  }

  def admin = Action {
    Ok(views.html.admin(null))
  }
  def deleteRes = Action {
    Ok(views.html.deleteRes(null))
  }

  def totalOperation(hotelNumber:Int, nights:Int, amountAdults:Int, amountOthers:Int, floor:Int, spa:Int, gym:Int, fcfm:Int):Int = {
    // println("Inside function totalOperation")
    var total = 0
    if (hotelNumber == 1) {
      if (spa == 1){
        total = total + 50
      }
      if (gym == 1){
        total = total + 50
      }
      if (floor == 1){
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 1200)
        }
      } else if (floor == 2){
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 1700)
        }
      } else {
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 2700)
        }
      }
      if (amountOthers != 0) {
        total = total + (amountOthers * nights * 800)
      }
      if (fcfm == 1) {
        var rest = total.toDouble * .1
        total = total - rest.toInt
      }
    }

    if (hotelNumber == 2) {
      if (spa == 1){
        total = total + 300
      }
      if (gym == 1){
        total = total + 300
      }
      if (floor == 1){
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 4000)
        }
      } else if (floor == 2){
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 4500)
        }
      } else {
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 6000)
        }
      }
      if (amountOthers != 0) {
        total = total + (amountOthers * nights * 3800)
      }
      if (fcfm == 1) {
        var rest = total.toDouble * .5
        total = total - rest.toInt
      }
    }

    if (hotelNumber == 3) {
      if (spa == 1){
        total = total + 700
      }
      if (gym == 1){
        total = total + 700
      }
      if (floor == 1){
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 7000)
        }
      } else if (floor == 2){
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 7500)
        }
      } else {
        if (amountAdults != 0){
          total = total + (amountAdults * nights * 8500)
        }
      }
      if (amountOthers != 0) {
        total = total + (amountOthers * nights * 6000)
      }
      if (fcfm == 1) {
        var rest = total.toDouble * .9
        total = total - rest.toInt
      }
    }

    println("TOTAL = " + total)
    return total
  }

  def hotelPost(hotelNumber:Int) = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val formBody = body.asFormUrlEncoded

    // Seq: trait that represents indexed sequences (defined order) that are immutable
    // Map: collection of key-value pairs
    val values: Seq[String] = { // List
          formBody map {
              params: Map[String, Seq[String]] => {
                for ((key: String, value: Seq[String]) <- params) yield value.mkString
              }.toSeq
          }
      }.getOrElse(Seq.empty[String])

    println(values)
    println("Hotel number: " + hotelNumber)

    val nights = values(10).toInt
    val amountAdults = values(3).toInt
    val amountOthers = values(4).toInt
    val floor = values(7).toInt
    val spa = values(5).toInt
    val gym = values(6).toInt
    val fcfm = values(11).toInt

    val total: Int = Application.totalOperation(hotelNumber, nights, amountAdults, amountOthers, floor, spa, gym, fcfm)

    var hotelName = ""
    if(hotelNumber == 1){
      hotelName = "Silver Hotel - Vallarta"
    } else if(hotelNumber == 2){
      hotelName = "Temptation Hotel - Cancun"
    } else {
      hotelName = "Hyatt Ziva - Los Cabos"
    }

    var hotelUrl = "hotel" + hotelNumber.toString

    Ok(views.html.result(total)(values)(hotelName)(hotelUrl))
  }

  def reservationToDB(values: Seq[String]) = Action { request: Request[AnyContent] =>
    println("In reservation to DB")

    var str = "" 

    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      str = "INSERT INTO reservations VALUES ('" + values(0) + "','" +
                    values(1)+"'," + values(2) +","+ values(3) + ","+
                    values(4) +","+ values(5) + ","+ values(6) +","+ values(7) + ","+
                    values(8) +","+
                     values(9) + ","+
                     values(10) +","+
                     values(11) + ")"


      print("STR: " + str)
      stmt.executeUpdate(str)

      
    } finally {
      println("Connection closed")
      conn.close()
    }

    Ok(views.html.index(null))
  } 



/////////////////////////////////////////////////////////////////////////////////

  def deleteX() = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val formBodyDelete = body.asFormUrlEncoded

    // Seq: trait that represents indexed sequences (defined order) that are immutable
    // Map: collection of key-value pairs
    val dato: Seq[String] = {
      formBodyDelete map {
        params: Map[String, Seq[String]] => {
          for ((key: String, value: Seq[String]) <- params) yield value.mkString
          }.toSeq
      }
      }.getOrElse(Seq.empty[String])

    println(dato)

    val phone = dato(0)
    var out = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      //val rs = stmt.executeQuery("SELECT tick FROM ticks")
      stmt.execute("DELETE FROM reservations WHERE phone ="+ phone +"")
    } finally {
      conn.close()
    }
    //println(phone)
    Ok(views.html.admin(null))
  }

  def allReservations = Action {
    var out = ""
    // Creating array buffer
    var names =  ArrayBuffer[String]()
    var phones =  ArrayBuffer[String]()
    var emails =  ArrayBuffer[String]()
    var adults =  ArrayBuffer[String]()
    var others =  ArrayBuffer[String]()
    var spas =  ArrayBuffer[String]()
    var gyms =  ArrayBuffer[String]()
    var floors =  ArrayBuffer[String]()
    var months =  ArrayBuffer[String]()
    var days =  ArrayBuffer[String]()
    var nights =  ArrayBuffer[String]()
    var fcfms =  ArrayBuffer[String]()
    var totals =  ArrayBuffer[String]()

    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      //stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      //stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      //val rs = stmt.executeQuery("SELECT tick FROM ticks")
      val hotelsDB = stmt.executeQuery("SELECT name, email, phone, adults, others, spa, gym, floor, month , day, nights, fcfm, total FROM reservations")

      while (hotelsDB.next) {
        names += hotelsDB.getString("name")
        emails += hotelsDB.getString("email")
        phones += hotelsDB.getString("phone")
        adults += hotelsDB.getString("adults")
        others += hotelsDB.getString("others")
        spas += hotelsDB.getString("spa")
        gyms += hotelsDB.getString("gym")
        floors += hotelsDB.getString("floor")
        months += hotelsDB.getString("month")
        days += hotelsDB.getString("day")
        nights += hotelsDB.getString("nights")
        fcfms += hotelsDB.getString("fcfm")
        totals += hotelsDB.getString("total")

        //out += "Read from DB: " + hotelsDB.getString("name") + "\n"

      }
    } finally {
      conn.close()
    }

    Ok(views.html.seeAll(names)(emails)(phones)(adults)(others)(spas)(gyms)(floors)(months)(days)(nights)(fcfms)(totals))
  }


  
  def db = Action {
    var out = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      //val rs = stmt.executeQuery("SELECT tick FROM ticks")
      val hotels = stmt.executeQuery("SELECT name FROM hotels")

      while (hotels.next) {
        out += "Read from DB: " + hotels.getString("name") + "\n"
      }
    } finally {
      conn.close()
    }
    Ok(out)
  }

}

package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import play.api.db._
import scala.collection.mutable.ArrayBuffer

object Application extends Controller {

  ///////////////////// VIEWS

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

  def reservationUpdateForm(phone: Int) = Action {
    Ok(views.html.reservationUpdateForm(phone))
  }
  def searchReservation(action: Int) = Action {
    Ok(views.html.searchReservation("Search Reservation")(action))
  }

  /////////////////////// CRUD OPERATIONS

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

  def reservationUpdate(phone: Int) = Action { request: Request[AnyContent] =>
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

    val name = values(0)
    val email = values(1)
    val originalPhone = values(2).toInt
    val nights = values(10).toInt
    val month = values(8).toInt
    val dayIn = values(9).toInt
    val amountAdults = values(3).toInt
    val amountOthers = values(4).toInt
    val floor = values(7).toInt
    val spa = values(5).toInt
    val gym = values(6).toInt
    val fcfm = values(11).toInt


    println("In update to DB")



    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      var str = ""
      str = "UPDATE public.reservations set name='"+name+
            "', email='"+email+"', phone="+originalPhone+","+
            """ "amountAdults"=  """+amountAdults+
            """, "amountOthers"=  """+amountOthers+
            """, "amountOthers"=  """+amountOthers+
            ", spa="+spa+
            ", gym="+gym+", floor="+floor+", month="+month+
            """, "dayIn"=  """+dayIn+", nights="+nights+
            " WHERE phone = " + phone


      print("STR: " + str)

      stmt.executeUpdate(str)


    } finally {
      println("Connection closed")
      conn.close()
    }

    Ok(views.html.confirmOperation("Update"))
  }


  def searchReservationForm(action: Int) = Action { request: Request[AnyContent] =>
    val body: AnyContent = request.body
    val formBody = body.asFormUrlEncoded

    val phone = formBody.get("phone")(0).toInt
    // Application.searchReservationDB(phone)

    val stringValues = ArrayBuffer[String]()
    val numericValues = ArrayBuffer[Int]()

    val conn = DB.getConnection()


    try {
      val stmt = conn.createStatement

      val queryStr = """SELECT * FROM public.reservations WHERE "phone" = """ + phone
      // println(queryStr)
      //val rs = stmt.executeQuery("SELECT tick FROM ticks")
      val reservation = stmt.executeQuery(queryStr)

      while (reservation.next) {
        val name = reservation.getString("name")
        val email = reservation.getString("email")
        val phone = reservation.getInt("phone")
        val amountAdults = reservation.getInt("amountAdults")
        val amountOthers = reservation.getInt("amountOthers")
        val spa = reservation.getInt("spa")
        val gym = reservation.getInt("gym")
        val fcfm = reservation.getInt("fcfm")
        val floor = reservation.getInt("floor")
        val month = reservation.getInt("month")
        val dayIn = reservation.getInt("dayIn")
        val nights = reservation.getInt("nights")
        stringValues ++= List(name, email)
        numericValues ++= List(phone, amountAdults, amountOthers,
        spa, gym, fcfm, floor, month, dayIn, nights)
        println("In search reservation")
        println(stringValues)
        println(numericValues)
      }
    } finally {
      conn.close()
    }

    Ok(views.html.showReservation(stringValues)(numericValues)(action))

  }

  def allReservations = Action {
    var out = ""
    // Creating array buffer
    var names =  ArrayBuffer[String]()
    var phones =  ArrayBuffer[String]()
    var emails =  ArrayBuffer[String]()
    var amountAdults =  ArrayBuffer[String]()
    var amountOthers =  ArrayBuffer[String]()
    var spas =  ArrayBuffer[String]()
    var gyms =  ArrayBuffer[String]()
    var floors =  ArrayBuffer[String]()
    var months =  ArrayBuffer[String]()
    var dayIn =  ArrayBuffer[String]()
    var nights =  ArrayBuffer[String]()
    var fcfms =  ArrayBuffer[String]()
    var totals =  ArrayBuffer[String]()
    var hotelName =  ArrayBuffer[String]()

    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      out = """ SELECT name, email, phone, "amountAdults", "amountOthers", spa, gym, fcfm, floor, month, "dayIn", nights, total, "hotelName" FROM public.reservations;"""
      val hotelsDB = stmt.executeQuery(out)

      while (hotelsDB.next) {
        names += hotelsDB.getString("name")
        emails += hotelsDB.getString("email")
        phones += hotelsDB.getString("phone")
        amountAdults += hotelsDB.getString("amountAdults")
        amountOthers += hotelsDB.getString("amountOthers")
        spas += hotelsDB.getString("spa")
        gyms += hotelsDB.getString("gym")
        floors += hotelsDB.getString("floor")
        months += hotelsDB.getString("month")
        dayIn += hotelsDB.getString("dayIn")
        nights += hotelsDB.getString("nights")
        fcfms += hotelsDB.getString("fcfm")
        totals += hotelsDB.getString("total")
        hotelName += hotelsDB.getString("hotelName")

        //out += "Read from DB: " + hotelsDB.getString("name") + "\n"

      }
    } finally {
      conn.close()
    }

    Ok(views.html.seeAll(names)(emails)(phones)(amountAdults)(amountOthers)(spas)(gyms)(floors)(months)(dayIn)(nights)(fcfms)(totals)(hotelName))
  }


  def deleteX = Action { request: Request[AnyContent] =>
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
      stmt.execute("DELETE FROM reservations WHERE phone ="+ phone +"")
    } finally {
      conn.close()
    }
    //println(phone)
    Ok(views.html.confirmOperation("delete"))
  }


  ///////////////////////// HELPERS

  def getPricesFromDB(hotelNumber: Int): ArrayBuffer[Int] = {
    println("In getPricesFromDB")

    val prices = ArrayBuffer[Int]()
    var str = ""

    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      str = """SELECT "priceFloorOne", "priceFloorTwo", "priceFloorThree", gym, spa, "priceOther" FROM public.hotels WHERE "hotelNumber" = """ + hotelNumber
      // println(str)
      val data = stmt.executeQuery(str)

      while(data.next) {
        val priceFloorOne = data.getInt("priceFloorOne")
        val priceFloorTwo = data.getInt("priceFloorTwo")
        val priceFloorThree = data.getInt("priceFloorThree")
        val priceOthers = data.getInt("priceOther")
        val spa = data.getInt("spa")
        val gym = data.getInt("gym")
        prices ++= List(priceFloorOne, priceFloorTwo, priceFloorThree, priceOthers, spa, gym)
        //println(prices)
        return prices
      }
      return prices

    } finally {
      println("Connection closed")
      conn.close()
    }

  }


  def totalOperation(hotelNumber:Int, nights:Int, amountAdults:Int, amountOthers:Int, floor:Int, spa:Int, gym:Int, fcfm:Int):Int = {
    // println("Inside function totalOperation")
    val prices = Application.getPricesFromDB(hotelNumber)
    println(prices)
    val priceFloorOne = prices(0)
    val priceFloorTwo = prices(1)
    val priceFloorThree = prices(2)
    val priceOthers = prices(3)
    val spaPrice = prices(4)
    val gymPrice = prices(5)

    var total = 0

      if (spa == 1) {
        total = total + spaPrice
      }
      if (gym == 1) {
        total = total + gymPrice
      }
      if (floor == 1) {
        if (amountAdults != 0){
          total = total + (amountAdults * nights * priceFloorOne)
        }
      } else if (floor == 2){
        if (amountAdults != 0){
          total = total + (amountAdults * nights * priceFloorTwo)
        }
      } else {
        if (amountAdults != 0){
          total = total + (amountAdults * nights * priceFloorThree)
        }
      }
      if (amountOthers != 0) {
        total = total + (amountOthers * nights * priceOthers)
      }
      if (fcfm == 1) {
        var rest = total.toDouble * .1
        total = total - rest.toInt
      }

    println("TOTAL = " + total)
    return total
  }

  def reservationToDB(values: Seq[String], total:Int, hotelName: String) = Action { request: Request[AnyContent] =>
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
                     values(11) +","+
                     total + ", '"+
                     hotelName + "') "


      print("STR: " + str)
      stmt.executeUpdate(str)


    } finally {
      println("Connection closed")
      conn.close()
    }

    Ok(views.html.thankYou())
  }

  def moreProfit = Action {
    var out = ""
    // Creating array buffer
    var month =  ArrayBuffer[String]()
    var income =  ArrayBuffer[String]()
    var occupation =  ArrayBuffer[String]()
    var reservations =  ArrayBuffer[String]()

    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val hotelsDB = stmt.executeQuery("SELECT month, SUM (total) AS income, SUM (amountAdults)+SUM(amountOthers) AS occupation, COUNT(phone) AS reservations FROM reservations GROUP BY month ORDER BY SUM (total) DESC LIMIT 1")

      while (hotelsDB.next) {
        month += hotelsDB.getString("month")
        income += hotelsDB.getString("income")
        occupation += hotelsDB.getString("occupation")
        reservations += hotelsDB.getString("reservations")
      }
    } finally {
      conn.close()
    }
    Ok(views.html.moreProfit(month)(income)(occupation)(reservations))
  }
}

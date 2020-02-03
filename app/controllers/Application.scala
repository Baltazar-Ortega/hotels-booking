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

    print("TOTAL = " + total)
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
    Ok(views.html.result(total))
  }

  

  def hotel3Post = Action { request =>
  
    val resRaw = request.body.asText.toString()

    val res = resRaw.replaceAll("Some", "")
    val resNoIzq = res.replace("(", "")
    val newRes = resNoIzq.replace(")", "")
    // println("Esto es: " + newRes)
    val arrayRes = newRes.split(",")

    var str = "" 

    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      str = "INSERT INTO reservations VALUES ('" + arrayRes(0) + "','" +
                    arrayRes(1)+"'," + arrayRes(2) +","+ arrayRes(3) + ","+
                    arrayRes(4) +","+ arrayRes(5) + ","+ arrayRes(6) +","+ arrayRes(7) + ","+
                    arrayRes(8) +","+
                     arrayRes(9) + ","+
                     arrayRes(10) +","+
                     arrayRes(11) + ")"


      print("STR: " + str)
      stmt.executeUpdate(str)

      
    } finally {
      print("Connection closed")
      conn.close()
    }

    Ok(views.html.index(null)) 
    
  }

  def admin = Action {
    Ok(views.html.admin(null))
  }
  def configuracion = Action {
    Ok(views.html.configuracion(null))
  }

  def todo = Action {
    var out = ""
    // Creating array buffer
    var hotels =  ArrayBuffer[String]()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      //stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      //stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      //val rs = stmt.executeQuery("SELECT tick FROM ticks")
      val hotelsDB = stmt.executeQuery("SELECT name FROM hotels")

      while (hotelsDB.next) {
        hotels += hotelsDB.getString("name")
        //out += "Read from DB: " + hotelsDB.getString("name") + "\n"

      }
    } finally {
    conn.close()
    }
    //print(out + "\n\n")
    print(hotels(0))
    Ok(views.html.todo(hotels))
  }

  def estrellas = Action { request=>
    val valor=  request.body.asText.toString()
    var hotels =  ArrayBuffer[String]()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      //val rs = stmt.executeQuery("SELECT tick FROM ticks")
      val hotelsDB = stmt.executeQuery("SELECT name  FROM hotels WHERE estrellas='valor'")
      while (hotelsDB.next) {
        hotels += hotelsDB.getString("name")
      }
    } finally {
      conn.close()
    }

    Ok(views.html.estrellas(null))
  }
  def reservacion = Action {
    var out = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      //val rs = stmt.executeQuery("SELECT tick FROM ticks")
      val hotels = stmt.executeQuery("DELETE * FROM reservaciones")
    } finally {
      conn.close()
    }

    Ok(views.html.reservacion(null))
  }
  def menor () = Action { request =>
    val valor=  request.body.asText.toString()
    var hoteles =  ArrayBuffer[String]()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      //val rs = stmt.executeQuery("SELECT tick FROM ticks")
      val hotels = stmt.executeQuery("SELECT name  FROM hotels WHERE costo<'valor'")
      while (hotels.next) {
        hoteles += hotels.getString("name")

      }
    } finally {
      conn.close()
    }


    Ok(views.html.menor(null))
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

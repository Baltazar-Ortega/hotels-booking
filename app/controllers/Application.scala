package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current

import play.api.db._

import scala.collection.mutable.ArrayBuffer

object Application extends Controller {


  def hotel1 = Action {
    Ok(views.html.hotel1(null))
  }
  def hotel3 = Action {
    Ok(views.html.hotel3(null))
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

  def index = Action {
    Ok(views.html.index(null))
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

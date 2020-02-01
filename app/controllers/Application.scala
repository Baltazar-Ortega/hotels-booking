package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current

import play.api.db._

object Application extends Controller {

/*
  def hotel1 = Action {
    Ok(views.html.hotel1)
  }
  */

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

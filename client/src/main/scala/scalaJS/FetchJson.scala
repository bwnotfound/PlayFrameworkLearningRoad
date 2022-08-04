package scalaJS

import org.scalajs.dom.experimental._
import play.api.libs.json._
import scala.scalajs.js.Thenable.Implicits._
import scala.concurrent.ExecutionContext

object FetchJson {

  def fetchPost[A, B](url: String, data: A, success: B => Unit, error: JsError => Unit)(implicit
      csrfToken: String, writes: Writes[A], reads: Reads[B], ec: ExecutionContext): Unit = {
    val headers = new Headers()
    headers.set("Content-Type", "application/json")
    headers.set("Csrf-Token", csrfToken)
    Fetch
      .fetch(
        url,
        RequestInit(
          method = HttpMethod.POST,
          mode = RequestMode.cors,
          headers = headers,
          body = Json.toJson(data).toString
        )
      )
      .flatMap(res => res.text())
      .map { jsonString =>
        Json.fromJson[B](Json.parse(jsonString)) match {
          case JsSuccess(b, _) =>
            success(b)
          case e @ JsError(_) =>
            error(e)
        }
      }
  }

  def fetchGet[A](url: String, success: A => Unit, error: JsError => Unit)(implicit
      csrfToken: String, reads: Reads[A], ec: ExecutionContext): Unit = {
    val headers = new Headers()
    headers.set("Content-Type", "application/json")
    headers.set("Csrf-Token", csrfToken)
    Fetch
      .fetch(
        url,
        RequestInit(
          method = HttpMethod.GET,
          mode = RequestMode.cors,
          headers = headers,
        )
      )
      .flatMap(res => res.text())
      .map { jsonString =>
        Json.fromJson[A](Json.parse(jsonString)) match {
          case JsSuccess(a, _) =>
            success(a)
          case e @ JsError(_) =>
            error(e)
        }
      }
  }

}

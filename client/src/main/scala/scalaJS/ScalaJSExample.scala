package scalaJS

import shared.SharedMessages
import org.scalajs.dom
import org.scalajs.dom.html

object ScalaJSExample {

  def main(args: Array[String]): Unit = {

    if(dom.document.getElementById("version6")!=null){
      Version6.init()
    }

  }
}

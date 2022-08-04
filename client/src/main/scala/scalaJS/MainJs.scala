package scalaJS

import shared.SharedMessages
import org.scalajs.dom
import org.scalajs.dom._
import slinky.web.ReactDOM
import slinky.web.html._

object ScalaJSExample {

  def main(args: Array[String]): Unit = {

    if(dom.document.getElementById("version6")!=null){
      Version6.init()
    }

    if(dom.document.getElementById("version7")!=null){
      ReactDOM.render(
        Version7MainComponent(),
        document.getElementById("react-root")
      )
    }
    
  }
}

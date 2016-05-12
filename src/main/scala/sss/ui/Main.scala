package sss.ui

import sss.ancillary.{DynConfig, InitServlet, ServerConfig, ServerLauncher}

/**
  * Created by alan on 5/11/16.
  */
object Main {

  def main(args: Array[String]) {

    val port = args.find(_.startsWith("port=")).map(_.substring(5).toInt).getOrElse(8080)

    val svlt = new Servlet
    val server = ServerLauncher(DynConfig[ServerConfig]("httpServerConfig"), InitServlet(svlt, "/*"))
    server.start
    server.join
  }

}

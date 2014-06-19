package com.socrata.geospace

import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

object Geospace extends App {
  val config = new GeospaceConfig(ConfigFactory.load())
  val port = config.port
  val server = new Server(port)
  val context = new WebAppContext()

  context setContextPath "/"
  context.setResourceBase("src/main/webapp")
  context.addEventListener(new ScalatraListener)
  context.addServlet(classOf[DefaultServlet], "/")

  server.setHandler(context)

  server.start
  server.join
}

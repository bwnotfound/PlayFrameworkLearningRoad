package models

object CodeGen extends App {
  slick.codegen.SourceCodeGenerator.run(
    "slick.jdbc.PostgresProfile", 
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost/tasklist?user=root&password=12345",
    "/root/pfwlearn/BwLearnPfw/server/app/", 
    "models", None, None, true, false
  )
}
package xrrocha.dynamic

object Example extends App {
  import DYaml._

  val countries = dyaml"""
    |- name: USA
    |  currency: USD
    |  population: 313.9
    |  motto: In God We Trust
    |  languages:
    |    - { name: English, comment: Unofficially official }
    |    - { name: Spanish, comment: Widely spoken all over }
    |  flag: http://upload.wikimedia.org/wikipedia/en/thumb/a/a4/Flag_of_the_United_States.svg/30px-Flag_of_the_United_States.svg.png
    |- name: Canada
    |  currency: CAD
    |  population: 34.9
    |  motto: |
    |    A Mari Usque ad Mare<br>
    |    (<i>From sea to sea, D'un océan à l'autre</i>)
    |  languages:
    |    - { name: English, comment: 'Official, yes' }
    |    - { name: French, comment: 'Officiel, oui' }
    |  flag: http://upload.wikimedia.org/wikipedia/en/thumb/c/cf/Flag_of_Canada.svg/30px-Flag_of_Canada.svg.png
    |- name: Mexico
    |  currency: MXN
    |  population: 116.1
    |  motto: |
    |    Patria, Libertad, Trabajo y Cultura<br>
    |    (<i>Homeland, Freedom, Work and Culture</i>)
    |  languages:
    |    - { name: Spanish, comment: 'Oficial, sí' }
    |    - { name: Zapoteco, comment: Dxandi' anja }
    |  flag: http://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Flag_of_Mexico.svg/30px-Flag_of_Mexico.svg.png
  """.toList

  import Html._
  def country2Html(country: SDynamic) = html"""
          |<tr>
          |  <td><img src="${country.flag}"></td>
          |  <td>${country.name}</td>
          |  <td>${country.motto}</td>
          |  <td>
          |    <ul>
          |      ${
                    country.languages.toList.map { lang =>
                      s"<li>${lang.name}: ${lang.comment}</li>"
                    }.
                    mkString("\n")
                 }
          |    </ul>
          |  </td>
          |</tr>
        """

  val pageHtml = html"""
          |<html>
          |<head><title>NAFTA Countries</title><meta charset="UTF-8"></head>
          |<body>
          |<table border='1'>
          |<tr>
          |  <th>Flag</th>
          |  <th>Name</th>
          |  <th>Motto</th>
          |  <th>Languages</th>
          |</tr>
          |<tr>${(countries map country2Html).mkString}</tr>
          |</table>
          |</body>
          |</html>
        """

  val out = new java.io.FileOutputStream("src/test/resources/countries.html")
  out.write(pageHtml.getBytes("UTF-8"))
  out.flush()
  out.close()
}

object Html {
  implicit class HtmlString(val sc: StringContext) extends AnyVal {
    def html(args: Any*) = sc.s(args: _*).stripMargin.trim
  }
}

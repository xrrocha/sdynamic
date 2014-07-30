package xrrocha.dynamic

import org.scalatest.FunSuite
import org.yaml.snakeyaml.Yaml
import xrrocha.syaml.SYaml

class DYamlSuite extends FunSuite {
  import xrrocha.dynamic.DYaml._

  test("SnakeYaml-built Java map is accessible through dynamic properties") {
    val person = dyaml"""
        |name: Alex
        |age: 14
        |languageSkills:
        |  - { language: English,  level: advanced }
        |  - { language: Spanish,  level: intermediate }
        |  - { language: Japanese, level: basic }
        |hobbies:
        |  animé: a lot
        |  reading: sure, on screen
        |  programming: uh, yeah
        |  books: when unavailable in e-format
        |now: !!java.util.Date []
      """

    assert(person.now.value.isInstanceOf[java.util.Date])

    assert(person.name == "Alex")

    assert(person.age.toString.toInt == 14)

    val languageSkills: Seq[SDynamic] = person.languageSkills
    assert(languageSkills.length == 3)
    assert(languageSkills(0).language == "English")
    assert(languageSkills(0).level == "advanced")

    val hobbies: Map[String, SDynamic] = person.hobbies
    assert(hobbies.size == 4)
    assert(hobbies.keySet == Set("animé", "reading", "programming", "books"))
    assert(hobbies("programming") == "uh, yeah")
  }

  test("SYaml-built Scala map is accessible through dynamic properties") {
    import SYaml._
    val person = syaml"""
        |name: Alex
        |age: 14
        |languageSkills:
        |  - { language: English,  level: advanced }
        |  - { language: Spanish,  level: intermediate }
        |  - { language: Japanese, level: basic }
        |hobbies:
        |  animé: a lot
        |  reading: sure, on screen
        |  programming: uh, yeah
        |  books: when unavailable in e-format
      """

    assert(person.name == "Alex")

    assert(person.age.toString.toInt == 14)

    val languageSkills: Seq[SDynamic] = person.languageSkills
    assert(languageSkills.length == 3)
    assert(languageSkills(0).language == "English")
    assert(languageSkills(0).level == "advanced")

    val hobbies: Map[String, SDynamic] = person.hobbies
    assert(hobbies.size == 4)
    assert(hobbies.keySet == Set("animé", "reading", "programming", "books"))
    assert(hobbies("programming") == "uh, yeah")
  }

  test("Plays well with interpolation") {
    val id = 42
    val name = "Mr. Anderson"
    val language = "English"

    val neo = dyaml"{ id: $id, name: $name, languages: [ $language ]}"

    assert(neo.id == id)
    assert(neo.name == name)
    assert(neo.languages.toList.length == 1)
    assert(neo.languages.toList(0) == language)
  }

  test("example") {
    val naftaCountries = dyaml"""
      |- { name: USA,  currency: USD, population: 313.9,
      |    motto: In God We Trust, languages: [ English ] }
      |- { name: Canada, currency: CAD, population: 34.9,
      |    motto: A Mari Usque ad Mare, languages: [ English, French ] }
      |- { name: Mexico, currency: MXN, population: 116.1,
      |    motto: 'Patria, Libertad, Trabajo y Cultura', languages: [ Spanish ] }
    """.toList

    assert(naftaCountries.length == 3)
    assert(naftaCountries(0).name == "USA")
    assert(naftaCountries(1).population == 34.9)
    assert(naftaCountries(2).motto == "Patria, Libertad, Trabajo y Cultura")
    assert(naftaCountries(1).languages.toList == Seq("English", "French"))
  }

  // Look ma: case classes get the job done too:
  case class Country(name: String, currency: String, population: Double, motto: String, languages: Seq[String])
  val naftaCountries = Seq(
    Country(
      name = "USA",
      currency = "UDS",
      population = 313.9,
      motto = "In God We Trust",
      languages = Seq("English")),
    Country(
      name = "Canada",
      currency = "CAD",
      population = 34.9,
      motto = "A Mari Usque ad Mare",
      languages = Seq("English", "French")),
    Country(
      name = "Mexico",
      currency = "MXN",
      population = 116.1,
      motto = "Patria, Libertad, Trabajo y Cultura",
      languages = Seq("Spanish"))
  )
}

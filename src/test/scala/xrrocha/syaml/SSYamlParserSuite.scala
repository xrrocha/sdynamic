package xrrocha.syaml

import org.scalatest.FunSuite

// Copied from:
//   https://github.com/daltontf/scala-yaml/blob/master/src/test/scala/tfd/scala/yaml/YAMLParserTest.scala

class SSYamlParserSuite extends FunSuite {
  import xrrocha.syaml.SYamlParser._

  test("Builds empty inline map") {
    assert(Map() == parse("""{}""").get)
  }

  test("Builds empty inline list") {
    assert(List() == parse("""[]""").get)
  }

  test("Builds list with empty inline map") {
    assert(
      List("foo", Map(), "bar") ==
      parse("""|- foo
              		         |- {}
              			     |- bar""".stripMargin).get)
  }

  test("Builds map with empty inline list") {
    assert(
      Map("foo bar" -> "true", "snafu" -> List(), "empty" -> Map()) ==
      parse("""|foo bar: true
              				 |snafu: []
              				 |empty: {}""".stripMargin).get)
  }

  test("Builds simple map") {
    assert(Map("key"->"value") == parse("""key: value""").get)
    assert(Map("key1"->"value1", "key2"->"value2") == parse(
      """|
        				   |key1: value1
        				   |key2: value2
        				   |""".stripMargin).get)
    assert(Map("key 1"->"value 1", "key 2"->"value 2") == parse(
      """|
        				   |key 1: value 1
        				   |key 2: value 2
        				   |""".stripMargin).get)
  }

  test("Builds simple list") {
    assert(List("item1") == parse(
      """|
        				   |- item1
        				   |""".stripMargin).get)
    assert(List("item1", "item2") == parse(
      """|
        				   |- item1
        				   |- item2
        				   |""".stripMargin).get)
  }


  test("Builds nested list") {
    assert(List(
      List("item11", "item12"),
      List("item21", "item22")) ==
      parse(
        """|
          						|-
          						|  - item11
          						|  - item12
          						|-
          						|  - item21
          						|  - item22
          						|""".stripMargin).get)
  }

  test("Builds list of maps") {
    assert(List(
      Map("name"-> "John Smith", "age"->"33"),
      Map("name"-> "Mary Smith", "age"->"27")) ==
      parse(
        """|
          						|- name: John Smith
          						|  age: 33
          						|- name: Mary Smith
          						|  age: 27
          						|""".stripMargin).get)
  }

  test("Builds map of lists") {
    assert(Map("men" -> List("John Smith", "Bill Jones"), "women" -> List("Mary Smith", "Susan Williams")) ==
      parse(
        """|
          					   |men:
          					   |  - John Smith
          					   |  - Bill Jones
          					   |women:
          					   |  - Mary Smith
          					   |  - Susan Williams
          					   |""".stripMargin).get)
  }

  test("Builds inline map") {
    assert(Map("key"->"value") == parse("""key: value""").get)
    assert(Map("key1"->"value1", "key2"->"value2") == parse(
      """{ key1: value1, key2: value2 }""").get)
  }


  test("Builds nested map") {
    assert(Map("JFrame" -> Map("name" -> "myFrame", "title" -> "My App Frame")) ==
      parse(
        """|
          						|JFrame:
          						|     name: myFrame
          						|     title: My App Frame
          						|""".stripMargin).get)
  }
  test("Builds nested map with empty element") {
    assert(Map("JFrame" -> Map("content" -> Map("button" -> "press"))) ==
      parse(
        """|
          						|JFrame:
          						|     content:
          						|     button: press
          						|""".stripMargin).get)
  }

  test("Builds simple bracketed list") {
    assert(List("item1", "item2") ==
      parse("""[ item1, item2 ]""").get)
  }

  test("Builds map with bracketed lists") {
    assert(Map("men" -> List("John Smith", "Bill Jones"), "women" -> List("Mary Smith", "Susan Williams")) ==
      parse(
        """|
          					   |men: [ John Smith, Bill Jones ]
          					   |women: [ Mary Smith, Susan Williams ]
          					   |""".stripMargin).get)
  }

  test("Builds more complex graph") {
    assert(
      Map("address" ->
        Map("first_name" -> "Brian",
          "last_name" -> "Reece",
          "email" -> "brian@majordomo.com",
          "company" ->
            Map("name"->"Five Apart, Ltd.",
              "street_address"->"8458 5th Street, San Francisco, CA 94107"))) ==
      parse(
        """|address:
          	  | first_name: Brian
          	  | last_name: Reece
          	  | email: brian@majordomo.com
          	  | company:
          	  |  name: Five Apart, Ltd.
          	  |  street_address: 8458 5th Street, San Francisco, CA 94107
        """.stripMargin).get)
  }
}

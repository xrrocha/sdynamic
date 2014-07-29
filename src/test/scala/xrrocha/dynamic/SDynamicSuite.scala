package xrrocha.dynamic

import org.scalatest.FunSuite

class SDynamicSuite extends FunSuite {
  val types = Seq(ScalaDynamic, JavaDynamic)

  test("Scala map is accessible through dynamic properties") {
    val map = Map(
      "name" -> "Alex",
      "age" -> "14",
      "birthdate" -> "11/24/1999",
      "languageSkills" -> List(
        Map("language" -> "English", "level" -> "advanced"),
        Map("language" -> "Spanish", "level" -> "intermediate"),
        Map("language" -> "Japanese", "level" -> "basic")
      ),
      "hobbies" -> Map(
        "animé" -> "a lot",
        "reading" -> "sure, on screen",
        "programming" -> "uh, yeah",
        "books" -> "when unavailable in e-format"
      )
    )

    val person = ScalaDynamic(map)
    doTest(person)
  }

  test("Java map is accessible through dynamic properties") {
    val map = new java.util.HashMap[String, Any]
    map.put("name", "Alex")
    map.put("age", "14")
    map.put("birthdate", "11/24/1999")

    val languageSkills = new java.util.ArrayList[java.util.Map[String, Any]]
    map.put("languageSkills", languageSkills)
    languageSkills.add({
      val languageSkill = new java.util.HashMap[String, Any]
      languageSkill.put("language", "English")
      languageSkill.put("level", "advanced")
      languageSkill
    })
    languageSkills.add({
      val languageSkill = new java.util.HashMap[String, Any]
      languageSkill.put("language", "Spanish")
      languageSkill.put("level", "intermediate")
      languageSkill
    })
    languageSkills.add({
      val languageSkill = new java.util.HashMap[String, Any]
      languageSkill.put("language", "Japanese")
      languageSkill.put("level", "basic")
      languageSkill
    })

    val hobbies = new java.util.HashMap[String, Any]
    map.put("hobbies", hobbies)
    hobbies.put("animé", "a lot")
    hobbies.put("reading", "sure, on screen")
    hobbies.put("programming", "uh, yeah")
    hobbies.put("books", "when unavailable in e-format")

    val person = JavaDynamic(map)
    doTest(person)
  }

  test("Fails for unmapped properties") {
    types foreach { dynamicType =>
      intercept[IllegalArgumentException] {
        dynamicType("nope").selectDynamic("nonExistent")
      }
    }
  }

  test("Fails for non-indexed properties") {
    types foreach { dynamicType =>
      intercept[IllegalArgumentException] {
        dynamicType("nope").applyDynamic("nonExistent")(0)
      }
    }
    intercept[IllegalArgumentException] {
      new ScalaDynamic(Map("name" -> "Neo")).applyDynamic("name")(0)
    }
    intercept[IllegalArgumentException] {
      val map = new java.util.HashMap[String, Any]()
      map.put("name", "Neo")
      new JavaDynamic(map).applyDynamic("name")(0)
    }
  }

  test("equals() uses value") {
    val value = 42
    types foreach { dynamicType =>
      val dynamicValue = dynamicType(value)
      assert(dynamicValue == value)
      assert(dynamicValue.equals(value))
    }
  }

  test("hasCode() uses value") {
    val value = 69
    types foreach { dynamicType =>
      val dynamicValue = dynamicType(value)
      assert(dynamicValue.hashCode == value.hashCode)
    }
  }

  test("toString() uses value") {
    val value = new java.util.Date
    types foreach { dynamicType =>
      val dynamicValue = dynamicType(value)
      assert(dynamicValue.toString == value.toString)
    }
  }

  def doTest(person: SDynamic) {
    assert(person.name == "Alex")
    assert(person.birthdate == "11/24/1999")

    val age: Int = person.age
    assert(age == 14)

    val languageSkills: Seq[SDynamic] = person.languageSkills
    assert(languageSkills.length == 3)
    assert(languageSkills(0).language == "English")
    assert(languageSkills(0).level == "advanced")

    val hobbies: Map[String, SDynamic] = person.hobbies
    assert(hobbies.size == 4)
    assert(hobbies.keySet == Set("animé", "reading", "programming", "books"))
    assert(hobbies("programming") == "uh, yeah")
  }
}

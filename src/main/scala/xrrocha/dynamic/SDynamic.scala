package xrrocha.dynamic

trait SDynamic extends Dynamic {
  def value: Any

  def selectDynamic(name: String): SDynamic =
    try {
      newInstance(asMap(name))
    } catch {
      case e: Exception => throw new IllegalArgumentException(s"No such property: $name", e)
    }

  def applyDynamic(name: String)(index: Int): SDynamic =
    try {
      newInstance(newInstance(asMap(name)).asList(index))
    } catch {
      case e: Exception => throw new IllegalArgumentException(s"Property '$name' can't be indexed", e)
    }

  def toList = asList map newInstance
  def toMap = asMap mapValues newInstance

  protected def newInstance(newValue: Any): SDynamic

  protected def asList: List[_]
  protected def asMap: Map[String, _]

  override def equals(other: Any) = other match {
    case dynamic: SDynamic => value equals dynamic.value
    case _ => value equals other
  }

  override def hashCode = value.hashCode

  override def toString = value.toString
}

object SDynamic {
  implicit def dynamic2String(dynamic: SDynamic) = dynamic.toString
  implicit def dynamic2Int(dynamic: SDynamic) = dynamic.toString.toInt
  implicit def dynamic2Double(dynamic: SDynamic) = dynamic.toString.toDouble
  implicit def dynamic2Seq(dynamic: SDynamic): List[SDynamic] = dynamic.toList
  implicit def dynamic2Map(dynamic: SDynamic): Map[String, SDynamic] = dynamic.toMap
}

case class ScalaDynamic(value: Any) extends SDynamic {
  def newInstance(newValue: Any) = ScalaDynamic(newValue)
  def asList = value.asInstanceOf[List[_]]
  def asMap = value.asInstanceOf[Map[String @unchecked, _]]
}

case class JavaDynamic(value: Any) extends SDynamic {
  import collection.JavaConversions._

  def newInstance(newValue: Any) = JavaDynamic(newValue)
  def asList = asScalaBuffer(value.asInstanceOf[java.util.List[_]]).toList
  def asMap = mapAsScalaMap(value.asInstanceOf[java.util.Map[String @unchecked, _]]).toMap
}
package xrrocha.dynamic

import org.yaml.snakeyaml.Yaml

object DYaml {
  import scala.language.experimental.macros

  implicit class DYamlString(val sc: StringContext) extends AnyVal {
    def dyaml(args: Any*): SDynamic = macro dyamlImpl
    def syaml(args: Any*): SDynamic = macro syamlImpl

    def dyaml0(args: Any*): SDynamic = ScalaDynamic(DYamlParser.parse(sc.s(args: _*).stripMargin.trim).get)
    def syaml0(args: Any*) = JavaDynamic(new Yaml().load(sc.s(args: _*).stripMargin.trim))
  }

  import scala.reflect.macros.blackbox.Context

  def dyamlImpl(c: Context)(args: c.Expr[Any]*): c.Expr[SDynamic] = {
    import c.universe._

    def lift(any: Any): Tree = any match {
      case map: Map[_, _] => q"Map(..${map.keySet map { k => lift(k) -> lift(map(k))}})"
      case list: List[_] => q"List(..${list map (e => lift(e))})"
      case _ => q"${any.toString}"
    }

    c.prefix.tree match {
      case Apply(_, List(Apply(_, List(yamlLiteral @Literal(Constant(yamlText: String)))))) =>
        val text = yamlText.stripMargin.trim

        val result = try {
          DYamlParser.parse(text).get
        } catch {
          case e: Exception =>
            val message = s"Invalid yaml literal (${e.getMessage}}): '${text.substring(0, math.min(text.length, 64))}'"
            c.abort(c.enclosingPosition, message)
        }

        reify {
          ScalaDynamic(c.Expr[Any](lift(result)).splice)
        }
      case compound =>
        val rts = compound.tpe.decl(TermName("dyaml0"))
        val rt = internal.gen.mkAttributedSelect(compound, rts)
        c.Expr[SDynamic](Apply(rt, args.map(_.tree).toList))
    }
  }

  def syamlImpl(c: Context)(args: c.Expr[Any]*): c.Expr[SDynamic] = {
    import c.universe._

    c.prefix.tree match {
      case Apply(_, List(Apply(_, List(yamlLiteral @Literal(Constant(yamlText: String)))))) =>
        val text = yamlText.stripMargin.trim
        try {
          new Yaml().load(text)
        } catch {
          case e: Exception =>
            val message = s"Invalid yaml literal (${e.getMessage}}): '${text.substring(0, math.min(text.length, 64))}'"
            c.abort(c.enclosingPosition, message)
        }
        reify {
          JavaDynamic(new Yaml().load(c.Expr[String](yamlLiteral).splice.stripMargin.trim))
        }
      case compound =>
        val rts = compound.tpe.decl(TermName("syaml0"))
        val rt = internal.gen.mkAttributedSelect(compound, rts)
        c.Expr[SDynamic](Apply(rt, args.map(_.tree).toList))
    }
  }
}

package xrrocha.dynamic

import org.yaml.snakeyaml.Yaml

object DYaml {
  import scala.language.experimental.macros

  val yaml = new Yaml

  implicit class DYamlString(val sc: StringContext) extends AnyVal {
    def dyaml(args: Any*): SDynamic = macro dyamlImpl

    def dyaml0(args: Any*) = JavaDynamic(yaml.load(sc.s(args: _*).stripMargin.trim))
  }

  import scala.reflect.macros.blackbox.Context
  def dyamlImpl(c: Context)(args: c.Expr[Any]*): c.Expr[SDynamic] = {
    import c.universe._

    c.prefix.tree match {
      case Apply(_, List(Apply(_, List(yamlLiteral @Literal(Constant(yamlText: String)))))) =>
        val text = yamlText.stripMargin.trim
        try {
          yaml.load(text)
        } catch {
          case e: Exception => c.abort(c.enclosingPosition, s"Invalid yaml literal starting with '${text.substring(0, math.min(text.length, 64))}'")
        }
        // TODO Too lazy now to lift above-built Yaml result into a compile-time literal, shom!
        reify{
          val yaml = new Yaml
          JavaDynamic(yaml.load(c.Expr[String](yamlLiteral).splice.stripMargin.trim))
        }
      case compound =>
        val rts = compound.tpe.decl(TermName("dyaml0"))
        val rt = internal.gen.mkAttributedSelect(compound, rts)
        c.Expr[SDynamic](Apply(rt, args.map(_.tree).toList))
    }
  }
}

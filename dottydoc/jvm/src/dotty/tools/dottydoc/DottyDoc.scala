package dotty.tools
package dottydoc

import core.Phases.DocPhase
import dotc.config.CompilerCommand
import dotc.config.Printers.dottydoc
import dotc.core.Contexts._
import dotc.core.Phases.Phase
import dotc.typer.FrontEnd
import dotc.{Compiler, Driver}

/** Custom Compiler with phases for the documentation tool
 *
 *  The idea here is to structure `dottydoc` around the new infrastructure. As
 *  such, dottydoc will itself be a compiler. It will, however, produce a format
 *  that can be used by other tools or web-browsers.
 *
 *  Example:
 *    1. Use the existing FrontEnd to typecheck the code being fed to dottydoc
 *    2. Create an AST that is serializable
 *    3. Serialize to JS-Object and write to file
 *    4. Deserialize on client side with Scala.js
 *    5. Serve content using Scala.js
 */
case object DottyDocCompiler extends Compiler {
  override def phases: List[List[Phase]] =
    List(new FrontEnd) ::
    List(new DocPhase) ::
    Nil
}

object DottyDoc extends Driver {
  override protected def initCtx =
    new InitialContext(new ContextBase, new DottyDocSettings)

  override def setup(args: Array[String], rootCtx: Context): (List[String], Context) = {
    val ctx = rootCtx.fresh
    val summary = CompilerCommand.distill(args)(ctx)
    ctx.setSettings(summary.sstate)
    ctx.setSetting(ctx.settings.YkeepComments, true)
    val fileNames = CompilerCommand.checkUsage(summary, sourcesRequired)(ctx)
    (fileNames, ctx)
  }

  override def newCompiler(implicit ctx: Context): Compiler = DottyDocCompiler
}

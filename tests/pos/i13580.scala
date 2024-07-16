//> using options -language:experimental.modularity -source future
trait IntWidth:
  type Out
given IntWidth with
  type Out = 155

trait IntCandidate:
  type Out
given (using tracked val w: IntWidth): IntCandidate with
  type Out = w.Out

val x = summon[IntCandidate]
val xx = summon[x.Out =:= 155]

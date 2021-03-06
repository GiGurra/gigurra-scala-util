package se.gigurra.util

object Mutate {
  implicit class Mutable[T](val t: T) extends AnyVal {
    def mutate[A](f: T => A): T = {
      f(t)
      t
    }
  }
}

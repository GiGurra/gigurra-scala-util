package se.gigurra.util

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentLinkedQueue

import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.language.implicitConversions
import scala.reflect.ClassTag

class CallBuffer[InterfaceType: ClassTag] {

  private val cls = scala.reflect.classTag[InterfaceType].runtimeClass
  private val bufferedCalls = new ConcurrentLinkedQueue[Call]

  private val handler = new InvocationHandler {
    @throws[Throwable]
    override def invoke(proxy: Object, method: Method, args: Array[Object]): Object = {
      bufferedCalls.add(Call(method, args))
      return null;
    }
  }

  private val proxy =
    java.lang.reflect.Proxy.newProxyInstance(
      cls.getClassLoader(),
      Array(cls),
      handler).asInstanceOf[InterfaceType]

  def flush(receiver: InterfaceType) {
    while (bufferedCalls.nonEmpty) {
      val call = bufferedCalls.poll()
      call.method.invoke(receiver, call.args: _*)
    }
  }

}

case class Call(method: Method, args: Array[Object])

object CallBuffer {
  implicit def toInterface[InterfaceType](cb: CallBuffer[InterfaceType]): InterfaceType = cb.proxy
}

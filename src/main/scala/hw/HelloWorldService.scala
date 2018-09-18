package hw

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.google.protobuf.wrappers.Int32Value
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future
import scala.util.Random

class HelloWorldServiceImpl(materializer: Materializer) extends HelloWorld with LazyLogging {
  import materializer.executionContext
  private implicit val mat: Materializer = materializer

  override def sayHello(request: ToBeGreeted): Future[Greeting] = Future {
    logger.debug(s"sayHello")
    val greetedPerson = request.person match {
      case Some(person) => person.name
      case None         => "anonymous"
    }
    Greeting(message = s"Hello $greetedPerson!")
  }

  override def sayAnything(in: Greeting): Future[Greeting] = Future {
    logger.debug(s"sayAnything")
    val nGreeting = Greeting(in.message + "!!!")
    nGreeting
  }

  override def genIntStream(in: Int32Value): Source[Int32Value, NotUsed] = {
    import scala.concurrent.duration._
    Source(1 to in.value)
      .throttle(1, 500.millis)
      .map { i =>
        val next = Random.nextInt()
        logger.debug(s"nextInt = $next")
        Int32Value(next)
      }
  }

  override def intStreamToStatsStream(in: Source[Int32Value, NotUsed]): Source[MinMax, NotUsed] = ???
}

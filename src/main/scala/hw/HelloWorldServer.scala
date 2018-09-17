package hw

import akka.actor.ActorSystem
import akka.http.scaladsl.UseHttp2.Always
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}

object HelloWorldServer {

  def main(args: Array[String]): Unit = {
    // Important: enable HTTP/2 in ActorSystem's config
    // We do it here programmatically, but you can also set it in the application.conf
    val conf = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())
    val system = ActorSystem("HelloWorld", conf)
    new HelloWorldServer(system).run()
    // ActorSystem threads will keep the app alive until `system.terminate()` is called
  }
}

class HelloWorldServer(system: ActorSystem) {

  def run(): Future[Http.ServerBinding] = {
    // Akka boot up code
    implicit val sys: ActorSystem = system
    implicit val mat: Materializer = ActorMaterializer()
    implicit val ec: ExecutionContext = sys.dispatcher

    // Create service handler
    val service: HttpRequest => Future[HttpResponse] =
      HelloWorldHandler(new HelloWorldServiceImpl(mat))

    // Bind service handler server to localhost:8080
    val bound = Http().bindAndHandleAsync(
      service,
      interface = "127.0.0.1",
      port = 8080,
      // Needed to allow running multiple requests concurrently, see https://github.com/akka/akka-http/issues/2145
      parallelism = 256,
      connectionContext = HttpConnectionContext(http2 = Always))

    // report successful binding
    bound.foreach { binding =>
      println(s"gRPC server bound to: ${binding.localAddress}")
    }

    bound
  }
}
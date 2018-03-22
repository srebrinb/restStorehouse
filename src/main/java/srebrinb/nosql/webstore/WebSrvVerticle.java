package srebrinb.nosql.webstore;

import srebrinb.nosql.webstore.kv.blob.NoSQLstore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class WebSrvVerticle extends AbstractVerticle {

    NoSQLstore store;

    public static void main(String... args) {
        Vertx.vertx().deployVerticle(WebSrvVerticle.class.getName());
    }

    /**
     * This method is called when the verticle is deployed. It creates a HTTP
     * server and registers a simple request handler.
     * <p/>
     * Notice the `listen` method. It passes a lambda checking the port binding
     * result. When the HTTP server has been bound on the port, it call the
     * `complete` method to inform that the starting has completed. Else it
     * reports the error.
     *
     * @param fut the future
     */
    @Override
    public void start(Future<Void> fut) {
        store = new NoSQLstore();
        
        startWebApp((http) -> completeStartup(http, fut));

    }
    @Override
    public void stop(){
        store.close();
    }
    
    private void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
        if (http.succeeded()) {
            fut.complete();
        } else {
            fut.fail(http.cause());
        }
    }

    private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        // Create a router object.
        Router router = Router.router(vertx);

        // Bind "/" to our hello message.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Restful Store</h1>");
        });

        router.route("/assets/*").handler(StaticHandler.create("assets"));
        DocHandler docHandler = new DocHandler(store);

        router.route("/api/docs/*").handler(BodyHandler.create());
        router.get("/api/docs").handler(docHandler::getAll);
        router.post("/api/docs").handler(docHandler::addOne);
        router.get("/api/docs/:id").handler(docHandler::getOne);
        router.put("/api/docs/:id").handler(docHandler::updateOne);
        router.delete("/api/docs/:id").handler(docHandler::deleteOne);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        next::handle
                );
    }
}

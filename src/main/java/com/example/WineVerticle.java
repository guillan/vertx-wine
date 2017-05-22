package com.example;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.ext.mongo.MongoClient;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;

/**
 * Created by glannebere on 17/05/2017.
 */
public class WineVerticle extends AbstractVerticle {

    private MongoClient mongo;

    @Override
    public void start() throws Exception {

        mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "wines"));

        Router router = Router.router(vertx);
        router.get("/appelations/:appelation").handler(this::getByAppelation);
        router.get("/types/:type").handler(this::getByType);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }

    private void getByAppelation(RoutingContext rc) {

        HttpServerResponse response = rc.response().setChunked(true);

        String appelation = rc.pathParam("appelation");

        JsonObject query = new JsonObject().put("appelation", appelation);

        mongo.rxFind("wine", query)
            .subscribe(
                    results -> {
                        response.end(Json.encode(results));
                    }
            );
    }

    private void getByType(RoutingContext rc) {
        HttpServerResponse response = rc.response().setChunked(true);

        String type = rc.pathParam("type");

        JsonObject query = new JsonObject().put("type", type);

        mongo.rxFind("wine", query)
                .subscribe(
                    results -> {
                        response.end(Json.encode(results));
                    }
                );
    }


}


package com.claudioliveira.rest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Claudio Eduardo de Oliveira (claudioed.oliveira@gmail.com)
 */
public class SimpleHttpRest extends AbstractVerticle {

    private final Map<String,JsonObject> users = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpRest.class);

    public static void main(String[] args) {
        VertxOptions options = new VertxOptions();
        Vertx server = Vertx.vertx(options);
        server.deployVerticle(new SimpleHttpRest());
    }

    @Override
    public void start() throws Exception {
        this.buildUsersList();
        this.logUsers();
        Router router = Router.router(this.vertx);
        router.route().handler(BodyHandler.create());
        router.get("/users/:id").handler(this::getUser);
        this.vertx.createHttpServer().requestHandler(router::accept).listen(4004);
    }

    private void getUser(RoutingContext routingContext){
        String userId = routingContext.request().getParam("id");
        HttpServerResponse response = routingContext.response();
        if (userId != null){
            response.putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            JsonObject user = this.users.get(userId);
            response.end(user != null ? user.encodePrettily() : new JsonObject().encodePrettily());
        }else{
            response.setStatusCode(400).end();
        }
    }

    private void buildUsersList(){
        add(new JsonObject().put("id",UUID.randomUUID().toString()).put("name","John").put("email","john@example.org"));
        add(new JsonObject().put("id",UUID.randomUUID().toString()).put("name","Carl").put("email","carl@example.org"));
        add(new JsonObject().put("id",UUID.randomUUID().toString()).put("name","Mary").put("email","mary@example.org"));
    }

    private void add(JsonObject user){
        this.users.put(user.getString("id"), user);
    }

    private void logUsers(){
        users.keySet().stream().forEach(LOGGER::info);
    }

}

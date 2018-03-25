/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srebrinb.nosql.webstore;

import srebrinb.nosql.webstore.kv.NoSQLstore;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;

public class DocHandler {

    private final NoSQLstore store;

    DocHandler(NoSQLstore store) {
        this.store = store;
    }
    //post("/api/docs")

    void addOne(RoutingContext routingContext) throws FileNotFoundException, IOException {
//    final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(),
//        Whisky.class);
//
//    mongo.insert(COLLECTION, whisky.toJson(), r ->
//        routingContext.response()
//            .setStatusCode(201)
//            .putHeader("content-type", "application/json; charset=utf-8")
//            .end(Json.encodePrettily(whisky.setId(r.result()))));

        if (routingContext.request().getHeader("Content-Type").startsWith("multipart")) {
            Set<FileUpload> uploads = routingContext.fileUploads();
            for (FileUpload upload : uploads) {
                // System.out.println("upload = " + upload.fileName());
                String key = store.put(IOUtils.toByteArray(new FileInputStream(upload.uploadedFileName())));
                store.putMeta(key, "Content-Type", upload.contentType());
                store.putMeta(key, "FileName", upload.fileName());
            }
        } else {
            Buffer body = routingContext.getBody();

            String contentType = routingContext.request().getHeader("Content-Type");
            System.out.println("contentType = " + contentType);
            String key = store.put(routingContext.getBodyAsString().getBytes());
            store.putMeta(key, "Content-Type", contentType);
        }
        routingContext.response()
                .setStatusCode(200)
                .end("{\"ok\":true}");;
    }

    void getOne(RoutingContext routingContext) {
        byte[] res = null;
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
            return;
        } else {
//      mongo.findOne(COLLECTION, new JsonObject().put("_id", id), null, ar -> {
//        if (ar.succeeded()) {
//          if (ar.result() == null) {
//            routingContext.response().setStatusCode(404).end();
//            return;
//          }
//          Whisky whisky = new Whisky(ar.result());
//          routingContext.response()
//              .setStatusCode(200)
//              .putHeader("content-type", "application/json; charset=utf-8")
//              .end(Json.encodePrettily(whisky));
//        } else {
//          routingContext.response().setStatusCode(404).end();
//        }
//      });
            res = store.get(id);
        }
        if (res == null) {
            routingContext.response().setStatusCode(404).end();
            return;
        }
        routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(new String(res));
    }

    void updateOne(RoutingContext routingContext) {
//    final String id = routingContext.request().getParam("id");
//    JsonObject json = routingContext.getBodyAsJson();
//    if (id == null || json == null) {
//      routingContext.response().setStatusCode(400).end();
//    } else {
//      mongo.update(COLLECTION,
//          new JsonObject().put("_id", id), // Select a unique document
//          // The update syntax: {$set, the json object containing the fields to update}
//          new JsonObject()
//              .put("$set", json),
//          v -> {
//            if (v.failed()) {
//              routingContext.response().setStatusCode(404).end();
//            } else {
//              routingContext.response()
//                  .putHeader("content-type", "application/json; charset=utf-8")
//                  .end(Json.encodePrettily(new Whisky(id, json.getString("name"), json.getString("origin"))));
//            }
//          });
//    }
    }

    void deleteOne(RoutingContext routingContext) {
//    String id = routingContext.request().getParam("id");
//    if (id == null) {
//      routingContext.response().setStatusCode(400).end();
//    } else {
//      mongo.removeOne(COLLECTION, new JsonObject().put("_id", id),
//          ar -> routingContext.response().setStatusCode(204).end());
//    }
    }

    void getAll(RoutingContext routingContext) {
//    mongo.find(COLLECTION, new JsonObject(), results -> {
//      List<JsonObject> objects = results.result();
//      List<Whisky> whiskies = objects.stream().map(Whisky::new).collect(Collectors.toList());
//      routingContext.response()
//          .putHeader("content-type", "application/json; charset=utf-8")
//          .end(Json.encodePrettily(whiskies));
//    });
    }
}

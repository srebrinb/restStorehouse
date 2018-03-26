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
import static io.vertx.core.buffer.Buffer.buffer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

public class DocHandler {

    private final NoSQLstore store;

    DocHandler(NoSQLstore store) {
        this.store = store;
    }
    //post("/api/docs")

    void addOne(RoutingContext routingContext) {

        String jsonRest;

        if (routingContext.request().getHeader("Content-Type").startsWith("multipart")) {
            ArrayList listContents = new ArrayList();
            Set<FileUpload> uploads = routingContext.fileUploads();
            for (FileUpload upload : uploads) {
                try {
                    HashMap restContent = new HashMap();
                    // System.out.println("upload = " + upload.fileName());
                    FileInputStream os = new FileInputStream(upload.uploadedFileName());
                    byte[] contents=IOUtils.toByteArray(os);
                    os.close();
                    String key = store.put(contents);
                    store.putMeta(key, "Content-Type", upload.contentType());
                    store.putMeta(key, "FileName", upload.fileName());
                    restContent.put("Key", key);
                    restContent.put("Name", upload.fileName());
                    restContent.put("Size", upload.size());
                    listContents.add(restContent);
                    //Files.delete(upload.uploadedFileName());
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DocHandler.class.getName()).log(Level.SEVERE, null, ex);
                    routingContext.response()
                            .setStatusCode(200)
                            .end(Json.encodePrettily(ex));
                } catch (IOException ex) {
                    Logger.getLogger(DocHandler.class.getName()).log(Level.SEVERE, null, ex);
                    routingContext.response()
                            .setStatusCode(200)
                            .end(Json.encodePrettily(ex));
                }

            }
            jsonRest = Json.encodePrettily(listContents);
        } else {
            Buffer body = routingContext.getBody();
            String contentType = routingContext.request().getHeader("Content-Type");
            System.out.println("contentType = " + contentType);
            String key = store.put(routingContext.getBodyAsString().getBytes());
            store.putMeta(key, "Content-Type", contentType);
            HashMap restContent = new HashMap();
            restContent.put("Key", key);
            jsonRest = Json.encodePrettily(restContent);
        }
        routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(jsonRest);
    }

    void getOne(RoutingContext routingContext) {
        String contentType;
        byte[] res = null;
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
            return;
        } else {
            res = store.get(id);
            contentType=store.getMeta(id, "Content-Type");
        }
        if (res == null) {
            routingContext.response().setStatusCode(404).end();
            return;
        }
        Buffer buffer=Buffer.buffer(res);
        routingContext.response()
                .setStatusCode(200)
                .putHeader("content-type", contentType)
                .putHeader("Content-Length", ""+res.length)
                .write(buffer);
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

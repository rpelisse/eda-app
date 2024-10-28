package org.redhat;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

import java.lang.Thread;
import jakarta.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;

@ApplicationScoped
@Path("app")
public class Service {

    private static StringBuffer OOM = new StringBuffer("");
    private static boolean isDbPoolEmpty = false;

    @Resource(lookup = "java:jboss/datasources/ExampleDS")
    private DataSource ds;

    @GET
    @Path("hello")
    public String helloworld() {
        return "Hello World!";
    }

    @GET
    @Path("oom")
    public String oom() {
        for ( int i = 0; i < 100000000 ; i++ )
            OOM.append(new StringBuffer(1000000000));
        return "Generating an Out of Memory exception";
    }

    @POST
    @Path("toogleDbPool")
    public String toggleDbPool() {
        Service.isDbPoolEmpty = ! Service.isDbPoolEmpty;
        return "Db Pool state: " + Service.isDbPoolEmpty + "\n";
    }

    @GET
    @Path("dbState")
    public String dbState() {
        try {
            if ( ds == null ) throw new IllegalStateException("DS was not injected");
            Connection conn = ds.getConnection();
            Thread.sleep(120000);
            conn.close();
        } catch ( Exception e) {
            throw new IllegalStateException("Error while connecting to data source:" + e.getMessage(), e);
        }
        return "" + Service.isDbPoolEmpty + "\n";
    }
}

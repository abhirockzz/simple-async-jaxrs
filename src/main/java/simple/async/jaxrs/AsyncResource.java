/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple.async.jaxrs;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Response;

/**
 *
 * @author abhi
 */
@Path("async")
@Stateless
public class AsyncResource {
    
    @Resource
    ManagedExecutorService mes;

    @GET
    public void async(final @Suspended AsyncResponse ar) {
        
        ar.setTimeout(3, TimeUnit.SECONDS);
        
        ar.setTimeoutHandler(new TimeoutHandler() {
            @Override
            public void handleTimeout(AsyncResponse asyncResponse) {
                asyncResponse.resume(Response.accepted(UUID.randomUUID().toString()).build());
            }
        });
        
        final String initialThread = Thread.currentThread().getName();
        System.out.println(initialThread + " in action...");
        
        mes.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        String processingThread = Thread.currentThread().getName();
                        System.out.println("Processing thread: " + processingThread);

                        Thread.sleep(5000);
                        String respBody = "Process initated in " + initialThread + " and finished in " + processingThread;
                        ar.resume(Response.ok(respBody).build());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AsyncResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
       
        System.out.println(initialThread + " freed ...");
    }
}

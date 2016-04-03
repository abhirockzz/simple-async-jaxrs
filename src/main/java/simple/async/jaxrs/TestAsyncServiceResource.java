/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple.async.jaxrs;

import java.util.concurrent.Future;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 *
 * @author abhi
 */
@Path("test")
public class TestAsyncServiceResource {
    Client client;
    WebTarget target;
    
    @PostConstruct
    public void init(){
        client = ClientBuilder.newBuilder().build();
        target = client.target("http://localhost:8080/simple-async-jaxrs/async");
        System.out.println("Client & WebTarget ready");
                
    }
    
    @GET
    public Response test() throws Exception{
        long start = System.currentTimeMillis();
//        Response response = target
//                .request()
//                .get();
        Invocation.Builder reqBuilder = target.request();
        AsyncInvoker asyncInvoker = reqBuilder.async();
        
        Future<Response> futureResp = asyncInvoker.get();
        
        System.out.println("Elapsed..." + (System.currentTimeMillis() - start) +" millis");

        Response response = futureResp.get(); //blocks until client responds or times out
        
        System.out.println("Response HTTP status: "+response.getStatus());
        String responseBody = response.readEntity(String.class);
        System.out.println("Response from Async Service..." + responseBody);
        return Response.status(response.getStatus()).entity(responseBody).build();
    }
    
    @PreDestroy
    public void destroy(){
        client.close();
        System.out.println("Client closed...");
    }
}

package ljworker;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import ljworker.client.GrpcClient;
import ljworker.server.GrpcServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test client-server mutual TLS authentication. */
@RunWith(JUnit4.class)
public class AuthTest {
  /**
   * Test that the client and server are able to communicate. The client sends a
   * health check request, and the return status should be 'OK'.
   * 
   * @throws IOException
   * @throws InterruptedException
   */
  @Test
  public void authorizedHealthCheck() throws IOException, InterruptedException {
    GrpcServer server = new GrpcServer("sslcert/server.crt", "sslcert/server.pem", "sslcert/ca.crt");
    GrpcClient client = new GrpcClient("sslcert/client.crt", "sslcert/client.pem", "sslcert/ca.crt");

    // start client & server
    server.start();
    client.init();

    // send health check request, status should be 'OK'
    HealthCheckResponse resp = client.healthCheck();
    assertEquals("OK", resp.getStatus());

    // shutdown client & server
    server.stop();
    client.close();
  }

  /**
   * Test an invalid client certificate. This should result in the client unable
   * to communicate with the server and get a null response of the health check.
   */
  @Test
  public void unauthorizedHealthCheck() throws IOException, InterruptedException {
    // Replaced the client certificate with the server's certificate.
    GrpcServer server = new GrpcServer("sslcert/server.crt", "sslcert/server.pem", "sslcert/ca.crt");
    GrpcClient client = new GrpcClient("sslcert/server.crt", "sslcert/client.pem", "sslcert/ca.crt");

    // start client & server
    server.start();
    client.init();

    // send health check request, the client should not be able to connect to
    // the server and should return null.
    HealthCheckResponse resp = client.healthCheck();
    assertEquals(null, resp);

    // shutdown client & server
    server.stop();
    client.close();
  }
}

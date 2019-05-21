package ljworker.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;
import ljworker.LinuxJobServiceGrpc;
import ljworker.StartRequest;
import ljworker.StartResponse;
import ljworker.StopRequest;
import ljworker.StopResponse;
import ljworker.server.GrpcServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/** Test LinuxJobServiceImpl */
@RunWith(JUnit4.class)
public class LinuxJobServiceTest {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private GrpcServer server;
    private ManagedChannel channel;
    private LinuxJobServiceGrpc.LinuxJobServiceBlockingStub blockingStub;

    /**
     * Test start RPC.
     * 
     * Request 'stream echo test'. Should return: "[OUTPUT] test", "ExitValue: 0"
     */
    @Test
    public void startTest() {
        logger.info("Testing start RPC...");

        // create new Start Request
        StartRequest.Builder builder = StartRequest.newBuilder();
        builder.addArgs("echo")
                .addArgs("test");
        StartRequest request = builder.build();

        // write stream to output list
        List<String> outputList = new ArrayList<>();

        // send request
        Iterator<StartResponse> response;
        response = blockingStub.startStream(request);
        while (response.hasNext()) {

            for (String output : response.next()
                    .getOutputList()) {
                outputList.add(output);
            }
        }

        String[] expected = new String[] {"[OUTPUT] test", "ExitValue: 0"};
        String[] actual = outputList.toArray(new String[0]);

        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
        logger.info("Start RPC PASSED");
    }

    /**
     * Test stop RPC.
     * 
     * Request stop for a valid id should return a StopResponse with sucess=true
     * Request stop for a invalid id should return a StopResonse with success=false
     */
    @Test
    public void stopTest() {
        logger.info("Testing stop RPC...");

        // create new Start Request
        StartRequest.Builder startBuilder = StartRequest.newBuilder();
        startBuilder.addArgs("echo")
                .addArgs("test");
        StartRequest startRequest = startBuilder.build();
        blockingStub.start(startRequest);


        StopRequest.Builder stopBuilder = StopRequest.newBuilder();
        StopRequest stopRequest;
        StopResponse stopResponse;

        // 'Stop 1' should return success=true
        stopBuilder.setId(1);
        stopRequest = stopBuilder.build();
        stopResponse = blockingStub.stop(stopRequest);
        boolean success1 = stopResponse.getSuccess();
        assertTrue(success1);

        // 'Stop 2' should return success=false
        stopBuilder.setId(2);
        stopRequest = stopBuilder.build();
        stopResponse = blockingStub.stop(stopRequest);
        boolean success2 = stopResponse.getSuccess();
        assertFalse(success2);

        logger.info("Stop RPC PASSED");
    }

    /** Initialize server and open channel. */
    @Before
    public void beforeEachTest() throws InstantiationException, IllegalAccessException, IOException {
        server = new GrpcServer("sslcert/server.crt", "sslcert/server.pem", "sslcert/ca.crt");
        server.start();
        channel = NettyChannelBuilder.forAddress(HOST, PORT)
                // .usePlaintext()
                .negotiationType(NegotiationType.TLS)
                .sslContext(getSslContext("sslcert/client.crt", "sslcert/client.pem", "sslcert/ca.crt"))
                .build();
        blockingStub = LinuxJobServiceGrpc.newBlockingStub(channel);
    }

    /** Close channel and stop server. */
    @After
    public void afterEachTest() {
        channel.shutdownNow();
        server.stop();
    }

    private SslContext getSslContext(String certChainFilePath, String privateKeyFilePath,
            String trustCertCollectionFilePath) throws SSLException {
        File keyCertChainFile = new File(certChainFilePath);
        File keyFile = new File(privateKeyFilePath);
        File trustCertCollectionFile = new File(trustCertCollectionFilePath);

        SslContextBuilder builder = GrpcSslContexts.forClient();
        builder.trustManager(trustCertCollectionFile);
        builder.keyManager(keyCertChainFile, keyFile);
        return builder.build();
    }

}

import helpers.Util;
import fileIO.FileIO;
import org.junit.Test;
import peer.Peer;


import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Tests {

    @Test
    public void testBrokerLocationSplitting() {
        String location = "hostname:8080";
        AbstractMap.SimpleEntry<String, Integer> kv = Util.splitLocationString(location);

        assertArrayEquals(new String[]{"hostname", "8080"}, new String[]{kv.getKey(), kv.getValue().toString()});
    }

   /* @Test
    public void testDataRead() {
        String dataFilePath = "access.log";
        String topic = "topic";
        Queue<byte[]> messages = FileIO.readLogs(dataFilePath, topic, 0);

        assertEquals(messages.isEmpty(), false);
    }*/

    /*@Test
    public void testConnectAndClose() {

        Peer peer = new Peer("1", 8080, "");
        Peer peer2 = new Peer("2", 8081);

        peer.startAcceptingRequests();
        for (int i = 0; i < 1000; i++) {
            peer2.fillMessages(("hello" + i).getBytes(StandardCharsets.UTF_8));
        }

        peer2.startSending("127.0.0.1", 8080);
        for (int i = 0; i < 50; i++) {
            //wait
        }
        boolean close = peer.close();

        assertEquals(close, true);
    }*/

   /* @Test
    public void testConfigExtraction() {
        HostConfigModel hcm = Config.parseConfig("testconfig.json");

        String brokerLocation = hcm.getBrokerLocation();

        assertEquals(brokerLocation, "test");
    }*/







}
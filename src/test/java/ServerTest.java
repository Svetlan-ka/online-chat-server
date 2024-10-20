import org.example.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ServerTest {

    @Test
    public void getPort_numberPort() {
        int expected = 5000;
       int port = Server.getPort(new File("D:/Java/COURSE_PROJECTS", "settings.txt"));
       assertEquals(port, expected);
    }

}

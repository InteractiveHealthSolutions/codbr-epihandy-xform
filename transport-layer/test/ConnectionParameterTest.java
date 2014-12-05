import org.fcitmuk.communication.ConnectionParameter;

public class ConnectionParameterTest {
	
	public static void testit() {
		ConnectionParameter cp = new ConnectionParameter("n1", "v1");
		cp.addParam("n2", "v2");
		cp.addParam("n3", "v3");
		
		System.out.println(cp.getParams(";"));
	}
}

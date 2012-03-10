
public class Log {
	
	public static boolean enabled = false;

	public static void debug(Object caller, Object msg) {
		if (enabled) {
			println(caller, msg.toString());
		}
	}
	
	public static void error(Object caller, Object msg) {
		println(caller, msg.toString());
	}
	
	private static void println(Object caller, String msg) {
		String callerClass = caller.getClass().getName();
		System.out.println(callerClass + ": " + msg);
	}
}

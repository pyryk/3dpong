
public class Log {
	
	public static boolean enabled = false;

	public static void debug(Object caller, String msg) {
		if (enabled) {
			println(caller, msg);
		}
	}
	
	public static void error(Object caller, String msg) {
		println(caller, msg);
	}
	
	private static void println(Object caller, String msg) {
		String callerClass = caller.getClass().getName();
		System.out.println(callerClass + ": " + msg);
	}
}

import java.io.*;
import java.net.*;
public class SocketMessenger
{
	public static void main(String[] args) throws Exception
	{
		Socket s = new Socket("localhost", Integer.parseInt(args[0]));
		PrintWriter out = new PrintWriter(s.getOutputStream());
		for (int x = 1; x < args.length; x++)
		{
			out.print(args[x]);
			if (x!=args.length-1) out.print(" ");
		}
		out.print("\n#@~SocketManagerStatus DISCONNECTED");
		out.flush();
		out.close();
		s.close();
	}
}
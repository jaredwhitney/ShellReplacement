import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;
public class YoutubeSongInformationGrabber
{
	private static final String NO_URL_STR = "[no url found]";
	private static String sessionrestoreFolderPath;
	static
	{
		try
		{
			sessionrestoreFolderPath = new File(System.getenv("APPDATA") + "\\Mozilla\\Firefox\\Profiles").listFiles()[0].getAbsolutePath();
		}
		catch (Exception ex)
		{
			System.err.println("Could not find a firefox profile :(\n\tno profile exists at " + System.getenv("APPDATA") + "\\Mozilla\\Firefox\\Profiles");
		}
	}
	public static void main(String[] args) throws Exception
	{
		YoutubeVideoInformation nowPlaying = YoutubeSongInformationGrabber.getNowPlayingInfo();
		System.out.println("Now Playing: " + nowPlaying.niceName);
	}
	public static String getFirefoxTabBackup()
	{
		try
		{
			File f = new File(sessionrestoreFolderPath + "\\sessionstore-backups\\recovery.js.tmp");
			if (!f.exists()) f = new File(sessionrestoreFolderPath + "\\sessionstore-backups\\recovery.js");
			Scanner sc = new Scanner(new FileInputStream(f));

			String inp = sc.nextLine();
			while (sc.hasNext())
				inp += sc.nextLine();
			sc.close();
			
			return inp;	
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return "[Unable to create tab backup.]";
	}
	public static String cleanFirefoxTabBackup(String tabBackup)
	{
		return tabBackup.split("\\Q,\"_closedWindows\":\\E")[0];
	}
	public static String getRawName(String tabBackup)
	{
		String[] entries0 = tabBackup.split("\\Q],\"lastAccessed\":\\E");
		
		String[] entries1 = new String[entries0.length];
		for (int x = 0; x < entries0.length; x++)
		{
			String[] arr = entries0[x].split("\\Q\",\"title\":\"\\E");
			if (arr.length > 1)
				entries1[x] = arr[arr.length-1];
			else
				entries1[x] = "";
		}
		
		String[] entries2 = new String[entries1.length];
		for (int x = 0; x < entries1.length; x++)
		{
			entries2[x] = entries1[x].split("\\Q\",\"\\E")[0];
		}
		
		for (int x = 0; x < entries0.length; x++)
			if (entries2[x].indexOf(" - YouTube") != -1)
				return entries2[x];
		
		return "No song detected.";
	}
	public static String cleanRawName(String rawName)
	{
		String[] parts = rawName.split(" - ");
		if (parts.length == 2)
			return parts[0];
		else if (parts.length == 3)
			return parts[1] + " by " + parts[0];
		return "[" + rawName + "]";
	}
	public static String getURL(String tabBackup, String rawName)
	{
		if (tabBackup.indexOf(rawName)==-1)
		{
			System.err.println("Umm!~ Please make sure a correctly formatted tabBackup and rawName are being sent to getURL().." + rawName);
			return NO_URL_STR;
		}
		String[] arr = tabBackup.split("\\Q" + rawName + "\\E")[0].split("\\Q\"url\":\"\\E");
		return arr[arr.length-1].split("\\Q\",\"\\E")[0];
	}
	public static String getVideoID(String url)
	{
		return url.split("\\Q?v=\\E")[1].split("\\Q&\\E")[0];
	}
	/*public static String getVideoAuthor(String url)
	{
		return Util.httpsReadUrl(url).split("\\Q,\"author\":\"")[1].split("\\Q\"\\E")[0];
	}*/
	public static BufferedImage getVideoThumbnail(String videoId)
	{
		try
		{
			return ImageIO.read(new URL("http://img.youtube.com/vi/" + videoId + "/hqdefault.jpg"));
		}
		catch (Exception ex)
		{
			return new BufferedImage(100, 100, 3);
		}
	}
	public static YoutubeVideoInformation getNowPlayingInfo()
	{
		YoutubeVideoInformation ret = new YoutubeVideoInformation();
		ret._tabBackup = cleanFirefoxTabBackup(getFirefoxTabBackup());
		ret.rawName = getRawName(ret._tabBackup);
		ret.niceName = cleanRawName(ret.rawName);
		ret.url = getURL(ret._tabBackup, ret.rawName);
		if (ret.url==NO_URL_STR)
			return ret;
		ret.id = getVideoID(ret.url);
		ret.thumb = getVideoThumbnail(ret.id);
		return ret;
	}
	public static YoutubeVideoInformation getNowPlayingInfoNoThumbnail()
	{
		YoutubeVideoInformation ret = new YoutubeVideoInformation();
		ret._tabBackup = cleanFirefoxTabBackup(getFirefoxTabBackup());
		ret.rawName = getRawName(ret._tabBackup);
		ret.niceName = cleanRawName(ret.rawName);
		ret.url = getURL(ret._tabBackup, ret.rawName);
		if (ret.url==NO_URL_STR)
			return ret;
		ret.id = getVideoID(ret.url);
		return ret;
	}
}
class YoutubeVideoInformation
{
	String _tabBackup;
	String rawName;
	String niceName;
	String url;
	String id;
	String author;
	BufferedImage thumb;
}

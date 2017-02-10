import java.awt.*;
import javax.swing.*;
public class Win10YTSongNotifier implements Runnable
{
	static String lastSongID;
	public static void main(String[] args) throws InterruptedException
	{
		while (true)
		{
			YoutubeVideoInformation info = YoutubeSongInformationGrabber.getNowPlayingInfo();
			if (!info.id.equals(lastSongID))
			{
				//Win10Notification.notify("YT Music", "Now Playing: " + info.niceName);
				NowPlayingNotification.notify("Now Playing: " + info.niceName);
				lastSongID = info.id;
			}
			Thread.sleep(2000);
		}
	}
	public void run()
	{
		try
		{
			main(null);
		}
		catch(Exception ex){System.err.println("Error in YTSongNotifier");}
	}
}
class NowPlayingNotification extends JComponent implements Runnable
{
	int tic;
	JFrame frame;
	String msg = "";
	public NowPlayingNotification(String... text)
	{
		for (String s : text)
			msg += s + " ";
	}
	public static void main(String[] args)
	{
		notify("Now Playing: Faith by Third Eye Blind");
	}
	public static void notify(String... text)
	{
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		frame.setSize(1920, 1080);
		frame.setLocation(0, 0);
		frame.setAlwaysOnTop(true);
		frame.setFocusable(false);
		frame.setBackground(new Color(0, 0, 0, 0));
		frame.setDefaultCloseOperation(3);
		NowPlayingNotification noti = new NowPlayingNotification(text);
		frame.add(noti);
		noti.frame = frame;
		new Thread(noti).start();
		noti.setOpaque(false);
		frame.setVisible(true);
	}
	public void paint(Graphics g)
	{
		g.setColor(new Color(0, 0, 0, 120));
		if (tic < 1080)
			g.fillRect(Math.max(1920-tic*10,0), 1080/5, 1920, 32);
		else
			g.fillRect(0, 1080/5, Math.max(1920-(tic-1080)*10,0), 32);
		g.setColor(Color.WHITE);
		matchFontSize((Graphics2D)g, 28);
		g.drawString(msg, 1920-(tic-180)*2, 1080/5+28);
	}
	public void run()
	{
		while (tic < 1080+192)
		{
			try{Thread.sleep(10);}catch(Exception e){}
			tic++;
			frame.repaint();
		}
		frame.setVisible(false);
	}
	public static void matchFontSize(Graphics2D g2, float desiredSize)
	{
		Font f = g2.getFont();
		float size = f.getSize2D();
		if (g2.getFontMetrics(g2.getFont()).getHeight() > desiredSize)
			for (; g2.getFontMetrics(g2.getFont()).getHeight() > desiredSize; size -= 0.5f)
				g2.setFont(f.deriveFont(size));
		else
			for (; g2.getFontMetrics(g2.getFont()).getHeight() < desiredSize; size += 0.5f)
				g2.setFont(f.deriveFont(size));
	}
}
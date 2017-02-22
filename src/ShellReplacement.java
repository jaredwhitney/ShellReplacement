import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import java.text.*;
import javax.swing.filechooser.*;
import sun.awt.shell.*;
public class ShellReplacement extends JPanel
{
	
	static JFrame altTabFrame, commandLineFrame, volumeNotificationFrame, infoPanelFrame, taskbarFrame;
	
	static AltTabMenuCanvas altTabCanvas;
	static CommandLineCanvas commandLineCanvas;
	static VolumeNotificationCanvas volumeNotificationCanvas;
	static InfoPanelCanvas infoPanelCanvas;
	static TaskbarCanvas taskbarCanvas;
	
	static int screenHeight, screenWidth;
	
	public static void main(String[] args) throws Exception
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenHeight = screenSize.height;
		screenWidth = screenSize.width;
		
		JFrame frame = new JFrame();
		frame.setSize(screenWidth, screenHeight);
		frame.setLocation(0, 0);
		frame.setUndecorated(true);
		frame.setFocusableWindowState(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.add(new ShellReplacement());
		frame.setVisible(true);
		frame.toBack();
		
		Runtime.getRuntime().exec("taskkill /F /FI \"IMAGENAME eq explorer.exe\"");
		Runtime.getRuntime().exec("..\\exe\\redirecthotkeys");
	/*	Runnable runnable = new Win10YTSongNotifier();
		new Thread(runnable).start();	*/
		
		frame.invalidate();
		frame.repaint();
		
		ServerSocket server = new ServerSocket(40902);
		while (true)
		{
			Socket connection = server.accept();
			Scanner in = new Scanner(connection.getInputStream());
			String input = "";
			while (!input.equals("#@~SocketManagerStatus DISCONNECTED"))
			{
				if (in.hasNext())
				{
					input = in.nextLine();
					if (input.equals("#@~SocketManagerStatus DISCONNECTED"))
						continue;
					if (input.equals("KEYCOMBO ALT_TAB"))
					{
						System.out.println("caught alt-tab");
						altTabCanvas.menuElements.clear();
						Runtime.getRuntime().exec("..\\exe\\sendwindowlist");
						showAltTabMenu();
					}
					else if (input.equals("KEYCOMBO ALT_SHIFT_TAB"))
					{
						System.out.println("caught alt-shift-tab");
						altTabCanvas.menuElements.clear();
						Runtime.getRuntime().exec("..\\exe\\sendwindowlist");
						showAltTabMenu();
					}
					else if (input.equals("KEYCOMBO WINDOWS"))
					{
						System.out.println("caught windows button");
						if (!commandLineFrame.isVisible())
							showCommandLine();
						else
							commandLineFrame.setVisible(false);
					}
					else if (input.equals("KEYCOMBO VOL_UP"))
					{
						System.out.println("Raise volume.");
						showVolumeNotification(VolumeNotificationCanvas.VOLUME_UP);
						Runtime.getRuntime().exec("..\\exe\\raisevolume");
					}
					else if (input.equals("KEYCOMBO VOL_DOWN"))
					{
						System.out.println("Lower volume.");
						showVolumeNotification(VolumeNotificationCanvas.VOLUME_DOWN);
						Runtime.getRuntime().exec("..\\exe\\lowervolume");
					}
					else if (input.equals("KEYCOMBO R_WIN_DOWN"))
					{
						System.out.println("Show info panel...");
						infoPanelFrame.setVisible(true);
						new Thread(infoPanelCanvas).start();
					}
					else if (input.equals("KEYCOMBO R_WIN_UP"))
					{
						System.out.println("Hide info panel.");
						infoPanelFrame.setVisible(false);
					}
					else if (input.split(" ")[0].equals("WINDOWLIST"))
					{
						String payload = input.replaceAll("WINDOWLIST ","");
						if (payload.equals("~~~"))
							continue;
						String name = payload.split("\\Q~~~\\E")[0];
						if (name.equals("")||name.equals("WINDOWLIST")||name.equals("NVIDIA GeForce Overlay")||name.equals("puush")||name.equals("Settings")||name.equals("Movies & TV")||name.equals("Calculator")||name.equals("Alarms & Clock"))
							continue;
						String exeName;
						try
						{
							exeName = input.replaceAll("WINDOWLIST ","").split("\\Q~~~\\E")[1];
						}
						catch (ArrayIndexOutOfBoundsException ex)
						{
							exeName = "[unknown]";
						}
						System.out.println("'" + name + "'");
						altTabCanvas.menuElements.add(new AltTabMenuElement(name, exeName));
						continue;
					}
					else if (input.split(" ")[0].equals("NETLIST"))
					{
						String payload = input.replaceAll("NETLIST ", "");
						String[] splits = payload.split(" ");
						if (TaskbarCanvas.wifimenu.items.size()==0)
							if (!splits[0].equals("CURRENT"))
								TaskbarCanvas.wifimenu.items.add("Not connected to a network.");
						if (splits.length > 1)	// otherwise it was a blank entry
							TaskbarCanvas.wifimenu.items.add(splits[1]);
					}
				}
				
				Thread.sleep(100);
				frame.repaint();
				if (altTabFrame.isVisible())
				{
					altTabFrame.repaint();
					altTabFrame.requestFocus();
				}
				if (commandLineFrame.isVisible())
					commandLineFrame.repaint();
				if (volumeNotificationFrame.isVisible())
					volumeNotificationFrame.repaint();
			}
			in.close();
			connection.close();
		}
		
	}
	public ShellReplacement()
	{
	/*	setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.DARK_GRAY);
		add(BorderLayout.SOUTH, buttonPanel);
		
		TaskbarButton button2 = new TaskbarButton();
		button2.setIcon("..\\img\\folder-icon.png");
		button2.action = "javaw FileViewer";
		buttonPanel.add(button2);
	*/
		altTabFrame = new JFrame();
		altTabFrame.setUndecorated(true);
		altTabFrame.setAlwaysOnTop(true);
		altTabCanvas = new AltTabMenuCanvas();
		altTabFrame.add(altTabCanvas);
		altTabFrame.pack();
		altTabFrame.setLocation(screenWidth/2-altTabFrame.getWidth()/2, screenHeight/2-altTabFrame.getHeight()/2);
		
		
		commandLineFrame = new JFrame();
		commandLineFrame.setUndecorated(true);
		commandLineFrame.setAlwaysOnTop(true);
		commandLineCanvas = new CommandLineCanvas();
		commandLineFrame.add(commandLineCanvas);
		
		JPanel commandLineButtonPanel = new JPanel();
		commandLineButtonPanel.setBackground(Color.DARK_GRAY);
		commandLineFrame.add(BorderLayout.EAST, commandLineButtonPanel);
		
		TaskbarButton clbutton2 = new TaskbarButton();
		clbutton2.setIcon("..\\img\\folder-icon.png");
		clbutton2.action = "javaw FileViewer";
		clbutton2.closeWindow = true;
		commandLineButtonPanel.add(clbutton2);
		
		commandLineFrame.pack();
		commandLineFrame.setLocation(screenWidth/2-commandLineFrame.getWidth()/2, screenHeight-20-commandLineFrame.getHeight());
		
		
		volumeNotificationFrame = new JFrame();
		volumeNotificationFrame.setUndecorated(true);
		volumeNotificationFrame.setAlwaysOnTop(true);
		volumeNotificationFrame.setFocusable(false);
		volumeNotificationFrame.setFocusableWindowState(false);
		volumeNotificationCanvas = new VolumeNotificationCanvas();
		volumeNotificationFrame.add(volumeNotificationCanvas);
		volumeNotificationFrame.pack();
		volumeNotificationFrame.setLocation(screenWidth/2-volumeNotificationFrame.getWidth()/2, screenHeight-20-commandLineFrame.getHeight()-20-volumeNotificationFrame.getWidth());
		volumeNotificationFrame.setOpacity(0.6f);
		
		infoPanelFrame = new JFrame();
		infoPanelFrame.setUndecorated(true);
		infoPanelFrame.setAlwaysOnTop(true);
		infoPanelFrame.setFocusable(false);
		infoPanelFrame.setFocusableWindowState(false);
		infoPanelCanvas = new InfoPanelCanvas();
		infoPanelFrame.add(infoPanelCanvas);
		infoPanelFrame.pack();
		infoPanelFrame.setLocation(0, 0);
		infoPanelFrame.setOpacity(0.9f);
		
		taskbarFrame = new JFrame();
		taskbarFrame.setUndecorated(true);
		taskbarFrame.setAlwaysOnTop(true);
		taskbarFrame.setFocusable(false);
		taskbarFrame.setFocusableWindowState(false);
		taskbarFrame.setLocation(0, screenHeight - TaskbarCanvas.TASKBAR_HEIGHT);
		taskbarCanvas = new TaskbarCanvas();
		taskbarFrame.add(taskbarCanvas);
		taskbarFrame.pack();
		taskbarFrame.setOpacity(0.9f);
		new Thread(taskbarCanvas).start();
		
	}
	public static void showAltTabMenu()
	{
		if (!altTabFrame.isVisible())
			altTabFrame.setVisible(true);
		altTabFrame.requestFocus();
	}
	public static void showCommandLine()
	{
		commandLineCanvas.setText("");
		commandLineFrame.setVisible(true);
	}
	public static void showVolumeNotification(int type)
	{
		volumeNotificationCanvas.type = type;
		volumeNotificationCanvas.timer.restart();
		if (!volumeNotificationFrame.isVisible())
			volumeNotificationFrame.setVisible(true);
	}
	public static void pullToTop(String windowTitle) throws Exception
	{
		Runtime.getRuntime().exec("..\\exe\\pulltotop \"" + windowTitle + "\"");
	}
	public static void closeWindow(String windowTitle) throws Exception
	{
		Runtime.getRuntime().exec("..\\exe\\closewin \"" + windowTitle + "\"");
	}
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
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
class TaskbarButton extends JComponent
{
	BufferedImage icon;
	String action;
	boolean closeWindow = false;
	public TaskbarButton(){}
	public void setIcon(String str)
	{
		try
		{
			icon = ImageIO.read(new File(str));
		}
		catch(Exception ex){}
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					if (closeWindow)
						SwingUtilities.windowForComponent(e.getComponent()).setVisible(false);
					Runtime.getRuntime().exec(action);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}});
	}
	public Dimension getPreferredSize()
	{
		return new Dimension(60, 60);
	}
	public void paintComponent(Graphics g)
	{
		if (icon!=null)
		{
			g.drawImage(icon, 0, 0, 60, 60, null);
		}
		else
		{
			g.setColor(Color.PINK);
			g.fillRect(0, 0, 60, 60);
		}
	}
}
class AltTabMenuCanvas extends JComponent
{
	static AltTabMenuCanvas instance;
	CopyOnWriteArrayList<AltTabMenuElement> menuElements = new CopyOnWriteArrayList<AltTabMenuElement>();
	int textHeight = 24;
	public AltTabMenuCanvas()
	{
		instance = this;
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					System.out.println(e.getY()/textHeight);
					ShellReplacement.altTabFrame.setVisible(false);
					if(e.getX()>instance.getWidth()-5-textHeight+5&&e.getX()<instance.getWidth()-5-textHeight+5+textHeight-5)
						ShellReplacement.closeWindow(instance.menuElements.get(e.getY()/textHeight).name);
					else
						ShellReplacement.pullToTop(instance.menuElements.get(e.getY()/textHeight).name);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}});
		ShellReplacement.altTabFrame.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent ev)
			{
				try
				{
					if (ShellReplacement.altTabFrame.isVisible()&&ev.getKeyCode()==KeyEvent.VK_ALT)
					{
						ShellReplacement.altTabFrame.setVisible(false);
						while(AltTabMenuCanvas.instance.menuElements.size()<=1)
							Thread.sleep(10);
						ShellReplacement.pullToTop(AltTabMenuCanvas.instance.menuElements.get(1).name);
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}});
	}
	public Dimension getPreferredSize()
	{
		return new Dimension(150*4, 80*4);
	}
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D)g;
		textHeight = g2.getFontMetrics().getHeight();
		int y = 0;
		if (menuElements.size()==0)
			g2.drawString("Waiting on window list...", 0, y += textHeight);
		else
			for (AltTabMenuElement es : menuElements)
			{
				String s = es.name;
				g.setColor(Color.RED);
				g.fillRect(getWidth()-5-textHeight+5, y+2, textHeight-5, textHeight-5);
				g.drawImage(es.icon, 0, y+2, null);
				g.setColor(Color.WHITE);
				g2.drawString(s, es.icon.getWidth()+5, (y += textHeight));
			}
	}
}
class AltTabMenuElement
{
	String name;
	BufferedImage icon;
	static BufferedImage folderico = null, missingico = null;
	private static Map<String, BufferedImage> localImages = new HashMap<String, BufferedImage>();
	static
	{
		try
		{
			folderico = ImageIO.read(new File("..\\img\\folder-icon.png"));
			missingico = ImageIO.read(new File("..\\img\\missing-icon.png"));
		}
		catch (IOException ex)
		{
			System.err.println("Unable to load builtin alt-tab exe icons...");
		}
	}
	public AltTabMenuElement(String name, String exePath)
	{
		this.name = name;
		int size = ShellReplacement.altTabCanvas.textHeight;
		this.icon = localImages.get(exePath);
		if (this.icon == null)
		{
			this.icon = new BufferedImage(size, size, 3);
			Image img = null;
			File f = new File(exePath);
			try
			{
				if (((exePath.contains("java.exe")||exePath.contains("javaw.exe")))&&name.contains(" - FileViewer"))
					img = folderico;
				else
				{
					img = ShellFolder.getShellFolder(f).getIcon(true);
				}
			}
			catch (Exception ex)
			{
				Icon icon = FileSystemView.getFileSystemView().getSystemIcon(f);
				if (icon==null)
					img = missingico;
				else
					img = ((ImageIcon)icon).getImage();
			}
			finally
			{
				icon.getGraphics().drawImage(img, 0, 0, size, size, null);
				localImages.put(exePath, icon);
			}
		}
	}
}
class CommandLineCanvas extends JTextArea
{
	static CommandLineCanvas instance;
	static String selectionText = "cmd.exe";
	public CommandLineCanvas()
	{
		instance = this;
		addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
						ShellReplacement.commandLineFrame.setVisible(false);
					else if (e.getKeyCode()!=KeyEvent.VK_ENTER)
					{
						((Component)e.getSource()).getParent().dispatchEvent(e);
					}
					else
					{
						try
						{
							e.consume();
							ShellReplacement.commandLineFrame.setVisible(false);
							if (!instance.getText().equals(""))
								Runtime.getRuntime().exec("cmd /c start " + instance.getText());
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}
			});
		Font f = getFont();
		float size = f.getSize2D();
		for (; getFontMetrics(getFont()).getHeight() < getPreferredSize().height; size += 0.5f)
			setFont(f.deriveFont(size));
		setBackground(Color.DARK_GRAY);
		setForeground(Color.WHITE);
		setFocusable(true);
		requestFocusInWindow();
	}
	public Dimension getPreferredSize()
	{
		return new Dimension(150*4, 80);
	}
}
class VolumeNotificationCanvas extends JComponent
{
	static final int VOLUME_UP = 0;
	static final int VOLUME_DOWN = 1;
	int type;
	javax.swing.Timer timer = new javax.swing.Timer(1000, new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
			ShellReplacement.volumeNotificationFrame.setVisible(false);
		}});
	public Dimension getPreferredSize()
	{
		return new Dimension(200, 200);
	}
	public void paint(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D)g;
		if (type==VOLUME_UP)
			g2.drawPolygon(new int[]{getWidth()/3,2*getWidth()/3,getWidth()/2}, new int[]{2*getHeight()/3,2*getHeight()/3,getHeight()/3}, 3);
		else if (type==VOLUME_DOWN)
			g2.drawPolygon(new int[]{getWidth()/3,2*getWidth()/3,getWidth()/2}, new int[]{getHeight()/3,getHeight()/3,2*getHeight()/3}, 3);
	}
}
class InfoPanelCanvas extends JComponent implements Runnable
{
	private static String lastID, lastName = "Fetching song data...";
	private static BufferedImage lastThumb;
	public Dimension getPreferredSize()
	{
		return new Dimension(ShellReplacement.screenWidth, ShellReplacement.screenHeight);
	}
	public void paint(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		ShellReplacement.matchFontSize(g2, 240);
		DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
		Date date = new Date();
		String dateString = dateFormat.format(date);
		g2.drawString(dateString, getWidth()/2-g2.getFontMetrics(g2.getFont()).stringWidth(dateString)/2, getHeight()/3);
		
		if (lastThumb != null)
			g2.drawImage(lastThumb, getWidth()/2-lastThumb.getWidth()/2, getHeight()-lastThumb.getHeight(), null);
		
		ShellReplacement.matchFontSize(g2, 60);
		String nowplaying = "Now Playing: " + lastName;
		if (lastThumb!=null)
			g2.drawString(nowplaying, getWidth()/2-g2.getFontMetrics(g2.getFont()).stringWidth(nowplaying)/2, getHeight()-20-lastThumb.getHeight());
	}
	public void run()
	{
		while (ShellReplacement.infoPanelFrame.isVisible())
		{
			YoutubeVideoInformation nowPlaying = YoutubeSongInformationGrabber.getNowPlayingInfoNoThumbnail();
			if (nowPlaying.id.equals(lastID))
			{
				nowPlaying.thumb = lastThumb;
			}
			else
			{
				nowPlaying.thumb = YoutubeSongInformationGrabber.getVideoThumbnail(nowPlaying.id);
				lastThumb = nowPlaying.thumb;
				lastID = nowPlaying.id;
				lastName = nowPlaying.niceName;
			}
			ShellReplacement.infoPanelFrame.repaint();
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ex){}
		}
	}
}
class TaskbarCanvas extends JComponent implements Runnable
{
	static final int TASKBAR_HEIGHT = 50;
	static int lmy, my;
	static BufferedImage wifiico = null;
	final int wifiicox = getPreferredSize().width-(TASKBAR_HEIGHT-4)-2;
	final int wifiicosize = TASKBAR_HEIGHT-4;
	static TaskbarSubmenu wifimenu;
	static ArrayList<String> networks;
	static String currentNetwork;
	static
	{
		try
		{
			wifiico = ImageIO.read(new File("..\\img\\wifi.png"));
		}
		catch (IOException ex)
		{
			System.err.println("Unable to load builtin taskbar icons...");
		}
	}
	public TaskbarCanvas()
	{
		addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent ev)
			{
				if (wifimenu.isVisible() && !wifimenu.boundKeepOpen(ev.getX(), ev.getY()))
					wifimenu.setVisible(false);
				else if (!wifimenu.isVisible() && ev.getX() > wifiicox && ev.getX() < wifiicox+wifiicosize && ev.getY() > 2 && ev.getY() < 2+wifiicosize)
					wifimenu.mshow();
			}
		});
		wifimenu = new TaskbarSubmenu(Math.min(wifiicox, getPreferredSize().width-200), ShellReplacement.taskbarFrame.getLocation().y-500, 200, 500);
		wifimenu.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent ev)
			{
				int entry = ev.getY()/(wifimenu.fontheight+2);
				if (entry > wifimenu.items.size())
					return;
				String SSID = wifimenu.items.get(entry);
				String currentSSID = wifimenu.items.get(0);
				if (SSID.equalsIgnoreCase(currentSSID))
					return;
				try
				{
					Runtime.getRuntime().exec("netconnect.bat " + SSID).waitFor();
					Thread.sleep(750);
				}
				catch(Exception ex){}
				TaskbarCanvas.wifimenu.items.clear();
				try
				{
					Runtime.getRuntime().exec("net.bat");
				}
				catch(IOException ex){}
			}
		});
	}
	public Dimension getPreferredSize()
	{
		return new Dimension(ShellReplacement.screenWidth, TASKBAR_HEIGHT);
	}
	public void paint(Graphics g)
	{
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(wifiico, wifiicox, 2, wifiicosize, wifiicosize, null);
	}
	public void run()
	{
		while (true)
		{
			int mx = MouseInfo.getPointerInfo().getLocation().x;
			my = MouseInfo.getPointerInfo().getLocation().y;
			if (ShellReplacement.taskbarFrame.isVisible())
			{
				if (wifimenu.isVisible())
				{
					wifimenu.repaint();
				}
				if (my < ShellReplacement.screenHeight-TASKBAR_HEIGHT && !wifimenu.boundKeepOpen(mx, my))
				{
					ShellReplacement.taskbarFrame.setVisible(false);
					wifimenu.setVisible(false);
				}
				ShellReplacement.taskbarFrame.repaint();
			}
			else if (my == ShellReplacement.screenHeight-1 && lmy == ShellReplacement.screenHeight-1)
			{
				ShellReplacement.taskbarFrame.setVisible(true);
				TaskbarCanvas.wifimenu.items.clear();
				try
				{
					Runtime.getRuntime().exec("net.bat");
				}
				catch(IOException ex){}
			}
			lmy = my;
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException ex){}
		}
	}
}
class TaskbarSubmenu extends JFrame
{
	ArrayList<String> items = new ArrayList<String>();
	int x, y, w, h, fontheight;
	boolean isOpen;
	BufferedImage buffer;
	public TaskbarSubmenu(int x, int y, int w, int h)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		setUndecorated(true);
		setAlwaysOnTop(true);
		setFocusable(false);
		setFocusableWindowState(false);
		setLocation(x, y);
		setSize(w, h);
		setOpacity(0.9f);
		buffer = new BufferedImage(w, h, 3);
	}
	public void mshow()
	{
		isOpen = true;
		setVisible(true);
	}
	public void paint(Graphics g)
	{
		Graphics gx = buffer.getGraphics();
		gx.setColor(Color.DARK_GRAY);
		gx.fillRect(0, 0, getWidth(), getHeight());
		gx.setColor(Color.WHITE);
		gx.drawLine(0, fontheight+3, getWidth(), fontheight+3);
		gx.setColor(Color.WHITE);
		fontheight = gx.getFontMetrics(g.getFont()).getHeight();
		int qy = fontheight + 2;
		for (String s : items)
		{
			gx.drawString(s, 0, qy);
			qy += fontheight + 2;
		}
		g.drawImage(buffer, 0, 0, null);
	}
	public boolean boundKeepOpen(int mx, int my)
	{
		return (isVisible() && mx > x && mx < x+w && my > y && my < y+h);
	}
}

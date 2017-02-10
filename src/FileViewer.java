import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.filechooser.*;
import sun.awt.shell.*;
public class FileViewer extends JComponent
{
	static JFrame fileViewerFrame;
	static FileViewer fileViewer;
	static String file = "C:\\Users\\";
	static boolean inDrag = false;
	static int lastx, lasty;
	BufferedImage folderIcon;
	int offset = 0;
	public static void main(String[] args) throws Exception
	{
		fileViewerFrame = new JFrame();
		fileViewerFrame.setUndecorated(true);
		fileViewerFrame.setTitle(file + " - " + "FileViewer");
		FileViewer fileViewer = new FileViewer();
		fileViewerFrame.add(fileViewer);
		fileViewerFrame.pack();
		fileViewerFrame.setLocation(1920/2-fileViewerFrame.getWidth()/2, 1080/2-fileViewerFrame.getHeight()/2);

		// external only
		fileViewerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fileViewerFrame.setVisible(true);
		
		BitmapFileCacher.invalidate();
		
	}
	public FileViewer()
	{
		try
		{
			folderIcon = ImageIO.read(new File("..\\img\\folder-icon.png"));
			addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)
				{
					if (e.getY()<20)
					{
						if (e.getX()<20)
						{
							file.replaceAll("\\Q/\\E","\\");
							String[] fileparts = file.split("\\Q\\\\E");
							if (fileparts.length >= 2)
							{
								file = "";
								for (int s = 0; s < fileparts.length-1; s++)
									file += fileparts[s] + "\\";
								file = file.substring(0, file.length()-1);
								if (file.charAt(file.length()-1)==':')
									file += "\\";
								offset = 0;
								BitmapFileCacher.invalidate();
								fileViewerFrame.setTitle(file + " - " + "FileViewer");
							}
						}
						else if (e.getX()>getWidth()-20)
						{
							System.exit(0);
						}
					}
					else
					{
						int target = getWidth()/(85)*((e.getY()-20)/(85+20+5))+((e.getX()-20)/(85))+getWidth()/(85)*offset;
						int x=0;
						boolean targetFound = false;
						for (File f : new File(file).listFiles())	// there's a better way to do this...
						{
							if (x==target)
							{
								String[] arr = f.getName().split("\\Q.\\E");
								String type = arr[arr.length-1].toLowerCase();
					//			if (e.getButton()==MouseEvent.BUTTON_1)
					//			{
									if (f.isDirectory())
									{
										file = f.getAbsolutePath();
										offset = 0;
										BitmapFileCacher.invalidate();
										fileViewerFrame.setTitle(file + " - " + "FileViewer");
									}
									else if (type.equals("png")||type.equals("bmp")||type.equals("jpg")||type.equals("gif")||type.equals("lsimg"))
									{
										try
										{
											Runtime.getRuntime().exec("javaw ImageViewer \"" + f.getAbsolutePath() + "\"");
										}
										catch (Exception ex){ex.printStackTrace();}
									}
									else
										try
										{
											Desktop.getDesktop().open(f);
										}
										catch (Exception ex){ex.printStackTrace();}
					//			}
					/*			else if (e.getButton()==MouseEvent.BUTTON_2)
								{
									if (f.isDirectory())
										showMenu(e, "Open", "Rename", "Cut", "Copy", "Paste Here", "Delete");
									else
										showMenu(e, "Open", "Rename", "Cut", "Copy", "Delete");
								}
					*/			targetFound = true;
								break;
							}
							x++;
						}
					/*	if (!targetFound)
						{
							if (e.getButton()==MouseEvent.BUTTON_2)
								showMenu(e, "New...", "Paste");
						}
					*/}
					fileViewerFrame.repaint();
				}
				public void mousePressed(MouseEvent e)
				{
					if (inDrag)
						return;
					if (e.getY()<20)
					{
						if (!(e.getX()<20 || e.getX()>getWidth()-20))
						{
							inDrag = true;
							lastx = e.getX();
							lasty = e.getY();
						}
					}
				}
				public void mouseReleased(MouseEvent e)
				{
					inDrag = false;
				}
			});
			addMouseWheelListener(new MouseWheelAdapter()
			{
				public void mouseWheelMoved(MouseWheelEvent e)
				{
					int notches = e.getWheelRotation();
					offset = Math.min(Math.max(offset+notches, 0), new File(file).listFiles().length/(getWidth()/(85)));
					fileViewerFrame.repaint();
				}
			});
			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseDragged(MouseEvent e)
				{
					if (inDrag)
					{
						Point loc = fileViewerFrame.getLocation();
						fileViewerFrame.setLocation(loc.x+e.getX()-lastx, loc.y+e.getY()-lasty);
					}
				}
			});
		}
		catch (Exception ex)
		{
			System.exit(-20);
		}
	}
	public Dimension getPreferredSize()
	{
		return new Dimension(1000, 600);
	}
/*	public void showMenu(MouseEvent e, String... options)
	{
		menu = new Menu(e.getX(), e.getY(), options);
		new Thread(menu).start();
	}
*/	public void paint(Graphics g)
	{
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.DARK_GRAY.darker());
		g.fillRect(0, 0, getWidth(), 20);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 20, 20);
		g.setColor(Color.RED);
		g.fillRect(getWidth()-20, 0, 20, 20);
		int x = 20;
		int y = 20;
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D)g;
		ShellReplacement.matchFontSize(g2, 20);
		g.drawString(file, 25, 15);
		int q = 0;
		for (File f : new File(file).listFiles())
		{
			q++;
			if (q <= getWidth()/(85)*offset)
				continue;
			String name = f.getName();
			if (g2.getFontMetrics(g2.getFont()).stringWidth(name) > 80)
			{
				while (g2.getFontMetrics(g2.getFont()).stringWidth(name+"...") > 80)
					name = name.substring(0, name.length()-1);
				name += "...";
			}
			g.drawString(name, x+80/2-g2.getFontMetrics(g2.getFont()).stringWidth(name)/2, y+85+g2.getFontMetrics(g2.getFont()).getHeight()/2);
			if (f.isDirectory())
			{
				g.drawImage(folderIcon, x, y, 80, 80, null);
			}
			else
			{
				String[] arr = f.getName().split("\\Q.\\E");
				String type = arr[arr.length-1].toLowerCase();
				if (type.equals("png")||type.equals("bmp")||type.equals("jpg")||type.equals("gif")||type.equals("lsimg"))
				{
					try
					{
						BufferedImage img = BitmapFileCacher.getLocalCopy(f.getAbsolutePath());
						if (img==null)
						{
							g.setColor(Color.BLUE);
							g.fillRect(x, y, 80, 80);
							g.setColor(Color.WHITE);
						}
						else
						{
							g.drawImage(img, x, y, null);
						}
					}
					catch (Exception ex){}
				}
				else
				{
					Image img;
					try
					{
						img = ShellFolder.getShellFolder(f).getIcon(true);
					}
					catch (Exception ex)
					{
						Icon icon = FileSystemView.getFileSystemView().getSystemIcon(f);
						img = ((ImageIcon)icon).getImage();
					}
					g.drawImage(img, x+10, y+10, 60, 60, null);
				}
			}
			x += 85;
			if (x+85+20 > getWidth())
			{
				x = 20;
				y += 85+20+5;
			}
			if (y+85+20 > getHeight())
				break;
		}
	}
}
class CacheLoader implements Runnable
{
	public void run()
	{
		for (File f : new File(FileViewer.file).listFiles())
		{
			String[] arr = f.getName().split("\\Q.\\E");
			String type = arr[arr.length-1].toLowerCase();
			if (type.equals("png")||type.equals("bmp")||type.equals("jpg")||type.equals("gif")||type.equals("lsimg"))
			{
				BitmapFileCacher.load(f.getAbsolutePath());
				System.out.println("Precache " + f.getName());
				FileViewer.fileViewerFrame.repaint();
			}
		}
	}
}
class MouseWheelAdapter implements MouseWheelListener
{
	public void mouseWheelMoved(MouseWheelEvent e){}
}
class BitmapFileCacher
{
	private static Map<String, BufferedImage> storage = new HashMap<String, BufferedImage>();
	static Thread t;
	static public void invalidate()
	{
		try
		{
			if (t!=null)
				t.stop();
			storage.clear();
			t = new Thread(new CacheLoader());
			t.start();
		}
		catch (Exception e){storage.clear();}
	}
	static public synchronized BufferedImage load(String fullName)
	{
		BufferedImage storedImage = storage.get(fullName);
		if (storedImage!=null)
			return storedImage;
		try
		{
			BufferedImage img;
			if (fullName.substring(fullName.lastIndexOf(".")).equalsIgnoreCase(".lsimg"))
				img = LossyImageCompressionReader.readFile(fullName);
			else
				img = ImageIO.read(new File(fullName));
			BufferedImage thumb = new BufferedImage(80, 80, 3);
			Graphics g = thumb.getGraphics();
			if (img.getWidth()>=img.getHeight())
			{
				int height = (int)(80f/img.getWidth()*img.getHeight());
				g.drawImage(img, 0, 80/2-height/2, 80, height, null);
			}
			else
			{
				int width = (int)(80f/img.getHeight()*img.getWidth());
				g.drawImage(img, 80/2-width/2, 0, width, 80, null);
			}
			storage.put(fullName, thumb);
			return thumb;
		}
		catch(Exception e)
		{
			return new BufferedImage(1, 1, 3);
		}
	}
	static public BufferedImage getLocalCopy(String fullName)
	{
		return storage.get(fullName);
	}
}
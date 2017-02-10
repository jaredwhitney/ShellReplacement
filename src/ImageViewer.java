import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
public class ImageViewer extends JComponent
{
	static String file;
	static JFrame imageViewerFrame;
	static ImageViewer imageViewer;
	BufferedImage img;
	static float xoffs, yoffs;
	static int xmainsize, ymainsize;
	static float scale = 1;
	static boolean inDrag = false;
	static int lastx, lasty;
	public static void main(String[] args) throws Exception
	{
		file = args[0];
		imageViewerFrame = new JFrame();
		imageViewerFrame.setUndecorated(true);
		imageViewerFrame.setTitle(file + " - ImageViewer");
		ImageViewer imageViewer = new ImageViewer();
		imageViewerFrame.add(imageViewer);
		imageViewerFrame.pack();
		imageViewerFrame.setLocation(1920/2-imageViewerFrame.getWidth()/2, 1080/2-imageViewerFrame.getHeight()/2);
		
		imageViewerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		imageViewerFrame.setVisible(true);
	}
	public ImageViewer()
	{
		try
		{
			if (file.substring(file.lastIndexOf(".")).equalsIgnoreCase(".lsimg"))
				img = LossyImageCompressionReader.readFile(file);
			else
				img = ImageIO.read(new File(file));
			if (img.getWidth()/(getPreferredSize().width/(float)(getPreferredSize().height-20))>=img.getHeight())
			{
				ymainsize = (int)(getPreferredSize().width/(float)img.getWidth()*img.getHeight());
				xmainsize = getPreferredSize().width;
			}
			else
			{
				xmainsize = (int)((getPreferredSize().height-20)/(float)img.getHeight()*img.getWidth());
				ymainsize = getPreferredSize().height-20;
			}
			imageViewerFrame.addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
						System.exit(0);
				}
			});
			addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					if (inDrag)
						return;
					if (e.getY()<20)
					{
						if (e.getX()>getWidth()-20)
							System.exit(0);
						else
						{
							inDrag = true;
							lastx = e.getX();
							lasty = e.getY();
						}
					}
					else
					{
						lastx = e.getX();
						lasty = e.getY();
					}
				}
				public void mouseReleased(MouseEvent e)
				{
					inDrag = false;
				}
			});
			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseDragged(MouseEvent e)
				{
					if (inDrag)
					{
						Point loc = imageViewerFrame.getLocation();
						imageViewerFrame.setLocation(loc.x+e.getX()-lastx, loc.y+e.getY()-lasty);
					}
					else
					{
						xoffs += (e.getX()-lastx)/scale;
						yoffs += (e.getY()-lasty)/scale;
						imageViewerFrame.repaint();
						lastx = e.getX();
						lasty = e.getY();
					}
				}
			});
			addMouseWheelListener(new MouseWheelAdapter()
			{
				public void mouseWheelMoved(MouseWheelEvent e)
				{
					int notches = e.getWheelRotation();
					scale = Math.min(Math.max(1, scale-notches*0.1f), 7);
					imageViewerFrame.repaint();
				}
			});
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(-20);
		}
	}
	public Dimension getPreferredSize()
	{
		return new Dimension(1000, 600);
	}
	public void paint(Graphics g)
	{
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(img, getWidth()/2+(int)((xoffs*2-xmainsize)*scale)/2, 20+getHeight()/2+(int)((yoffs*2-ymainsize)*scale)/2, (int)(xmainsize*scale), (int)(ymainsize*scale), null);
		g.setColor(Color.DARK_GRAY.darker());
		g.fillRect(0, 0, getWidth(), 20);
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D)g;
		ShellReplacement.matchFontSize(g2, 20f);
		g.drawString(file, 5, 15);
		g.setColor(Color.RED);
		g.fillRect(getWidth()-20, 0, 20, 20);
	}
}
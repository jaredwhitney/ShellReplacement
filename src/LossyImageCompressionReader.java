import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
public class LossyImageCompressionReader
{
	static int COMPRESSION_LEVEL = 0;
	static int BIT_SIZE;
	static FileInputStream in;
	static int bnum = -1;
	static int cb = 0;
	public static void main(String[] args) throws Exception
	{
		ImageIO.write(readFile(args[0]), "png", new File("decodedimg.png"));
	}
	public static BufferedImage readFile(String fileName)
	{
		try
		{
			in = new FileInputStream(new File(fileName));
			int width = 0;
			for (int q = 24; q >= 0; q-=8)
				width  |= in.read()<<q;
			int height = 0;
			for (int q = 24; q >= 0; q-=8)
				height  |= in.read()<<q;
			for (int q = 24; q >= 0; q-=8)
				COMPRESSION_LEVEL |= in.read()<<q;
			BIT_SIZE = (int)Math.ceil(Math.log(256/COMPRESSION_LEVEL)/Math.log(2));
			System.out.println("image is " + width + " x " + height + ", compression level " + COMPRESSION_LEVEL);
			BufferedImage img = new BufferedImage(width, height, 3);
			for (int y = 0; y < height; y++)
			{
				int base = 0xFF000000;
				for (int z = 23; z >= 0; z--)
					base |= readbit()<<z;
				//System.out.println(base);
				img.setRGB(0, y, base);
	//			int[]n=new int[3];
				for (int x = 1; x < width; x++)
				{
					int now = readPixel();
					int c = 0xFF000000;
					for (int q = 0; q < 24; q+=8)
					{
						//System.out.println("dif: " + ((((now>>q)&0xFF)*COMPRESSION_LEVEL) ));//-(0xFF*COMPRESSION_LEVEL/2)));
						int r = ((base>>q)&0xFF)+(((now>>q)&0xFF)-(0xFF/COMPRESSION_LEVEL))*COMPRESSION_LEVEL-COMPRESSION_LEVEL/2;
	//					r-=n[q/8];
	//					n[q/8]=0;
						if (r > 0xFF)
						{
	//						n[q/8]=r-0xFF;
							r=0xFF;
						}
						else if (r < 0)
						{
	//						n[q/8]=0-r;
							r=0;
						}
						c |= (r<<q);
					}
					img.setRGB(x, y, c);
					base = c;
				}
			}
			return img;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	public static int readPixel() throws Exception
	{
		return 0xFF000000 | (getPart()) | (getPart()<<8) | (getPart()<<16);
	}
	public static int getPart() throws Exception
	{
		int c = 0;
		int b = readbit();
		if (b==0)
			return (0xFF/COMPRESSION_LEVEL);
		b = readbit();
		if (b==0)
			return (0xFF/COMPRESSION_LEVEL)+1;
		b = readbit();
		if (b==0)
			return (0xFF/COMPRESSION_LEVEL)-1;
		for (int q = BIT_SIZE; q >= 0; q--)
			c |= readbit() << q;
		c&=0xFF;
		return c;
	}
	public static int readbit() throws Exception
	{
		bnum--;
		if (bnum<0)
		{
			bnum=7;
			cb = in.read();
		}
		return (cb>>bnum)&0b1;
	}
}
package af.swing.image;

import java.awt.Rectangle;

// 图片缩放计算
public class AfImageScaler
{
	// 输入参数
	private int imgW,imgH; // 图片的宽度、高度
	private Rectangle rect; // 要绘制的目标区域
	
	// imgW, imgH, 图片的宽度/高度
	// rect: 目标区域
	public AfImageScaler(int imgW, int imgH, Rectangle rect)
	{
		this.imgW = imgW;
		this.imgH = imgH;
		this.rect = rect;	
	}
	public AfImageScaler(int imgW, int imgH, int dstW, int dstH)
	{
		this(imgW, imgH, new Rectangle(dstW, dstH));	
	}
	
	// 拉伸显示,占满空间（比例可能失调）
	public Rectangle fitXY()
	{		
		return this.rect;
	}
	
	// 居中显示，保持长宽比，且适合目标矩形
	public Rectangle fitCenter()
	{
		int width = rect.width;
		int height = rect.height;
		
		int fitW = width;
		int fitH = width * imgH / imgW;
		if( fitH > height )
		{
			fitH = height;
			fitW = height * imgW / imgH;
		}
		int x = (width - fitW ) /2;
		int y = (height - fitH ) /2;
		
		// 结果
		return new Rectangle(rect.x + x, rect.y + y, fitW, fitH);
	}
	
	// 如果图片小于目标矩形，则直接居中显示
	// 如果图片大于目标矩形，则按fitCenter()缩放后显示
	public Rectangle fitCenterInside()
	{
		int width = rect.width;
		int height = rect.height;
		int fitW, fitH;
		
		if(imgW <= width && imgH <= height)
		{
			fitW = imgW;
			fitH = imgH;
			
			int x = (width - fitW ) /2;
			int y = (height - fitH ) /2;
			return new Rectangle(rect.x + x, rect.y + y, fitW, fitH);
		}
		else
		{
			return fitCenter();
		}
	}
	
}

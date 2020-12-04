package af.swing.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;

/* Swing入门篇 10.4节 */

/*
 * 2019-01-27: 最后优化，主要是支持透明的PNG的绘制
 */

public class AfImageView extends JPanel
{
	// 缩放类型 (也可以用枚举语法 Enum 来定义)
	public static final int FIT_XY = 0;
	public static final int FIT_CENTER = 1;
	public static final int FIT_CENTER_INSIDE = 2;
	
	private Image image;
	private int scaleType = FIT_CENTER;
	private Color bgColor = Color.WHITE; // 背景色
	
	public AfImageView()
	{
		this.setOpaque(false);
	}
	
	public Image getImage()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image = image;
		repaint();
	}

	public int getScaleType()
	{
		return scaleType;		
	}

	public void setScaleType(int scaleType)
	{
		this.scaleType = scaleType;
		repaint();
	}

	public Color getBgColor()
	{
		return bgColor;		
	}

	public void setBgColor(Color bgColor)
	{
		this.bgColor = bgColor;
		repaint();
	}
	
	
	@Override
	protected void paintComponent(Graphics g)
	{
		// 先调用父类的 paintComponent(), 绘制必要的边框和背景
		super.paintComponent(g);
		
		// 本控件的宽度和高度
		int width = this.getWidth();
		int height = this.getHeight();

		// 清除显示
		//g.clearRect(0, 0, width, height);
		
		// 背景色
		g.setColor(bgColor);
		g.fillRect(0, 0, width,height);
		
		/////////////////////////
		if(image != null)
		{
			int imgW = image.getWidth(null);
			int imgH = image.getHeight(null);
			
			// 使用  AfImageScaler 来计算
			AfImageScaler scaler = new AfImageScaler(imgW, imgH, width,height);
			
			// 根据缩放类型，来计算目标区域
			Rectangle fit = scaler.fitXY();
			if(scaleType == FIT_CENTER)
				fit = scaler.fitCenter();
			else if(scaleType == FIT_CENTER_INSIDE)
				fit = scaler.fitCenterInside();

			// 绘制
			g.drawImage(image, fit.x, fit.y, fit.width, fit.height,	null);
		}
	}

}

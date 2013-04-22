/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.image;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import se.unlogic.standardutils.io.FileUtils;

public class ImageUtils {

	//TODO this class needs a makeover and a rethink to bring all loose ends together

	public static final String JPG = "jpg";
	public static final String JPEG = "jpeg";
	public static final String GIF = "gif";
	public static final String PNG = "png";
	public static final String BMP = "bmp";
	public static final String WBMP = "wbmp";

	public static BufferedImage getImageByResource(String url) throws IOException {

		return ImageIO.read(ImageUtils.class.getResource(url));
	}

	public static BufferedImage getImageByURL(URL resource) throws IOException {

		return ImageIO.read(resource);
	}

	public static BufferedImage getImage(String path) throws IOException {

		return ImageIO.read(new File(path));
	}

	public static BufferedImage getImage(byte[] data) throws IOException {

		return ImageIO.read(new ByteArrayInputStream(data));
	}

	public static BufferedImage getImage(File file) throws IOException {

		return ImageIO.read(file);
	}

	public static BufferedImage getImage(InputStream inputStream) throws IOException {

		return ImageIO.read(inputStream);
	}

	public static BufferedImage scaleImage(BufferedImage image, int maxHeight, int maxWidth, int quality, int imageType) {

		if (image.getWidth() > maxWidth && image.getHeight() > maxHeight) {

			//TODO this algorithm is not 100% correct and can result in images with either too large height or width
			double aspectRatio = (double) image.getWidth() / (double) image.getHeight();

			if (aspectRatio >= 1) {

				return scaleImageByWidth(image, maxWidth, quality, imageType);

			} else {

				return scaleImageByHeight(image, maxHeight, quality, imageType);
			}

		} else if (image.getWidth() > maxWidth) {

			return scaleImageByWidth(image, maxWidth, quality, imageType);

		} else if (image.getHeight() > maxHeight) {

			return scaleImageByHeight(image, maxHeight, quality, imageType);

		} else if (image.getType() != imageType){

			return scale(image, image.getHeight(), image.getWidth(), quality, imageType);

		} else {

			return image;
		}
	}

	public static BufferedImage scaleImageByWidth(BufferedImage image, int maxWidth, int quality, int imageType) {

		double scale;

		if (image.getWidth() > image.getHeight()) {
			scale = (double) maxWidth / (double) image.getWidth();
		} else {
			scale = (double) maxWidth / (double) image.getHeight();
		}

		int scaledW = (int) (scale * image.getWidth());
		int scaledH = (int) (scale * image.getHeight());

		scaledW = checkSize(scaledW);
		scaledH = checkSize(scaledH);

		return scale(image, scaledH, scaledW, quality, imageType);
	}

	private static int checkSize(int value) {

		if (value < 1) {
			return 1;
		} else {
			return value;
		}
	}

	public static BufferedImage scaleImageByHeight(BufferedImage image, int maxHeight, int quality, int imageType) {

		double scale;

		if (image.getHeight() > image.getWidth()) {
			scale = (double) maxHeight / (double) image.getHeight();
		} else {
			scale = (double) maxHeight / (double) image.getWidth();
		}

		int scaledW = (int) (scale * image.getWidth());
		int scaledH = (int) (scale * image.getHeight());

		scaledW = checkSize(scaledW);
		scaledH = checkSize(scaledH);

		return scale(image, scaledH, scaledW, quality, imageType);
	}

	public static byte[] convertImage(BufferedImage image, String format) throws IOException, NullPointerException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		ImageIO.write(image, format, outputStream);

		return outputStream.toByteArray();
	}

	public static void writeImage(BufferedImage image, String path, String format) throws IOException, NullPointerException {

		// write image to file
		if (!path.endsWith("." + format)) {
			path += "." + format;
		}

		File outputfile = new File(path);

		ImageIO.write(image, format, outputfile);
	}

	public static BufferedImage scale(BufferedImage image, double xFactor, double yFactor, int imageType) {

		// scale image based on factor x and y
		AffineTransform scaleTransform = new AffineTransform();
		scaleTransform.scale(xFactor, yFactor);

		BufferedImage result = new BufferedImage((int) (image.getWidth() * xFactor), (int) (image.getHeight() * yFactor), imageType);

		Graphics2D g2 = (Graphics2D) result.getGraphics();


		setBackground(g2, result, image);

		g2.drawImage(image, scaleTransform, null);

		return result;
	}

	public static BufferedImage scale(BufferedImage image, int height, int width, int quality, int imageType) {

		BufferedImage result = new BufferedImage(width, height, imageType);

		Graphics2D g2 = (Graphics2D) result.getGraphics();

		setBackground(g2, result, image);

		Canvas canvas = new Canvas();
		Image tImage = image.getScaledInstance(width, height, quality);

		g2.drawImage(tImage, 0, 0, canvas);

		return result;
	}

	private static void setBackground(Graphics2D g2, BufferedImage result, BufferedImage source) {
		
		int targetType = result.getType();
		int sourceType = source.getType();

		if(targetType == BufferedImage.TYPE_INT_RGB && (sourceType == BufferedImage.TYPE_4BYTE_ABGR || sourceType == BufferedImage.TYPE_4BYTE_ABGR_PRE || sourceType == BufferedImage.TYPE_INT_ARGB || sourceType == BufferedImage.TYPE_INT_ARGB_PRE)){

			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, result.getWidth(), result.getHeight());
		}
	}

	public static BufferedImage changeImageType(BufferedImage image, int desiredImagetype, Color background){

		if(image.getType() != desiredImagetype){

			BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), desiredImagetype);

			if(background != null){

				Graphics2D graphics2d = (Graphics2D)newImage.getGraphics();

				graphics2d.setColor(background);
				graphics2d.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());

				graphics2d.drawImage(image, 0, 0, null);
			}

			return newImage;
		}

		return image;
	}

	public static boolean isImage(String filename) {

		String extension = FileUtils.getFileExtension(filename);

		if(extension != null) {

			extension = extension.toLowerCase();

			if (extension.equals(ImageUtils.JPG) || extension.equals(ImageUtils.JPEG) || extension.equals(ImageUtils.GIF) || extension.equals(ImageUtils.PNG) || extension.equals(ImageUtils.BMP) || extension.equals(ImageUtils.WBMP)) {

				return true;

			}

		}

		return false;

	}

	public static Dimension getImageDimensions(String path) throws FileNotFoundException, IOException {

		String suffix = FileUtils.getFileExtension(path);

		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);

		if (iter.hasNext()) {

			ImageReader reader = iter.next();
			ImageInputStream stream = null;

			try {
				stream = new FileImageInputStream(new File(path));
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				return new Dimension(width, height);
			} finally {
				reader.dispose();

				if(stream != null){

					try {
						stream.close();
					} catch (IOException e) {}
				}
			}
		}

		return null;
	}

	public static BufferedImage copyImage(BufferedImage image){

		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);

		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/*public static void scaleAndWriteImage(String inImgURL, String outImgURL, String format, double xFactor, double yFactor ) throws IOException{

		// scale and write the scaled image to file
		BufferedImage image = getImage(inImgURL);

		// changed
		final int MaxDim = 120;
		int thumb_width = image.getWidth();
		int thumb_height = image.getHeight();
		int b = thumb_height > thumb_width ? thumb_height : image.getWidth();
		double per = (b > MaxDim) ? (MaxDim * 1.0) / b : 1.0;
		thumb_height = (int)(thumb_height * per);
	    thumb_width = (int)(thumb_width * per);
		////

		BufferedImage scImage = null;
		if(image != null){
			//scImage = scale(image, xFactor, yFactor);
			scImage = scale(image, thumb_height, thumb_width);
		}
		else
			throw new IOException();
		writeImage(scImage, outImgURL, format);


	}*/
}

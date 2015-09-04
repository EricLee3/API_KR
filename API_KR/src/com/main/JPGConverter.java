package com.main;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.media.jai.JAI;

public class JPGConverter {
	
	 
	public String ConvTifJpg(){
		 String sourcefilename = "D:\\20150106_pantos_test1.tiff";
		 String targetfilename = "D:\\20150108_pantos_test.jpg";
		 try {
			 FileSeekableStream stream = null;
			 stream = new FileSeekableStream(sourcefilename);
			 ImageDecoder dec = ImageCodec.createImageDecoder("tiff", stream,null);
		 RenderedImage image = dec.decodeAsRenderedImage(0);
		 JAI.create("filestore",image ,targetfilename,"JPEG");
		 } catch (IOException e) {
			 e.printStackTrace();
			 System.exit(0);
		 }
		 return targetfilename;
	 }

	public static void main(String args[]) {
		
		JPGConverter cvt = new JPGConverter();
		cvt.ConvTifJpg();
		
	}
}

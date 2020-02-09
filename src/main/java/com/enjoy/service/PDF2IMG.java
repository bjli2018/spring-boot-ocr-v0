package com.enjoy.service;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class PDF2IMG { 
    public static void pdf2Img(String inputFilePath) throws IOException {
        System.setProperty("apple.awt.UIElement", "true");
        String password = "";
        File inputFile = new File(inputFilePath);
        String pdfFile = inputFile.getPath();	 
        String outputFilePath=inputFile.getParentFile().getPath() + "/";	 
        String outputPrefix = inputFile.getName().substring(0, inputFile.getName().indexOf("."));
        String imageFormat = "jpg";
        int startPage = 1;
        int endPage = 2147483647;
        String color = "rgb";
        float cropBoxLowerLeftX = 0.0F;
        float cropBoxLowerLeftY = 0.0F;
        float cropBoxUpperRightX = 0.0F;
        float cropBoxUpperRightY = 0.0F;
        boolean showTime = false;
 
        int dpi;
        try {
            dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (HeadlessException var28) {
            dpi = 96;
        }
 
        if(pdfFile == null) {
            usage();
        } else {
            PDDocument var30 = null;
 
            try {
                var30 = PDDocument.load(new File(pdfFile), password);
                ImageType imageType = null;
                if("bilevel".equalsIgnoreCase(color)) {
                    imageType = ImageType.BINARY;
                } else if("gray".equalsIgnoreCase(color)) {
                    imageType = ImageType.GRAY;
                } else if("rgb".equalsIgnoreCase(color)) {
                    imageType = ImageType.RGB;
                } else if("rgba".equalsIgnoreCase(color)) {
                    imageType = ImageType.ARGB;
                }
 
                if(imageType == null) {
                    System.err.println("Error: Invalid color.");
                    System.exit(2);
                }
 
                if(cropBoxLowerLeftX != 0.0F || cropBoxLowerLeftY != 0.0F || cropBoxUpperRightX != 0.0F || cropBoxUpperRightY != 0.0F) {
                    changeCropBox(var30, cropBoxLowerLeftX, cropBoxLowerLeftY, cropBoxUpperRightX, cropBoxUpperRightY);
                }
 
                long startTime = System.nanoTime();
                boolean success = true;
                endPage = Math.min(endPage, var30.getNumberOfPages());
                PDFRenderer renderer = new PDFRenderer(var30);
 
                for(int endTime = startPage - 1; endTime < endPage; ++endTime) {
                    BufferedImage image = renderer.renderImageWithDPI(endTime, (float)dpi, imageType);
                    String duration = outputFilePath + outputPrefix + "." + imageFormat;
                    success &= ImageIOUtil.writeImage(image, duration,dpi);
                }
 
                long var31 = System.nanoTime();
                long var32 = var31 - startTime;
                int count = 1 + endPage - startPage;
                if(showTime) {
                    System.err.printf("Rendered %d page%s in %dms\n", new Object[]{Integer.valueOf(count), count == 1?"":"s", Long.valueOf(var32 / 1000000L)});
                }
 
                if(!success) {
                    System.err.println("Error: no writer found for image format \'" + imageFormat + "\'");
                    System.exit(1);
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }finally {
                if(var30 != null) {
                    var30.close();
                }
 
            }
            System.out.println("pdf to jpg completed");
 
        }
 
    }
    private static void usage() {
        String message = "Usage: java -jar pdfbox-app-x.y.z.jar PDFToImage [options] <inputfile>\n\nOptions:\n  -password  <password>            : Password to decrypt document\n  -format <string>                 : Image format: " + getImageFormats() + "\n" + "  -prefix <string>                 : Filename prefix for image files\n" + "  -page <number>                   : The only page to extract (1-based)\n" + "  -startPage <int>                 : The first page to start extraction (1-based)\n" + "  -endPage <int>                   : The last page to extract(inclusive)\n" + "  -color <int>                     : The color depth (valid: bilevel, gray, rgb, rgba)\n" + "  -dpi <int>                       : The DPI of the output image\n" + "  -cropbox <int> <int> <int> <int> : The page area to export\n" + "  -time                            : Prints timing information to stdout\n" + "  <inputfile>                      : The PDF document to use\n";
        System.err.println(message);
        System.exit(1);
    }
 
    private static String getImageFormats() {
        StringBuilder retval = new StringBuilder();
        String[] formats = ImageIO.getReaderFormatNames();
 
        for(int i = 0; i < formats.length; ++i) {
            if(formats[i].equalsIgnoreCase(formats[i])) {
                retval.append(formats[i]);
                if(i + 1 < formats.length) {
                    retval.append(", ");
                }
            }
        }
 
        return retval.toString();
    }
 
    private static void changeCropBox(PDDocument document, float a, float b, float c, float d) {
        Iterator<?> i$ = document.getPages().iterator();
 
        while(i$.hasNext()) {
            PDPage page = (PDPage)i$.next();
            System.out.println("resizing page");
            PDRectangle rectangle = new PDRectangle();
            rectangle.setLowerLeftX(a);
            rectangle.setLowerLeftY(b);
            rectangle.setUpperRightX(c);
            rectangle.setUpperRightY(d);
            page.setCropBox(rectangle);
        }
 
    }
}

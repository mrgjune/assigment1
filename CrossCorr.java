import java.io.IOException;
import java.util.Arrays; 
public class CrossCorr {

    public static RGBImage makeGray(RGBImage img) {
        RGBImage gray = new RGBImage(img.getHeight(), img.getWidth());

        for (int r = 0; r < img.getNumRows(); r++) {
            for (int c = 0; c < img.getNumCols(); c++) {
                int grayVal = (int) (img.getPixel(r, c).getRed() * 0.299 + img.getPixel(r, c).getGreen() * 0.587
                        + img.getPixel(r, c).getBlue() * 0.114);
                gray.setPixel(r, c, grayVal, grayVal, grayVal);
            }
        }

        return gray;
    }

    // expect img to be a color image
    // expect mask to be a blurring mask whose values all add up to 1
    // assuming the mask is odd width and odd height
    public static RGBImage crossCorrelate(double[][] mask, RGBImage img, int flag, double threshold) {
        RGBImage grayInput = makeGray(img);
        RGBImage resultImage = new RGBImage(grayInput);

        int halfMaskWidth = mask[0].length / 2; // half the # of cols
        int halfMaskHeight = mask.length / 2; // half the # of rows
        double total = 0;
        if(flag == 1) {
        //do a divide by the total of the mask before outputting new pixel
                for (int i=0; i<mask.length; i++) {
	                for (int j=0; j<mask[i].length; j++) {
		                total+=mask[i][j];
		        }
                 }
        }
        for (int r = halfMaskHeight; r < grayInput.getNumRows() - halfMaskHeight; r++) {

            for (int c = halfMaskWidth; c < grayInput.getNumCols() - halfMaskWidth; c++) {

                // here we have r,c which is the "center pixel" of a neighborhood
                double result = 0;
                for (int mr = 0; mr < mask.length; mr++) {
                    for (int mc = 0; mc < mask[mr].length; mc++) {
                        result += mask[mr][mc]
                                * grayInput.getPixel(r + mr - halfMaskHeight, c + mc - halfMaskWidth).getRed();
                    }
                }
                if (flag == 0) {
                    resultImage.setPixel(r, c, (int) result, (int) result, (int) result);
                }
                else if(flag == 1) {
                    double resultNorm = result/total;
                    resultImage.setPixel(r, c, (int) resultNorm, (int) resultNorm, (int) resultNorm);
                }
                else if(flag == 2) {
                    int resultbw;
                    if(Math.abs(result) < threshold) {
                            resultbw = 0;
                    }
                    else {
                            resultbw = 255;
                    }
                    resultImage.setPixel(r, c, resultbw, resultbw, resultbw);
                }
                        
            }

        }

        return resultImage;
    }
    public static RGBImage convole (double[][] mask, RGBImage img, int flag, double threshold) {
                   // int median = mask.getNumsRows/2;
                    double temp = 0;
            for (int row = 0; row < mask.length/2; row++) {
                for (int col = 0; col < mask.length; col++) {
                        temp = mask[mask.length-row-1][col];
                        mask[mask.length-row-1][col] = mask[row][col];
                        mask[row][col] = temp; 
                }
            }
        for (int col = 0; col < mask.length/2; col++) {
              for (int row = 0; row < mask.length; row++) {
                        temp = mask[mask.length-col-1][row];
                        mask[mask.length-col-1][row] = mask[col][row];
                        mask[col][row] = temp; 
                }
        }


            return img;
            
    }
    
    public static RGBImage medianFiltering(RGBImage img, int widthHeight) {
        RGBImage resultImage = new RGBImage(img);
        int half = (widthHeight-1)/2;
        int [] pixels = new int[widthHeight*widthHeight];
        int numPixels = 0;
        for(int row = 0; row < img.getNumRows(); row++) {
                for (int col = 0; col < img.getNumCols(); col++) {
                        int lowRow = row - half;
                        int highRow = row + half;
                        int lowCol = col - half;
                        int highCol = col + half;
                        numPixels = 0;
                        for(int rowTwo = lowRow; rowTwo < highRow; rowTwo++) {
                           for(int colTwo = lowCol; colTwo < highCol; colTwo++) {
                                   if(rowTwo < 0 || rowTwo > img.getNumRows()) {
                                           continue;
                                   }
                                   if(colTwo < 0 || colTwo > img.getNumCols()) {
                                           continue;
                                   }
                                   if(rowTwo == row && colTwo == col) {
                                           continue;
                                   }
                                   pixels[numPixels] = img.getPixel(rowTwo,colTwo).getGreen();
                                   numPixels++;
                           }
                                
                        }
                        Arrays.sort(pixels,0,numPixels);
                        int median = pixels[numPixels/2];
                       RGBPixel pixel = new RGBPixel(median,median,median);
                        resultImage.setPixel(row,col,pixel);
                }
                
        }
        return resultImage;

    }
    public static void main(String[] args) throws IOException {
        double sobelHorizontal [][] = {{-1,0,1},{-2,0,2},{-1,1,0}};
        double sobelVertical [][] = {{-1,-2,-1},{0,0,0},{1,2,3}};
        double testMask [][] =  {{1,1,1},{0,0,0},{2,2,2}};
        double blurMask[][] = { { 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49 },
                { 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49 },
                { 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49 },
                { 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49 },
                { 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49 },
                { 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49 },
                { 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49, 1.0 / 49 } };
        double gaussianV2[][] = { { 0.00090, 0.00316, 0.00668, 0.00858, 0.00668, 0.00316, 0.00090 },
                { 0.00316, 0.01102, 0.02333, 0.02996, 0.02333, 0.01102, 0.00316 },
                { 0.00668, 0.02333, 0.04940, 0.06342, 0.04940, 0.02333, 0.00668 },
                { 0.00858, 0.02996, 0.06342, 0.08144, 0.06342, 0.02996, 0.00858 },
                { 0.00668, 0.02333, 0.04940, 0.06342, 0.04940, 0.02333, 0.00668 },
                { 0.00316, 0.01102, 0.02333, 0.02996, 0.02333, 0.01102, 0.00316 },
                { 0.00090, 0.00316, 0.00668, 0.00858, 0.00668, 0.00316, 0.00090 } };
        double gaussianV3[][] = {
                { 0.00000, 0.00000, 0.00001, 0.00003, 0.00007, 0.00011, 0.00013, 0.00011, 0.00007, 0.00003, 0.00001,
                        0.00000, 0.00000 },
                { 0.00000, 0.00001, 0.00006, 0.00018, 0.00042, 0.00070, 0.00082, 0.00070, 0.00042, 0.00018, 0.00006,
                        0.00001, 0.00000 },
                { 0.00001, 0.00006, 0.00026, 0.00082, 0.00189, 0.00312, 0.00369, 0.00312, 0.00189, 0.00082, 0.00026,
                        0.00006, 0.00001 },
                { 0.00003, 0.00018, 0.00082, 0.00264, 0.00608, 0.01002, 0.01184, 0.01002, 0.00608, 0.00264, 0.00082,
                        0.00018, 0.00003 },
                { 0.00007, 0.00042, 0.00189, 0.00608, 0.01399, 0.02306, 0.02725, 0.02306, 0.01399, 0.00608, 0.00189,
                        0.00042, 0.00007 },
                { 0.00011, 0.00070, 0.00312, 0.01002, 0.02306, 0.03802, 0.04492, 0.03802, 0.02306, 0.01002, 0.00312,
                        0.00070, 0.00011 },
                { 0.00013, 0.00082, 0.00369, 0.01184, 0.02725, 0.04492, 0.05307, 0.04492, 0.02725, 0.01184, 0.00369,
                        0.00082, 0.00013 },
                { 0.00011, 0.00070, 0.00312, 0.01002, 0.02306, 0.03802, 0.04492, 0.03802, 0.02306, 0.01002, 0.00312,
                        0.00070, 0.00011 },
                { 0.00007, 0.00042, 0.00189, 0.00608, 0.01399, 0.02306, 0.02725, 0.02306, 0.01399, 0.00608, 0.00189,
                        0.00042, 0.00007 },
                { 0.00003, 0.00018, 0.00082, 0.00264, 0.00608, 0.01002, 0.01184, 0.01002, 0.00608, 0.00264, 0.00082,
                        0.00018, 0.00003 },
                { 0.00001, 0.00006, 0.00026, 0.00082, 0.00189, 0.00312, 0.00369, 0.00312, 0.00189, 0.00082, 0.00026,
                        0.00006, 0.00001 },
                { 0.00000, 0.00001, 0.00006, 0.00018, 0.00042, 0.00070, 0.00082, 0.00070, 0.00042, 0.00018, 0.00006,
                        0.00001, 0.00000 },
                { 0.00000, 0.00000, 0.00001, 0.00003, 0.00007, 0.00011, 0.00013, 0.00011, 0.00007, 0.00003, 0.00001,
                        0.00000, 0.00000 } };
            double gaussianV1[][] = {{ 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00000, 0.00000, 0.00001, 0.00003, 0.00005, 0.00003, 0.00001, 0.00000, 0.00000, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00000, 0.00002, 0.00024, 0.00107, 0.00177, 0.00107, 0.00024, 0.00002, 0.00000, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00001, 0.00024, 0.00292, 0.01306, 0.02154, 0.01306, 0.00292, 0.00024, 0.00001, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00003, 0.00107, 0.01306, 0.05855, 0.09653, 0.05855, 0.01306, 0.00107, 0.00003, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00005, 0.00177, 0.02154, 0.09653, 0.15915, 0.09653, 0.02154, 0.00177, 0.00005, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00003, 0.00107, 0.01306, 0.05855, 0.09653, 0.05855, 0.01306, 0.00107, 0.00003, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00001, 0.00024, 0.00292, 0.01306, 0.02154, 0.01306, 0.00292, 0.00024, 0.00001, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00000, 0.00002, 0.00024, 0.00107, 0.00177, 0.00107, 0.00024, 0.00002, 0.00000, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00000, 0.00000, 0.00001, 0.00003, 0.00005, 0.00003, 0.00001, 0.00000, 0.00000, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000},
            { 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000}};
        double gaussianV4[][] = 
            {{ 0.00000, 0.00002, 0.00006, 0.00014, 0.00027, 0.00039, 0.00044, 0.00039, 0.00027, 0.00014, 0.00006, 0.00002, 0.00000},
{ 0.00002, 0.00008, 0.00024, 0.00057, 0.00106, 0.00155, 0.00175, 0.00155, 0.00106, 0.00057, 0.00024, 0.00008, 0.00002},
{ 0.00006, 0.00024, 0.00073, 0.00175, 0.00327, 0.00476, 0.00540, 0.00476, 0.00327, 0.00175, 0.00073, 0.00024, 0.00006},
{ 0.00014, 0.00057, 0.00175, 0.00420, 0.00785, 0.01142, 0.01294, 0.01142, 0.00785, 0.00420, 0.00175, 0.00057, 0.00014},
{ 0.00027, 0.00106, 0.00327, 0.00785, 0.01467, 0.02134, 0.02418, 0.02134, 0.01467, 0.00785, 0.00327, 0.00106, 0.00027},
{ 0.00039, 0.00155, 0.00476, 0.01142, 0.02134, 0.03105, 0.03519, 0.03105, 0.02134, 0.01142, 0.00476, 0.00155, 0.00039},
{ 0.00044, 0.00175, 0.00540, 0.01294, 0.02418, 0.03519, 0.03987, 0.03519, 0.02418, 0.01294, 0.00540, 0.00175, 0.00044},
{ 0.00039, 0.00155, 0.00476, 0.01142, 0.02134, 0.03105, 0.03519, 0.03105, 0.02134, 0.01142, 0.00476, 0.00155, 0.00039},
{ 0.00027, 0.00106, 0.00327, 0.00785, 0.01467, 0.02134, 0.02418, 0.02134, 0.01467, 0.00785, 0.00327, 0.00106, 0.00027},
{ 0.00014, 0.00057, 0.00175, 0.00420, 0.00785, 0.01142, 0.01294, 0.01142, 0.00785, 0.00420, 0.00175, 0.00057, 0.00014},
{ 0.00006, 0.00024, 0.00073, 0.00175, 0.00327, 0.00476, 0.00540, 0.00476, 0.00327, 0.00175, 0.00073, 0.00024, 0.00006},
{ 0.00002, 0.00008, 0.00024, 0.00057, 0.00106, 0.00155, 0.00175, 0.00155, 0.00106, 0.00057, 0.00024, 0.00008, 0.00002},
{ 0.00000, 0.00002, 0.00006, 0.00014, 0.00027, 0.00039, 0.00044, 0.00039, 0.00027, 0.00014, 0.00006, 0.00002, 0.00000}};
        double blurMask315[][] = { { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 },
                { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 },
                { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 },
                { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 },
                { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 }, { 1.0 / 45, 1.0 / 45, 1.0 / 45 },
                { 1.0 / 45, 1.0 / 45, 1.0 / 45 } };

        RGBImage inImg = new RGBImage(args[0]); // reads the image on disk with name args[0] and creates an RGBImage
                                                // object
        RGBImage blurredImg;
        RGBImage medianImage;
        RGBImage sobelHor;
        RGBImage sobelVer;
        RGBImage testMa;
        testMa = convole(testMask, inImg,0,0);
        blurredImg = crossCorrelate(gaussianV4, inImg,0,0);
        // blurredImg.writeImage("blurred13x13V4-" + args[0]);
       
       sobelVer  = crossCorrelate(sobelVertical, inImg,2,200);
       //sobelVer.writeImage("sobelVertical-" + args[0]);
        
        medianImage = medianFiltering(inImg,3);
        medianImage.writeImage("median-saltpepper" + args[0]);
    }

}
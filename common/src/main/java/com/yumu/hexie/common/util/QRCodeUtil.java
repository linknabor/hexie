package com.yumu.hexie.common.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.util.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    private QRCodeUtil() {
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public static void writeToFile(BitMatrix matrix, String logoPath, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        image = addLogo(image, logoPath);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    public static void createQRCodeToFile(String url, String logoPath, File file) throws WriterException, IOException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 300, 300, hints);

        int margin = 10;
        int[] rec = bitMatrix.getEnclosingRectangle();
        int resWidth = rec[2] + margin*2;
        int resHeight = rec[3] + margin*2;
        BitMatrix resBitMatrix = new BitMatrix(resWidth, resHeight);
        resBitMatrix.clear();
        for(int i=margin; i<resWidth - margin; i++) {
            for(int j=margin; j<resHeight - margin; j++) {
                if(bitMatrix.get(i - margin + rec[0], j - margin + rec[1])) {
                    resBitMatrix.set(i, j);
                }
            }
        }

        writeToFile(resBitMatrix, logoPath, "jpg", file);
    }

    public static void writeToStream(BitMatrix matrix, String logoPath, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        image = addLogo(image, logoPath);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }

    public static void createQRCodeToIO(String url, String logoPath, OutputStream stream) throws WriterException, IOException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 300, 300, hints);

        int margin = 10;
        int[] rec = bitMatrix.getEnclosingRectangle();
        int resWidth = rec[2] + margin*2;
        int resHeight = rec[3] + margin*2;
        BitMatrix resBitMatrix = new BitMatrix(resWidth, resHeight);
        resBitMatrix.clear();
        for(int i=margin; i<resWidth - margin; i++) {
            for(int j=margin; j<resHeight - margin; j++) {
                if(bitMatrix.get(i - margin + rec[0], j - margin + rec[1])) {
                    resBitMatrix.set(i, j);
                }
            }
        }

        writeToStream(resBitMatrix, logoPath, "jpg", stream);
    }

    /**
     * 二维码添加logo(logo占二维码1/5)
     *
     * @param image
     * @param logoFilePath
     * @return
     * @throws IOException
     */
    public static BufferedImage addLogo(BufferedImage image, String logoFilePath) throws IOException {
        if(StringUtils.isEmpty(logoFilePath)) { //没有logo就原文件输出
            return image;
        }
        File file = new File(logoFilePath);
        if (!file.exists()) {
            throw new IOException("logo文件不存在");
        }

        BufferedImage logo = ImageIO.read(file);
        Graphics2D graph = image.createGraphics();
        graph.drawImage(logo, image.getWidth() * 2 / 5, image.getHeight() * 2 / 5
                , image.getWidth() * 2 / 10, image.getHeight() * 2 / 10, null);
        graph.dispose();

        return image;
    }
}

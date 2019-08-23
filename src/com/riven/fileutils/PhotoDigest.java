package com.riven.fileutils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 直方图原理实现图像内容相似度比较算法
 */
public class PhotoDigest {
    public static void main(String[] args) throws Exception {
        float percent = compare("D:\\photo整理\\[20150313,20150319].[IMG].[Italy,Lazio,Rome].[欧洲精选]\\IMG_20150313104027.jpg",
                "D:\\photo整理\\[20150313,20150319].[IMG].[Italy,Lazio,Rome].[欧洲精选]\\IMG_20150313104028_MI4_Italy,Lazio,Rome_欧洲精选.jpg");
//        float percent = compare("D:\\photo整理\\[20170704,20170708].[IMG].[上海市,浦东新区,湖南省,张家界市,武陵源区,永定区,湘西土家族苗族自治州,凤凰县].[张家界和凤凰古城]\\IMG_20170705104140_MI5_湖南省,张家界市,武陵源区.jpg",
//                "D:\\photo整理\\[20170704,20170708].[IMG].[上海市,浦东新区,湖南省,张家界市,武陵源区,永定区,湘西土家族苗族自治州,凤凰县].[张家界和凤凰古城]\\IMG_20170705104144_MI5_湖南省,张家界市,武陵源区.jpg");
//        float percent = compare("D:\\photo整理\\[20170704,20170708].[IMG].[上海市,浦东新区,湖南省,张家界市,武陵源区,永定区,湘西土家族苗族自治州,凤凰县].[张家界和凤凰古城]\\IMG_20170705104140_MI5_湖南省,张家界市,武陵源区.jpg",
//                "D:\\photo整理\\[20170704,20170708].[IMG].[上海市,浦东新区,湖南省,张家界市,武陵源区,永定区,湘西土家族苗族自治州,凤凰县].[张家界和凤凰古城]\\IMG_20170705104140_MI5_湖南省,张家界市,武陵源区_张家界和凤凰古城.jpg");
//        float percent = compare("D:\\photo整理\\[20150313,20150319].[IMG].[Italy,Lazio,Rome].[欧洲精选]\\IMG_20150313104027.jpg",
//                "D:\\ChannelSemanticParsing\\new_changning_def_openChannel.json");

        if (percent == 0) {
            System.out.println("无法比较");
        } else {
            System.out.println("两张图片的相似度为：" + percent + "%");
        }
    }

    public static int[] getData(BufferedImage img) {
        try {
//            BufferedImage img = ImageIO.read(new File(name));
            BufferedImage slt = new BufferedImage(2048, 2048,
                    BufferedImage.TYPE_INT_ARGB);
            slt.getGraphics().drawImage(img.getScaledInstance(2048, 2048, Image.SCALE_SMOOTH), 0, 0, 2048, 2048, null);
            // ImageIO.write(slt,"jpeg",new File("slt.jpg"));
            int[] data = new int[256];
            for (int x = 0; x < slt.getWidth(); x++) {
                for (int y = 0; y < slt.getHeight(); y++) {
                    int rgb = slt.getRGB(x, y);
                    Color myColor = new Color(rgb);
                    int r = myColor.getRed();
                    int g = myColor.getGreen();
                    int b = myColor.getBlue();
                    data[(r + g + b) / 3]++;
                }
            }
            // data 就是所谓图形学当中的直方图的概念
            return data;
        } catch (Exception exception) {
            System.out.println("有文件没有找到,请检查文件是否存在或路径是否正确");
            return null;
        }
    }

    public static float compare(String name1, String name2) {
        BufferedImage img1 = null, img2 = null;
        try {
            img1 = ImageIO.read(new File(name1));
            img2 = ImageIO.read(new File(name2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compare(img1, img2);
    }

    public static float compare(BufferedImage img1, BufferedImage img2) {
        int[] s = getData(img1);
        int[] t = getData(img2);
        try {
            float result = 0F;
            for (int i = 0; i < 256; i++) {
                int abs = Math.abs(s[i] - t[i]);
                int max = Math.max(s[i], t[i]);
                result += (1 - ((float) abs / (max == 0 ? 1 : max)));
            }
            return (result / 256) * 100;
        } catch (Exception exception) {
            return 0;
        }
    }
}

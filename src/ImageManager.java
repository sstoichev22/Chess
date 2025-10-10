import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageManager {
    //b, k, n, p, q, r, B, K, N, P, Q, R
    public static BufferedImage b;
    public static BufferedImage k;
    public static BufferedImage n;
    public static BufferedImage p;
    public static BufferedImage q;
    public static BufferedImage r;
    public static BufferedImage B;
    public static BufferedImage K;
    public static BufferedImage N;
    public static BufferedImage P;
    public static BufferedImage Q;
    public static BufferedImage R;
    public static void initImages(){
        try {
            b = ImageIO.read(ImageManager.class.getResourceAsStream("/res/bb.png"));
            k = ImageIO.read(ImageManager.class.getResourceAsStream("/res/bk.png"));
            n = ImageIO.read(ImageManager.class.getResourceAsStream("/res/bn.png"));
            p = ImageIO.read(ImageManager.class.getResourceAsStream("/res/bp.png"));
            q = ImageIO.read(ImageManager.class.getResourceAsStream("/res/bq.png"));
            r = ImageIO.read(ImageManager.class.getResourceAsStream("/res/br.png"));
            B = ImageIO.read(ImageManager.class.getResourceAsStream("/res/wb.png"));
            K = ImageIO.read(ImageManager.class.getResourceAsStream("/res/wk.png"));
            N = ImageIO.read(ImageManager.class.getResourceAsStream("/res/wn.png"));
            P = ImageIO.read(ImageManager.class.getResourceAsStream("/res/wp.png"));
            Q = ImageIO.read(ImageManager.class.getResourceAsStream("/res/wq.png"));
            R = ImageIO.read(ImageManager.class.getResourceAsStream("/res/wr.png"));
        } catch(IOException ignored){}
    }
}

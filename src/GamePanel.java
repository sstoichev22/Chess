import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
    private Thread gameThread;
    Input input;
    int screenWidth, screenHeight, boardWidth, boardHeight, tileSize, hoverX = -1, hoverY = -1, logOffset = 0;
    static Piece[][] board;
    String startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    //game end test = "k7/8/8/8/8/7K/1R6/2R5"
    //castle test = "r3k2r/8/8/8/8/8/8/R3K2R"
    //en passant test = "8/p6k/8/1P6/8/8/8/7K"
    String fen = startFen;
    Piece selectedPiece = null;
    Color green = new Color(159, 164, 78, 255), lightBrown = new Color(255, 185, 100), darkBrown = new Color(141, 84, 38);
    ArrayList<String> log;
    static boolean turn = true;
    boolean waiting = false;

    GamePanel(Input input, int screenWidth, int screenHeight, int boardWidth, int boardHeight){
        this.input = input;
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        tileSize = 100;
        ImageManager.initImages();
        board = new Piece[8][8];
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        log = new ArrayList<>();
        initBoard();
        setGameSize(this.screenWidth = screenWidth, this.screenHeight = screenHeight);
    }
    @Override
    public void run() {
        double FPS = 1_000_000_000.0/120;
        double UPS = 1_000_000_000.0/200;
        double deltaF = 0, deltaU = 0;
        long previousTime = System.nanoTime();
        long lastCheck = System.currentTimeMillis();
        long frames = 0, updates = 0;
        while(gameThread != null){
            long currentTime = System.nanoTime();
            deltaF += (currentTime-previousTime)/FPS;
            deltaU += (currentTime-previousTime)/UPS;
            previousTime = currentTime;
            if(deltaF >= 1){
                repaint();
                deltaF--;
                frames++;
            }
            if(deltaU >= 1){
                update();
                deltaU--;
                updates++;
            }
            if(System.currentTimeMillis()-lastCheck >= 1000){
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                lastCheck = System.currentTimeMillis();
                frames = 0;
                updates = 0;
            }
        }
    }
    public void update(){

    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //board
        drawGameTiles(g);
        drawPieces(g);
        drawMoves(g);
        drawCoords(g);
        drawHoverMove(g);
        //log
        drawLog(g);
        //pawn promo
        if(input.ppp != null) drawPawnPromotion(g);
        //king check
        drawKingChecked(g);
        //game end
        System.out.println(turn);
        if(getKingMoves().isEmpty()) drawGameEnd(g);
    }

    public ArrayList<Point> getKingMoves(){
        ArrayList<Point> list = new ArrayList<>();
        for(int i = 0; i < 8; i++)
            for(int j = 0 ; j < 8; j++)
                if(board[i][j] != null && board[i][j].getColor() == turn){
                    char name = board[i][j].name;
                    board[i][j].getMoves().forEach(p-> System.out.println(name + " " + p.x + " " + p.y));
                    list.addAll(board[i][j].getMoves());
                }
        return list;
    }

    public void drawGameEnd(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        Composite c = g2.getComposite();
        g2.setColor(Color.black);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .60F));
        g2.fillRect(0, 0, boardWidth, boardHeight);
        g2.setComposite(c);
        if(!waiting) {
            waiting = true;
            int response = JOptionPane.showOptionDialog(this, "Game Over.", "Game Over", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Ok"}, "Ok");
            if (response == JOptionPane.OK_OPTION) {
                fen = startFen;
                initBoard();
                waiting = false;
            }
        }
    }

    public void drawKingChecked(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        Composite c = g2.getComposite();
        g2.setColor(Color.red);
        for(int i = 0 ; i < 8; i++){
            for(int j = 0 ; j < 8; j++){
                if(board[i][j] instanceof King && board[i][j].getColor() && Input.checkW){
                    g2.setColor(Color.RED);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5F));
                    Area shape = new Area(new Rectangle(i*tileSize, j*tileSize, tileSize, tileSize));
                    shape.subtract(new Area(new Ellipse2D.Double(i*tileSize, j*tileSize, tileSize, tileSize)));
                    g2.fill(shape);
                }
                if(board[i][j] instanceof King && !board[i][j].getColor() && Input.checkB){
                    g2.setColor(Color.RED);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5F));
                    Area shape = new Area(new Rectangle(i*tileSize, j*tileSize, tileSize, tileSize));
                    shape.subtract(new Area(new Ellipse2D.Double(i*tileSize, j*tileSize, tileSize, tileSize)));
                    g2.fill(shape);
                }
            }
        }
        g2.setComposite(c);

    }

    public void drawPawnPromotion(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        Composite c = g2.getComposite();
        g2.setColor(Color.black);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .60F));
        g2.fillRect(0, 0, boardWidth, boardHeight);
        g2.setComposite(c);
        g2.setColor(Color.red);
        if(input.ppp.getColor()) {
            g2.drawImage(ImageManager.Q, tileSize*2, boardHeight/2, tileSize, tileSize, null);
            g2.drawImage(ImageManager.R, tileSize*3, boardHeight/2, tileSize, tileSize, null);
            g2.drawImage(ImageManager.B, tileSize*4, boardHeight/2, tileSize, tileSize, null);
            g2.drawImage(ImageManager.N, tileSize*5, boardHeight/2, tileSize, tileSize, null);
        } else{
            g2.drawImage(ImageManager.q, tileSize*2, boardHeight/2, tileSize, tileSize, null);
            g2.drawImage(ImageManager.r, tileSize*3, boardHeight/2, tileSize, tileSize, null);
            g2.drawImage(ImageManager.b, tileSize*4, boardHeight/2, tileSize, tileSize, null);
            g2.drawImage(ImageManager.n, tileSize*5, boardHeight/2, tileSize, tileSize, null);
        }
    }



    public void drawHoverMove(Graphics g){
        if(hoverX >= 0 && hoverX < 8 && hoverY >= 0 && hoverY < 8){
            Graphics2D g2 = (Graphics2D)g;
            Composite c = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .75F));
            g2.setColor(green);
            g2.fillRect(hoverX*tileSize, hoverY*tileSize, tileSize, tileSize);
            g2.setComposite(c);
        }
    }

    public void updateHoverMove(int x, int y){
        hoverX = x;
        hoverY = y;
    }

    public void drawLog(Graphics g){
        g.setColor(new Color(30, 30, 30));
        g.fillRect(boardWidth, 0, screenWidth-boardWidth, screenHeight);
        g.setFont(new Font("Calibre", Font.BOLD, 20));
        g.setColor(Color.white);
        int textHeight = 30;
        g.drawString("Moves:", screenWidth-(screenWidth-boardWidth)/2-textHeight, textHeight);
        for (int i = 0; i < log.size(); i += 2) {
            if((i/2 + 2) * textHeight - logOffset >= screenHeight-textHeight) logOffset++;
            if((i/2 + 2) * textHeight - logOffset >= textHeight*2) {
                g.drawString(log.get(i), boardWidth + textHeight, (i / 2 + 2) * textHeight - logOffset);
                if (i + 1 < log.size()) g.drawString(log.get(i + 1), boardWidth + textHeight * 4, (i / 2 + 2) * textHeight - logOffset);
            }
        }


    }

    public void drawMoves(Graphics g){
        if(selectedPiece != null){
            g.setColor(green);
            for(Point p : selectedPiece.getMoves())
                if(p.x != hoverX || p.y != hoverY) g.fillOval(tileSize*p.x + tileSize/3, tileSize*p.y + tileSize/3, tileSize/3, tileSize/3);
        }
    }
    private void drawCoords(Graphics g){
        g.setFont(new Font("Georgia", Font.BOLD, 17));
        int offset = 5;
        for(int i = 0 ; i < 8; i++){
            g.setColor(i%2==0?lightBrown:darkBrown);
            g.drawString(""+(char)('A'+i), i*tileSize+offset, screenHeight-offset);
        }
        g.setFont(new Font("Georgia", Font.BOLD, 20));
        for(int i = 0 ; i < 8; i++){
            g.setColor(i%2==1?lightBrown:darkBrown);
            g.drawString(""+(i+1), boardWidth-offset*3, screenHeight-i*tileSize-tileSize+offset*3);
        }
    }
    public void drawGameTiles(Graphics g){
        for(int i = 0 ;i < 8; i++){
            for(int j = 0 ; j < 8; j++){
                if((j+i) % 2 == 0) g.setColor(lightBrown);
                else g.setColor(darkBrown);
                if((board[i][j] != null && board[i][j].equals(selectedPiece))) {
                    Graphics2D g2 = (Graphics2D)g;
                    Composite c = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .75f));
                    g2.setColor(green);
                    g2.fillRect(tileSize*i, tileSize*j, tileSize, tileSize);
                    g2.setComposite(c);
                }
                else g.fillRect(tileSize*i, tileSize*j, tileSize, tileSize);
            }
        }
    }
    public void drawPieces(Graphics g){
        for(int i = 0 ; i < 8; i++)
            for(int j = 0 ; j < 8; j++)
                if(board[i][j] != null) g.drawImage(board[i][j].bi, i*tileSize, j*tileSize, tileSize, tileSize, null);
    }
    public void initBoard(){
        for (int i = 0, r = 0, c = 0; i < fen.length(); i++, c++) {
            if (Character.isDigit(fen.charAt(i))) {
                c += fen.charAt(i) - '0' - 1;
                continue;
            }
            if (fen.charAt(i) == '/') {
                r++;
                c = -1;
                continue;
            }
            switch (fen.charAt(i)) {
                case 'b':
                case 'B':
                    board[c][r] = new Bishop(fen.charAt(i), new Point(c, r));
                    break;
                case 'k':
                case 'K':
                    board[c][r] = new King(fen.charAt(i), new Point(c, r));
                    break;
                case 'n':
                case 'N':
                    board[c][r] = new Knight(fen.charAt(i), new Point(c, r));
                    break;
                case 'p':
                case 'P':
                    board[c][r] = new Pawn(fen.charAt(i), new Point(c, r));
                    break;
                case 'q':
                case 'Q':
                    board[c][r] = new Queen(fen.charAt(i), new Point(c, r));
                    break;
                case 'r':
                case 'R':
                    board[c][r] = new Rook(fen.charAt(i), new Point(c, r));
                    break;
                default:
                    board[c][r] = null;
            }
        }
    }
    public void setGameSize(int width, int height){
        Dimension d = new Dimension(width, height);
        this.setMinimumSize(d);
        this.setPreferredSize(d);
        this.setMaximumSize(d);
    }
    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }
}

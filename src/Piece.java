import java.awt.image.BufferedImage;
import java.util.ArrayList;


public abstract class Piece implements Cloneable{
    char name;
    Point p;
    BufferedImage bi;
    int[] dx = {1, 0, 0, -1, 1, 1, -1, -1};
    int[] dy = {0, 1, -1, 0, 1, -1, 1, -1};
    Piece(char name, Point p){
        this.name = name;
        bi = switch(name){
            case 'b'->ImageManager.b;
            case 'B'->ImageManager.B;
            case 'k'->ImageManager.k;
            case 'K'->ImageManager.K;
            case 'n'->ImageManager.n;
            case 'N'->ImageManager.N;
            case 'p'->ImageManager.p;
            case 'P'->ImageManager.P;
            case 'q'->ImageManager.q;
            case 'Q'->ImageManager.Q;
            case 'r'->ImageManager.r;
            case 'R'->ImageManager.R;
            default -> null;
        };
        this.p = p;
    }
    public abstract ArrayList<Point> getMoves();
    public ArrayList<Point> getLine(int x, int y, int xstep, int ystep){
        ArrayList<Point> list = new ArrayList<>();
        x += xstep;
        y += ystep;
        while(x >= 0 && x < 8 && y >= 0 && y < 8){
            if(GamePanel.board[x][y] != null){
                if(isOppositeColor(GamePanel.board[x][y])) list.add(new Point(x, y));
                break;
            }
            list.add(new Point(x, y));
            x += xstep;
            y += ystep;
        }
        return list;
    }
    public boolean isOppositeColor(Piece p){
        return p.getColor() != this.getColor();
    }
    public boolean isTurn(boolean turn) {
        return Character.isUpperCase(name) == turn;
    }
    public boolean getColor(){
        return Character.isUpperCase(name);
    }
    public static String getCoord(Point p){
        return "" + (char)('a' + p.x) + (8-p.y);
    }

    public ArrayList<Point> validMoves(ArrayList<Point> list) {
        Piece[][] _board = boardHelper();
        Piece clonedPiece = this.clone();

        for (int n = 0 ; n < list.size(); n++) {
            Point p = list.get(n);
            if (p.x < 0 || p.x >= 8 || p.y < 0 || p.y >= 8){
                list.remove(n);
                n--;
                continue;
            }
            Piece takenPiece = _board[p.x][p.y];
            _board[clonedPiece.p.x][clonedPiece.p.y] = null;
            int oldx = clonedPiece.p.x, oldy = clonedPiece.p.y;
            clonedPiece.p.x = p.x;
            clonedPiece.p.y = p.y;
            _board[clonedPiece.p.x][clonedPiece.p.y] = clonedPiece;

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) System.out.print(_board[j][i] == null ? "[ ]" : "[" + _board[j][i].name + "]");
                System.out.print('\n');
            }
            System.out.println(this.name + " x:" + clonedPiece.p.x + " y:" + clonedPiece.p.y + " " + iskingInCheck(_board, clonedPiece.getColor()));
            System.out.println(takenPiece != null ? takenPiece.name : "N/A");

            if(iskingInCheck(_board, this.getColor())){
                list.remove(n);
                n--;
            }

            _board[clonedPiece.p.x][clonedPiece.p.y] = takenPiece;
            clonedPiece.p.x = oldx;
            clonedPiece.p.y = oldy;
            _board[clonedPiece.p.x][clonedPiece.p.y] = clonedPiece;

        }
        return list;
    }

    public boolean iskingInCheck(Piece[][] _board, boolean color){
        for(int i = 0 ; i < 8;i++)
            for(int j = 0 ; j < 8; j++)
                if(_board[i][j] instanceof King && _board[i][j].getColor() == color && ((King) _board[i][j]).isChecked()) return true;
        return false;
    }

    private Piece[][] boardHelper(){
        Piece[][] _board = new Piece[8][8];
        for(int i = 0 ; i < 8; i++)
            for(int j = 0 ; j < 8; j++){
                if(GamePanel.board[i][j] != null) _board[i][j] = GamePanel.board[i][j].clone();
                else _board[i][j] = null;
            }
        return _board;
    }
    @Override
    public Piece clone() {
        try {
            Piece cloned = (Piece) super.clone();
            cloned.p = new Point(this.p.x, this.p.y);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

import java.util.ArrayList;
import java.util.Collections;

public class King extends Piece{
    boolean firstMove = true;
    Point castleS, castleL;
    King(char name, Point p) {
        super(name, p);
        castleS = new Point(p.x+2, p.y);
        castleL = new Point(p.x-2, p.y);
    }

    @Override
    public ArrayList<Point> getMoves() {
        ArrayList<Point> list = new ArrayList<>();
        for(int i = 0 ; i < 8; i++) {
            if(p.x+dx[i] >= 0 && p.x+dx[i] < 8 && p.y+dy[i] >= 0 && p.y+dy[i] < 8 && GamePanel.board[p.x + dx[i]][p.y + dy[i]] != null)
                if (!this.isOppositeColor(GamePanel.board[p.x + dx[i]][p.y + dy[i]])) continue;
            list.add(new Point(p.x + dx[i], p.y + dy[i]));

        }
        if(p.x+2 < 8 &&
                GamePanel.board[p.x+2][p.y] == null &&
                GamePanel.board[p.x+1][p.y] == null &&
                firstMove &&
                !validMoves(new ArrayList<>(Collections.singletonList(new Point(p.x + 1, p.y)))).isEmpty() &&
                GamePanel.board[p.x+3][p.y] instanceof Rook && ((Rook) GamePanel.board[p.x+3][p.y]).firstMove)
            list.add(castleS);
        if(p.x-2 >= 0 &&
                GamePanel.board[p.x-2][p.y] == null &&
                GamePanel.board[p.x-1][p.y] == null &&
                firstMove &&
                !validMoves(new ArrayList<>(Collections.singletonList(new Point(p.x - 1, p.y)))).isEmpty() &&
                GamePanel.board[p.x-4][p.y] instanceof Rook && ((Rook) GamePanel.board[p.x-4][p.y]).firstMove)
            list.add(castleL);
        return validMoves(list);
    }

    public boolean isChecked(){
        ArrayList<Point> list = new ArrayList<>();
        boolean checked = false;
        //rook and queen dirs
        for(int i = 0 ; i < 4; i++) list.addAll(getLine(p.x, p.y, dx[i], dy[i]));
        for(Point p : list)
            if((GamePanel.board[p.x][p.y] instanceof Rook && GamePanel.board[p.x][p.y].isOppositeColor(this)) || (GamePanel.board[p.x][p.y] instanceof Queen && GamePanel.board[p.x][p.y].isOppositeColor(this)))
                checked = true;
        list.clear();
        //bishop and queen dirs
        for(int i = 4 ; i < 8; i++) list.addAll(getLine(p.x, p.y, dx[i], dy[i]));
        for(Point p : list)
            if((GamePanel.board[p.x][p.y] instanceof Bishop && GamePanel.board[p.x][p.y].isOppositeColor(this)) || (GamePanel.board[p.x][p.y] instanceof Queen && GamePanel.board[p.x][p.y].isOppositeColor(this)))
                checked = true;
        list.clear();
        //knight dirs
        int[] ndx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] ndy = {1, -1, 2, -2, 2, -2, 1, -1};
        for(int i = 0 ; i < 8; i++) {
            if(p.x+ndx[i] >= 0 && p.x+ndx[i] < 8 && p.y+ndy[i] >= 0 && p.y+ndy[i] < 8 && GamePanel.board[p.x + ndx[i]][p.y + ndy[i]] != null)
                if(GamePanel.board[p.x + ndx[i]][p.y + ndy[i]] instanceof Knight && GamePanel.board[p.x + ndx[i]][p.y + ndy[i]].isOppositeColor(this))
                    checked = true;
        }
        //pawn dirs
        if (p.x + 1 < 8 && p.y + (!getColor() ? 1 : -1) >= 0 && p.y + (!getColor() ? 1 : -1) < 8 && GamePanel.board[p.x + 1][p.y + (!getColor() ? 1 : -1)] instanceof Pawn && GamePanel.board[p.x + 1][p.y + (!getColor() ? 1 : -1)].isOppositeColor(this))
            checked = true;
        if (p.x - 1 >= 0 && p.y + (!getColor() ? 1 : -1) >= 0 && p.y + (!getColor() ? 1 : -1) < 8 && GamePanel.board[p.x - 1][p.y + (!getColor() ? 1 : -1)] instanceof Pawn && GamePanel.board[p.x - 1][p.y + (!getColor() ? 1 : -1)].isOppositeColor(this))
            checked = true;
        return checked;
    }

}

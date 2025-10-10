import java.util.ArrayList;

public class Pawn extends Piece{
    boolean firstMove = true, lastFirstMove = false;
    int move;
    Pawn(char name, Point p) {
        super(name, p);
        move = this.getColor() ? -1 : 1;
    }

    @Override
    public ArrayList<Point> getMoves() {
        ArrayList<Point> list = new ArrayList<>();
        if(GamePanel.board[p.x][p.y+move] == null) list.add(new Point(p.x, p.y+move));
        if((this.getColor() ? p.y >= 2 : p.y < 6) && GamePanel.board[p.x][p.y+move*2] == null && firstMove && GamePanel.board[p.x][p.y+move] == null) list.add(new Point(p.x, p.y+move*2));
        if(p.x >= 1 &&
                ((GamePanel.board[p.x-1][p.y+move] != null &&
                    GamePanel.board[p.x-1][p.y+move].isOppositeColor(this)) ||
                (GamePanel.board[p.x-1][p.y] instanceof Pawn && ((Pawn) GamePanel.board[p.x-1][p.y]).lastFirstMove && GamePanel.board[p.x-1][p.y].isOppositeColor(this)))) list.add(new Point(p.x-1, p.y+move));
        if(p.x < 7 &&
                ((GamePanel.board[p.x+1][p.y+move] != null &&
                    GamePanel.board[p.x+1][p.y+move].isOppositeColor(this)) ||
                (GamePanel.board[p.x+1][p.y] instanceof Pawn && ((Pawn) GamePanel.board[p.x+1][p.y]).lastFirstMove && GamePanel.board[p.x+1][p.y].isOppositeColor(this)))) list.add(new Point(p.x+1, p.y+move));
        return validMoves(list);
    }
}

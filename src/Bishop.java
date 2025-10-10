import java.util.ArrayList;

public class Bishop extends Piece{
    Bishop(char name, Point p) {
        super(name, p);
    }

    @Override
    public ArrayList<Point> getMoves() {
        ArrayList<Point> list = new ArrayList<>();
        for(int i = 4 ; i < 8; i++)
            list.addAll(super.getLine(p.x, p.y, dx[i], dy[i]));
        return validMoves(list);
    }
}

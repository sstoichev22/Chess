import java.util.ArrayList;

public class Queen extends Piece{
    Queen(char name, Point p) {
        super(name, p);
    }

    @Override
    public ArrayList<Point> getMoves() {
        ArrayList<Point> list = new ArrayList<>();
        for(int i = 0 ; i < 8; i++)
            list.addAll(super.getLine(p.x, p.y, dx[i], dy[i]));
        return validMoves(list);
    }
}

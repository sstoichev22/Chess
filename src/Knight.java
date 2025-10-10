import java.util.ArrayList;

public class Knight extends Piece{

    Knight(char name, Point p) {
        super(name, p);
        dx = new int[]{-2, -2, -1, -1, 1, 1, 2, 2};
        dy = new int[]{1, -1, 2, -2, 2, -2, 1, -1};
    }

    @Override
    public ArrayList<Point> getMoves() {
        ArrayList<Point> list = new ArrayList<>();
        for(int i = 0 ; i < 8; i++) {
            if(p.x+dx[i] >= 0 && p.x+dx[i] < 8 && p.y+dy[i] >= 0 && p.y+dy[i] < 8 && GamePanel.board[p.x + dx[i]][p.y + dy[i]] != null)
                if(!this.isOppositeColor(GamePanel.board[p.x + dx[i]][p.y + dy[i]]))
                    continue;
            list.add(new Point(p.x + dx[i], p.y + dy[i]));
        }
        return validMoves(list);
    }
}

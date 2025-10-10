import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Input implements MouseListener, MouseMotionListener {
    GamePanel gamePanel;
    int mouseX, mouseY;
    Piece ppp = null;
    Rectangle ppq, ppr, ppb, ppn;
    static boolean checkW = false, checkB = false;

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        ppq = new Rectangle(gamePanel.tileSize * 2, gamePanel.boardHeight / 2, gamePanel.tileSize, gamePanel.tileSize);
        ppr = new Rectangle(gamePanel.tileSize * 3, gamePanel.boardHeight / 2, gamePanel.tileSize, gamePanel.tileSize);
        ppb = new Rectangle(gamePanel.tileSize * 4, gamePanel.boardHeight / 2, gamePanel.tileSize, gamePanel.tileSize);
        ppn = new Rectangle(gamePanel.tileSize * 5, gamePanel.boardHeight / 2, gamePanel.tileSize, gamePanel.tileSize);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX() / gamePanel.tileSize;
        mouseY = e.getY() / gamePanel.tileSize;

        if (ppp != null) handlePawnPromotion();
        else if (gamePanel.selectedPiece != null && gamePanel.selectedPiece.isTurn(GamePanel.turn)) handlePieceMove();
        else selectOrDeselectPiece();
    }

    private void handlePawnPromotion() {
        if (ppq.contains(mouseX * gamePanel.tileSize, mouseY * gamePanel.tileSize))
            GamePanel.board[ppp.p.x][ppp.p.y] = new Queen(ppp.getColor() ? 'Q' : 'q', ppp.p);
        else if (ppr.contains(mouseX * gamePanel.tileSize, mouseY * gamePanel.tileSize))
            GamePanel.board[ppp.p.x][ppp.p.y] = new Rook(ppp.getColor() ? 'R' : 'r', ppp.p);
        else if (ppb.contains(mouseX * gamePanel.tileSize, mouseY * gamePanel.tileSize))
            GamePanel.board[ppp.p.x][ppp.p.y] = new Bishop(ppp.getColor() ? 'B' : 'b', ppp.p);
        else if (ppn.contains(mouseX * gamePanel.tileSize, mouseY * gamePanel.tileSize))
            GamePanel.board[ppp.p.x][ppp.p.y] = new Knight(ppp.getColor() ? 'N' : 'n', ppp.p);
        else return;
        ppp = null;
    }

    private void handlePieceMove() {
        boolean moved = false;
        for (Point p : gamePanel.selectedPiece.getMoves()) {
            if (p.x == mouseX && p.y == mouseY) {
                moved = true;
                Piece ex = GamePanel.board[mouseX][mouseY];
                if (gamePanel.selectedPiece.name == 'p' || gamePanel.selectedPiece.name == 'P') {
                    if (ex != null) gamePanel.log.add(Piece.getCoord(gamePanel.selectedPiece.p).charAt(0) + "x" + Piece.getCoord(new Point(mouseX, mouseY)));
                    else gamePanel.log.add(Piece.getCoord(new Point(mouseX, mouseY)));
                } else gamePanel.log.add(gamePanel.selectedPiece.name + (ex != null ? "x" + Piece.getCoord(ex.p) : Piece.getCoord(new Point(mouseX, mouseY))));

                GamePanel.board[gamePanel.selectedPiece.p.x][gamePanel.selectedPiece.p.y] = null;
                gamePanel.selectedPiece.p.x = mouseX;
                gamePanel.selectedPiece.p.y = mouseY;
                GamePanel.board[mouseX][mouseY] = gamePanel.selectedPiece;
                if (GamePanel.board[mouseX][mouseY] instanceof Pawn pawn) {
                    if (pawn.firstMove) {
                        pawn.firstMove = false;
                        pawn.lastFirstMove = true;
                    } else if (pawn.lastFirstMove) pawn.lastFirstMove = false;
                }

                if (GamePanel.board[mouseX][mouseY] instanceof Rook) ((Rook) GamePanel.board[mouseX][mouseY]).firstMove = false;
                if (GamePanel.board[mouseX][mouseY] instanceof King && ((King) GamePanel.board[mouseX][mouseY]).firstMove){
                    if(((King) GamePanel.board[mouseX][mouseY]).castleS.equals(p)){
                        ((King)GamePanel.board[mouseX][mouseY]).firstMove = false;
                        Rook temp = (Rook) GamePanel.board[mouseX+1][mouseY];
                        GamePanel.board[mouseX+1][mouseY] = null;
                        temp.p.x = mouseX-1;
                        GamePanel.board[mouseX-1][mouseY] = temp;
                    }
                    if(((King) GamePanel.board[mouseX][mouseY]).castleL.equals(p)){
                        ((King)GamePanel.board[mouseX][mouseY]).firstMove = false;
                        Rook temp = (Rook) GamePanel.board[mouseX-2][mouseY];
                        GamePanel.board[mouseX-2][mouseY] = null;
                        temp.p.x = mouseX+1;
                        GamePanel.board[mouseX+1][mouseY] = temp;
                    }
                }
                areKingsInCheck();
                if(checkW || checkB) gamePanel.log.set(gamePanel.log.size()-1, gamePanel.log.get(gamePanel.log.size()-1)+"+");
                promotePawnIfNecessary();
                gamePanel.updateHoverMove(-1, -1);
                GamePanel.turn = !GamePanel.turn;
                gamePanel.selectedPiece = null;
            }
        }
        if (!moved) selectOrDeselectPiece();
    }

    private void areKingsInCheck(){
        checkW = false;
        checkB = false;
        for(int i = 0 ;  i < 8; i++)
            for(int j = 0 ; j < 8; j++)
                if(GamePanel.board[i][j] instanceof King)
                    if(((King) GamePanel.board[i][j]).isChecked()) {
                        if (GamePanel.board[i][j].getColor()) checkW = true;
                        else checkB = true;
                    }
    }

    private void promotePawnIfNecessary() {
        int pawnY = gamePanel.selectedPiece.p.y;
        if (gamePanel.selectedPiece instanceof Pawn && (pawnY == 0 || pawnY == 7)) {
            ppp = GamePanel.board[gamePanel.selectedPiece.p.x][pawnY];
            GamePanel.board[gamePanel.selectedPiece.p.x][pawnY] = null;
        }
    }

    private void selectOrDeselectPiece() {
        Piece targetPiece = GamePanel.board[mouseX][mouseY];
        if (targetPiece != null && targetPiece.isTurn(GamePanel.turn)) gamePanel.selectedPiece = targetPiece;
        else gamePanel.selectedPiece = null;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX() / gamePanel.tileSize;
        mouseY = e.getY() / gamePanel.tileSize;
        updateHoverMove();
    }

    private void updateHoverMove() {
        boolean updated = false;
        if (gamePanel.selectedPiece != null) {
            for (Point p : gamePanel.selectedPiece.getMoves()) {
                if (p.x == mouseX && p.y == mouseY) {
                    gamePanel.updateHoverMove(mouseX, mouseY);
                    updated = true;
                    break;
                }
            }
        }
        if (!updated) {
            gamePanel.updateHoverMove(-1, -1);
        }
    }
}

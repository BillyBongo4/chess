import chess.*;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, DeploymentException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess client.Client: " + piece);

        var serverUrl = "http://localhost:8080";
        new Repl(serverUrl).run();
    }
}
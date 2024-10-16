import chess.*;
import dataaccess.MemoryDataAccess;
import server.Server;
import service.RegisterService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(memoryDataAccess);
        Server server = new Server(registerService);
        
        server.run(8080);
    }
}
import chess.*;
import dataaccess.MemoryDataAccess;
import server.Server;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(memoryDataAccess);
        LoginService loginService = new LoginService(memoryDataAccess);
        LogoutService logoutService = new LogoutService(memoryDataAccess);
        Server server = new Server(registerService, loginService, logoutService);

        server.run(8080);
    }
}
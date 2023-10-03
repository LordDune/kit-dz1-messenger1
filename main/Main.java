package pac.main;

public class Main {
    public static void main(String[] args) {

        ServerWindow server = new ServerWindow(); // создание сервера
        server.newClient(); // создание окна клиента
        server.newClient(); // создание окна клиента
    }
}
package pac.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class ServerWindow extends JFrame {

    enum Condition { // состояние сервера (вкл/выкл)
        Online, Offline
    }

    private static final int WIDTH = 400;
    private static final int HEIGHT = 350;
    Condition condition = Condition.Offline; // начальное состояние сервера (выкл)
    JTextArea logArea;
    JButton start = new JButton("Start");
    JButton stop = new JButton("Stop");
    ArrayList<ClientWindow> clients = new ArrayList<>(); // список клиентских окон, которые будут создаваться

    public ServerWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setTitle("Server");
        panelBottom();
        panelLog();
        setVisible(true);
    }

    public void newClient(){ // метод создания клиента
        ClientWindow client = new ClientWindow(this);
        clients.add(client);
    }

    private void panelLog() { // добавление поля, где отображается чат
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        add(new JScrollPane(logArea));
    }

    public void changeCondition(Condition condition){ // метод изменения состояния сервера (цвет кнопок)
        if (this.condition != condition) {
            this.condition = condition;
        }
        if (condition == Condition.Online) {
            stop.setBackground(Color.LIGHT_GRAY);
            start.setBackground(Color.GREEN);
            logArea.setText("");
        } else {
            start.setBackground(Color.LIGHT_GRAY);
            stop.setBackground(Color.RED);
        }
    }

    public void panelBottom(){ // добавление поля с кнопками
        JPanel panelBottom = new JPanel(new GridLayout(1, 2));
        panelBottom.add(start);
        panelBottom.add(stop);
        add(panelBottom, BorderLayout.SOUTH);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // старт сервера, считывание лога чата из файла
                changeCondition(Condition.Online);
                try {
                    BufferedReader br = new BufferedReader (new FileReader("log.txt"));
                    String str;
                    while ((str = br.readLine()) != null){
                        logArea.append(str + "\n");
                    }
                    br.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        stop.addActionListener(new ActionListener() { // стоп сервера, вызов у всех клиентов метода отключения
            @Override
            public void actionPerformed(ActionEvent e) {
                changeCondition(Condition.Offline);
                for (ClientWindow client: clients) {
                    client.changeCondition(ClientWindow.Condition.Offline);
                }
            }
        });
    }

    public void saveLog(String text){ // сохранение передаваемого текста в лог файла
        try {
            FileWriter file = new FileWriter("./log.txt", true);
            file.append(text);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void delivered(String text) {  // добавление сообщений в окно сервера и отправка сообщений всем экземплярам клиентского окна
        logArea.append(text);
        for (ClientWindow client: clients) {
            if (client.condition == ClientWindow.Condition.Online) {
                client.inputText(text); // вызов у клиента метода добавления сообщения в окно чата
            }
        }
        saveLog(text); // вызов метода добавления сообщения в лог файл
    }
}
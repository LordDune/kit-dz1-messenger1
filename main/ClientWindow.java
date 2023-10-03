package pac.main;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClientWindow extends JFrame {

    enum Condition { // состояние клиента (вкл/выкл)
        Online, Offline
    }
    Condition condition = Condition.Offline; // начальное состояние сервера (выкл)

    private static final int WIDTH = 400;
    private static final int HEIGHT = 350;
    String TITLE = "Client";
    String SEND_BUTTON_TITLE = "send";
    String logFile = "./log.txt";
    String LOGIN_BUTTON_TITLE = "login";

    String ONLINE_MESSAGE = "Соединение установлено\n";
    String DISCONNECT_MESSAGE = "WARNING: Сервер недоступен\n";
    String SET_VALUE_MESSAGE = "WARNING: Введите ";

    ServerWindow serverWindow;
    JButton login, send;
    JPanel panelTop, panelBottom;
    JPasswordField password;
    JTextArea logArea;
    JTextField name, ip, port, textInput;
    JLabel clear;

    public ClientWindow(ServerWindow serverWindow) {
        this.serverWindow = serverWindow;
        setVisible(true);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setTitle(TITLE);
        login = new JButton(LOGIN_BUTTON_TITLE);
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // подключение к серверу при условии ввода всех данных и вклюенном состоянии сервера
                if (isConnectServer() &
                        isNotNull(name, "имя") &
                        isNotNull(password, "пароль") &
                        isNotNull(ip, "ip адрес сервера") &
                        isNotNull(port, "порт")) {
                    changeCondition(Condition.Online);
                    logArea.setText(""); // очистка окна сообщений
                    try { // восстановление окна сообщений из лог файла при условии подключения клиента
                        BufferedReader br = new BufferedReader (new FileReader(logFile));
                        String str;
                        while ((str = br.readLine()) != null){
                            logArea.append(str + "\n");
                        }
                        br.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        panelTop(); // добавление панели ввода данных и подключения
        log(); // добавление окна чата
        panelBottom(); // добавление панели ввода сообщения
        setVisible(true);
    }

    private void log() {
        logArea = new JTextArea();
        add(new JScrollPane(logArea));
    }

    private void panelBottom() { // метод добавления панели ввода сообщения
        panelBottom = new JPanel(new BorderLayout());
        textInput = new JTextField();
        send = new JButton(SEND_BUTTON_TITLE);
        panelBottom.add(textInput);
        panelBottom.add(send, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);
        send.addActionListener(new ActionListener() {
            @Override  // обработка нажатия кнопки send (отправка сообщений)
            public void actionPerformed(ActionEvent e) {
                sendMessage(); // вызов метода отправки сообщения
            }
        });

        textInput.addKeyListener(new KeyListener() { // обработка нажатия клавиши энтер при вводе сообщения

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(); // вызов метода отправки сообщения
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    public void panelTop() { // панель ввода данных и подключения
        panelTop = new JPanel(new GridLayout(2,3));
        ip = new JTextField();
        port = new JTextField();
        name = new JTextField();
        password = new JPasswordField();
        clear = new JLabel();
        panelTop.add(ip);
        panelTop.add(port);
        panelTop.add(clear);
        panelTop.add(name);
        panelTop.add(password);
        panelTop.add(login);
        add(panelTop, BorderLayout.NORTH);
    }

    public void inputText(String text){ // метода добавления сообщения в окно чата (вызывается на сервере)
        logArea.append(text);
    }

    public void changeCondition(Condition condition) { // метод изменения состояния клмента (цвет кнопок)
        if (this.condition != condition) {
            this.condition = condition;
        }
        if (this.condition == Condition.Offline) {
            login.setBackground(Color.RED);
        } else if (this.condition == Condition.Online) {
            login.setBackground(Color.GREEN);
            logArea.append(ONLINE_MESSAGE);
        }
    }

    public boolean isConnectServer(){ // проверка сервера на доступность
        if (serverWindow.condition == ServerWindow.Condition.Online) {
            return true;
        } else {
            logArea.append(DISCONNECT_MESSAGE);
            return false;
        }
    }

    public boolean isNotNull(JTextComponent e, String text){ // проверка полей на наличие введенных значений
        if (e.getText().length() != 0) {
            return true;
        } else {
            logArea.append(SET_VALUE_MESSAGE + text + "\n");
            return false;
        }
    }

    public void sendMessage(){ // отправка сообщений на сервер
        if (condition == Condition.Online) {
            if (textInput.getText().length() != 0) {
                String str = textInput.getText();
                String text = name.getText() + ": " + str + "\n";
                textInput.setText("");
                serverWindow.delivered(text);
            }
        } else {
            logArea.append(DISCONNECT_MESSAGE);
        }
    }
}



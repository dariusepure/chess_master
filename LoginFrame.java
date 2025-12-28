import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private Main app;
    private ChessGUI gui;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame(Main app, ChessGUI gui) {
        this.app = app;
        this.gui = gui;

        setTitle("Chess - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Logo/Titlu
        JLabel titleLabel = new JLabel("Chess Master", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel pentru formular
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Butoane
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        JButton registerButton = new JButton("Create Account");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        formPanel.add(loginButton);
        formPanel.add(registerButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);



        add(mainPanel);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password!");
            return;
        }

        User user = app.login(email, password);
        if (user != null) {
            gui.showMainMenu();
        } else {
            JOptionPane.showMessageDialog(this, "Login failed! Check your credentials.");
        }
    }

    private void register() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password!");
            return;
        }

        // Dialog pentru confirmare parolă
        JPasswordField confirmField = new JPasswordField();
        Object[] message = {"Confirm Password:", confirmField};

        int option = JOptionPane.showConfirmDialog(this, message,
                "Confirm Password", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String confirmPassword = new String(confirmField.getPassword());

            User user = app.newAccount(email, password, confirmPassword);
            if (user != null) {
                gui.showMainMenu();
            } else {
                JOptionPane.showMessageDialog(this, "Account creation failed!");
            }
        }
    }
}
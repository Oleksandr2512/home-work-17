import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Calculator {
    private JFrame frame;
    private JTextField display;
    private String memoryValue = "";
    private boolean isMemoryActive = false;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Calculator window = new Calculator();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Calculator() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Calculator");
        frame.setBounds(100, 100, 400, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // Display Area
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 30));
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        frame.getContentPane().add(display, BorderLayout.NORTH);

        // Panel for buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 10, 10));
        frame.getContentPane().add(panel, BorderLayout.CENTER);

        // Buttons and their actions
        addButton(panel, "7", e -> appendToDisplay("7"));
        addButton(panel, "8", e -> appendToDisplay("8"));
        addButton(panel, "9", e -> appendToDisplay("9"));
        addButton(panel, "/", e -> appendToDisplay("/"));

        addButton(panel, "4", e -> appendToDisplay("4"));
        addButton(panel, "5", e -> appendToDisplay("5"));
        addButton(panel, "6", e -> appendToDisplay("6"));
        addButton(panel, "*", e -> appendToDisplay("*"));

        addButton(panel, "1", e -> appendToDisplay("1"));
        addButton(panel, "2", e -> appendToDisplay("2"));
        addButton(panel, "3", e -> appendToDisplay("3"));
        addButton(panel, "-", e -> appendToDisplay("-"));

        addButton(panel, "0", e -> appendToDisplay("0"));
        addButton(panel, ".", e -> appendToDisplay("."));
        addButton(panel, "=", e -> calculateResult());
        addButton(panel, "+", e -> appendToDisplay("+"));

        addButton(panel, "x²", e -> calculateSquare());
        addButton(panel, "√", e -> calculateSquareRoot());
        addButton(panel, "C", e -> clearDisplay());
        addButton(panel, "M+", e -> storeMemory());

        addButton(panel, "MC", e -> clearMemory());
        addButton(panel, "MR", e -> recallMemory());
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.addActionListener(action);
        button.setBackground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);

        // Add color change effect when button is pressed
        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                button.setBackground(Color.GRAY);
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(Color.LIGHT_GRAY);
            }
        });

        panel.add(button);
    }

    private void appendToDisplay(String value) {
        display.setText(display.getText() + value);
    }

    private void clearDisplay() {
        display.setText("");
    }

    private void calculateResult() {
        try {
            String result = String.valueOf(eval(display.getText()));
            display.setText(result);
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    private void calculateSquare() {
        try {
            double value = Double.parseDouble(display.getText());
            display.setText(String.valueOf(value * value));
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    private void calculateSquareRoot() {
        try {
            double value = Double.parseDouble(display.getText());
            display.setText(String.valueOf(Math.sqrt(value)));
        } catch (Exception e) {
            display.setText("Error");
        }
    }

    private void storeMemory() {
        memoryValue = display.getText();
        isMemoryActive = true;
    }

    private void clearMemory() {
        memoryValue = "";
        isMemoryActive = false;
    }

    private void recallMemory() {
        if (isMemoryActive) {
            display.setText(memoryValue);
        }
    }

    private double eval(String expression) {
        // Basic evaluation for simple mathematical expressions
        return new Object() {
            int pos = -1, c;

            void nextChar() {
                c = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (c == ' ') nextChar();
                if (c == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) c);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((c >= '0' && c <= '9') || c == '.') {
                    while ((c >= '0' && c <= '9') || c == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) c);
                }

                return x;
            }
        }.parse();
    }
}


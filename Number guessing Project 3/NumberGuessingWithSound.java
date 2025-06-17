import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import java.io.*;

public class NumberGuessingWithSound extends JFrame implements ActionListener {
    private int randomNumber, maxNumber, maxAttempts, attempts, wins = 0, losses = 0;
    private boolean gameOver = false;

    private JTextField guessField;
    private JButton guessButton, resetButton;
    private JLabel messageLabel, attemptsLabel, scoreLabel, rangeLabel;
    private JTextArea historyArea;
    private JComboBox<String> difficultyBox;

    private List<Integer> guessHistory = new ArrayList<>();

    public NumberGuessingWithSound() {
        setTitle("🎯 Number Guessing Game ");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        difficultyBox = new JComboBox<>(new String[]{"Easy (1–100)", "Medium (1–500)", "Hard (1–1000)"});
        difficultyBox.setFont(new Font("Times New Roman", Font.BOLD, 16));
        difficultyBox.addActionListener(e -> resetGame());
        rangeLabel = new JLabel("", JLabel.CENTER);
        rangeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        topPanel.add(difficultyBox);
        topPanel.add(rangeLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        JPanel inputPanel = new JPanel();
        guessField = new JTextField(10);
        guessField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(new JLabel("Enter guess: "));
        inputPanel.add(guessField);

        guessButton = new JButton("Guess");
        guessButton.setFont(new Font("Arial", Font.BOLD, 16));
        guessButton.addActionListener(this);
        inputPanel.add(guessButton);
        centerPanel.add(inputPanel);

        messageLabel = new JLabel("Guess the number!", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        centerPanel.add(messageLabel);

        attemptsLabel = new JLabel("Attempts Left: ", JLabel.CENTER);
        attemptsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        centerPanel.add(attemptsLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Right Panel: History
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyPanel.add(new JLabel("📜 Guess History:"), BorderLayout.NORTH);
        historyPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        add(historyPanel, BorderLayout.EAST);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        scoreLabel = new JLabel("Wins: 0 | Losses: 0", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton = new JButton("🔁 New Game");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.addActionListener(e -> resetGame());

        bottomPanel.add(scoreLabel);
        bottomPanel.add(resetButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        resetGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver)
            return;

        String input = guessField.getText().trim();
        try {
            int guess = Integer.parseInt(input);
            if (guess < 1 || guess > maxNumber) {
                messageLabel.setText("⚠️ Enter number between 1 and " + maxNumber);
                return;
            }

            guessHistory.add(guess);
            attempts++;
            int remaining = maxAttempts - attempts;
            updateHistory();

            if (guess == randomNumber) {
                playSound("correct.wav");
                messageLabel.setText("🎉 Correct! You won in " + attempts + " attempts.");
                wins++;
                endGame();
            } else if (attempts >= maxAttempts) {
                playSound("lost.wav");
                messageLabel.setText("❌ You lost! Number was " + randomNumber);
                losses++;
                endGame();
            } else if (Math.abs(guess - randomNumber) <= 5) {
                playSound("close.wav");
                messageLabel.setText("🔥 Very Close! Try again.");
            } else {
                playSound("wrong.wav");
                messageLabel.setText(guess < randomNumber ? "🔼 Too Low!" : "🔽 Too High!");
            }

            attemptsLabel.setText("Attempts Left: " + (maxAttempts - attempts));
        } catch (NumberFormatException ex) {
            messageLabel.setText("❌ Please enter a valid number!");
        }

        guessField.setText("");
    }

    private void resetGame() {
        gameOver = false;
        guessHistory.clear();
        updateHistory();

        int selected = difficultyBox.getSelectedIndex();
        switch (selected) {
            case 0:
                maxNumber = 100;
                maxAttempts = 10;
                break;
            case 1:
                maxNumber = 500;
                maxAttempts = 7;
                break;
            case 2:
                maxNumber = 1000;
                maxAttempts = 5;
                break;
            default:
                maxNumber = 100;
                maxAttempts = 10;
                break;
        }

        randomNumber = new Random().nextInt(maxNumber) + 1;
        attempts = 0;

        messageLabel.setText("Guess the number!");
        attemptsLabel.setText("Attempts Left: " + maxAttempts);
        rangeLabel.setText("🎯 Number Range: 1 to " + maxNumber);
        guessButton.setEnabled(true);
        guessField.setEditable(true);
        scoreLabel.setText("Wins: " + wins + " | Losses: " + losses);
        guessField.setText("");
    }

    private void endGame() {
        gameOver = true;
        guessButton.setEnabled(false);
        guessField.setEditable(false);
        scoreLabel.setText("Wins: " + wins + " | Losses: " + losses);
    }

    private void updateHistory() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guessHistory.size(); i++) {
            sb.append("Try ").append(i + 1).append(": ").append(guessHistory.get(i)).append("\n");
        }
        historyArea.setText(sb.toString());
    }

    private void playSound(String fileName) {
        try {
            File soundFile = new File(fileName);
            if (!soundFile.exists()) {
                System.out.println("Sound file not found: " + fileName);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.setFramePosition(0);
            clip.start();
        } catch (Exception e) {
            System.out.println("Error playing sound: " + fileName);
        }
    }

    public static void main(String[] args) {
        new NumberGuessingWithSound();
    }
}

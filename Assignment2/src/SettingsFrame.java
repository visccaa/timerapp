import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.Random;

public class SettingsFrame extends JFrame implements ActionListener {

    private MainFrame mainFrame;
    private JRadioButton onTimeRadioButton;
    private JRadioButton countdownRadioButton;
    private JLabel timeLabel;
    private JSpinner hoursSpinner;
    private JSpinner minutesSpinner;
    private JSpinner secondsSpinner;
    private JLabel secondsLabel;
    private JSpinner countdownSpinner;
    private JLabel colorLabel;
    private JButton chooseColorButton;
    private JLabel speedLabel;
    private JComboBox<String> speedComboBox;
    private JButton startButton;
    private JButton stopButton;
    private Color backgroundColor = Color.WHITE;
    private Timer timer;
    private boolean isCountdown;
    private int countdownSeconds;
    private int colorChangeSpeed;

    public SettingsFrame(MainFrame mainFrame){
        this.mainFrame = mainFrame;
        setTitle("Settings");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10 ,10, 10));

        onTimeRadioButton = new JRadioButton("On Time");
        onTimeRadioButton.addActionListener(this);
        panel.add(onTimeRadioButton);

        timeLabel = new JLabel("Time:");
        panel.add(timeLabel);

        hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        panel.add(hoursSpinner);

        minutesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        panel.add(minutesSpinner);

        secondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        panel.add(secondsSpinner);



        countdownRadioButton = new JRadioButton("Countdown");
        countdownRadioButton.addActionListener(this);
        panel.add(countdownRadioButton);

        ButtonGroup group = new ButtonGroup();
        group.add(onTimeRadioButton);
        group.add(countdownRadioButton);

        secondsLabel = new JLabel("Seconds:");
        panel.add(secondsLabel);

        countdownSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        panel.add(countdownSpinner);


        colorLabel = new JLabel("Color:");
        panel.add(colorLabel);

        chooseColorButton = new JButton("Choose color");
        chooseColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == chooseColorButton) {
                    Color selectedColor = JColorChooser.showDialog(null, "Choose a color", chooseColorButton.getBackground());
                }
            }
        });
        panel.add(chooseColorButton);

        speedLabel = new JLabel("Color change speed:");
        panel.add(speedLabel);

        String[] speedOptions = {"1 second", "2 seconds", "3 seconds", "4 seconds", "5 seconds"};
        speedComboBox = new JComboBox<>(speedOptions);
        panel.add(speedComboBox);

        startButton = new JButton("Start Countdown");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object startButton = e.getSource();
                if (startButton == chooseColorButton) {
                    Color color = JColorChooser.showDialog((Component) startButton, "Choose a Color", getBackground());
                    if (color != null) {
                        ColorFrame.setBackgroundColor(backgroundColor);
                        colorLabel.setText(String.format("RGB(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue()));
                    }
                } else if (startButton == startButton) {
                    startTimer();
                } else if (startButton == onTimeRadioButton || startButton == countdownRadioButton) {
                    boolean onTimeSelected = onTimeRadioButton.isSelected();
                    hoursSpinner.setEnabled(onTimeSelected);
                    minutesSpinner.setEnabled(onTimeSelected);
                    secondsSpinner.setEnabled(onTimeSelected);
                    countdownSpinner.setEnabled(!onTimeSelected);
                }
            }
        });
        panel.add(startButton);

        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        panel.add(stopButton);

        setVisible(true);
        add(panel);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == onTimeRadioButton) {
            isCountdown = false;
            countdownSpinner.setEnabled(false);
            hoursSpinner.setEnabled(true);
            minutesSpinner.setEnabled(true);
            secondsSpinner.setEnabled(true);
        } else if (e.getSource() == countdownRadioButton) {
            isCountdown = true;
            countdownSpinner.setEnabled(true);
            hoursSpinner.setEnabled(false);
            minutesSpinner.setEnabled(false);
            secondsSpinner.setEnabled(false);
        }
    }
    private Color getBackgroundColor(){
        return chooseColorButton.getBackground();
    }
    private void setBackgroundColor(Color color){
        chooseColorButton.setBackground(color);
        chooseColorButton.setForeground(Color.WHITE);
        colorLabel.setText("Selected color: RGB(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")");
    }
    private  int getColorChangeSpeed(){
        String speedString = (String) speedComboBox.getSelectedItem();
        return Integer.parseInt(speedString.substring(0, speedString.indexOf(' '))) * 1000;
    }
    private int getTimeDuration() {
        if (isCountdown) {
            return (int) countdownSpinner.getValue();
        } else {
            int hours = (int) hoursSpinner.getValue();
            int minutes = (int) minutesSpinner.getValue();
            int seconds = (int) secondsSpinner.getValue();
            return hours * 3600 + minutes * 60 + seconds;
        }
    }
    private void startTimer(){
        int duration = getTimeDuration();
        countdownSeconds = isCountdown ? duration : duration - getCurrrentSeconds();
        colorChangeSpeed = getColorChangeSpeed();
        disableControls();
        timer = new Timer(colorChangeSpeed, new ActionListener() {
            private boolean isColorChangeInProgres = false;
            private boolean isWhiteColor = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdownSeconds <= 0){
                    stopTimer();
                    showColorFrame();
                    return;
                }
                countdownSeconds--;
                setTitle("Settings (" + getCountdownTime() +")");

                if (!isColorChangeInProgres) {
                    isColorChangeInProgres = true;
                }else{
                        ColorFrame.setBackgroundColor(Color.WHITE);
                    }
                    isWhiteColor = !isWhiteColor;
                    isColorChangeInProgres = false;
                }

        });
        timer.start();
        mainFrame.setTimer(countdownSeconds);
    }
    private void stopTimer(){
        if (timer != null && timer.isRunning()){
            timer.stop();
            timer = null;
            setTitle("Settings");
            enableControls();
        }
    }
    private String getCountdownTime() {
        int hours = countdownSeconds / 3600;
        int minutes = (countdownSeconds % 3600) / 60;
        int seconds = countdownSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    private int getCurrrentSeconds() {
        LocalTime now = LocalTime.now();
        int hours = now.getHour();
        int minutes = now.getMinute();
        int seconds = now.getSecond();
        return hours * 3600 + minutes * 60 + seconds;
    }

    private void disableControls() {
        onTimeRadioButton.setEnabled(false);
        countdownRadioButton.setEnabled(false);
        hoursSpinner.setEnabled(false);
        minutesSpinner.setEnabled(false);
        secondsSpinner.setEnabled(false);
        countdownSpinner.setEnabled(false);
        chooseColorButton.setEnabled(false);
        speedComboBox.setEnabled(false);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void showColorFrame(){
        ColorFrame colorFrame = new ColorFrame(this , getBackgroundColor(), getColorChangeSpeed());
        colorFrame.setVisible(true);
    }


    private void enableControls() {
        onTimeRadioButton.setEnabled(true);
        countdownRadioButton.setEnabled(true);
        hoursSpinner.setEnabled(true);
        minutesSpinner.setEnabled(true);
        secondsSpinner.setEnabled(true);
        countdownSpinner.setEnabled(true);
        chooseColorButton.setEnabled(true);
        speedComboBox.setEnabled(true);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

}
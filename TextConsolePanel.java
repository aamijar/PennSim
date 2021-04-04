import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;



import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;


public class TextConsolePanel extends JPanel implements KeyListener, FocusListener, ActionListener {
  private JTextArea screen;
  
  private JScrollPane spane;
  
  private KeyboardDevice kbd;
  
  private MonitorDevice monitor;
  
  private PipedInputStream kbin;
  
  private PipedOutputStream kbout;
  
  TextConsolePanel(KeyboardDevice paramKeyboardDevice, MonitorDevice paramMonitorDevice) {
    this.screen = new JTextArea(5, 21);
    this.screen.setEditable(false);
    this.screen.addKeyListener(this);
    this.screen.addFocusListener(this);
    this.screen.setLineWrap(true);
    this.screen.setWrapStyleWord(true);
    this.spane = new JScrollPane(this.screen, 22, 30);
    // this.spane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    // this.spane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    // this.spane.setBorder(new RoundBorder(20));
    
    this.spane.setBorder(null);
    this.kbd = paramKeyboardDevice;
    this.kbout = new PipedOutputStream();
    try {
      this.kbin = new PipedInputStream(this.kbout);
    } catch (IOException iOException) {
      ErrorLog.logError(iOException);
    } 
    paramKeyboardDevice.setInputStream(this.kbin);
    paramKeyboardDevice.setDefaultInputStream();
    paramKeyboardDevice.setInputMode(KeyboardDevice.INTERACTIVE_MODE);
    paramKeyboardDevice.setDefaultInputMode();
    this.monitor = paramMonitorDevice;
    paramMonitorDevice.addActionListener(this);
    add(this.spane);
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    Object object = paramActionEvent.getSource();
    if (object instanceof Integer) {
      Document document = this.screen.getDocument();
      try {
        document.remove(0, document.getLength());
      } catch (BadLocationException badLocationException) {
        Console.println(badLocationException.getMessage());
      } 
    } else {
      String str = (String)paramActionEvent.getSource();
      this.screen.append(str);
    } 
  }
  
  public void keyReleased(KeyEvent paramKeyEvent) {}
  
  public void keyPressed(KeyEvent paramKeyEvent) {}
  
  public void keyTyped(KeyEvent paramKeyEvent) {
    char c = paramKeyEvent.getKeyChar();
    try {
      this.kbout.write(c);
      this.kbout.flush();
    } catch (IOException iOException) {
      ErrorLog.logError(iOException);
    } 
  }
  
  public void focusGained(FocusEvent paramFocusEvent) {
    this.screen.setBackground(new Color(194,194,194));
    // LineBorder roundedLineBorder = new LineBorder(Color.black, 5, true);
    // TitledBorder roundedTitledBorder = new TitledBorder(roundedLineBorder, "Title");
    // this.screen.setBorder(roundedTitledBorder);
    // this.screen.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
  }
  
  public void focusLost(FocusEvent paramFocusEvent) {
    this.screen.setBackground(Color.white);
    this.screen.setBorder(null);
  }
  
  public void setEnabled(boolean paramBoolean) {
    this.screen.setEnabled(paramBoolean);
    if (paramBoolean) {
      this.screen.setBackground(Color.white);
    } else {
      this.screen.setBackground(Color.gray);
    } 
  }

  public class RoundBorder implements Border {

    private int radius;

    public RoundBorder(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, getRadius(), getRadius()));
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int value = getRadius() / 2;
        return new Insets(value, value, value, value);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

  }



}

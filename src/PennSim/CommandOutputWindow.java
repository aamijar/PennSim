import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class CommandOutputWindow extends JFrame implements PrintableConsole {
  private JTextArea textArea;
  
  public CommandOutputWindow(String paramString) {
    super(paramString);
    this.textArea = new JTextArea();
    this.textArea.setEditable(false);
    this.textArea.setLineWrap(true);
    this.textArea.setWrapStyleWord(true);
    // 22, 30
    JScrollPane jScrollPane = new JScrollPane(this.textArea, 22, 30);
    getContentPane().add(jScrollPane);
  }
  
  public void print(String paramString) {
    this.textArea.append(paramString);
  }
  
  public void clear() {
    Document document = this.textArea.getDocument();
    try {
      document.remove(0, document.getLength());
    } catch (BadLocationException badLocationException) {
      ErrorLog.logError(badLocationException);
    } 
  }
  
}

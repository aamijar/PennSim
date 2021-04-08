import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicSliderUI.ComponentHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class GUI implements ActionListener, TableModelListener {
  private final Machine mac;
  
  public static String LOOKANDFEEL = "System";
  
  private final JFrame frame = new JFrame("PennSim - " + PennSim.version + " - " + PennSim.getISA());
  
  private final JFileChooser fileChooser = new JFileChooser(".");
  
  private final JMenuBar menuBar = new JMenuBar();
  
  private final JMenu fileMenu = new JMenu("File");
  
  private final JMenu aboutMenu = new JMenu("About");
  
  private final JMenuItem openItem = new JMenuItem("Open .obj File");
  
  private final JMenuItem quitItem = new JMenuItem("Quit");
  
  private final JMenuItem commandItem = new JMenuItem("Open Command Output Window");
  
  private final JMenuItem versionItem = new JMenuItem("Simulator Version");
  
  private final String openActionCommand = "Open";
  
  private final String quitActionCommand = "Quit";
  
  private final String openCOWActionCommand = "OutputWindow";
  
  private final String versionActionCommand = "Version";
  
  private final JPanel leftPanel = new JPanel();
  
  private final JPanel controlPanel = new JPanel();
  
  private final JButton nextButton = new JButton("Next");
  
  private final String nextButtonCommand = "Next";
  
  private final JButton stepButton = new JButton("Step");
  
  private final String stepButtonCommand = "Step";
  
  private final JButton continueButton = new JButton("Continue");
  
  private final String continueButtonCommand = "Continue";
  
  private final JButton stopButton = new JButton("Stop");
  
  private final String stopButtonCommand = "Stop";
  
  private final String statusLabelRunning = "    Running ";
  
  private final String statusLabelSuspended = "Suspended ";
  
  private final String statusLabelHalted = "       Halted ";
  
  private final JLabel statusLabel = new JLabel("");
  
  private final Color runningColor = new Color(43, 129, 51);
  
  private final Color suspendedColor = new Color(209, 205, 93);
  
  private final Color haltedColor = new Color(161, 37, 40);
  
  private final JTable regTable;
  
  private final CommandLinePanel commandPanel;
  
  private final CommandOutputWindow commandOutputWindow;
  
  private final JPanel memoryPanel = new JPanel(new BorderLayout());
  
  private final JTable memTable;
  
  private final JScrollPane memScrollPane;
  
  public static final Color BreakPointColor = new Color(7, 73, 217);
  
  public static final Color PCColor = Color.YELLOW;
  
  private final JPanel devicePanel = new JPanel();
  
  private final JPanel registerPanel = new JPanel();
  
  private final TextConsolePanel ioPanel;
  
  private final VideoConsole video;

  private final TableCellRenderer renderer; //my new line
  
  private void setupMemoryPanel() {
    this.memoryPanel.add(this.memScrollPane, "Center");
    this.memoryPanel.setMinimumSize(new Dimension(400, 100));
    // this.memoryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Memory"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    this.memoryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5,5,5,5), "Memory"));
    this.memTable.getModel().addTableModelListener(this);
    this.memTable.getModel().addTableModelListener(this.video);
    this.memTable.getModel().addTableModelListener((HighlightScrollBar)this.memScrollPane.getVerticalScrollBar());
    this.memTable.setPreferredScrollableViewportSize(new Dimension(400, 460));
  }
  
  private void setupControlPanel() {
    byte b = 4;
    this.controlPanel.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 2;
    this.nextButton.setActionCommand("Next");
    this.nextButton.addActionListener(this);
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    this.controlPanel.add(this.nextButton, gridBagConstraints);
    this.stepButton.setActionCommand("Step");
    this.stepButton.addActionListener(this);
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    this.controlPanel.add(this.stepButton, gridBagConstraints);
    this.continueButton.setActionCommand("Continue");
    this.continueButton.addActionListener(this);
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    this.controlPanel.add(this.continueButton, gridBagConstraints);
    this.stopButton.setActionCommand("Stop");
    this.stopButton.addActionListener(this);
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    this.controlPanel.add(this.stopButton, gridBagConstraints);
    gridBagConstraints.gridx = 4;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = 0;
    gridBagConstraints.anchor = 22;
    setStatusLabelSuspended();
    this.controlPanel.add(this.statusLabel, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 6;
    this.controlPanel.add(Box.createRigidArea(new Dimension(5, 5)), gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 6;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.ipady = 100;
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.weighty = 1.0D;
    gridBagConstraints.fill = 1;
    this.controlPanel.add(this.commandPanel, gridBagConstraints);
    // 100, 150
    this.controlPanel.setMinimumSize(new Dimension(100, 175));
    this.controlPanel.setPreferredSize(new Dimension(100, 175));
    // this.controlPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Controls"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    this.controlPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5,5,5,5), "Controls"));
    this.controlPanel.setVisible(true);
  }
  
  private void setupRegisterPanel() {
    this.registerPanel.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.fill = 2;
    this.registerPanel.add(this.regTable, gridBagConstraints);
    // this.registerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Registers"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    this.registerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5,5,5,5), "Registers"));
    this.registerPanel.setVisible(true);
  }
  
  private void setupDevicePanel() {
    this.devicePanel.setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.BOTH; // 10
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 1.0D;
    this.devicePanel.add(this.video, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.fill = 0;
    //gridBagConstraints.fill = GridBagConstraints.BOTH; // 0
    this.devicePanel.add(this.ioPanel, gridBagConstraints);
    // this.devicePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Devices"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    this.devicePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5,5,5,5), "Devices"));
    // this.devicePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Devices"), new RoundBorder(20)));
    // this.devicePanel.setBorder((Border) new RoundBorder(20));
    this.devicePanel.setVisible(true);
  }
  
  public GUI(final Machine mac, CommandLine paramCommandLine) {
    this.mac = mac;
    RegisterFile registerFile = mac.getRegisterFile();
    this.regTable = new JTable(registerFile);
    TableColumn tableColumn = this.regTable.getColumnModel().getColumn(0);
    tableColumn.setMaxWidth(30);
    tableColumn.setMinWidth(30);
    tableColumn = this.regTable.getColumnModel().getColumn(2);
    tableColumn.setMaxWidth(30);
    tableColumn.setMinWidth(30);
    Memory memory = mac.getMemory();
    this.memTable = new JTable(memory) {
        // TableCellRenderer renderer = new TableCellRenderer(){
        //   @Override
        //   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        //       boolean hasFocus, int row, int column) {
        //     return null;
        //   }
        // };
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int param1Int1, int param1Int2) {
          Component component = super.prepareRenderer(renderer, param1Int1, param1Int2);
          if (param1Int2 == 0) {

            

            //ImageIcon icon = new ImageIcon("./checkboxborder.png");
            //ImageIcon check = new ImageIcon("./checkbox.png");
            CustomCheckBox jCheckBox = new CustomCheckBox();
            // JCheckBox jCheckBox = new JCheckBox();
            // jCheckBox.setSize(100, 100);
            if (param1Int1 < 65024) {
              if (GUI.this.mac.getMemory().isBreakPointSet(param1Int1)) {
                
                //jCheckBox.setIcon(check);
                jCheckBox.setSelected(true);
                jCheckBox.setBackground(GUI.BreakPointColor);
                jCheckBox.setForeground(GUI.BreakPointColor);

              } else {
                jCheckBox.setSelected(false);
                jCheckBox.setBackground(getBackground());
                //jCheckBox.setIcon(icon);
              } 
            } else {
              jCheckBox.setEnabled(false);
            } 
            return jCheckBox;
          } 
          if (param1Int1 == GUI.this.mac.getRegisterFile().getPC()) {
            component.setBackground(GUI.PCColor);
          } else if (GUI.this.mac.getMemory().isBreakPointSet(param1Int1)) {
            component.setBackground(GUI.BreakPointColor);
            component.setForeground(Color.WHITE);
          } 
          // this was overriding the CustomTableCellRender setBackground
          // else {
          //   component.setBackground(getBackground());
          // } 
          return component;
        }
        
        public void tableChanged(TableModelEvent param1TableModelEvent) {
          if (mac != null)
            super.tableChanged(param1TableModelEvent); 
        }
      };
    
    this.renderer = new TableCellRenderer(){

      JLabel label;

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        if(isSelected){
          System.out.println("cell is selected");
          System.out.println(table.getBackground());
          System.out.println(table.getSelectionBackground());
        }
        System.out.println("cell is not selected");
        System.out.println(table.getBackground());
        
        label.setText("text");
        return label;
      }
      
    };
    
    // this.memTable.setFocusable(false);
    this.memTable.setCellSelectionEnabled(true);
    // this.memTable.setShowGrid(true);
    // this.memTable.setGridColor(new Color(1, 94, 105));
    this.memTable.setDefaultRenderer(Object.class, new CustomTableRenderer());
    // this.memTable.setDefaultEditor(Boolean.class, new DefaultCellEditor(new JCheckBox())); // Boolean.class may not be working since not implemented in Memory.java
    // this.memTable.setDefaultRenderer(Boolean.class, new CustomBooleanCellRenderer());
    // this.memTable.setBackground(Color.BLUE);
    // this.memTable.setRowSelectionAllowed(true);

    this.memScrollPane = new JScrollPane(this.memTable) {
        public JScrollBar createVerticalScrollBar() {
          return new HighlightScrollBar(mac);
        }
      };
    this.memScrollPane.getVerticalScrollBar().setBlockIncrement(this.memTable.getModel().getRowCount() / 512);
    this.memScrollPane.getVerticalScrollBar().setUnitIncrement(1);
    this.memScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    this.memScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    

    tableColumn = this.memTable.getColumnModel().getColumn(0);
    tableColumn.setMaxWidth(30); //30
    tableColumn.setMinWidth(30); //30
    // tableColumn.setCellEditor(new CustomBooleanCellEditor());
    // tableColumn.setCellEditor(new CustomBooleanCellEditor());
    // tableColumn.setCellEditor(new DefaultCellEditor(new JCheckBox()));
    tableColumn.setCellEditor(new DefaultCellEditor(new CustomCheckBox()));
    // tableColumn.setCellRenderer(new CustomBooleanCellRenderer());
    tableColumn = this.memTable.getColumnModel().getColumn(2);
    tableColumn.setMinWidth(50);
    tableColumn.setMaxWidth(50);
    this.commandPanel = new CommandLinePanel(mac, paramCommandLine);
    this.commandOutputWindow = new CommandOutputWindow("Command Output");
    WindowListener windowListener = new WindowListener() {
        public void windowActivated(WindowEvent param1WindowEvent) {}
        
        public void windowClosed(WindowEvent param1WindowEvent) {}
        
        public void windowClosing(WindowEvent param1WindowEvent) {
          GUI.this.commandOutputWindow.setVisible(false);
        }
        
        public void windowDeactivated(WindowEvent param1WindowEvent) {}
        
        public void windowDeiconified(WindowEvent param1WindowEvent) {}
        
        public void windowIconified(WindowEvent param1WindowEvent) {}
        
        public void windowOpened(WindowEvent param1WindowEvent) {}
      };
    this.commandOutputWindow.addWindowListener(windowListener);
    this.commandOutputWindow.setSize(700, 600);
    Console.registerConsole(this.commandPanel);
    Console.registerConsole(this.commandOutputWindow);
    this.ioPanel = new TextConsolePanel(mac.getMemory().getKeyBoardDevice(), mac.getMemory().getMonitorDevice());
    this.ioPanel.setMinimumSize(new Dimension(256, 85));
    this.video = new VideoConsole(mac);
    this.commandPanel.setGUI(this);
  }

  public class CustomTableRenderer extends DefaultTableCellRenderer {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      // setBorder(new LineBorder(Color.BLUE));
      
      // if (table.isCellSelected(row, column)){
      //   //setForeground(Color.BLACK);
      //   setBackground(Color.BLUE);
      // }
      // else{
      //   setForeground(table.getForeground());
      //   setBackground(table.getBackground());
      // }
      
      // else if (table.isRowSelected(row))
      //     setForeground(Color.green);
      // else if (table.isColumnSelected(column))
      //     setForeground(Color.blue);
      // else
      //     setForeground(Color.black);
      // if (isSelected){
      //     setBackground(table.getSelectionBackground());
      //     setForeground(table.getSelectionForeground());
      // }
      // else{
      //     setBackground(table.getBackground());
      //     setForeground(table.getForeground());
      // }

      if (table.isCellSelected(row, column)){
        setForeground(Color.WHITE);
        // c.setBackground(new Color(0, 122, 255));
        // c.setBackground(new Color(10, 132, 255));
        c.setBackground(new Color(7, 73, 217));
      }
      // else if (table.isRowSelected(row)){
      //   setForeground(Color.green);
      //   c.setBackground(Color.black);
      // }
        
      // else if (table.isColumnSelected(column)){
      //   setForeground(Color.blue);
      //   c.setBackground(Color.black);
      // }
        
      else {
        setForeground(Color.black);
        c.setBackground(Color.WHITE);
      }
      return c;
    }

  }

  public class CustomCheckBox extends JCheckBox{

    private ImageIcon checkBox;
    private ImageIcon checkBoxBorder;

    public CustomCheckBox(){
      //checkBox = new ImageIcon("./src/res/checkbox.png");
      //checkBoxBorder = new ImageIcon("./src/res/checkboxborder.png");
      checkBox = new ImageIcon(this.getClass().getResource("/src/res/checkbox.png"));
      checkBoxBorder = new ImageIcon(this.getClass().getResource("/src/res/checkboxborder.png"));
    }

    @Override
    public void setSelected(boolean selected) {
      super.setSelected(selected);
      if (selected){
        setIcon(checkBox);
        // setPressedIcon(checkBox);
      } else{
        setIcon(checkBoxBorder);
      }
    }

  }

  public class CustomBooleanCellRenderer extends CustomCheckBox implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof Boolean) {
            boolean selected = (boolean) value;
            setSelected(selected);
        }
        return this;
    }

  }

  public class CustomBooleanCellEditor extends AbstractCellEditor implements TableCellEditor {

    private CustomCheckBox editor;

    public CustomBooleanCellEditor() {
        editor = new CustomCheckBox();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Boolean) {
            boolean selected = (boolean) value;
            editor.setSelected(selected);
        }
        return editor;
    }

    @Override
    public Object getCellEditorValue() {
        return editor.isSelected();
    }

  }


  // public class RoundBorder implements Border {

  //   private int radius;

  //   public RoundBorder(int radius) {
  //       this.radius = radius;
  //   }

  //   public int getRadius() {
  //       return radius;
  //   }

  //   @Override
  //   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
  //       Graphics2D g2d = (Graphics2D) g.create();
  //       g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, getRadius(), getRadius()));
  //       g2d.dispose();
  //   }

  //   @Override
  //   public Insets getBorderInsets(Component c) {
  //       int value = getRadius() / 2;
  //       return new Insets(value, value, value, value);
  //   }

  //   @Override
  //   public boolean isBorderOpaque() {
  //       return false;
  //   }

  // }


  public void setUpGUI() {
    initLookAndFeel();
    JFrame.setDefaultLookAndFeelDecorated(true);
    this.mac.setStoppedListener(this.commandPanel);
    this.fileChooser.setFileSelectionMode(2);
    this.fileChooser.addChoosableFileFilter(new FileFilter() {
          public boolean accept(File param1File) {
            if (param1File.isDirectory())
              return true; 
            String str = param1File.getName();
            if (str != null && str.toLowerCase().endsWith(".obj"))
              return true; 
            return false;
          }
          
          public String getDescription() {
            return "*.obj";
          }
        });
    this.openItem.setActionCommand("Open");
    this.openItem.addActionListener(this);
    this.fileMenu.add(this.openItem);
    this.commandItem.setActionCommand("OutputWindow");
    this.commandItem.addActionListener(this);
    this.fileMenu.add(this.commandItem);
    this.fileMenu.addSeparator();
    this.quitItem.setActionCommand("Quit");
    this.quitItem.addActionListener(this);
    this.fileMenu.add(this.quitItem);
    this.versionItem.setActionCommand("Version");
    this.versionItem.addActionListener(this);
    this.aboutMenu.add(this.versionItem);
    this.menuBar.add(this.fileMenu);
    this.menuBar.add(this.aboutMenu);
    this.frame.setJMenuBar(this.menuBar);
    setupControlPanel();
    setupDevicePanel();
    setupMemoryPanel();
    setupRegisterPanel();
    this.regTable.getModel().addTableModelListener(this);
    this.frame.getContentPane().setLayout(new GridBagLayout());
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = 1;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.weighty = 1.0D;
    gridBagConstraints.gridwidth = 0;
    this.frame.getContentPane().add(this.controlPanel, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weightx = 0.0D;
    gridBagConstraints.fill = 2;
    this.frame.getContentPane().add(this.registerPanel, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.weightx = 0.0D;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.fill = 1;
    this.frame.getContentPane().add(this.devicePanel, gridBagConstraints);
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.fill = 1;
    gridBagConstraints.weightx = 1.0D;
    this.frame.getContentPane().add(this.memoryPanel, gridBagConstraints);
    this.frame.setSize(new Dimension(700, 725));
    this.frame.setDefaultCloseOperation(3);
    this.frame.pack();
    this.frame.setVisible(true);
    scrollToPC();
    this.commandPanel.actionPerformed(null);
  }
  
  public void scrollToIndex(int paramInt) {
    this.memTable.scrollRectToVisible(this.memTable.getCellRect(paramInt, 0, true));
  }
  
  public void scrollToPC() {
    scrollToPC(0);
  }
  
  public void scrollToPC(int paramInt) {
    int i = this.mac.getRegisterFile().getPC() + paramInt;
    this.memTable.scrollRectToVisible(this.memTable.getCellRect(i, 0, true));
  }
  
  public void tableChanged(TableModelEvent paramTableModelEvent) {
    if (!this.mac.isContinueMode());
  }
  
  public void confirmExit() {
    Object[] arrayOfObject = { "Yes", "No" };
    int i = JOptionPane.showOptionDialog(this.frame, "Are you sure you want to quit?", "Quit verification", 0, 3, null, arrayOfObject, arrayOfObject[1]);
    if (i == 0) {
      this.mac.cleanup();
      System.exit(0);
    } 
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    try {
      int i;
      try{
          i = Integer.parseInt(paramActionEvent.getActionCommand());
          scrollToIndex(i);
          return;
      } catch (NumberFormatException numberFormatException) {
        if ("Next".equals(paramActionEvent.getActionCommand())) {
          this.mac.executeNext();
        } else if ("Step".equals(paramActionEvent.getActionCommand())) {
          this.mac.executeStep();
        } else if ("Continue".equals(paramActionEvent.getActionCommand())) {
          this.mac.executeMany();
        } else if ("Quit".equals(paramActionEvent.getActionCommand())) {
          confirmExit();
        } else if ("Stop".equals(paramActionEvent.getActionCommand())) {
          Console.println(this.mac.stopExecution(true));
        } else if ("OutputWindow".equals(paramActionEvent.getActionCommand())) {
          this.commandOutputWindow.setVisible(true);
        } else if ("Version".equals(paramActionEvent.getActionCommand())) {
          JOptionPane.showMessageDialog(this.frame, PennSim.getVersion(), "Version", 1);
        } else if ("Open".equals(paramActionEvent.getActionCommand())) {
          i = this.fileChooser.showOpenDialog(this.frame);
          if (i == 0) {
            File file = this.fileChooser.getSelectedFile();
            Console.println(this.mac.loadObjectFile(file));
          } else {
            Console.println("Open command cancelled by user.");
          } 
        } 
      }
    } catch (ExceptionException exceptionException) {
      exceptionException.showMessageDialog(this.frame);
    } 
  }
  
  public static void initLookAndFeel() {
    String str = null;
    JFrame.setDefaultLookAndFeelDecorated(true);
    if (LOOKANDFEEL != null) {
      if (LOOKANDFEEL.equals("Metal")) {
        str = UIManager.getCrossPlatformLookAndFeelClassName();
      } else if (LOOKANDFEEL.equals("System")) {
        str = UIManager.getSystemLookAndFeelClassName();
      } else if (LOOKANDFEEL.equals("Motif")) {
        str = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
      } else if (LOOKANDFEEL.equals("GTK+")) {
        str = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
      } else if(LOOKANDFEEL.equals("Nimbus")) {
        str = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
      } else {
        ErrorLog.logError("Unexpected value of LOOKANDFEEL specified: " + LOOKANDFEEL);
        str = UIManager.getCrossPlatformLookAndFeelClassName();
      } 
      try {
        UIManager.setLookAndFeel(str);
      } catch (ClassNotFoundException classNotFoundException) {
        ErrorLog.logError("Couldn't find class for specified look and feel:" + str);
        ErrorLog.logError("Did you include the L&F library in the class path?");
        ErrorLog.logError("Using the default look and feel.");
      } catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
        ErrorLog.logError("Can't use the specified look and feel (" + str + ") on this platform.");
        ErrorLog.logError("Using the default look and feel.");
      } catch (Exception exception) {
        ErrorLog.logError("Couldn't get specified look and feel (" + str + "), for some reason.");
        ErrorLog.logError("Using the default look and feel.");
        ErrorLog.logError(exception);
      } 
    } 
  }
  
  public JFrame getFrame() {
    return this.frame;
  }
  
  public void setStatusLabelRunning() {
    this.statusLabel.setText("    Running ");
    this.statusLabel.setForeground(this.runningColor);
  }
  
  public void setStatusLabelSuspended() {
    this.statusLabel.setText("Suspended ");
    this.statusLabel.setForeground(this.suspendedColor);
  }
  
  public void setStatusLabelHalted() {
    this.statusLabel.setText("       Halted ");
    this.statusLabel.setForeground(this.haltedColor);
  }
  
  public void setStatusLabel(boolean paramBoolean) {
    if (paramBoolean) {
      setStatusLabelSuspended();
    } else {
      setStatusLabelHalted();
    } 
  }
  
  public void setTextConsoleEnabled(boolean paramBoolean) {
    this.ioPanel.setEnabled(paramBoolean);
  }
  
  public void reset() {
    setTextConsoleEnabled(true);
    this.commandPanel.reset();
    this.video.reset();
    scrollToPC();
  }

}

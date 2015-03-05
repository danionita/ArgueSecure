package argTool;

import java.awt.*;
import java.awt.event.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import data.*;

public class View {

    //used to consume numpad events for the textfield
    private boolean consumeNext;
    private Model mod;
    //used to indicate whether the next input is a claim or an assumption
    private boolean claim;
    private DefaultMutableTreeNode revision;
    private JLabel claimCounter;
    private JLabel inputDescription;
    private JToggleButton transfer_risk;
    private JButton new_risk;
    private JToggleButton toggle_implemented;

    private JTextArea inputClaim;
    private JScrollPane inputPane;
    public JScrollPane argTreePane;

    public static int LINE_WIDTH = 600;
    public static int FONT_SIZE = 22;
    public static String FORMAT = "<body style='width: " + LINE_WIDTH + "px;font-size: " + FONT_SIZE + "pt;'>";

    private JTree argTree;
    private JFrame topFrame;
    private JPanel topPanel;
    //used to track removed rounds
    private int remPosition;
    private int remPositionC;

    //used to track undo/redos
    private Stack<DefaultMutableTreeNode> editHistory;
    //used to store loaded images
    private HashMap<String, Image> resources;

    private static final String CLAIM = "New CLAIM: (Press Control + Space to switch)";
    private static final String DEFENCE = "New DEFENCE: (Press Control + Space to switch to assmptions)";
    private static final String ATTACK = "New ATTACK: (Press Control + Space to switch to assmptions)";
    private static final String ASSUMPTION = "New ASSUMPTION: (Press Control + Space to add a new attack/defence)";
    private static final String REVISION = "Currently revising a statement, press ESC to cancel.";
    private static final Dimension BUTTON_SIZE = new Dimension(120, 60);

    //used to reconfigure the newline/subminput behaviour of the input box
    private static final String TEXT_SUBMIT = "text-submit";
    private static final String INSERT_BREAK = "insert-break";

    public View(Model m) {
        resources = new HashMap<String, Image>();
        readImages();
        mod = m;
        remPosition = -1;
        claim = true;
        consumeNext = false;
        revision = null;
        editHistory = new Stack<DefaultMutableTreeNode>();
        topFrame = new JFrame("ArgueSecure");
        topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponents(topFrame.getContentPane());
        //addRisk();
        setupKeys();

        // get the screen size as a java dimension
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
 // get 2/3 of the height, and 2/3 of the width
        int screenHeight = screenSize.height * 2 / 3;
        int screenWidth = screenSize.width * 2 / 3;
// set the jframe height and width
        topFrame.setPreferredSize(new Dimension(screenWidth, screenHeight));

        topFrame.pack();
        topFrame.setVisible(true);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        Dimension dim = new Dimension(width, height);
        topFrame.setLocation(dim.width / 2 - topFrame.getSize().width / 2, dim.height / 2 - topFrame.getSize().height / 2);

        inputClaim.grabFocus();

    }

    private void addComponents(Container contentPane) {
        GridBagConstraints gbc = new GridBagConstraints();

        topPanel = new JPanel(new GridBagLayout());

        //-----------BUTTONS PANEL-------------//
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        JButton undo = new JButton("<html><u>U</u>ndo</html>", new ImageIcon(resources.get("undo")));
        undo.setVerticalTextPosition(SwingConstants.BOTTOM);
        undo.setHorizontalTextPosition(SwingConstants.CENTER);
        JButton redo = new JButton("<html>Red<u>o</u></html>", new ImageIcon(resources.get("redo")));
        redo.setVerticalTextPosition(SwingConstants.BOTTOM);
        redo.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton edit = new JButton("Revise", new ImageIcon(resources.get("edit")));
        edit.setVerticalTextPosition(SwingConstants.BOTTOM);
        edit.setHorizontalTextPosition(SwingConstants.CENTER);
        JButton revisit = new JButton("<html><u>R</u>evisit<html>", new ImageIcon(resources.get("revisit")));
        revisit.setVerticalTextPosition(SwingConstants.BOTTOM);
        revisit.setHorizontalTextPosition(SwingConstants.CENTER);

        new_risk = new JButton("<html><u>N</u>ew Risk</html>", new ImageIcon(resources.get("finalize")));
        new_risk.setVerticalTextPosition(SwingConstants.BOTTOM);
        new_risk.setHorizontalTextPosition(SwingConstants.CENTER);
        new_risk.setEnabled(false);

        JButton load_assessment = new JButton("<html>Load Assess<u>m</u>ent</html>", new ImageIcon(resources.get("load")));
        load_assessment.setVerticalTextPosition(SwingConstants.BOTTOM);
        load_assessment.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton save_assessment = new JButton("<html><u>S</u>ave Assessment</html>", new ImageIcon(resources.get("save")));
        save_assessment.setVerticalTextPosition(SwingConstants.BOTTOM);
        save_assessment.setHorizontalTextPosition(SwingConstants.CENTER);

        JButton new_category = new JButton("<html>New Categor<u>y</u></html>", new ImageIcon(resources.get("category")));
        new_category.setVerticalTextPosition(SwingConstants.BOTTOM);
        new_category.setHorizontalTextPosition(SwingConstants.CENTER);

        toggle_implemented = new JToggleButton("<html>Im<u>p</u>lemented</html>", new ImageIcon(resources.get("implemented")));
        toggle_implemented.setEnabled(false);

        transfer_risk = new JToggleButton("<html>Risk <u>T</u>ransfer</html>", new ImageIcon(resources.get("transfer")));

        transfer_risk.setEnabled(false);

        new_risk.setPreferredSize(BUTTON_SIZE);
        new_category.setPreferredSize(BUTTON_SIZE);
        undo.setPreferredSize(BUTTON_SIZE);
        redo.setPreferredSize(BUTTON_SIZE);
        edit.setPreferredSize(BUTTON_SIZE);
        revisit.setPreferredSize(BUTTON_SIZE);

        load_assessment.setPreferredSize(BUTTON_SIZE);
        save_assessment.setPreferredSize(BUTTON_SIZE);

        buttonPanel.add(save_assessment, gbc);

        gbc.gridx = 1;
        buttonPanel.add(load_assessment, gbc);

        gbc.gridy = 1;
        buttonPanel.add(redo, gbc);
        gbc.gridx = 0;
        buttonPanel.add(undo, gbc);
        gbc.gridy = 2;
        buttonPanel.add(edit, gbc);
        gbc.gridx = 1;
        buttonPanel.add(revisit, gbc);
        gbc.gridy = 3;
        gbc.gridx = 0;
        buttonPanel.add(new_category, gbc);
        gbc.gridx = 1;
        buttonPanel.add(new_risk, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 100;
        JLabel filler = new JLabel("");
        buttonPanel.add(filler, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;

        buttonPanel.add(toggle_implemented, gbc);
        buttonPanel.add(transfer_risk, gbc);

        gbc.gridy = 5;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_START;

        buttonPanel.add(transfer_risk, gbc);

        gbc.gridwidth = 1;

        //------------TREE PANEL---------------//
        argTree = new JTree(mod.getAssessment());
        argTree.setRowHeight(0);
        argTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        for (int i = 0; i < argTree.getRowCount(); i++) {
            argTree.expandRow(i);
        }

        @SuppressWarnings("serial")
        DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer() {

            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
                //insert custom code here

                boolean active = ((DefaultMutableTreeNode) value).isNodeAncestor(mod.getActiveElement());
                this.setEnabled(active);
                if (((DefaultMutableTreeNode) value).isNodeDescendant(mod.getActiveElement())) {
                    this.setEnabled(true);
                }

                if (value instanceof Category) {
                    this.setIcon(new ImageIcon(resources.get("categoryS")));
                }

                if (value instanceof Risk) {
                    this.setIcon(new ImageIcon(resources.get("risk")));
                }
                if (value instanceof Assumption) {
                    this.setIcon(new ImageIcon(resources.get("assumption")));
                }
                if (value instanceof Claim) {
                    Claim c = (Claim) value;
                    int i = c.getParent().getIndex(c);
                    if (i % 2 == 0) {
                        this.setIcon(new ImageIcon(resources.get("attacker")));
                    } else {
                        this.setIcon(new ImageIcon(resources.get("defender")));
                    }
                    if (c.getTransferClaim()) {
                        this.setIcon(new ImageIcon(resources.get("transferred")));
                    }
                }

                return this;
            }
        };

        argTree.setCellRenderer(defaultRenderer);
        argTree.setLargeModel(true);
        argTree.setRowHeight(0);

        //argTree.setRootVisible(false);
        argTreePane = new JScrollPane(argTree);
        argTreePane.setPreferredSize(new Dimension(400, argTreePane.getPreferredSize().height));

        //------------TEXT INPUT PANEL--------//
        JPanel inputPanel = new JPanel();

        inputPanel.setLayout(new GridBagLayout());
        inputDescription = new JLabel(CLAIM);
        inputDescription.setFont(inputDescription.getFont().deriveFont((float)FONT_SIZE));
        claimCounter = new JLabel("C1:");
        inputClaim = new JTextArea("", 2, 50);
        inputClaim.setLineWrap(true);
        inputClaim.setWrapStyleWord(true);
        inputClaim.setEnabled(false);
        inputClaim.setFont(inputDescription.getFont().deriveFont((float)FONT_SIZE));
        InputMap input = inputClaim.getInputMap();
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        inputClaim.getInputMap().put(shiftEnter, INSERT_BREAK);
        input.put(enter, TEXT_SUBMIT);

        inputPane = new JScrollPane(inputClaim);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        //gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(inputDescription, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        //inputPanel.add(claimCounter, gbc);
        gbc.gridx = 1;
        gbc.weightx = 120;
        inputPanel.add(inputPane, gbc);

        //-------------ADD TOP LEVEL PANELS----------//
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 10;
        gbc.weighty = 20;
        gbc.fill = GridBagConstraints.BOTH;
        topPanel.add(argTreePane, gbc);

        gbc.gridy = 1;
        gbc.weighty = 2;
        topPanel.add(inputPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 3;
        gbc.weighty = 10;
        gbc.gridheight = 2;
        topPanel.add(buttonPanel, gbc);

        contentPane.add(topPanel);

        //---------------------------------------//
        //------------ACTION LISTENERS-----------//
        //---------------------------------------//
        new_risk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRisk();
            }
        });

        new_category.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCategory();
            }
        });

        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });

        revisit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                revisit();
            }
        });

        save_assessment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //saveAssessment();
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
                fc.setFileFilter(xmlFilter);
                fc.showSaveDialog(topFrame);
                File f = new File(fc.getSelectedFile().toString() + ".xml");
                saveAssessment(f);
            }
        });

        load_assessment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
                fc.addChoosableFileFilter(xmlFilter);
                fc.setFileFilter(xmlFilter);
                fc.showOpenDialog(topFrame);
                File f = fc.getSelectedFile();
                loadAssessment(f);
            }
        });

        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TreePath path = argTree.getLeadSelectionPath();
                if (path == null) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                argTree.setSelectionPath(path);
                revise(node);
            }
        });

        //new submit-text behaviour for inputClaim
        ActionMap actions = inputClaim.getActionMap();
        actions.put(TEXT_SUBMIT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addInput(inputClaim);
            }
        });

        toggle_implemented.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                impToggle();
            }
        });

        argTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) argTree.getLastSelectedPathComponent();
                if (node instanceof Claim) {
                    if (((Claim) node).isDefender()) {
                        toggle_implemented.setEnabled(true);
                    } else {
                        toggle_implemented.setEnabled(false);
                    }
                }
            }
        });

        //KeyboardFocusManager keyFManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        //Disable focus traversal on input pane using tab etc
        //inputClaim.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
        //-------------EDITING KEYS------------//
        //keyFManager.addKeyEventDispatcher( new ArgToolDispatcher(this, mod));
        //add a listener to switch between Claim/Assumption input on a tab
        inputClaim.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {

                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE && revision != null) {
                    inputClaim.setText("");
                    inputClaim.setBackground(Color.white);
                    updateGUIState();
                    revision = null;
                }

            }

            public void keyTyped(KeyEvent evt) {
                if (consumeNext) {
                    evt.consume();
                    consumeNext = false;
                }
            }

        });

        argTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    TreePath path = argTree.getPathForLocation(e.getX(), e.getY());
                    if (path == null) {
                        return;
                    }
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    argTree.setSelectionPath(path);
                    revise(node);
                }

                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
                    if (revision != null) {
                        inputClaim.setText("");
                        inputClaim.setBackground(Color.white);
                        updateGUIState();
                        revision = null;
                    }
                }
            }
        });

    }

    /**
     * updated to jTree. adds a new element to the argumentation panel.
     */
    private void addInput(JTextArea inp) {
        DefaultTreeModel tmod = (DefaultTreeModel) argTree.getModel();
        //<---Revision
        if (revision != null) {
            revision.setUserObject(inputClaim.getText());
            tmod.nodeChanged(revision);
            inputClaim.setBackground(Color.white);
            updateGUIState();
            revision = null;
            inputClaim.grabFocus();
        } else {
            boolean nothingHere = ((DefaultMutableTreeNode) tmod.getRoot()).getLastLeaf().equals(tmod.getRoot());
            if (nothingHere) {
                System.err.println("Error: Tried to add a claim without active risk");
                return;
            }
            //<---Claim
            if (claim) {
                Claim newClaim = new Claim(inp.getText());
                tmod.insertNodeInto(newClaim, mod.getRisk(), mod.getRisk().getChildCount());
                if (transfer_risk.isSelected()) {
                    newClaim.setTransferClaim(true);
                    switchClaim();
                }
                validate(newClaim);
                switchClaim();

                //<---Assumption				
            } else {
                Claim parentClaim = (Claim) mod.getRisk().getLastChild();
                Assumption newAssumption = new Assumption(inp.getText());
                tmod.insertNodeInto(newAssumption, parentClaim, parentClaim.getChildCount());
                updateGUIState();
            }
            editHistory.clear();
        }
        inp.setText("");

        DefaultMutableTreeNode nodeIWant = mod.getRisk().getLastLeaf();
        argTree.scrollPathToVisible(new TreePath(nodeIWant.getPath()));
        //topFrame.pack();
    }

    private void addRisk() {
        if (mod.getAssessment().getChildCount() == 0) {
            return;
        }
        DefaultTreeModel tmod = (DefaultTreeModel) argTree.getModel();
        editHistory.clear();

        String desc = "";
        String s = (String) JOptionPane.showInputDialog(topFrame, "Risk title?");
        //If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            desc = s;
        }

        Risk risk = new Risk(desc);

        Element active_element = mod.getActiveElement();
        if (active_element instanceof Risk) {
            Category active_category = (Category) ((Risk) active_element).getParent();
            tmod.insertNodeInto(risk, active_category, active_category.getChildCount());
            mod.setActiveElement(risk);
        } else if (active_element instanceof Category) {
            tmod.insertNodeInto(risk, active_element, active_element.getChildCount());
            mod.setActiveElement(risk);
        } else {
            System.err.println("An error occurred while attempting to insert a new risk into the assessment: Expected type either category or risk, found something else...");
            System.err.println(active_element);
        }
        argTree.makeVisible(new TreePath(risk.getPath()));
        mod.setRisk(risk);
        claim = true;
        updateGUIState();
        inputClaim.grabFocus();
    }

    private void addCategory() {
        DefaultTreeModel tmod = (DefaultTreeModel) argTree.getModel();
        editHistory.clear();

        String desc = "";
        int pos = 0;
        String s = (String) JOptionPane.showInputDialog(topFrame, "Category title?");
        //If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            desc = s;
        }
        try {
            Risk active_risk = mod.getRisk();
            Category active_category = null;
            try {
                if (mod.getActiveElement() instanceof Category) {
                    active_category = (Category) mod.getActiveElement();
                } else {
                    active_category = (Category) ((Risk) mod.getActiveElement()).getParent();
                }

                pos = tmod.getIndexOfChild(mod.getAssessment(), active_category) + 1;

            } catch (NullPointerException | IllegalArgumentException e) {
            }

            Category new_category = new Category(desc);

            tmod.insertNodeInto(new_category, mod.getAssessment(), pos);
            argTree.expandRow(mod.getAssessment().getIndex(new_category));
            mod.setActiveElement(new_category);
            mod.setRisk(null);
            claim = true;
            updateGUIState();
            inputClaim.grabFocus();
        } catch (NoSuchElementException e) {
            System.err.println("Errors occurred trying to create a new category: No such element exception.");
        }
    }

    /**
     * validates the claims so far based on the last claim in the argumentation
     */
    private void validate(Claim c) {
        boolean validate = true;
        Risk cParent = (Risk) c.getParent();
        for (int cCount = cParent.getChildCount() - 1; cCount >= 0; cCount--) {
            Claim sibling = (Claim) cParent.getChildAt(cCount);
            sibling.setValid(validate);
            validate = !validate;
        }

    }

    /**
     * Switches between input of a claim and input of an assumption
     *
     * @param cl
     */
    public void switchClaim() {
        claim = !claim;
        updateGUIState();
    }

    /**
     * updates the claim counter and tree to what comes after the current last
     * input.
     */
    public void updateGUIState() {
        DefaultMutableTreeNode risk = mod.getRisk();
        int c = 1;
        int a = 1;
        try {
            c = risk.getChildCount() + 1;
            if (risk.isLeaf()) {
                claim = true;
            } else {
                a = risk.getLastChild().getChildCount() + 1;
            }
            try {
                //Code for the transfer risk checkbox
                if (risk.getLastChild() instanceof Claim) {
                    Claim lastClaim = (Claim) risk.getLastChild();
                    transfer_risk.setEnabled(!lastClaim.isDefender() && claim);
                    if (!transfer_risk.isEnabled()) {
                        transfer_risk.setSelected(false);
                    }
                }
            } catch (NoSuchElementException e) {
                //There is no specified claim or risk, so an exception is thrown.
            }
        } catch (NullPointerException e) {
            //Do nothing.
        }

        String result = (claim) ? "C" + c + ":" : "A" + (c - 1) + "." + a + ":";
        claimCounter.setText(result);
        inputDescription.setText(ASSUMPTION);
        if (claim) {
            inputDescription.setText(CLAIM);
        }

        if (mod.getRisk() == null) {
            inputClaim.setEnabled(false);
        } else {
            inputClaim.setEnabled(true);
        }
        new_risk.setEnabled(mod.getAssessment().getChildCount() > 0);
        //topFrame.pack();
    }

    /**
     * Code to set state to the round specified.
     *
     * @param i
     */
    public void revisit() {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) argTree.getLastSelectedPathComponent();
        Risk targetRound = null;
        if (n instanceof Category) {
            mod.setActiveElement((Category) n);
        } else if (n instanceof Risk) {
            targetRound = (Risk) n;
        } else if (n instanceof Claim) {
            targetRound = (Risk) n.getParent();
        } else if (n instanceof Assumption) {
            targetRound = (Risk) n.getParent().getParent();
        }
        if (targetRound != null) {
            mod.setActiveElement(targetRound);
            mod.setRisk(targetRound);
        }
        argTree.treeDidChange();
        if (!(n instanceof Category) || mod.getAssessment().getChildCount() == 0) {
            claim = (mod.getRisk().getLastLeaf() instanceof Risk);
        }
        editHistory.clear();
        updateGUIState();
    }

    /**
     * starts the process of revising an existing assumption or claim.
     *
     * @param e the claim to be revised
     */
    public void revise(DefaultMutableTreeNode e) {
        if (e == null) {
            return;
        }
        revision = e;
        inputClaim.setBackground(new Color(235, 231, 176));
        inputDescription.setText(REVISION);
        inputClaim.setText(revision.getUserObject().toString());
        claimCounter.setText("REV:");
        inputClaim.grabFocus();
    }

    /**
     * rolls back the last statement of a round. Deletes the round instead if it
     * is empty.
     */
    public void undo() {
        DefaultTreeModel tmod = (DefaultTreeModel) argTree.getModel();
        if (mod.getAssessment().getChildCount() == 0) {
            return;
        }
        try {
            DefaultMutableTreeNode n = (DefaultMutableTreeNode) mod.getActiveElement().getLastLeaf();
            System.out.println(n);
            DefaultMutableTreeNode nParent = (DefaultMutableTreeNode) n.getParent();
            System.out.println(nParent + ", " + nParent.getChildAt(0));
            //if its a risk, remember where we put it.
            if (n instanceof Risk) {
                remPosition = nParent.getIndex(n);
            }
            if (n instanceof Category) {
                remPositionC = nParent.getIndex(n);
            }
            tmod.removeNodeFromParent(n);
            editHistory.push(n);
            claim = (n instanceof Claim);

            if (n instanceof Risk) {
                //if its a risk, set the
                if (nParent.getChildCount() > 0) {
                    Risk lastRisk = (Risk) nParent.getLastChild();
                    mod.setRisk(lastRisk);
                    mod.setActiveElement(lastRisk);
                } else {
                    claim = true;
                    mod.setRisk(null);
                    mod.setActiveElement((Category) nParent);
                }
            } else if (n instanceof Category) {
                claim = true;
                if (mod.getAssessment().getChildCount() > 0) {
                    if (mod.getAssessment().getLastChild().getChildCount() > 0) {
                        Category c = (Category) mod.getAssessment().getChildAt(remPositionC - 1);
                        Risk risk = (Risk) c.getLastChild();
                        mod.setRisk(risk);
                        mod.setActiveElement(risk);
                    } else {
                        mod.setRisk(null);
                        System.out.println("Testing: " + remPositionC);
                        mod.setActiveElement((Element) mod.getAssessment().getChildAt(remPositionC - 1));
                    }
                } else {
                    mod.setActiveElement(null);
                }
            }
        } catch (NoSuchElementException e) {
            //A problem occurred when trying to get the children of lastRisk (which does not exist)
        } finally {
            updateGUIState();
        }
    }

    public void redo() {
        DefaultTreeModel tmod = (DefaultTreeModel) argTree.getModel();
        DefaultMutableTreeNode n = null;
        try {
            n = editHistory.pop();
        } catch (EmptyStackException e) {
        }
        if (n != null) {
            if (n instanceof Claim) {
                tmod.insertNodeInto(n, mod.getRisk(), mod.getRisk().getChildCount());
                claim = false;
            } else if (n instanceof Assumption) {
                Claim parentClaim = (Claim) mod.getRisk().getLastChild();
                tmod.insertNodeInto(n, parentClaim, parentClaim.getChildCount());
            } else if (n instanceof Risk) {

                Category nParent = null;
                if (mod.getActiveElement() instanceof Category) {
                    nParent = (Category) mod.getActiveElement();
                } else {
                    nParent = (Category) mod.getActiveElement().getParent();
                }
                tmod.insertNodeInto(n, nParent, remPosition);
                mod.setRisk((Risk) n);
                mod.setActiveElement((Risk) n);
                remPosition++;
            } else if (n instanceof Category) {
                tmod.insertNodeInto(n, mod.getAssessment(), remPositionC);
                mod.setRisk(null);
                mod.setActiveElement((Category) n);
                remPositionC++;
            }
            argTree.scrollPathToVisible(new TreePath(n.getPath()));
            updateGUIState();
        }
    }

    public void saveAssessment(File f) {
        XMLEncoder e;
        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream(f)));
            e.writeObject(argTree.getModel());
            e.close();
        } catch (FileNotFoundException e1) {
            System.err.println("error saving assessment: Invalid path specified");
            e1.printStackTrace();
        }
    }

    public void loadAssessment(File f) {
        FileInputStream os;
        try {
            os = new FileInputStream(f);
            XMLDecoder encoder = new XMLDecoder(os);
            DefaultTreeModel modl = (DefaultTreeModel) encoder.readObject();
            encoder.close();

            argTree.setModel(modl);
            mod.setAssessment((DefaultMutableTreeNode) modl.getRoot());
            updateGUIState();

            for (int i = 0; i < argTree.getRowCount(); i++) {
                argTree.expandRow(i);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Error loading assessment: File not found.");
            e.printStackTrace();
        }

    }

    private void impToggle() {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode) argTree.getLastSelectedPathComponent();
        if (!(n instanceof Claim)) {
            return;
        }
        Claim c = (Claim) n;
        if (!c.isDefender()) {
            return;
        }
        c.setImplementedClaim(!c.isImplementedClaim());
        argTree.treeDidChange();
    }

    private void readImages() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URL input = classLoader.getResource("resources/check.png");

        try {
            //tree images
            input = classLoader.getResource("resources/assumption_icon.png");
            resources.put("assumption", ImageIO.read(input));
            input = classLoader.getResource("resources/risk_icon.png");
            resources.put("risk", ImageIO.read(input));
            input = classLoader.getResource("resources/defender_icon.png");
            resources.put("defender", ImageIO.read(input));
            input = classLoader.getResource("resources/attacker_icon.png");
            resources.put("attacker", ImageIO.read(input));
            input = classLoader.getResource("resources/transferred_icon.png");
            resources.put("transferred", ImageIO.read(input));
            input = classLoader.getResource("resources/categoryS_icon.png");
            resources.put("categoryS", ImageIO.read(input));

            //button images
            input = classLoader.getResource("resources/save_icon.png");
            resources.put("save", ImageIO.read(input));
            input = classLoader.getResource("resources/load_icon.png");
            resources.put("load", ImageIO.read(input));
            input = classLoader.getResource("resources/undo_icon.png");
            resources.put("undo", ImageIO.read(input));
            input = classLoader.getResource("resources/redo_icon.png");
            resources.put("redo", ImageIO.read(input));
            input = classLoader.getResource("resources/edit_icon.png");
            resources.put("edit", ImageIO.read(input));
            input = classLoader.getResource("resources/revisit_icon.png");
            resources.put("revisit", ImageIO.read(input));
            input = classLoader.getResource("resources/risk_icon_large.png");
            resources.put("finalize", ImageIO.read(input));
            input = classLoader.getResource("resources/transfer_icon.png");
            resources.put("transfer", ImageIO.read(input));
            input = classLoader.getResource("resources/category_icon.png");
            resources.put("category", ImageIO.read(input));

            input = classLoader.getResource("resources/implemented_icon.png");
            resources.put("implemented", ImageIO.read(input));

        } catch (IOException e) {
            System.err.println("error loading icons: File(s) not found");
        }
    }

    @SuppressWarnings("serial")
    private void setupKeys() {
        InputMap inp = ((JPanel) topFrame.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = ((JPanel) topFrame.getContentPane()).getActionMap();

        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_DOWN_MASK);
        String desc = "selectNext";
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = argTree.getLeadSelectionRow();
                if (row < argTree.getRowCount() - 1) {
                    argTree.setSelectionRow(argTree.getLeadSelectionRow() + 1);
                }
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK);
        desc = "selectPrevious";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = argTree.getLeadSelectionRow();
                if (row <= 0) {
                    argTree.setSelectionRow(0);
                } else {
                    argTree.setSelectionRow(row - 1);
                }
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK);
        desc = "collapseRow";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = argTree.getLeadSelectionRow();
                if (row != -1) {
                    argTree.collapseRow(row);
                }
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK);
        desc = "expandRow";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = argTree.getLeadSelectionRow();
                if (row != -1) {
                    argTree.expandRow(row);
                }
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK);
        desc = "undo";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
        desc = "redo";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK);
        desc = "switchClaim";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchClaim();
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK);
        desc = "revise";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) argTree.getLastSelectedPathComponent();
                revise(n);
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
        desc = "newRound";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRisk();
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK);
        desc = "revisit";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                revisit();
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        desc = "saveAssessment";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //saveAssessment();
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
                fc.setFileFilter(xmlFilter);
                fc.showSaveDialog(topFrame);
                File f = new File(fc.getSelectedFile().toString() + ".xml");
                saveAssessment(f);
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK);
        desc = "loadAssessment";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
                fc.addChoosableFileFilter(xmlFilter);
                fc.setFileFilter(xmlFilter);
                fc.showOpenDialog(topFrame);
                File f = fc.getSelectedFile();
                loadAssessment(f);
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK);
        desc = "transferClaimToggle";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (transfer_risk.isEnabled()) {
                    transfer_risk.setSelected(!transfer_risk.isSelected());
                }
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK);
        desc = "newCategory";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCategory();
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK);
        desc = "implemented";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                impToggle();
            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.CTRL_DOWN_MASK);
        desc = "increaseFontSize";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FONT_SIZE = FONT_SIZE + 2;
                System.out.println("Font size is now: " + FONT_SIZE);
                FORMAT = "<body style='width: " + LINE_WIDTH + "px;font-size: " + FONT_SIZE + "pt;'>";
                inputDescription.setFont(inputDescription.getFont().deriveFont(Math.min((float)24, (float)FONT_SIZE)));
                inputClaim.setFont(inputDescription.getFont().deriveFont(Math.min((float)24, (float)FONT_SIZE)));
                argTree.repaint();
                ((DefaultTreeModel) argTree.getModel()).reload((DefaultMutableTreeNode) argTree.getModel().getRoot());

            }
        };
        inp.put(key, desc);
        am.put(desc, action);

        key = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK);
        desc = "decreaseFontSize";
        action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FONT_SIZE = FONT_SIZE - 2;
                System.out.println("Font size is now: " + FONT_SIZE);
                FORMAT = "<body style='width: " + LINE_WIDTH + "px;font-size: " + FONT_SIZE + "pt;'>";
                inputDescription.setFont(inputDescription.getFont().deriveFont(Math.min((float)24, (float)FONT_SIZE)));
                inputClaim.setFont(inputDescription.getFont().deriveFont(Math.min((float)24, (float)FONT_SIZE)));
                argTree.repaint();
                ((DefaultTreeModel) argTree.getModel()).reload((DefaultMutableTreeNode) argTree.getModel().getRoot());
                

            }
        };
        inp.put(key, desc);
        am.put(desc, action);

    }

}
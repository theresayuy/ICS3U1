
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

public class TheresaYu {

    private static ArrayList<BufferedImage> editHistory;
    private static FileNameExtensionFilter[] filters;
    private static String[] supportedFiles;
    private static JFileChooser chooser1, chooser2;
    private static BufferedImage modify, original, src;
    private static JMenuBar menuBar;
    private static JLabel label;
    private static int currentImgIndex;
    private static boolean fileSaved;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Image Editor GUI");
        menuBar = new JMenuBar();
        JPanel panel = new JPanel();
        JMenuItem[] fileMenuItems = {new JMenuItem("Open"), new JMenuItem("Save As"), new JMenuItem("Exit")};
        JMenuItem[] optionMenuItems = {new JMenuItem("Undo"), new JMenuItem("Redo"), new JMenuItem("Restore to Original"), new JMenuItem("Horizontal Flip"), new JMenuItem("Vertical Flip"), new JMenuItem("Gray Scale"), new JMenuItem("Sepia Tone"), new JMenuItem("Invert Colours"), new JMenuItem("Gaussian Blur"), new JMenuItem("Bulge Effect")};
        int[] fileMenuKeyCodes = {KeyEvent.VK_O, KeyEvent.VK_S, KeyEvent.VK_E};
        int[] optionMenuKeyCodes = {KeyEvent.VK_Z, KeyEvent.VK_Y, KeyEvent.VK_R, KeyEvent.VK_H, KeyEvent.VK_V, KeyEvent.VK_G, KeyEvent.VK_P, KeyEvent.VK_I, KeyEvent.VK_U, KeyEvent.VK_B};
        label = new JLabel(); //used to display the image 	
        editHistory = new ArrayList<>();
        chooser1 = new JFileChooser(); //used to open
        chooser2 = new JFileChooser(); //used to save
        supportedFiles = ImageIO.getWriterFileSuffixes();
        filters = new FileNameExtensionFilter[supportedFiles.length];

        for (int i = 0; i < supportedFiles.length; i++) {
            filters[i] = new FileNameExtensionFilter(supportedFiles[i].toUpperCase() + " Image Files (*." + supportedFiles[i] + ")", supportedFiles[i]);
            chooser2.addChoosableFileFilter(filters[i]);
        }

        chooser1.setFileFilter(new FileNameExtensionFilter("All Image Files", ImageIO.getReaderFileSuffixes()));
        chooser1.setAcceptAllFileFilterUsed(false);
        chooser2.setFileFilter(filters[0]);
        chooser2.setAcceptAllFileFilterUsed(false);
        label.setHorizontalAlignment(JLabel.CENTER); //centers image 
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(1050, 1050));
        panel.add(label, BorderLayout.CENTER);
        menuBar.add(getMenu(fileMenuItems, fileMenuKeyCodes, new int[]{2}, "File", new FileMenuActions())); // sets up file menu and adds it to menuBar
        menuBar.add(getMenu(optionMenuItems, optionMenuKeyCodes, new int[]{2, 4}, "Options", new OptionMenuActions())); //sets up options menu	and adds it to menuBar
        editEnability(false, false);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setJMenuBar(menuBar);
        frame.pack();
    }

    //returns a jmenu object with the jmenuitem objects from the array called menuItems 
    public static JMenu getMenu(JMenuItem[] menuItems, int[] keyCodes, int[] indexes, String menuTitle, ActionListener listener) {
        JMenu menu = new JMenu(menuTitle);
        menu.setFont(new Font("Arial", Font.PLAIN, 12));

        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i].addActionListener(listener);
            menuItems[i].setActionCommand(Integer.toString(i));
            menuItems[i].setAccelerator(KeyStroke.getKeyStroke(keyCodes[i], ActionEvent.CTRL_MASK)); //lets you use keyboard shortcut to perform action associated with the JMenuItem
            menuItems[i].setFont(new Font("Arial", Font.PLAIN, 12));
            menu.add(menuItems[i]);
        }

        for (int i : indexes) {
            menu.insertSeparator(i);
        }

        return menu;
    }

    //changes whether the undo/redo jmenuitems in the option menu are enabled or not
    public static void editEnability(boolean c1, boolean c2) {
        (menuBar.getMenu(1).getItem(0)).setEnabled(c1); //sets enability for undo option
        (menuBar.getMenu(1).getItem(1)).setEnabled(c2);	//sets enability for redo option	
    }

    private static class FileMenuActions implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            switch (Integer.parseInt(event.getActionCommand())) {
                case 0: //open
                    remindToSave("opening another one");

                    if (chooser1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        try {
                            original = ImageIO.read(chooser1.getSelectedFile());
                        } catch (IOException exception) {
                            System.out.println("error opening file");
                        }

                        if (original.getWidth() < 1001 && original.getHeight() < 1001 && src != original) {
                            src = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB); //pixels are sourced from this image
                            src = original;
                            label.setIcon(new ImageIcon(original));
                            editHistory.clear(); //edit history from previous image loaded will be cleared
                            editHistory.add(src);
                            currentImgIndex = editHistory.indexOf(src);
                            fileSaved = true; //the program wont ask you to save it until you start applying filters
                            editEnability(false, false);
                        }
                    }
                    break;
                case 1: //save 
                    save();
                    break;
                case 2: //quit 
                    remindToSave("exiting");
                    System.exit(0);
                    break;
            }
        }

        //shows joptionpane asking if you want to save image
        public void remindToSave(String str) {
            if (!fileSaved && original != null && JOptionPane.showConfirmDialog(null, "Would you like to save the image before " + str + "?", "Alert", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                save();
            }
        }

        public void save() {
            if (original != null && chooser2.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String suffix = "jpg";

                for (int i = 0; i < supportedFiles.length; i++) {
                    if (((FileNameExtensionFilter) chooser2.getFileFilter()).equals(filters[i])) {
                        suffix = supportedFiles[i];
                    }
                }

                try {
                    ImageIO.write(src, suffix, new File(chooser2.getSelectedFile().getPath() + "." + suffix));
                    fileSaved = true;
                } catch (IOException e) {
                    System.out.println("error saving file");
                    fileSaved = false;
                }
            }
        }
    }

    private static class OptionMenuActions implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            int command = Integer.parseInt(event.getActionCommand());
            if (original != null) {
                modify = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);

                switch (command) {
                    case 0: //undo (restores image prior to adding effect or pressing redo option)
                        retrieve(-1);
                        break;
                    case 1: //redo (restores image prior to pressing undo option)
                        retrieve(1);
                        break;
                    case 2: //restore
                        currentImgIndex = editHistory.indexOf(src) + 1;
                        src = original;
                        break;
                    case 3: //horizontal flip 
                    case 4: //vertical flip 
                        for (int i = 0; i < original.getHeight(); i++) {
                            for (int j = 0; j < original.getWidth(); j++) {
                                modify.setRGB(j, i, src.getRGB(command == 3 ? src.getWidth() - (j + 1) : j, command == 3 ? i : src.getHeight() - (i + 1)));
                            }
                        }
                        break;
                    case 5: //gray scale 
                        double[] vals = {Math.pow(3, -1), Math.pow(3, -1), Math.pow(3, -1)};
                        filter(vals, vals, vals, 0);
                        break;
                    case 6: //sepia
                        filter(new double[]{0.393, 0.796, 0.189}, new double[]{0.349, 0.686, 0.168}, new double[]{0.272, 0.534, 0.131}, 0);
                        break;
                    case 7: //invert 
                        filter(new double[]{-1, 0, 0}, new double[]{0, -1, 0}, new double[]{0, 0, -1}, 255);
                        break;
                    case 8: //gaussian blur (radius of 5.5)
                        double[][] weightKernel = new double[11][11];

                        for (int i = 0; i < 11; i++) {
                            for (int j = 0; j < 11; j++) {
                                weightKernel[i][j] = Math.pow(11 * Math.PI, -1) * Math.exp(-1 * (Math.pow(j - 5, 2) + Math.pow(i - 5, 2)) * Math.pow(11, -1));
                            }
                        }

                        for (int i = 0; i < original.getHeight(); i++) {
                            for (int j = 0; j < original.getWidth(); j++) {
                                int[][] matrixX = new int[11][11]; //stores all x-coordinates within kernel dimensions
                                int[][] matrixY = new int[11][11]; //stores all y-coordinates within kernel dimensions
                                double sum = 0;
                                double setR = 0;
                                double setB = 0;
                                double setG = 0;

                                for (int k = 0; k < 11; k++) {
                                    for (int l = 0; l < 11; l++) {
                                        matrixX[k][l] = j + l - 5;
                                        matrixY[k][l] = i + k - 5;
                                        sum += (matrixX[k][l] < 0 || matrixX[k][l] >= original.getWidth() || matrixY[k][l] < 0 || matrixY[k][l] >= original.getHeight()) ? 0 : weightKernel[k][l]; //weights are added to sum if the coordinates (stored in matrixX and matrixY) exist in image 
                                    }
                                }

                                for (int k = 0; k < 11; k++) {
                                    for (int l = 0; l < 11; l++) {
                                        setR += (matrixX[k][l] < 0 || matrixX[k][l] >= original.getWidth() || matrixY[k][l] < 0 || matrixY[k][l] >= original.getHeight()) ? 0 : new Color(src.getRGB(matrixX[k][l], matrixY[k][l])).getRed() * weightKernel[k][l] * Math.pow(sum, -1);
                                        setG += (matrixX[k][l] < 0 || matrixX[k][l] >= original.getWidth() || matrixY[k][l] < 0 || matrixY[k][l] >= original.getHeight()) ? 0 : new Color(src.getRGB(matrixX[k][l], matrixY[k][l])).getGreen() * weightKernel[k][l] * Math.pow(sum, -1);
                                        setB += (matrixX[k][l] < 0 || matrixX[k][l] >= original.getWidth() || matrixY[k][l] < 0 || matrixY[k][l] >= original.getHeight()) ? 0 : new Color(src.getRGB(matrixX[k][l], matrixY[k][l])).getBlue() * weightKernel[k][l] * Math.pow(sum, -1);
                                    }
                                }

                                modify.setRGB(j, i, new Color((int) setR, (int) setG, (int) setB).getRGB());
                            }
                        }
                        break;
                    case 9: //bulge effect (k = 2)
                        double centerX = original.getWidth() * 0.5;
                        double centerY = original.getHeight() * 0.5;
                        double m = Math.pow(Math.hypot(centerX, centerY), 2) * Math.cos(Math.atan2(-centerY, -centerX)) * Math.pow(centerX * -1.5, -1); //m depends on image dimensions

                        for (int i = 0; i < original.getHeight(); i++) {
                            for (int j = 0; j < original.getWidth(); j++) {
                                double radius = Math.pow(Math.hypot(j - centerX, i - centerY), 2) * Math.pow(m, -1);
                                double y = (radius * Math.sin(Math.atan2(i - centerY, j - centerX))) + centerY;
                                double x = (radius * Math.cos(Math.atan2(i - centerY, j - centerX))) + centerX;

                                if (x > -1 && x < original.getWidth() && y > -1 && y < original.getHeight()) { //the pixel is set if new cartesian coordinates exist in image
                                    modify.setRGB(j, i, src.getRGB((int) x, (int) y));
                                }
                            }
                        }
                        break;
                }

                if (command >= 3) {
                    editHistory.remove(src);
                    editHistory.add(src); //source image is relocated so it is easier to undo
                    editHistory.add(modify);
                    currentImgIndex = editHistory.indexOf(modify);
                    src = modify; //pixels will be sourced from modified image, allows overlaying filters 
                }

                label.setIcon(new ImageIcon(command <= 2 ? src : modify));
                fileSaved = false;
                editEnability(currentImgIndex > 0, currentImgIndex < editHistory.size() - 1 && command != 2);
            }
        }

        public void filter(double[] mR, double[] mG, double[] mB, int add) {
            for (int i = 0; i < original.getHeight(); i++) {
                for (int j = 0; j < original.getWidth(); j++) {
                    int[] pixelRGB = {new Color(src.getRGB(j, i)).getRed(), new Color(src.getRGB(j, i)).getGreen(), new Color(src.getRGB(j, i)).getBlue()};
                    int setR = (int) (pixelRGB[0] * mR[0] + pixelRGB[1] * mR[1] + pixelRGB[2] * mR[2]) + add;
                    int setG = (int) (pixelRGB[0] * mG[0] + pixelRGB[1] * mG[1] + pixelRGB[2] * mG[2]) + add;
                    int setB = (int) (pixelRGB[0] * mB[0] + pixelRGB[1] * mB[1] + pixelRGB[2] * mB[2]) + add;

                    modify.setRGB(j, i, new Color(setR > 255 ? 255 : setR, setG > 255 ? 255 : setG, setB > 255 ? 255 : setB).getRGB());
                }
            }
        }

        public void retrieve(int add) {
            src = editHistory.get(currentImgIndex + add); //pixels will be sourced from the image retrieved from array list
            currentImgIndex = editHistory.indexOf(src);
        }
    }
}

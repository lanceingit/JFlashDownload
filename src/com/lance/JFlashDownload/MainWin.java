package com.lance.JFlashDownload;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

public class MainWin {
    private JButton downloadButton;
    private JPanel panel1;
    private JTextArea textAreaFilePath;
    private JTextArea textAreaMsg;
    private String filePath;

    public MainWin() {
        textAreaFilePath.setLineWrap(true);
        textAreaMsg.setLineWrap(true);
        textAreaFilePath.setTransferHandler(new TransferHandler() {
            public boolean importData(JComponent comp, Transferable t) {
                try {
                    Object o = t.getTransferData(DataFlavor.javaFileListFlavor);

                    String filepath = o.toString();
                    System.out.println(filepath);
                    if(filepath.startsWith("[")) {
                        filepath = filepath.substring(1);
                    }
                    if(filepath.endsWith("]")) {
                        filepath = filepath.substring(0, filepath.length()-1);
                    }

                    if(!filepath.endsWith(".hex")) {
                        File file = new File(filepath + "\\build");
                        for (File f : file.listFiles()) {
                            if (f.toString().endsWith(".hex")) {
                                filepath = f.toString();
                            }
                        }
                    }

                    textAreaFilePath.setText(filepath);
                    filePath = filepath;
                    return true;

                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            public boolean canImport(JComponent comp, DataFlavor[] flavors) {
                for(int i=0; i<flavors.length; i++) {
                    if(DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                        return true;
                    }
                }
                return false;
            }
        });


        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Properties properties = System.getProperties();
                String currPath =  properties.getProperty("user.dir");
                //JFlash.exe -openprj'sm702.jflash' -open'app.hex' -autoreset -exit
                String cmd = "JFlash.exe -openprj"+currPath+"\\sm702.jflash -open"+filePath+" -autoreset -exit";
                System.out.println(cmd);
                Process process = null;
                try {
                    Runtime runtime = Runtime.getRuntime();
                    process = runtime.exec(cmd);

                    while(process.isAlive()) {
                        ///System.out.println("alive");
                    }

                    File file = new File("F:\\jflash.log");
                    BufferedReader reader = null;

                    try {
                        reader = new BufferedReader(new FileReader(file));
                        String lineString = null;
                        StringBuilder text = new StringBuilder();
                        int line=0;
                        while((lineString = reader.readLine())!=null) {
                            line++;
                            if(line >= 8) {
                                text.append(lineString+"\n");
                            }
                        }
                        textAreaMsg.setText(text.toString());
                        textAreaMsg.setCaretPosition(textAreaMsg.getText().length());
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(process != null) {
                        process.destroy();
                    }
                }
            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Download");
        frame.setContentPane(new MainWin().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(500, 300);
        frame.setAlwaysOnTop(true);
        int screenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        int frameX = frame.getSize().width;
        int screenY = Toolkit.getDefaultToolkit().getScreenSize().height;
        int frameY = frame.getSize().height;
        frame.setLocation(screenX-frameX, screenY-frameY-40);
    }
}


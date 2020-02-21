/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author LuisMiguel
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    JFileChooser open = new JFileChooser();
    ArrayList<Lexeme> tokens = new ArrayList<>();
    ArrayList<RegularExpression> expressions = new ArrayList<>();
    File file;
    FileInputStream input;
    FileOutputStream output;

    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
        this.show();
    }

    public String openFile(File file) {
        String document = "";
        try {
            input = new FileInputStream(file);
            int ascci;
            while ((ascci = input.read()) != -1) {
                char character = (char) ascci;
                document += character;
            }
        } catch (Exception e) {

        }
        return document;
    }

    public String saveFile(File file, String document) {
        String message = null;
        try {
            output = new FileOutputStream(file);
            byte[] bytxt = document.getBytes();
            output.write(bytxt);
            message = "Document saved";
        } catch (Exception e) {

        }
        return message;
    }

    private void Analyse() {
        String concat = "";
        int currentState = 0;
        //ArrayList<Character> txt = new ArrayList<>();
        char[] txt = jTextArea1.getText().toCharArray();
        for (int i = 0; i < txt.length; i++) {
            switch (currentState) {
                case 0: //estado inicial
                    if (Character.isLetter(txt[i])) {
                        concat = String.valueOf(txt[i]);
                        currentState = 1;
                    } else {
                        switch (txt[i]) {
                            case '/':
                                currentState = 2;
                                break;
                            case '<':
                                currentState = 4;
                                break;
                            case '"':
                                currentState = 7;
                                break;
                            default:                                
                                Lexeme a = typeOfSymbol(txt[i]);
                                if(a.getToken() != null){
                                    tokens.add(a);
                                }
                                break;
                        }
                    }
                    break;
                case 1: //identificadores y reservadas
                    if (Character.isLetter(txt[i]) || Character.isDigit(txt[i]) || txt[i] == '_') {
                        concat += String.valueOf(txt[i]);
                    } else {
                        if ("CONJ".equals(concat)) {
                            Lexeme aux = new Lexeme(concat, 1);
                            tokens.add(aux);
                        } else {
                            Lexeme aux = new Lexeme(concat, 2);
                            tokens.add(aux);
                        }
                        concat = "";
                        currentState = 0;
                        i--;
                    }
                    break;
                case 2: //transición a comentarios
                    if (txt[i] == '/') {
                        currentState = 3;
                    }
                    break;
                case 3: //concatenación y aceptación de comentarios
                    if (txt[i] != '\n') {
                        concat += txt[i];
                    } else {
                        Lexeme aux = new Lexeme(concat, 3);
                        tokens.add(aux);
                        concat = "";
                        currentState = 0;
                    }
                    break;
                case 4: //transición a comentarios multilinea
                    if (txt[i] == '!') {
                        currentState = 5;
                    }
                    break;
                case 5: //concatenación de comentarios multilínea
                    if (txt[i] != '!') {
                        concat += txt[i];
                    } else {
                        currentState = 6;
                    }
                    break;
                case 6: //aceptación de comentarios multilinea o regreso a estado 5
                    if (txt[i] == '>') {
                        Lexeme aux = new Lexeme(concat, 4);
                        tokens.add(aux);
                        concat = "";
                        currentState = 0;
                    } else {
                        concat += '!';
                        currentState = 5;
                    }
                    break;
                case 7: //concatenando y aceptando strings
                    if (txt[i] != '"') {
                        concat += txt[i];
                    } else {
                        Lexeme aux = new Lexeme(concat, 18);
                        tokens.add(aux);
                        concat = "";
                        currentState = 0;
                    }
                    break;

            }
        }
    }

    private Lexeme typeOfSymbol(char txt) {
        Lexeme symbol = new Lexeme();
        switch (txt) {
            case '{':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(6);
                break;
            case '}':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(7);
                break;
            case '~':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(8);
                break;
            case '%':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(9);
                break;
            case '>':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(10);
                break;
            case ':':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(11);
                break;
            case ';':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(12);
                break;
            case '.':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(13);
                break;
            case '-':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(14);
                break;
            case '+':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(15);
                break;
            case '*':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(16);
                break;
            case '?':
                symbol.setToken(String.valueOf(txt));
                symbol.setCode(17);
                break;
        }
        return symbol;
    }
    
    private void getRegularExpression(){
        for(int i=0;i<tokens.size();i++){
            if(tokens.get(i).getCode()==1){ //skiping all de CONJ definitions
                while(tokens.get(i).getCode()!= 12){
                    i++;
                }
            }else if(tokens.get(i).getCode()==2){ //reading regular expressions
                RegularExpression re = new RegularExpression();
                re.setName(tokens.get(i).getToken());
                ArrayList<String> nodes = new ArrayList<>();
                i++;
                while(tokens.get(i).getCode()!=12){
                    if(tokens.get(i).getCode()!=6 || tokens.get(i).getCode()!=7 || tokens.get(i).getCode()!=10 || tokens.get(i).getCode()!=14){
                       nodes.add(tokens.get(i).getToken()); 
                    }                    
                    i++;
                }
                re.setNode(nodes);
                expressions.add(re);
                re=null;
            }else if(tokens.get(i).getCode()==9){ //exit because %%%% so no more regular expressions
                i=tokens.size();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                .addContainerGap())
        );

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Open");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Save");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Compiler");

        jMenuItem3.setText("Analyze file");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem4.setText("Generate automata");
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        if (open.showDialog(null, "open") == JFileChooser.APPROVE_OPTION) {
            file = open.getSelectedFile();
            if (file.canRead()) {
                if (file.getName().endsWith("er")) {
                    String document = openFile(file);
                    jTextArea1.setText(document);
                } else {
                    JOptionPane.showMessageDialog(null, "Incompatible file");
                }
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        Analyse();
        getRegularExpression();
        int abc =0;
        /*for(int i = 0;i<expressions.size();i++){
            System.out.println(expressions.get(i).getName());
            for(int j = 0;j<expressions.get(i).getNode().size();j++){
                System.out.println(expressions.get(i).getNode().get(j));
            }
            System.out.println("\n"); 
            
        }*/
        //expressions.forEach((e) -> System.out.println(e.getNode().get(abc)));
        tokens.forEach((t) -> System.out.println(t.getToken()+" ---> "+t.getCode()));
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        if(open.showDialog(null, "Save")==JFileChooser.APPROVE_OPTION){
            file=open.getSelectedFile();
            if(file.getName().endsWith("er")){
                String document = jTextArea1.getText();
                String message = saveFile(file,document);
                if(message != null){
                    JOptionPane.showMessageDialog(null, message);
                }else{
                    JOptionPane.showMessageDialog(null, "Incompatible file");
                }
            }else{
                JOptionPane.showMessageDialog(null, "Save text document");
            }
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}

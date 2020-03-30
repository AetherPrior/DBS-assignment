/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBSGUI;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author l12-o-0-554
 */
public class DBSInput extends javax.swing.JFrame {

    /**
     * Creates new form DBSInput
     */
    public DBSInput() {
        initComponents();
    }
    static Set<String> closureHandler(String[] X, String FD)
    {
        FD = FD.replaceAll("\\s", "");
        
        Set<String> Cl = new HashSet<>(Arrays.asList(X.clone())); 
        
        Set<String> XY = new HashSet<>(Arrays.asList(FD.split(";")));
        
        do{
        Set<String>OldCl = new HashSet<>(Cl);
             
        for(String i: XY)
        {
            String[] lr = i.split("->");
            String X_Arr[] = lr[0].split(","); //indexing not supported in sets
            String Y_Arr[] = lr[1].split(",");
            Set<String> Xdep = new HashSet<>();
            Xdep.addAll(Arrays.asList(X_Arr));
            Set<String> XSC = new HashSet<>(Cl);
            XSC.retainAll(Xdep);
            if (XSC == Xdep) {
                //then Xset was a superset.
                Cl.addAll(Arrays.asList(Y_Arr));
            }
        }
        if(OldCl.equals(Cl))
            break;
        }while(true);
        
        
        return Cl;
    }
    
    static Set<Set<String>> candidateKeys(String[] X, String FD)
    {
        FD = FD.replaceAll("\\s", "");
        
        Set<String> Xset = new HashSet<>(Arrays.asList(X.clone())); 
        
        Set<String> XY = new HashSet<>(Arrays.asList(FD.split(";")));

        Set<Set<String>> s1 = new HashSet<>();
        s1.add(Xset);
        
        while(true)
        {
        Set<Set<String>> oldS1 = new HashSet<>(s1);
        boolean modifiedFlag = false;
        for(Set<String> set: oldS1) 
        {
            for(String i: XY)
            {
                String[] lr = i.split("->");
                String X_Arr[] = lr[0].split(","); //indexing not supported in sets
                String Y_Arr[] = lr[1].split(",");
                
                Set<String> Xdep = new HashSet<>(Arrays.asList(X_Arr));
                Set<String> Ydep = new HashSet<>(Arrays.asList(Y_Arr));
                Set<String> YSC = new HashSet<>(set);
                YSC.retainAll(Ydep);
                
                Set<String> XSC = new HashSet<>(set);
                XSC.retainAll(Xdep);
                
                if (YSC.equals(Ydep) && XSC.equals(Xdep)) 
                {
                    //then set was a superset.
                    Set<String> Subset = new HashSet(set);
                    Subset.removeAll(Ydep);
                    if(!modifiedFlag)
                    {
                        modifiedFlag = true;
                        s1.clear();
                    }
                    s1.add(Subset);
                } 
            }
        }
        if(!modifiedFlag)
            break;
        }
        return s1;
    }
    
    static Map<Set<String>, Set<String>> parseFD(String FD)
    {
        FD = FD.replaceAll("\\s", "");
        Set<String> XY = new HashSet<>(Arrays.asList(FD.split(";")));
        Map<Set<String>, Set<String>> F = new HashMap<>();
        
        for(var i: XY)
        {
            String[] lr = i.split("->");
            String X_Arr[] = lr[0].split(","); 
            String Y_Arr[] = lr[1].split(",");

            Set<String> X = new HashSet<>(Arrays.asList(X_Arr));
            Set<String> Y = new HashSet<>(Arrays.asList(Y_Arr));
            F.put(X,Y);
        }       
        return F;
    }
    
    static boolean isPrime(String X, Set<Set<String>> Keys)
    {
        for(var CD:Keys)
            if(CD.contains(X))
                return true;
        return false;
    }
    
    static Set<Set<String>> to2NF(Set<Set<String>> R, Map<Set<String>,Set<String>> FD, Set<Set<String>> Keys, Set<String> PK)
    {
        Set<Set<String>> Rnew = new HashSet<>(R);
        
        while(true)
        {
            boolean modifiedFlag = false;
            for(var Ri : Rnew)
            {
                for(var i: FD.entrySet())
                {
                    var X = i.getKey();
                    var Y = i.getValue();
                    
                    if(isPrime(Y.iterator().next(), Keys))
                        continue;
                    
                    var t = new HashSet<>(X);
                    t.retainAll(PK);
                    if(!PK.equals(X)) 
                    {
                        t = new HashSet<>(Ri);
                        t.retainAll(X);
                        if(t.equals(X))
                        {
                            Rnew.remove(Ri);
                            t = new HashSet<>(Y);
                            t.removeAll(X);
                            var t2 = new HashSet<>(Ri); 
                            t2.removeAll(t);
                            Rnew.add(t2);
                            
                            X.addAll(Y);
                            Rnew.add(X);
                           
                            //modifiedFlag = true;
                            break;
                        }
                    } 
                }
            }
            if(!modifiedFlag)
                break;
        }
        
        return Rnew;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(600, 300));
        setPreferredSize(new java.awt.Dimension(600, 296));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBS.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(DBSInput.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DBSInput.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DBSInput.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DBSInput.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        //Before importing any image, make sure you add it to the project resources.
        
        java.awt.EventQueue.invokeLater(() -> {
            new DBSInput().setVisible(true);
        });
        String FD = new String();
        //FD = "A->B;B,C->E;E,D->A;";
        FD = "A,B->C;B->D;A->E;";
        
        //input A->B 
        //      BC->E
        //      ED->A
        
        //output:
        //      ACD,BCD,CDE
        
        String[] X = new String[5];
        X[0] = "A";
        X[1] = "B";
        X[2] = "C";
        X[3] = "D";
        X[4] = "E";
        Set<Set<String>> S1 = candidateKeys(X,FD);
        
        for(Set<String> i: S1)
        {
            for(String j:i)
            {
                System.out.print(j);
            }
            System.out.println("");
        }
        
        var F = parseFD(FD);
        
        Set<String> inputR = new HashSet<>();
        inputR.add("A");
        inputR.add("B");
        inputR.add("C");
        inputR.add("D");
        inputR.add("E");
        
        Set<Set<String>> R = new HashSet<>();
        R.add(inputR);
  
        System.out.println(R);
        
        Set<String> PK = new HashSet<>();
        PK.add("A");
        PK.add("B");
        
        R = to2NF(R, F, S1, PK);
        
        System.out.println(R);
        //for(var i : F.entrySet())
        //    System.out.println();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}

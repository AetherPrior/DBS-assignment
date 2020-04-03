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
import java.util.concurrent.ConcurrentHashMap;

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
    
    static Set<String> closure(Set<String> X, Map<Set<String>,Set<String>> FD)
    {
        Set<String> Cl = new HashSet<>(X); 
        
        do{
            Set<String> OldCl = new HashSet<>(Cl);

            for(var i: FD.entrySet())
            {
                var Y = i.getKey();
                var Z = i.getValue();
                var t = new HashSet<>(Y);
                t.removeAll(Cl);
                if (t.isEmpty()) {
                    //then Cl is superset of Y
                    Cl.addAll(Z);
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
    
    static Set<Set<String>> candidateKeys2(Set<String> X, Map<Set<String>,Set<String>> FD)
    {        
        Set<String> Xset = new HashSet<>(X);

        Set<Set<String>> s1 = new HashSet<>();
        s1.add(Xset);
        
        while(true)
        {
        Set<Set<String>> oldS1 = new HashSet<>(s1);
        boolean modifiedFlag = false;
        for(Set<String> set: oldS1) 
        {
            for(var i: FD.entrySet())
            {
                var Xdep = i.getKey();
                var Ydep = i.getValue();
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
            F.put(X, Y);
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
    
    static boolean isPartKey(Set<Set<String>> ck, Set<String> x)
    {
        for(var key: ck)
        {
            if(x.equals(key))
                return false;
            boolean flag = true;
            for(String k: x)
                if(!key.contains(k))
                {
                    flag=false;
                    break;
                }
            if(flag==true)
                return true;
        }
        return false;
    }

    static boolean is2NF(Set<Set<String>> ck, Map<Set<String>, Set<String>> fd)
    {
        //System.out.println(ck);
	for(var i: fd.entrySet())
	{
            var X = i.getKey();
            var Y = i.getValue();
            for(String s: Y)
            {
                if(!isPrime(s, ck))
                {
                    System.out.println("baba");
                    for(var key: ck)
                    {			
                        if(X.equals(key))
                            return true;
                        boolean flag = true;
                        for(String k: X)
                            if(!key.contains(k))
                            {
                                flag=false;
                                break;
                            }
                        if(flag==true)
                            return false;
                    }
                }
            }
	}
	return true;
    }
    
    static boolean is3NF(Set<Set<String>> ck, Map<Set<String>, Set<String>> fd)
    {
	for(var i: fd.entrySet())
	{
            var X = i.getKey();
            var Y = i.getValue();
            for(String s: Y)
            {
                if(!isPrime(s, ck))
                {
                    boolean flag1 = false;
                    for(var key: ck)
                    {
                        boolean flag2 = true;
                        for(String k: key)
                            if(!(X.contains(k)))
                                flag2 = false;
                        if(flag2)
                        {
                            flag1=true;
                            break;
                        }
                    }
                    if(!flag1)
                        return false;
                }
            }
	}
	return true;
    }

    static boolean isBCNF(Set<Set<String>> ck, Map<Set<String>, Set<String>> fd)
    {
	for(var i: fd.entrySet())
	{
            var X = i.getKey();
            boolean flag1 = false;
            for(var key: ck)
            {
                boolean flag2 = true;
                for(String k: key)
                    if(!(X.contains(k)))
                        flag2 = false;
                if(flag2)
                {
                    flag1=true;
                    break;
                }
            }
            if(!flag1)
                return false;
	}
	return true;
    }

    static void checkNF(Set<Set<String>> ck, Map<Set<String>, Set<String>> fd)
    {
	if(is2NF(ck, fd)==true)
            if(is3NF(ck, fd))
                if(isBCNF(ck, fd))
                    System.out.println("BCNF");
                else
                    System.out.println("3NF");
            else
                System.out.println("2NF");
	else
            System.out.println("1NF");
    }
    
    static Set<Set<String>> to2NF(Set<Set<String>> R, Map<Set<String>,Set<String>> FD, Set<Set<String>> Keys, Set<String> PK)
    {
        
        Set<Set<String>> Rnew = new HashSet<>();
        //Set<Set<String>> Rnew = ConcurrentHashMap.newKeySet();
        //Rnew.addAll(R);
        Set<Set<String>> violate = new HashSet<>();
        
        for(var r : R)
        {
            for(var i: FD.entrySet())
            {
                var X = i.getKey();
                var Y = i.getValue();

                if(X.equals(PK))
                    continue;
                
                for(var y : Y)
                {
                    if(isPrime(y, Keys))
                        continue;
                    
                    var t = new HashSet<>(X);
                    t.removeAll(PK);

                    if(t.isEmpty()) 
                        violate.add(X);  
                }
            }
        }
        
        Set<String> done = new HashSet<>();
        for(var v : violate)
        {
            var c = closure(v,FD);
            var t = new HashSet<>(c);
            t.removeAll(v);
            done.addAll(t);
            Rnew.add(c);
        }
        
        var c = closure(PK, FD);
        c.removeAll(done);
        Rnew.add(c);
        
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

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBS.png"))); // NOI18N
        jLabel1.setText("jLabel2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        
        /*
            java.awt.EventQueue.invokeLater(() -> {
            new DBSInput().setVisible(true);
        });
        */
                
        String FD = new String();
        //FD = "A->B;B,C->E;E,D->A;";
        FD = "A->B,C;C,D->E;B->D;E->A";
        
        //input A->B 
        //      BC->E
        //      ED->A
        
        //output:
        //      ACD,BCD,CDE
        
        String[] x = new String[]{"A", "B", "C", "D", "E"};
        Set<String> X = new HashSet<>(Arrays.asList(x));
        
        var F = parseFD(FD);
        System.out.println(FD);
        System.out.println(F);
        
        Set<Set<String>> CK = candidateKeys2(X,F);
        System.out.println("Candidate keys: " + CK);
        
        String []r1 = new String[]{"A","B","C","D","E"};
        Set<String> inputR = new HashSet<>(Arrays.asList(r1));
        
        Set<Set<String>> R = new HashSet<>();
        R.add(inputR);
        
        System.out.println(R);
        
        String []pk = new String[]{"A","B"};
        Set<String> PK = new HashSet<>(Arrays.asList(pk));
        
        R = to2NF(R, F, CK, PK);
        System.out.println(R);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}

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
        Set<String> C = new HashSet<>(X); 
        
        do{
            Set<String> OldC = new HashSet<>(C);

            for(var i: FD.entrySet())
            {
                var Y = i.getKey();
                var Z = i.getValue();
                if(C.containsAll(Y))
                    C.addAll(Z);
            }
            if(OldC.equals(C))
                break;
        }while(true);
        
        return C;
    }
    
    static boolean covers(Map<Set<String>,Set<String>> F, Map<Set<String>,Set<String>> E)
    {
        for(var i : E.entrySet())
        {
            var X = i.getKey();
            var Y = i.getValue();
            if(!closure(X,F).containsAll(Y))
                return false;
        }
        return true;
    }
    
    static boolean equivalent(Map<Set<String>,Set<String>> F, Map<Set<String>,Set<String>> E)
    {
        if(covers(F,E) && covers(E,F))
            return true;
        else
            return false;
    }
    
    static Map<Set<String>,Set<String>> minimalCover(Map<Set<String>,Set<String>> E)
    {
        Map<Set<String>, Set<String>> F = new ConcurrentHashMap<>(E);
        var f = F.entrySet();
        
        here1:
        for(var X_A : f)
        {
            var X = X_A.getKey();
            var A = X_A.getValue();
                
            for(var Ai : A)
            {
                for(var B : X)
                {
                    var F1 = new HashMap<>(F);
                    F1.remove(X);
                    
                    var a = new HashSet<>(A);
                    a.remove(Ai);
                    F1.put(X,a);
                    
                    var t = new HashSet<>(X);
                    t.remove(B);

                    if(F1.containsKey(t))
                    {
                        var t1 = F1.get(t);
                        a = new HashSet<>(A);
                        a.addAll(t1);
                        F1.put(t,a);
                    }
                                       
                    if(equivalent(F,F1))
                    {
                        F.clear();
                        F.putAll(F1);
                        break here1;
                    }
                }
            }
        }
        
        here2:
        for(var X_A : f)
        {
            var X = X_A.getKey();
            var A = X_A.getValue();
                
            for(var Ai : A)
            {
                var F1 = new HashMap<>(F);
                F1.remove(X);
                var t = new HashSet<>(A);
                t.remove(Ai);
                F1.put(X,t);

                if(equivalent(F,F1))
                {
                    F.clear();
                    F.putAll(F1);
                    break here2;
                }
            }
        }
        
        return F;
    }
    
    static Set<String> minimize(Set<String> R, Map<Set<String>,Set<String>> F)
    {        
        Set<String> K = ConcurrentHashMap.newKeySet();
        K.addAll(R);
        
        for(var A : K)
        {
            var t = new HashSet(K);
            t.remove(A);
            
            if(closure(t,F).equals(closure(K,F)))
                K.remove(A);
        }
        
        return K;
    }
    
    static Set<Set<String>> candidateKeys(Set<String> R, Map<Set<String>, Set<String>> F)
    {
        Set<Set<String>> CK = ConcurrentHashMap.newKeySet();
        CK.add(minimize(R,F));
      
        while(true)
        {
            boolean modified = false;
            for(var i : F.entrySet())
            {
                var X = i.getKey();
                var Y = i.getValue();
                
                boolean found = false;
                for(var k : CK)
                {
                    var S = new HashSet<>(X);
                    var t = new HashSet<>(k);
                    t.removeAll(Y);
                    S.addAll(t);

                    found = false;
                    for(var j : CK)
                    {   
                        var t2 = new HashSet<>(j);
                        t2.removeAll(S);
                        if(t2.isEmpty())
                        {
                            found = true;
                            break;
                        }
                    }
                    if(!found)
                    {
                        CK.add(minimize(S,F));
                        modified = true;
                    }
                }
            }
            if(!modified)
                break;
        }
        
        return CK;
    }
    
    static Map<Set<String>,Set<String>> parseFD(String FD)
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
    
    static boolean isPrime(Set<Set<String>> CK, String X)
    {
        for(var K : CK)
            if(K.contains(X))
                return true;
        return false;
    }
    
    static boolean isPartKey(Set<Set<String>> CK, Set<String> X)
    {
        for(var K : CK)
        {
            if(K.equals(X))
                return false;
            if(K.containsAll(X))
                return true;
        }
        return false;
    }
    
    static boolean isSuperKey(Set<Set<String>> CK, Set<String> X)
    {
        for(var K : CK)
            if(X.containsAll(K))
                return true;
        return false;
    }

    static boolean is2NF(Set<Set<String>> ck, Map<Set<String>,Set<String>> fd)
    {
        for(var i: fd.entrySet())
        {
            var X = i.getKey();
            var Y = i.getValue();
            for(String s: Y)
            {
                if(!isPrime(ck, s))
                    if(isPartKey(ck, X))
                        return false;
            }
        }
        return true;
    }

    static boolean is3NF(Set<Set<String>> CK, Map<Set<String>,Set<String>> FD)
    {
	for(var i : FD.entrySet())
	{
            var X = i.getKey();
            var Y = i.getValue();
            for(String y : Y)
                if(!isPrime(CK,y) && !isSuperKey(CK,X))
                    return false;
	}
	return true;
    }
    
    static boolean isBCNF(Set<Set<String>> CK, Map<Set<String>, Set<String>> FD)
    {
	for(var i : FD.entrySet())
	{
            var X = i.getKey();
            if(!isSuperKey(CK,X))
                return false;
	}
	return true;
    }

    static void checkNF(Set<Set<String>> CK, Map<Set<String>,Set<String>> FD)
    {
	if(is2NF(CK, FD))
            if(is3NF(CK, FD))
                if(isBCNF(CK, FD))
                    System.out.println("BCNF");
                else
                    System.out.println("3NF");
            else
                System.out.println("2NF");
	else
            System.out.println("1NF");
    }
    
    static Set<Set<String>> higherNF(Set<String> R, Set<Set<String>> CK, Map<Set<String>,Set<String>> FD)
    {
	if(is2NF(CK, FD))
            if(is3NF(CK, FD))
                if(isBCNF(CK, FD))
                {
                    System.out.println("Already in BCNF");
                    Set<Set<String>> D = new HashSet<>();
                    D.add(R);
                    return D;
                }
                else
                {
                    System.out.println("Decompose to BCNF");
                    return toBCNF(R,CK,FD);
                }
            else
            {
                System.out.println("Decompose to BCNF");
                return to3NF(R,CK,FD);
            }
                
	else
        {
            System.out.println("Decompose to BCNF");
            return to2NF(R,CK,FD);        
        }      
    }
    
    static Set<Set<String>> to2NF(Set<String> R, Set<Set<String>> CK, Map<Set<String>,Set<String>> FD)
    {
        Set<Set<String>> Rnew = new HashSet<>();
        Set<Set<String>> violate = new HashSet<>();

        for(var i: FD.entrySet())
        {
            var X = i.getKey();
            var Y = i.getValue();

            if(CK.contains(X))
                continue;                

            for(var y : Y)
            {
                if(isPrime(CK, y))
                    continue;

                if(isPartKey(CK, X))
                    violate.add(X);
            }
        }
        
        Set<String> done = new HashSet<>();
        for(var v : violate)
        {
            var c = closure(v,FD);
            var t = new HashSet<>(c);
            c.removeAll(done);
            if(!c.equals(v))
                Rnew.add(c);
            t.removeAll(v);
            done.addAll(t);
        }
        
        var c = new HashSet<>(R);
        c.removeAll(done);
        Rnew.add(c);
        
        return Rnew;
    }
    
    static Set<Set<String>> to3NF(Set<String> R, Set<Set<String>> CK, Map<Set<String>,Set<String>> FD)
    {
        Set<Set<String>> Rnew = ConcurrentHashMap.newKeySet();

        var G = minimalCover(FD);
        System.out.println("Minimal cover of F = " + G);
        System.out.println(G + "is 3NF? " + is3NF(CK,G));
        
        Set<String> done = new HashSet<>();
        for(var i: G.entrySet())
        {
            var X = i.getKey();
            var Y = i.getValue();      
            var t = new HashSet(X);
            t.addAll(Y);
            Rnew.add(t);
            done.addAll(t);
        }
        
        var c = new HashSet<>(R);
        c.removeAll(done);
        if(!c.isEmpty())
            Rnew.add(c);
        
        return Rnew;        
    }
    
    static Set<Set<String>> toBCNF(Set<String> R, Set<Set<String>> CK, Map<Set<String>,Set<String>> FD)
    {
        Set<Set<String>> Rnew = new HashSet<>();
        
        for(var i : FD.entrySet())
	{
            var X = i.getKey();
            var Y = i.getValue();
            if(!isSuperKey(CK,X))
            {
                var Q = new HashSet<>(R);
                Q.removeAll(Y);
                var t = new HashSet<>(X);
                t.addAll(Y);
                Rnew.add(Q);
                Rnew.add(t);
                break;
            }
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
                
        String FD;
        //FD = "A->B;B,C->E;E,D->A;";
        //FD = "A->B,C;C,D->E;B->D;E->A";
        //FD = "A,B->C;B->D;A->E";
        FD = "A,B,D->C;B,D,C->A";
        
        String[] r = new String[]{"A", "B", "C", "D", "E"};
        Set<String> R1 = new HashSet<>(Arrays.asList(r));
        Set<Set<String>> R = new HashSet<>();
        R.add(R1);
        
        System.out.println("Relation: R " + R);
        
        var F = parseFD(FD);
        
        System.out.println("FDs: " + Arrays.asList(FD.split(";")));
        
        Set<Set<String>> CK = candidateKeys(R1,F);
        System.out.println("Candidate keys: " + CK);
        
        System.out.print("Highest normal form: ");
        checkNF(CK,F);
        
        R = higherNF(R1,CK,F);
        
        int i = 1;
        for(var Ri : R)
        {
            System.out.print("R" + i + " " + Ri);
            System.out.println(" PK: " + minimize(Ri,F));
            i++;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}

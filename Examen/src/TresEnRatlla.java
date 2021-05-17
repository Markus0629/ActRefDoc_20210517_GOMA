import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class TresEnRatlla extends JFrame {
   private static final int filaGOMA = 3;
private static final int columnaGOMA = 3;
public static final int MIDA_CELLA = 150; // Amplada i al�ada de la cel�la (quadrat)
   public static final int AMPLADA_TAULA = MIDA_CELLA * columnaGOMA;  // Amplada del taulell de joc
   public static final int ALSSADA_TAULA = MIDA_CELLA * filaGOMA;  // Al�ada del taulell de joc
   // L'amplada i l'l�ada de la taula �s igual a la mida de la cel�la
   // multiplicat per la quantitat de cel�les que hi ha, �s a dir igual a 3
   public static final int AMPLADA_QUADRAT = 10;                   // Amplada de la l�nia de quadr�cula
   public static final int MEITAT_AMPLADA_QUADRAT = AMPLADA_QUADRAT / 2; // La meitat de l'amplada de la l�nia de quadr�cula
   // Els s�mbols (creu / cercle) es mostren dins d�una cel�la,
   // amb dist�ncia des de la vora (padding)
   public static final int MARGE_CELLA = MIDA_CELLA / 6; // Marge de la cel�la.
   public static final int MIDA_CARACTER = MIDA_CELLA - MARGE_CELLA * 2; // MIDA DEL CAR�CTER
 
   // Es fa servir una enumeraci� (classe interna) per representar els diversos estats del joc
   public enum EstatDelJoc {
      EN_JOC, EMPAT, GUANYA_CREU, GUANYA_CERCLE
   }
   
   private EstatDelJoc estatActual;  // Estat actual del joc
 
   // Es fa servir una enumeraci� (classe interna) per representar el contingut de les cel�les
   public enum Cella {
      BUIDA, CREU, CERCLE
   }

   private Cella jugadorActual;  // el jugador actual
 
   private Cella[][] taulell   ; // Taulell de 3 per 3 cel�les
   private EspaiTreball canvas; // Dibuixa un "canvas" (JPanel) pel taulell de joc
   private JLabel barraDEstat;  // Barra d'estat
 
   /* Constructor per configurar el joc i els components de la GUI  */
   public TresEnRatlla() {
      canvas = new EspaiTreball();  // Construeix un canvas (espai) (JPanel)
      canvas.setPreferredSize(new Dimension(AMPLADA_TAULA, ALSSADA_TAULA));
 
      // L'espai (JPanel) activa un MouseEvent pel clic del ratol�
      canvas.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {  // Gestor del clic del ratol�
            int posXDelRatoli = e.getX();
            int posYDelRatoli = e.getY();
            // Obt� la fila i la columna d'on s'hafet el clic del ratol�
            int filaSeleccionada = posYDelRatoli / MIDA_CELLA;
            int coluSeleccionada = posXDelRatoli / MIDA_CELLA;
 
            if (estatActual == EstatDelJoc.EN_JOC) {
               if (filaSeleccionada >= 0 && filaSeleccionada < 3 && coluSeleccionada >= 0
                     && coluSeleccionada < 3 && taulell[filaSeleccionada][coluSeleccionada] == Cella.BUIDA) {
                  taulell[filaSeleccionada][coluSeleccionada] = jugadorActual; // Fa un moviment
                  actualitzaElJocGOMA(jugadorActual, filaSeleccionada, coluSeleccionada); // actualitza l'estat del joc
                  // Canvia de jugador
                  jugadorActual = (jugadorActual == Cella.CREU) ? Cella.CERCLE : Cella.CREU;
               }
            } else {         // Acaba el joc!!
               iniciaElJocGOMA(); // Inicia el joc
            }
            // M�tode de class Component que torna a dibuixar el taulell a l'espai de joc
            repaint();  // Fa una crida al metode paintComponent().
         }
      });
 
      // Configuraci� de la barra d'estat (JLabel) per mostrar els missatges d'estat
      barraDEstat = new JLabel("  ");
      barraDEstat.setHorizontalAlignment(0);
      barraDEstat.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
      barraDEstat.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
 
      Container contenidor = getContentPane();
      contenidor.setLayout(new BorderLayout());
      contenidor.add(canvas, BorderLayout.CENTER);
      contenidor.add(barraDEstat, BorderLayout.PAGE_END);
 
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      pack();  // Empaqueta tots els components JFrame
      setTitle("Tres en ratlla");
      setVisible(true);  // mostra el JFrame
 
      taulell = new Cella[3][3]; // assigna la matriu
      iniciaElJocGOMA(); // per inicialitzar el contingut i les variables del taulell de joc
   }
 
   /* Inicialitzeu el contingut i l'estat del taulell de joc */
   public void iniciaElJocGOMA() {
      for (int i = 0; i < 3; ++i) {
         for (int j = 0; j < 3; ++j) {
            taulell[i][j] = Cella.BUIDA; // Buida totes les cel�les!
         }
      }
      estatActual = EstatDelJoc.EN_JOC; // Estat del joc preparat per jugar
      jugadorActual = Cella.CREU;       // El torn inicial �s per les creus
   }
 
   /* Actualitza l'estat de joc desrp�s que el jugador hagi col�locat el seu moviment
       (filaSeleccionada, coluSeleccionada). */
   public void actualitzaElJocGOMA(Cella laTirada, int filaSeleccionada, int coluSeleccionada) {
      if (hiHaJuanyador(laTirada, filaSeleccionada, coluSeleccionada)) {  // comprovar si hi ha guanyador
         estatActual = (laTirada == Cella.CREU) ? EstatDelJoc.GUANYA_CREU : EstatDelJoc.GUANYA_CERCLE;
      } else if (hiHaEmpat()) {  // Comprova si hi ha un empat
         estatActual = EstatDelJoc.EMPAT;
      }
      // En cas contrari, no hi haur� cap canvi a l�estat actual (encara EstatDelJoc.EN_JOC).
   }
 
   /* Torna cert si hi ha un empat (p.e., no hi ha cap cel�la buida) */
   public boolean hiHaEmpat() {
      for (int i = 0; i < 3; ++i) {
         for (int j = 0; j < 3; ++j) {
            if (taulell[i][j] == Cella.BUIDA) {
               return false; // s'ha trobat una cel�la buida, no hi ha empat, surt
            }
         }
      }
      return true;  // ja no hi ha cap cel�la buida, �s un empat
   }
 
   /* Torna cert si el jugador amb "laTirada" ha guanyat despr�s de col�locar-se a
       (filaSeleccionada, coluSeleccionada) */
   public boolean hiHaJuanyador(Cella laTirada, int filaSeleccionada, int coluSeleccionada) {
      boolean guanyador = false;
      
      boolean diagonalPrincipal = false;
      boolean diagonalSecundaria = false;
      
      if (taulell[filaSeleccionada][0] == laTirada  // 3 a una fila
           && taulell[filaSeleccionada][1] == laTirada
           && taulell[filaSeleccionada][2] == laTirada
       || taulell[0][coluSeleccionada] == laTirada      // 3 a una columna
           && taulell[1][coluSeleccionada] == laTirada
           && taulell[2][coluSeleccionada] == laTirada) {
         guanyador = true;
      } 
      if (filaSeleccionada == coluSeleccionada            // 3 a diagonal principal
           && taulell[0][0] == laTirada
           && taulell[1][1] == laTirada
           && taulell[2][2] == laTirada) {
         guanyador = true;
        } 
      if (filaSeleccionada + coluSeleccionada == 2  // 3 a diagonal secundaria
           && taulell[0][2] == laTirada
           && taulell[1][1] == laTirada
           && taulell[2][0] == laTirada) {
         guanyador = true;
      }
         return guanyador;
   }
 
   /*
    *  EspaiTreball �s una classe interna (amplia JPanel) que es
    *    fa servir per dibuixar gr�fics personalitzats.
    */
   class EspaiTreball extends JPanel {
      @Override
      public void paintComponent(Graphics g) {  // invocar mitjan�ant repaint()
         super.paintComponent(g);    // omplir fons (background)
         setBackground(Color.WHITE); // canvia el seu color de fons (background)
 
         // Dibuixa les l�nies de quadr�cula
         g.setColor(Color.LIGHT_GRAY);
         for (int i = 1; i < 3; ++i) {
            g.fillRoundRect(0, MIDA_CELLA * i - MEITAT_AMPLADA_QUADRAT,
                  AMPLADA_TAULA-1, AMPLADA_QUADRAT, AMPLADA_QUADRAT, AMPLADA_QUADRAT);
         }
         for (int j = 1; j < 3; ++j) {
            g.fillRoundRect(MIDA_CELLA * j - MEITAT_AMPLADA_QUADRAT, 0,
                  AMPLADA_QUADRAT, ALSSADA_TAULA-1, AMPLADA_QUADRAT, AMPLADA_QUADRAT);
         }
 
         // Dibuixa "les tirades" de totes les cel�les si no estan buides
         // Fa servir la classe Graphics2D que ens permet establir el tra� del llapis
         Graphics2D graficEnDuesDimensions = (Graphics2D)g;
         // amplada de la ploma = 10
         graficEnDuesDimensions.setStroke(new BasicStroke(10,
               BasicStroke.CAP_ROUND,
               BasicStroke.JOIN_ROUND));  // Graphics2D exclusivament
         for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
               int x1 = j * MIDA_CELLA + MARGE_CELLA;
               int y1 = i * MIDA_CELLA + MARGE_CELLA;
               if (taulell[i][j] == Cella.CREU) {
                  graficEnDuesDimensions.setColor(Color.RED);
                  int x2 = (j + 1) * MIDA_CELLA - MARGE_CELLA;
                  int y2 = (i + 1) * MIDA_CELLA - MARGE_CELLA;
                  graficEnDuesDimensions.drawLine(x1, y1, x2, y2);
                  graficEnDuesDimensions.drawLine(x2, y1, x1, y2);
               } else if (taulell[i][j] == Cella.CERCLE) {
                  graficEnDuesDimensions.setColor(Color.BLUE);
                  graficEnDuesDimensions.drawOval(x1, y1, MIDA_CARACTER, MIDA_CARACTER);
               }
            }
         }
 
         // Mostra el missatge de la barra d'estat
         if (estatActual == EstatDelJoc.EN_JOC) {
            barraDEstat.setForeground(Color.BLACK);
            if (jugadorActual == Cella.CREU) {
               String textTornDeLesXsGoma = "Torn de les X's";
			barraDEstat.setText(textTornDeLesXsGoma);
            } else {
               String textTornDeLesOsGOMA = "Torn de les O's";
			barraDEstat.setText(textTornDeLesOsGOMA);
            }
         } else {
			String textSegueixGOMA = "Pitja per tornar a jugar!";
			if (estatActual == EstatDelJoc.EMPAT) {
			    barraDEstat.setBackground(Color.MAGENTA);
			    barraDEstat.setForeground(Color.RED);
			    barraDEstat.setText("Hi ha empat!" + textSegueixGOMA);
			 } else if (estatActual == EstatDelJoc.GUANYA_CREU) {
			    barraDEstat.setForeground(Color.RED);
			    barraDEstat.setText("Guanyen les 'X'!" + textSegueixGOMA);
			 } else if (estatActual == EstatDelJoc.GUANYA_CERCLE) {
			    barraDEstat.setForeground(Color.RED);
			    barraDEstat.setText("Guanyen les 'O'!" + textSegueixGOMA);
			 }
		}
      }
   }
}
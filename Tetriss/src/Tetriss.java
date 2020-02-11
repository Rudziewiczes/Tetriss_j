import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tetriss extends JFrame {

    final int R_BLOKI = 23;
    final int SZEROKOSC = 8;
    final int WYSOKOSC =15;

    Plansza plansza = new Plansza();
    Random los = new Random();
    Tetormino tetormino = new Tetormino();

    boolean start = false;

    int[][] tabr = new int[WYSOKOSC+1][SZEROKOSC];


    public static void main(String[] args)

    {
        Tetriss tetris= new Tetriss();
        tetris.run();
    }

    Tetriss() {
        final int K_LEWO = 37;
        final int K_GORA = 38;
        final int K_PRAWO = 39;
        final int K_DOL = 40;
        int POCZATKOWA_POZYCJA = 100;
        int FIGURE_X = 8;
        int FIGURE_Y = 25;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        plansza.setBackground(Color.white);
        add(BorderLayout.CENTER,plansza);
        setBounds(POCZATKOWA_POZYCJA, POCZATKOWA_POZYCJA, SZEROKOSC* R_BLOKI + FIGURE_X, WYSOKOSC * R_BLOKI +FIGURE_Y);////

        addKeyListener (new KeyAdapter()
        {public void keyPressed(KeyEvent e)
            { if (!start) {
                    int k = e.getExtendedKeyCode();
                    if (k == K_DOL) tetormino.padanie();
                    if (k == K_GORA) tetormino.obracanie();
                    if (k == K_LEWO) tetormino.ruch(e.getExtendedKeyCode());
                    if (k == K_PRAWO) tetormino.ruch(e.getExtendedKeyCode());
                }
                plansza.repaint();
            }
        });

    }
    public void run() {
        long czekaj, startgra, cykl;
        short op = 500;
        while (!start) {
            startgra = System.nanoTime();
            cykl = System.nanoTime()- startgra;
            czekaj = op-cykl/10000000;
            try {Thread.sleep(czekaj);} catch (InterruptedException e) {e.printStackTrace();}
            plansza.repaint();
            czyWszystkoZapelnione();
            if (tetormino.dotkniecieZiemi())
            {
                tetormino.naZiemi();
                tetormino = new Tetormino();
            } else
                tetormino.padanieNaPole();


        }
    }
    public void czyWszystkoZapelnione() {
        Arrays.fill(tabr[15],1);
        int wiersz = 14 ;
        while (wiersz > 0) {
            float zapelnione = 1;
            for (int j = 0; j<8; j++)
                zapelnione = zapelnione * Math.signum(tabr[14][j]);////////
            if (zapelnione > 0) {
                wiersz++;
                for (int i =14; i>0; i--)
                    System.arraycopy(tabr[i-1],0, tabr[i],0, 8);
            } else
                wiersz--;
        }
    }
    public class Bloczek {
        int x ;//////////////////////////////
        int y ;
        public Bloczek(int x, int y) {
            this.x = x;
            this.y =y;
        }
        public int getX() {
            return this.x;
        }
        public int getY() {
            return this.y;
        }

        public void setx(int x) {
            this.x = x;
        }

        public void sety(int y) {
            this.y = y;
        }

        void paint(Graphics g, int kolor)
        {
            g.drawRoundRect(x*R_BLOKI+1, y*R_BLOKI+1, R_BLOKI-2, R_BLOKI-2, R_BLOKI, R_BLOKI);
        }
        public int getx()  {
            return x;
        }
        public int gety()  {
            return y;
        }
    }

    public class Tetormino {
        final int[][][] MODEL= {
                {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0},{3} },/////
                {{0,0,1,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}, {3}},
                {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}, {3}},
                {{1,1,1,0}, {0,1,0,0}, {0,0,0,0}, {0,0,0,0}, {3}},
                {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}, {3}},
                {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}, {4}},
                {{0,0,0,0}, {0,1,1,0}, {0,1,1,0}, {0,0,0,0},{4} },
        };
        private ArrayList<Bloczek> tetormino = new ArrayList<Bloczek>();
        private int[][] tab = new int[4][4];
        int model;int kolor;int rozmiar;
        Tetormino() {
            model = los.nextInt(MODEL.length);/////////
            for (int i = 0; i<4; i++)
                System.arraycopy(MODEL[model][i],0,tab[i],0,MODEL[model][i].length);
            noweTetormino();
        }
        private boolean dotkniecieZiemi() {
            for (Bloczek bloczek:tetormino)/////
                if (tabr[bloczek.gety()+1][bloczek.getx()]>0) return true;
            return false;
        }
        protected void naZiemi() {
            kolor=MODEL[model][4][0];
            for (Bloczek bloczek:tetormino)
                tabr[bloczek.gety()][bloczek.getx()]=kolor;///

        }
        private void noweTetormino() {
            for (int x = 0; x<4; x++)
                for (int y = 0; y<4; y++)
                    if (tab[y][x]==1)tetormino.add(new Bloczek(x+this.x,y+this.y));
        }
        private boolean wyjscieZaPole(int obrot) {
            for (Bloczek bloczek:tetormino) {
                if (obrot == 39 && (bloczek.getx()==7 || tabr[bloczek.gety()][bloczek.getx()+1]>0))//////////////
                    return true;
                if (obrot == 37 && (bloczek.getx()==0 || tabr[bloczek.gety()][bloczek.getx()-1]>0))
                    return true;
            }
            return false;
        }
        private void ruch (int obrot)
        {
            if (!wyjscieZaPole(obrot))
            { for (Bloczek bloczek:tetormino)bloczek.setx(bloczek.getx()+obrot-38);
            }
        }
        /////////
        private int x=2;
        private int y=0;
        private void padanieNaPole() {
            for (Bloczek bloczek:tetormino)bloczek.sety(bloczek.gety()+1);///
            y++;
        }
        private void padanie ()
        {
            while (!dotkniecieZiemi())
                padanieNaPole();
        }
        private boolean nieprawidoweZapelnienie() {
            for (int x = 0;x<4; x++)
                for (int y = 0;y<4;y++)
                {
                    if (this.x<0 || this.x>7) return true;
                    if (tabr[this.x][this.y]>0) return true;
                }
            return false;
        }
        private void obracanie() {
            rozmiar = MODEL[model][4][0];
            for (int i=0; i<rozmiar; i++)
                for (int j=i; j<rozmiar-1-i;j++) {
                    int mod=tab[rozmiar-1-j][i];//////////
                    tab[rozmiar-1-j][i] = tab[rozmiar-1-i][rozmiar-1-j];///////////
                    tab[rozmiar-1-i][rozmiar-1-j] = tab[j][rozmiar-1-i];
                    tab[j][rozmiar-1-i] = tab[i][j];
                    tab[i][j]=mod;/////
                }
            if (!nieprawidoweZapelnienie()) {
                tetormino.clear();
                noweTetormino();
            }
        }
        private void paint(Graphics k) {
            for (Bloczek bloczek:tetormino) bloczek.paint(k,kolor);
        }
    }
    public class Plansza extends JPanel {
        @Override
        public void paint(Graphics k) {
            super.paint(k);
            tetormino.paint(k);
            for (int x=0; x<SZEROKOSC; x++)
                for (int y=0; y<WYSOKOSC; y++) {
                    if (tabr[y][x]>0) {
                        k.fillRect((x*R_BLOKI)+1,(y*R_BLOKI)+1,R_BLOKI-1,R_BLOKI-1);///////
                    }

                }
        }
    }
}

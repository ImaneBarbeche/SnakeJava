import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // dimensions de l'écran : le plateau est un carré de 600px, divisé en blocs de
    // 25px.
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    final int x[] = new int[GAME_UNITS]; // tableau pour stocker les coordonnées x du serpent
    final int y[] = new int[GAME_UNITS]; // tableau pour stocker les coordonnées y du serpent

    int bodyParts = 6; // le serpent commence avec 6 blocs de corps

    // Image pour la pomme
    Image cutestrawberry = new ImageIcon(getClass().getResource("img.png")).getImage();

    // coordonnées de la pomme
    int strawberryX;
    int strawberryY;

    char direction = 'R'; // le serpent commence à aller vers la droite
    boolean running = false; // variable pour savoir si le jeu est en cours
    Timer timer; // timer pour contrôler la vitesse du serpent
    Random random = new Random(); // placer la pomme aléatoirement
    int strawberrysEaten; // nombre de pommes mangées (score)

    public GamePanel() {
        this.setPreferredSize(new Dimension(600, 600));
        this.setBackground(new Color(230, 230, 250)); // lavande clair
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // bordure noire
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
    } // Configure l’apparence du jeu + prépare l'écoute des touches clavier.

    public void startGame() {
        newstrawberry(); // place la première pomme aléatoirement
        timer = new Timer(100, this); // 100 ms pour chaque mouvement
        timer.start(); // démarre le timer
        running = true; // le jeu commence
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move(); // déplace le serpent
            checkstrawberry(); // vérifie si le serpent mange une pomme
            checkCollision(); // vérifie les collisions
        }
        repaint(); // redessine le panel
    }

    public void newstrawberry() {
        strawberryX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        strawberryY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        bodyParts = 6; // réinitialise la taille du serpent
                        for (int i = 0; i < bodyParts; i++) {
                            x[i] = 0;
                            y[i] = 0;
                        }
                        strawberrysEaten = 0;
                        direction = 'R';
                        running = true;
                        newstrawberry();
                        timer = new Timer(100, GamePanel.this);
                        timer.start();
                    }
                    break;
            }
        }
    }

    public void move() {
        // Chaque segment du corps prend la position de celui devant lui.
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) { // met à jour la position de la tête du serpent en fonction de la direction
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
    }

    public void checkstrawberry() { // vérifie si le serpent mange une pomme

        if (x[0] == strawberryX && y[0] == strawberryY) { // si la tête du serpent est sur la même position que la pomme
            bodyParts++;
            strawberrysEaten++;
            newstrawberry();
        }
    }

    public void checkCollision() {
        // vérifie si le serpent se mord la queue ou sort de l'écran
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
            }
        }
        // vérifie si la tête du serpent sort de l'écran
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
            repaint();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(new Color(255, 182, 193)); // couleur de la pomme
            g.drawImage(cutestrawberry, strawberryX, strawberryY, UNIT_SIZE, UNIT_SIZE, this);
            // dessine la pomme

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(210, 235, 255)); // tête du serpent
                } else {
                    g.setColor(new Color(255, 220, 230)); // corps du serpent
                }
                g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("Score: " + strawberrysEaten, 10, 20);
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
    // fond arrondi
    g.setColor(new Color(255, 240, 245));
    g.fillRoundRect(100, 200, 400, 150, 30, 30);

    // "Game Over"
    g.setColor(new Color(255, 105, 180));
    g.setFont(new Font("Kristen ITC", Font.PLAIN, 18));
    g.drawString("Aïe... le serpent s'est cogné !", 150, 250);

    // score
    g.setColor(new Color(90, 90, 90));
    g.setFont(new Font("Kristen ITC", Font.PLAIN, 16));
    g.drawString("Score : " + strawberrysEaten, 150, 285);

    // message de relance
    g.setFont(new Font("Kristen ITC", Font.PLAIN, 14));
    g.drawString("Tu peux recommencer en appuyant sur Espace !", 150, SCREEN_HEIGHT / 2 + 70);

}

}

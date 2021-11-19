package Project;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Game {

    public static final int VIRUS_DELAY = 100;

    private Boolean paused;

    private int pauseDelay;
    private int restartDelay;
    private int virusDelay;

    private Player player;
    private ArrayList<Virus> virus;
    private Keyboard keyboard;

    public int score;
    public Boolean gameover;
    public Boolean started;

    public Game() {
        keyboard = Keyboard.getInstance();
        restart();
    }

    public void restart() {
        paused = false;
        started = false;
        gameover = false;

        score = 0;
        pauseDelay = 0;
        restartDelay = 0;
        virusDelay = 0;

        player = new Player();
        virus = new ArrayList<Virus>();
    }

    public void update() {
        watchForStart();

        if (!started)
            return;

        watchForPause();
        watchForReset();

        if (paused)
            return;

        player.update();

        if (gameover)
            return;

        moveVirus();
        checkForCollisions();
    }

    public ArrayList<Render> getRenders() {
        ArrayList<Render> renders = new ArrayList<Render>();
        renders.add(new Render(0, 0, "src/Project/pic/blackgroud2.jpg"));
        for (Virus v : virus)
            renders.add(v.getRender());
        renders.add(player.getRender());
        return renders;
    }

    private void watchForStart() {
        if (!started && keyboard.isDown(KeyEvent.VK_SPACE)) {
            started = true;
        }
    }

    private void watchForPause() {
        if (pauseDelay > 0)
            pauseDelay--;

        if (keyboard.isDown(KeyEvent.VK_P) && pauseDelay <= 0) {
            paused = !paused;
            pauseDelay = 10;
        }
    }

    private void watchForReset() {
        if (restartDelay > 0)
            restartDelay--;

        if (keyboard.isDown(KeyEvent.VK_R) && restartDelay <= 0) {
            restart();
            restartDelay = 10;
            return;
        }
    }

    private void moveVirus() {
        virusDelay--;

        if (virusDelay < 0) {
            virusDelay = VIRUS_DELAY;
            Virus northVirus = null;
            Virus southVirus = null;


            for (Virus v : virus) {
                if (v.x - v.width < 0) {
                    if (northVirus == null) {
                        northVirus = v;
                    } else if (southVirus == null) {
                        southVirus = v;
                        break;
                    }
                }
            }

            if (northVirus == null) {
                Virus a = new Virus("north");
                virus.add(a);
                northVirus = a;
            } else {
                northVirus.reset();
            }

            if (southVirus == null) {
                Virus a = new Virus("south");
                virus.add(a);
                southVirus = a;
            } else {
                southVirus.reset();
            }

            northVirus.y = southVirus.y + southVirus.height + 175;
        }

        for (Virus pipe : virus) {
            pipe.update();
        }
    }

    private void checkForCollisions() {

        for (Virus pipe : virus) {
            if (pipe.collides(player.x, player.y, player.width, player.height)) {
                gameover = true;
                player.dead = true;
            } else if (pipe.x == player.x && pipe.orientation.equalsIgnoreCase("south")) {
                score++;
            }
        }

        if (player.y + player.height > App.HEIGHT - 80) {
            gameover = true;
            player.y = App.HEIGHT - 80 - player.height;
        }
    }
}

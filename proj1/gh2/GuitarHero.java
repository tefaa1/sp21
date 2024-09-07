package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static void main(String[] args) {
        /** The “white keys” are on the qwerty and zxcv rows*/
        /** the “black keys” on the 12345 and asdf rows of the keyboard.*/
        GuitarString[] play = new GuitarString[37];
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        for (int i = 0; i < 37; i++) {
            play[i] = new GuitarString(440 * Math.pow(2, (i - 24) / 12));
        }
        while (true) {
            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                play[keyboard.indexOf(key)].pluck();
            }
            double sample = 0;
            for (int i = 0; i < 37; i++) {
                sample += play[i].sample();
            }
            StdAudio.play(sample);
            for (int i = 0; i < 37; i++) {
                play[i].tic();
            }
        }
    }
}

package com.github.thatcherdev.climessage;

import java.io.IOException;
import java.io.Reader;
import org.fusesource.jansi.Ansi;

public class RawConsole {

    /**
     * Enables "raw" mode on the current unix console.
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    public static void enable() throws InterruptedException, IOException {
        Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "stty raw </dev/tty" }).waitFor();
    }

    /**
     * Disables "raw" mode on the current unix console.
     * 
     * @throws InterruptedException
     * @throws IOException
     */
    public static void disable() throws InterruptedException, IOException {
        Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", "stty cooked </dev/tty" }).waitFor();
    }

    /**
     * Moves the cursor to the beginning of the current line, prints
     * {@link toPrint}, and moves the cursor to the beginning of the next line.
     * 
     * @param toPrint string to print
     */
    public static void println(String toPrint) {
        System.out.print(Ansi.ansi().cursorToColumn(0) + toPrint + "\n" + Ansi.ansi().cursorToColumn(0));
    }

    /**
     * Gets keyboard input when console is in "raw" mode.
     * 
     * @param reader {@link java.io.Reader} used to get typed characters
     * @return String keyboard input
     * @throws IOException
     * @throws InterruptedException
     */
    public static String getInput(Reader reader) throws IOException, InterruptedException {
        String ret = "";
        while (true) {
            int ascii = reader.read();
            if (ascii == 13 && !ret.isEmpty())
                break;
            else if (ascii == 127 && !ret.isEmpty()) {
                System.out.print("\b ");
                System.out.print(Ansi.ansi().cursorLeft(1));
                ret = ret.substring(0, ret.length() - 1);
            } else if (ascii == 3) {
                disable();
                Thread.sleep(100);
                System.out.print(Ansi.ansi().eraseScreen().cursor(0, 0));
                Thread.sleep(100);
                System.exit(0);
            } else if (ascii >= 32 && ascii <= 126) {
                char typed = (char) ascii;
                System.out.print(typed);
                ret += typed;
            }
        }
        return ret;
    }
}
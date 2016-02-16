/**
 * This file is part of Ogar.
 *
 * Ogar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Ogar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Ogar.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ogarproject.ogar.server.gui;

import com.ogarproject.ogar.server.OgarServer;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ServerGUI {

    private static boolean spawned = false;

    private ServerGUI() {
        //
    }

    public static void spawn(OgarServer server) {
        JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        frame.setTitle("Ogar Server 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea console = new JTextArea(40, 120);
        console.setFont(new Font("monospaced", Font.PLAIN, 12));
        console.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(console);

        JTextField textField = new JTextField();
        textField.addActionListener((event) -> {
            server.handleCommand(textField.getText());
            textField.setText("");
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        frame.getContentPane().add(scrollPane, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.weighty = 0.0;
        frame.getContentPane().add(textField, c);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        redirectOutputStreams(console);
        spawned = true;
    }

    public static boolean isHeadless() {
        return !Boolean.getBoolean("forcegui") && (GraphicsEnvironment.isHeadless() || System.console() != null || Boolean.getBoolean("nogui"));
    }

    public static boolean isSpawned() {
        return spawned;
    }

    private static void redirectOutputStreams(JTextArea textArea) {
        PrintStream stream = new PrintStream(new TextAreaOutputStream(textArea));
        System.setOut(stream);
        System.setErr(stream);
    }

    private static class TextAreaOutputStream extends OutputStream {

        private final JTextArea textArea;
        private final StringBuffer buffer = new StringBuffer();

        public TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            buffer.append((char) b);
        }

        @Override
        public void flush() throws IOException {
            String bufferedString = buffer.toString();
            buffer.setLength(0);
            SwingUtilities.invokeLater(() -> {
                textArea.append(bufferedString);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }
}

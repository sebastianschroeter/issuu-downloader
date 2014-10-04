/*
 * Copyright (C) 2013 by www.scseba.de, Germany. All Rights Reserved.
 */
package de.scseba.issuudownloader;

/**
 * Date: 20.10.13
 */
public class CliProgress {

    private static final int LINE_LENGTH = 40;
    private final Float stepWidth;
    private final int end;

    public CliProgress(final int end) {
        this.end = end;
        this.stepWidth = LINE_LENGTH / (float) end;
    }

    public void updateProgress(final int current) {
        int endStep = Math.round((current + 1) * stepWidth);
        System.out.print("[");
        for (int i = 0; i <= LINE_LENGTH; i++) {
            if (i <= endStep) {
                System.out.print("=");
            } else {
                System.out.print(" ");
            }
        }

        int percent = Math.round((float) (current + 1) / end * 100);
        System.out.printf("] [% 3d%%]\r", percent);
    }

}

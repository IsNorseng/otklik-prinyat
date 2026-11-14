package ru.jobquest.meme;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

final class JobArt {
    private static final int INK = 0xff403730;
    private static final int SHADOW = 0xff6f5f4b;
    private static final int CREAM = 0xffe9d9b4;
    private static final int IVORY = 0xfff5e8c9;
    private static final int CASE = 0xffb5a080;
    private static final int GREEN = 0xffa8b58a;
    private static final int GREEN_DARK = 0xff485645;
    private static final int GREEN_MID = 0xff7f916a;
    private static final int BRICK = 0xff813d34;
    private static final int BRICK_LIGHT = 0xffac6050;
    private static final int GOLD = 0xffd7ae62;
    private static final int COIN = 0xffa87446;
    private static final int PLUM = 0xff6a5065;
    private static final int TEAL = 0xff4e7170;

    // Canvas draws on the Android UI thread.
    private static final Paint PIXEL = new Paint();

    private static final int[] LOGO_BG = {BRICK, GREEN_DARK, PLUM, TEAL, 0xff876c48};
    private static final String[][] LOGOS = {
        {"..11111111..", "..11111111..", "............", "....22......", ".....22.....",
            "..11112211..", "..11111121..", ".....22.....", "....22......", "............", "..22222222.."},
        {"......2.....", ".....222....", "......2.....", "...1......2.", "..11111..222", ".1111111..2.",
            ".1122211....", ".1122211....", ".1122211....", ".1111111....", "..11111....."},
        {"....2222....", "...222222...", "..22111122..", "..21....12..", "..21.22.12..", "..21.22.12..",
            "..21....12..", "..22111122..", "...222222...", "....2222....", "....1..1...."},
        {"..22........", "..22...11...", "..22...11...", "..22...11.22", "..22.2211.22", "..22.2211.22",
            "..22.2211.22", "..22.2211.22", "..22.2211.22", "..22.2211.22", "111111111111"},
        {"....11......", "...111......", "...1211.....", "...12211....", "...122211...", "...1222211..",
            "...12222211.", "...12211111.", "...121122...", "...111122...", "......122..."}
    };

    private JobArt() {}

    static void employer(Canvas canvas, RectF bounds, int variant) {
        int logo = index(variant, LOGOS.length);
        enter(canvas, bounds, 24, 24);
        bevel(canvas, 1, 2, 23, 24, SHADOW);
        bevel(canvas, 0, 0, 23, 23, INK);
        bevel(canvas, 1, 1, 22, 22, LOGO_BG[logo]);
        rect(canvas, 3, 2, 17, 1, 0x40f5e8c9);
        rect(canvas, 2, 20, 19, 1, 0x38251d1e);
        String[] design = LOGOS[logo];
        for (int y = 0; y < design.length; y++) {
            String row = design[y];
            for (int x = 0; x < row.length(); x++) {
                char pixel = row.charAt(x);
                if (pixel != '.') rect(canvas, 6 + x, 6 + y, 1, 1, pixel == '1' ? IVORY : GOLD);
            }
        }
        canvas.restore();
    }

    static void avatar(Canvas canvas, RectF bounds, int variant) {
        int kind = index(variant, 3);
        int frame = kind == 0 ? BRICK : kind == 1 ? GREEN_DARK : PLUM;
        int body = kind == 0 ? BRICK_LIGHT : kind == 1 ? TEAL : CASE;
        int face = kind == 0 ? CREAM : kind == 1 ? GREEN : GOLD;
        enter(canvas, bounds, 24, 24);
        bevel(canvas, 1, 2, 23, 24, SHADOW);
        bevel(canvas, 0, 0, 23, 23, INK);
        bevel(canvas, 1, 1, 22, 22, frame);
        rect(canvas, 3, 2, 17, 1, 0x40f5e8c9);

        bevel(canvas, 5, 17, 18, 22, INK);
        rect(canvas, 7, 18, 9, 3, body);
        rect(canvas, 10, 18, 3, 2, IVORY);
        rect(canvas, 11, 20, 1, 1, GOLD);
        int antenna = kind == 1 ? 15 : 11;
        rect(canvas, antenna, 5, 1, 4, INK);
        rect(canvas, antenna - 1, 4, 3, 2, GOLD);
        bevel(canvas, 5, 7, 18, 18, INK);
        bevel(canvas, 6, 8, 17, 17, body);
        rect(canvas, 7, 10, 9, 5, face);
        rect(canvas, 8, 10, 7, 1, 0x45fff0d7);
        rect(canvas, 4, 11, 2, 4, GOLD);
        rect(canvas, 17, 11, 2, 4, GOLD);
        if (kind == 2) {
            rect(canvas, 8, 11, 7, 2, INK);
            rect(canvas, 10, 12, 1, 1, face);
            rect(canvas, 12, 12, 1, 1, face);
        } else {
            rect(canvas, 9, 11, 1, 2, INK);
            rect(canvas, 13, 11, 1, 2, INK);
        }
        rect(canvas, 10, 14, 3, 1, INK);
        if (kind == 0) {
            rect(canvas, 16, 14, 2, 2, GOLD);
            rect(canvas, 13, 16, 4, 1, GOLD);
        } else if (kind == 1) {
            rect(canvas, 7, 6, 4, 1, IVORY);
        } else {
            rect(canvas, 8, 15, 7, 1, IVORY);
        }
        canvas.restore();
    }

    static void terminal(Canvas canvas, RectF bounds) {
        enter(canvas, bounds, 48, 42);
        rect(canvas, 3, 36, 42, 3, 0x44352c28);
        terminalCore(canvas, 4, 4, false);
        sparkle(canvas, 42, 6, BRICK_LIGHT);
        sparkle(canvas, 3, 20, GREEN_MID);
        rect(canvas, 43, 21, 1, 1, BRICK_LIGHT);
        canvas.restore();
    }

    static void dataSold(Canvas canvas, RectF bounds) {
        enter(canvas, bounds, 64, 56);
        rect(canvas, 3, 48, 57, 3, 0x44352c28);
        terminalCore(canvas, 3, 13, true);

        // A receipt and a sale signal lead from the terminal to the coins.
        rect(canvas, 32, 30, 10, 2, INK);
        bevel(canvas, 33, 31, 45, 47, INK);
        bevel(canvas, 34, 31, 44, 46, IVORY);
        rect(canvas, 36, 34, 6, 1, CASE);
        rect(canvas, 36, 37, 5, 1, CASE);
        rect(canvas, 36, 41, 1, 3, BRICK);
        rect(canvas, 38, 41, 1, 3, BRICK);
        rect(canvas, 40, 41, 2, 3, BRICK);
        rect(canvas, 33, 45, 2, 2, INK);
        rect(canvas, 37, 45, 2, 2, INK);
        rect(canvas, 41, 45, 2, 2, INK);
        rect(canvas, 38, 19, 2, 2, BRICK_LIGHT);
        rect(canvas, 42, 19, 2, 2, BRICK_LIGHT);
        rect(canvas, 46, 19, 3, 2, BRICK);
        rect(canvas, 47, 18, 1, 4, BRICK);

        coin(canvas, 49, 42);
        coin(canvas, 49, 38);
        coin(canvas, 49, 34);
        coin(canvas, 53, 30);
        coin(canvas, 51, 26);
        sparkle(canvas, 55, 17, GOLD);
        sparkle(canvas, 13, 7, BRICK_LIGHT);
        rect(canvas, 21, 6, 2, 2, BRICK_LIGHT);
        rect(canvas, 58, 23, 2, 2, GOLD);
        canvas.restore();
    }

    private static void terminalCore(Canvas canvas, int x, int y, boolean sale) {
        bevel(canvas, x + 1, y + 1, x + 37, y + 27, SHADOW);
        bevel(canvas, x, y, x + 36, y + 26, INK);
        bevel(canvas, x + 1, y + 1, x + 35, y + 25, CREAM);
        rect(canvas, x + 3, y + 2, 29, 1, IVORY);
        rect(canvas, x + 3, y + 24, 30, 1, CASE);
        bevel(canvas, x + 4, y + 4, x + 31, y + 22, INK);
        bevel(canvas, x + 5, y + 5, x + 30, y + 21, GREEN_DARK);
        rect(canvas, x + 7, y + 7, 21, 1, GREEN_MID);
        rect(canvas, x + 32, y + 17, 2, 2, sale ? BRICK_LIGHT : GOLD);
        rect(canvas, x + 32, y + 21, 2, 1, SHADOW);

        int screenX = x + 7;
        int screenY = y + 10;
        if (sale) {
            rect(canvas, screenX + 2, screenY + 3, 17, 4, GREEN);
            rect(canvas, screenX + 3, screenY + 2, 6, 2, GREEN);
            rect(canvas, screenX + 10, screenY + 4, 8, 1, GREEN_DARK);
            rect(canvas, screenX + 12, screenY + 5, 6, 1, GREEN_DARK);
        } else {
            rect(canvas, screenX, screenY, 3, 3, GREEN);
            rect(canvas, screenX + 5, screenY, 13, 1, GREEN);
            rect(canvas, screenX + 5, screenY + 2, 10, 1, GREEN_MID);
            rect(canvas, screenX, screenY + 5, 3, 3, GREEN);
            rect(canvas, screenX + 5, screenY + 5, 10, 1, GREEN);
            rect(canvas, screenX + 5, screenY + 7, 6, 1, GREEN_MID);
            rect(canvas, screenX + 18, screenY + 6, 3, 2, GOLD);
        }

        rect(canvas, x + 13, y + 26, 11, 3, INK);
        rect(canvas, x + 15, y + 26, 6, 2, SHADOW);
        bevel(canvas, x + 5, y + 28, x + 33, y + 34, INK);
        rect(canvas, x + 7, y + 29, 24, 3, CASE);
        for (int i = 0; i < 5; i++) rect(canvas, x + 9 + i * 4, y + 30, 3, 1, IVORY);
    }

    private static void coin(Canvas canvas, int x, int y) {
        bevel(canvas, x, y, x + 10, y + 5, INK);
        rect(canvas, x + 1, y + 1, 8, 3, COIN);
        rect(canvas, x + 1, y + 1, 8, 1, GOLD);
        rect(canvas, x + 3, y + 2, 1, 1, GOLD);
        rect(canvas, x + 6, y + 2, 1, 1, GOLD);
    }

    private static void sparkle(Canvas canvas, int x, int y, int color) {
        rect(canvas, x, y - 2, 1, 5, color);
        rect(canvas, x - 2, y, 5, 1, color);
        rect(canvas, x - 1, y - 1, 3, 3, color);
    }

    private static void enter(Canvas canvas, RectF bounds, int width, int height) {
        float scale = Math.min(bounds.width() / width, bounds.height() / height);
        canvas.save();
        canvas.translate(bounds.centerX() - width * scale / 2, bounds.centerY() - height * scale / 2);
        canvas.scale(scale, scale);
    }

    private static int index(int variant, int count) {
        int remainder = variant % count;
        return remainder < 0 ? remainder + count : remainder;
    }

    private static void bevel(Canvas canvas, int left, int top, int right, int bottom, int color) {
        rect(canvas, left + 1, top, right - left - 2, 1, color);
        rect(canvas, left, top + 1, right - left, bottom - top - 2, color);
        rect(canvas, left + 1, bottom - 1, right - left - 2, 1, color);
    }

    private static void rect(Canvas canvas, int x, int y, int width, int height, int color) {
        PIXEL.setColor(color);
        canvas.drawRect(x, y, x + width, y + height, PIXEL);
    }
}

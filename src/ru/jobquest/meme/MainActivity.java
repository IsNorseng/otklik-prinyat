package ru.jobquest.meme;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final int DARK = 0xff302a27;
    private static final int EDGE = 0xff403730;
    private static final int INK = 0xff352b25;
    private static final int MUTED = 0xff685649;
    private static final int CASE = 0xffdfc99e;
    private static final int CREAM = 0xfff0e1be;
    private static final int PAPER = 0xfff5e8ca;
    private static final int RED = 0xff813d34;
    private static final int RED_DARK = 0xff562c29;
    private static final int GREEN = 0xff485543;
    private static final int LCD = 0xffa8b58a;
    private static final int LCD_LIGHT = 0xffbfca9f;
    private static final Typeface BOLD = Typeface.create("sans-serif", Typeface.BOLD);
    private static final Typeface TITLE = Typeface.create("sans-serif-condensed", Typeface.BOLD);
    private static final Typeface MONO = Typeface.create("monospace", Typeface.BOLD);
    private static final String[] DEPARTMENTS = {"Отдел первичного скрининга",
            "Отдел повторного скрининга", "Отдел финальных анкет"};

    private final Variety variety = new Variety(Copy.JOBS.length, Copy.RECRUITERS.length);
    private int[] vacancies = variety.vacancies();
    private int[] recruiters = variety.recruiters();
    private final Run run = new Run(vacancies.length, 2, 2, 3);
    private final ArrayList<String> answers = new ArrayList<>();
    private LinearLayout shell;
    private ScrollView scroller;
    private View card;
    private TextView swipeLeft, swipeRight;
    private boolean touchingCard, dragging, animating;
    private float downX, downY;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARK);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        render();
    }

    private void render() {
        card = null;
        swipeLeft = null;
        swipeRight = null;
        FrameLayout base = new FrameLayout(this);
        base.setBackground(new Dots());
        shell = column();
        shell.setFocusableInTouchMode(true);
        shell.setBackground(panel(CASE, EDGE, 10, 3));
        shell.setPadding(dp(13), dp(10), dp(13), dp(11));
        int available = getResources().getConfiguration().screenWidthDp;
        FrameLayout.LayoutParams frame = new FrameLayout.LayoutParams(
                available > 490 ? dp(460) : ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        frame.setMargins(dp(9), dp(8), dp(9), dp(8));
        base.addView(shell, frame);
        setContentView(base);
        shell.requestFocus();
        header();
        switch (run.screen()) {
            case RESUME: resume(); break;
            case DECK: deck(); break;
            case RECRUITER:
            case LINK: recruiter(); break;
            case END: ending(); break;
        }
    }

    private void header() {
        LinearLayout row = row();
        row.setGravity(Gravity.CENTER_VERTICAL);
        Art sign = new Art(0, 0);
        sign.setBackground(panel(LCD, GREEN, 4, 2));
        sign.setPadding(dp(4), dp(4), dp(4), dp(4));
        row.addView(sign, new LinearLayout.LayoutParams(dp(37), dp(35)));
        LinearLayout label = column();
        label.addView(text("ОТКЛИК ПРИНЯТ", 22, TITLE, INK));
        label.addView(text("ТЕРМИНАЛ ПОИСКА РАБОТЫ", 9, MONO, RED));
        LinearLayout.LayoutParams caption = new LinearLayout.LayoutParams(0, -2, 1);
        caption.leftMargin = dp(8);
        row.addView(label, caption);
        shell.addView(row);
        rule(shell, MUTED, 7, 8);
    }

    private LinearLayout scrolling() {
        scroller = new ScrollView(this);
        scroller.setFillViewport(true);
        scroller.setVerticalScrollBarEnabled(false);
        scroller.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout stack = column();
        scroller.addView(stack, new ScrollView.LayoutParams(-1, -2));
        shell.addView(scroller, new LinearLayout.LayoutParams(-1, 0, 1));
        return stack;
    }

    private void resume() {
        LinearLayout stack = scrolling();
        LinearLayout intro = inset(LCD, GREEN, 10);
        LinearLayout hero = row();
        hero.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout words = column();
        words.addView(text("ЛИЧНОЕ ДЕЛО", 10, MONO, GREEN));
        add(words, text("Составьте\nрезюме", 28, TITLE, INK), 4);
        add(words, text("Терминал требует заполнить все поля. Серьёзно.", 13, null, GREEN), 5);
        hero.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        hero.addView(new Art(0, 0), new LinearLayout.LayoutParams(dp(94), dp(87)));
        intro.addView(hero);
        stack.addView(intro);

        fieldLabel(stack, "НАЗВАНИЕ РЕЗЮМЕ", 14);
        EditText name = entry("Например, «Специалист на вырост»", 1, 80);
        stack.addView(name, new LinearLayout.LayoutParams(-1, dp(47)));
        fieldLabel(stack, "ЖЕЛАЕМАЯ ДОЛЖНОСТЬ", 11);
        Spinner role = new Spinner(this, Spinner.MODE_DROPDOWN);
        String[] roleOptions = new String[Copy.ROLES.length + 1];
        roleOptions[0] = "Выберите должность";
        System.arraycopy(Copy.ROLES, 0, roleOptions, 1, Copy.ROLES.length);
        ArrayAdapter<String> roles = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roleOptions);
        roles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(roles);
        role.setBackground(panel(PAPER, MUTED, 4, 2));
        role.setPadding(dp(12), 0, dp(25), 0);
        role.setContentDescription("Желаемая должность");
        FrameLayout dropdown = new FrameLayout(this);
        dropdown.addView(role, new FrameLayout.LayoutParams(-1, -1));
        TextView arrow = text("▾", 15, MONO, RED);
        FrameLayout.LayoutParams marker = new FrameLayout.LayoutParams(dp(21), -1, Gravity.END);
        marker.rightMargin = dp(6);
        arrow.setGravity(Gravity.CENTER);
        dropdown.addView(arrow, marker);
        stack.addView(dropdown, new LinearLayout.LayoutParams(-1, dp(47)));

        fieldLabel(stack, "НАВЫКИ", 11);
        EditText skills = entry("Владею Ctrl+Z на уровне носителя", 3, 340);
        stack.addView(skills, new LinearLayout.LayoutParams(-1, dp(90)));
        TextView error = error(stack);
        TextView start = button("К ВАКАНСИЯМ  ›", RED, CREAM, RED_DARK);
        start.setOnClickListener(v -> {
            int selected = role.getSelectedItemPosition();
            if (!run.start(name.getText().toString(), selected > 0 ? roleOptions[selected] : "",
                    skills.getText().toString())) {
                showError(error, "Отдел кадров требует название, должность и навыки.");
                return;
            }
            hideKeyboard();
            render();
        });
        add(stack, start, 10);
        TextView note = text("Пародия на поиск работы. Вакансии вымышлены.", 11, null, MUTED);
        note.setGravity(Gravity.CENTER);
        add(stack, note, 10);
    }

    private void deck() {
        LinearLayout strip = row();
        strip.addView(text("КАРТОТЕКА ВАКАНСИЙ", 11, MONO, RED), new LinearLayout.LayoutParams(0, -2, 1));
        strip.addView(text(String.format(Locale.ROOT, "%02d / %02d", run.card() + 1, vacancies.length),
                11, MONO, MUTED));
        shell.addView(strip);
        add(shell, meter(run.card(), vacancies.length), 5);
        LinearLayout stack = scrolling();
        stack.setPadding(0, dp(9), 0, dp(8));
        int vacancyId = vacancies[run.card()];
        String[] job = Copy.JOBS[vacancyId];
        LinearLayout vacancy = inset(PAPER, EDGE, 14);
        card = vacancy;
        stack.addView(vacancy, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout company = row();
        company.setGravity(Gravity.CENTER_VERTICAL);
        Art logo = new Art(1, vacancyId);
        logo.setBackground(panel(LCD, GREEN, 4, 2));
        logo.setPadding(dp(4), dp(4), dp(4), dp(4));
        company.addView(logo, new LinearLayout.LayoutParams(dp(43), dp(43)));
        LinearLayout identity = column();
        identity.addView(text("РАБОТОДАТЕЛЬ", 9, MONO, MUTED));
        add(identity, text(job[1], 16, BOLD, INK), 3);
        LinearLayout.LayoutParams name = new LinearLayout.LayoutParams(0, -2, 1);
        name.leftMargin = dp(9);
        company.addView(identity, name);
        vacancy.addView(company);
        rule(vacancy, 0xffc9b58e, 10, 10);
        vacancy.addView(text(job[0], 28, TITLE, INK));
        add(vacancy, text(job[2], 15, null, INK), 8);
        fieldLabel(vacancy, "ТРЕБОВАНИЯ", 13);
        vacancy.addView(text(job[3], 15, null, INK));
        vacancy.addView(new View(this), new LinearLayout.LayoutParams(1, dp(12), 1));
        LinearLayout salary = inset(LCD_LIGHT, GREEN, 9);
        salary.addView(text("ЗАРПЛАТА", 9, MONO, GREEN));
        add(salary, text(job[4], 17, TITLE, INK), 3);
        vacancy.addView(salary);

        LinearLayout controls = row();
        swipeLeft = button("‹  ОТКЛИК", RED, CREAM, RED_DARK);
        swipeRight = button("ПРОПУСК  ›", CREAM, INK, MUTED);
        swipeLeft.setOnClickListener(v -> advance(true));
        swipeRight.setOnClickListener(v -> advance(false));
        controls.addView(swipeLeft, new LinearLayout.LayoutParams(0, dp(50), 1));
        LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(0, dp(50), 1);
        second.leftMargin = dp(7);
        controls.addView(swipeRight, second);
        shell.addView(controls);
        TextView counter = text("Отклики: " + run.applications() + "     Пропуски: " + run.passes(),
                10, MONO, MUTED);
        counter.setGravity(Gravity.CENTER);
        add(shell, counter, 7);
    }

    private void advance(boolean apply) {
        if (animating || card == null || run.screen() != Run.Screen.DECK) return;
        animating = true;
        View outgoing = card;
        outgoing.animate().translationX((apply ? -1 : 1) * shell.getWidth() * 0.95f)
                .rotation(apply ? -9 : 9).alpha(0).setDuration(145).withEndAction(() -> {
                    animating = false;
                    if (run.swipe(apply) && run.screen() == Run.Screen.RECRUITER) answers.clear();
                    render();
                }).start();
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        if (card == null || animating) return super.dispatchTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Rect bounds = new Rect();
                touchingCard = card.getGlobalVisibleRect(bounds)
                        && bounds.contains((int) event.getRawX(), (int) event.getRawY());
                downX = event.getRawX();
                downY = event.getRawY();
                dragging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchingCard) {
                    float dx = event.getRawX() - downX;
                    float dy = event.getRawY() - downY;
                    if (!dragging && Math.abs(dx) > dp(13) && Math.abs(dx) > Math.abs(dy) * 1.35f) {
                        MotionEvent cancel = MotionEvent.obtain(event);
                        cancel.setAction(MotionEvent.ACTION_CANCEL);
                        super.dispatchTouchEvent(cancel);
                        cancel.recycle();
                        dragging = true;
                    } else if (!dragging && Math.abs(dy) > dp(15)) {
                        touchingCard = false;
                    }
                    if (dragging) {
                        card.setTranslationX(dx * 0.75f);
                        card.setRotation(dx / dp(24));
                        swipeLeft.setAlpha(dx < 0 ? 1f : 0.65f);
                        swipeRight.setAlpha(dx > 0 ? 1f : 0.65f);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchingCard = false;
                if (dragging) {
                    dragging = false;
                    float dx = event.getRawX() - downX;
                    if (event.getActionMasked() == MotionEvent.ACTION_UP && Math.abs(dx) >= dp(55)) {
                        advance(dx < 0);
                    } else {
                        card.animate().translationX(0).rotation(0).setDuration(120).start();
                        swipeLeft.setAlpha(1);
                        swipeRight.setAlpha(1);
                    }
                    return true;
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void recruiter() {
        int index = run.recruiter();
        int recruiterId = recruiters[index];
        String[] person = Copy.RECRUITERS[recruiterId];
        LinearLayout profile = row();
        profile.setGravity(Gravity.CENTER_VERTICAL);
        Art avatar = new Art(2, recruiterId);
        avatar.setBackground(panel(LCD, GREEN, 4, 2));
        avatar.setPadding(dp(3), dp(3), dp(3), dp(3));
        profile.addView(avatar, new LinearLayout.LayoutParams(dp(44), dp(44)));
        LinearLayout identity = column();
        identity.addView(text(person[0], 22, TITLE, INK));
        identity.addView(text(DEPARTMENTS[index], 11, null, MUTED));
        LinearLayout.LayoutParams who = new LinearLayout.LayoutParams(0, -2, 1);
        who.leftMargin = dp(8);
        profile.addView(identity, who);
        profile.addView(text("В СЕТИ", 9, MONO, GREEN));
        shell.addView(profile);
        rule(shell, MUTED, 8, 5);
        LinearLayout messages = scrolling();
        ScrollView current = scroller;
        messages.setPadding(0, dp(2), 0, dp(7));
        bubble(messages, person[0], person[1], false);
        for (int i = 0; i < answers.size(); i++) {
            bubble(messages, person[0], person[i + 2], false);
            bubble(messages, "ВЫ", answers.get(i), true);
        }
        boolean link = run.screen() == Run.Screen.LINK;
        if (link || run.awaitingReply()) {
            bubble(messages, person[0], person[link ? 6 : 5], false);
            TextView next = button(link ? "АНКЕТА КАНДИДАТА  ↗" : "К ВАКАНСИЯМ  ›", RED, CREAM, RED_DARK);
            next.setOnClickListener(v -> {
                if (link ? run.openLink() : run.closeRecruiter()) render();
            });
            add(shell, next, 5);
        } else {
            bubble(messages, person[0], person[run.question() + 2], false);
            fieldLabel(shell, "ОТВЕТ ОБЯЗАТЕЛЕН", 4);
            TextView error = error(shell);
            LinearLayout composer = row();
            composer.setGravity(Gravity.BOTTOM);
            EditText input = entry("Напишите что-нибудь…", 2, 260);
            composer.addView(input, new LinearLayout.LayoutParams(0, dp(54), 1));
            TextView send = button("ОТПР.", RED, CREAM, RED_DARK);
            send.setContentDescription("Отправить ответ");
            LinearLayout.LayoutParams sendSize = new LinearLayout.LayoutParams(dp(75), dp(54));
            sendSize.leftMargin = dp(6);
            composer.addView(send, sendSize);
            shell.addView(composer);
            Runnable submit = () -> {
                String answer = input.getText().toString();
                if (!run.answer(answer)) {
                    showError(error, "Терминал не принимает молчание.");
                    return;
                }
                answers.add(answer.trim());
                hideKeyboard();
                render();
            };
            send.setOnClickListener(v -> submit.run());
            input.setOnFocusChangeListener((v, focused) -> {
                if (focused) current.post(() -> current.smoothScrollTo(0, messages.getHeight()));
            });
            current.addOnLayoutChangeListener((v, l, t, r, b, oldL, oldT, oldR, oldB) -> {
                if (b - t < oldB - oldT && input.hasFocus()) {
                    current.post(() -> current.scrollTo(0, messages.getHeight()));
                }
            });
        }
        current.post(() -> current.smoothScrollTo(0, messages.getHeight()));
    }

    private void bubble(LinearLayout parent, String speaker, String message, boolean own) {
        LinearLayout bubble = inset(own ? LCD_LIGHT : PAPER, own ? GREEN : MUTED, 10);
        bubble.addView(text(speaker, 10, MONO, own ? GREEN : RED));
        add(bubble, text(message, 15, null, INK), 4);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2);
        size.setMargins(dp(own ? 37 : 0), dp(7), dp(own ? 0 : 22), 0);
        parent.addView(bubble, size);
    }

    private void ending() {
        LinearLayout stack = scrolling();
        stack.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout banner = inset(LCD, GREEN, 15);
        banner.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView path = text("АНКЕТА КАНДИДАТА", 11, MONO, GREEN);
        path.setGravity(Gravity.CENTER);
        banner.addView(path);
        Art graphic = new Art(3, 0);
        LinearLayout.LayoutParams artSize = new LinearLayout.LayoutParams(dp(173), dp(133));
        artSize.topMargin = dp(9);
        banner.addView(graphic, artSize);
        TextView message = text("Ваши данные уже проданы.\nВы нам не нужны", 31, TITLE, INK);
        message.setGravity(Gravity.CENTER);
        add(banner, message, 8);
        stack.addView(banner);
        TextView replay = button("ЗАПОЛНИТЬ НОВОЕ РЕЗЮМЕ", RED, CREAM, RED_DARK);
        replay.setOnClickListener(v -> restart());
        add(stack, replay, 13);
    }

    private void restart() {
        run.reset();
        answers.clear();
        variety.next();
        vacancies = variety.vacancies();
        recruiters = variety.recruiters();
        render();
    }

    @Override public void onBackPressed() {
        if (animating || run.screen() == Run.Screen.RECRUITER || run.screen() == Run.Screen.LINK) return;
        if (run.screen() != Run.Screen.RESUME && run.screen() != Run.Screen.END) {
            restart();
            return;
        }
        super.onBackPressed();
    }

    private void hideKeyboard() {
        View focus = getCurrentFocus();
        if (focus != null) {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (keyboard != null) keyboard.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            focus.clearFocus();
        }
    }

    private EditText entry(String hint, int lines, int limit) {
        EditText input = new EditText(this);
        input.setTextSize(15);
        input.setTextColor(INK);
        input.setHintTextColor(MUTED);
        input.setBackground(panel(PAPER, MUTED, 4, 2));
        input.setPadding(dp(11), dp(7), dp(11), dp(7));
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                | (lines > 1 ? InputType.TYPE_TEXT_FLAG_MULTI_LINE : 0));
        input.setSingleLine(lines == 1);
        input.setMaxLines(lines);
        input.setGravity(lines == 1 ? Gravity.CENTER_VERTICAL : Gravity.TOP);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        input.setHint(hint);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(limit)});
        input.setSaveEnabled(false);
        return input;
    }

    private TextView error(LinearLayout parent) {
        TextView error = text("", 12, BOLD, RED_DARK);
        error.setVisibility(View.GONE);
        error.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        parent.addView(error);
        return error;
    }

    private void showError(TextView target, String message) {
        target.setText(message);
        target.setVisibility(View.VISIBLE);
    }

    private View meter(int position, int total) {
        return new View(this) {
            private final Paint paint = new Paint();
            @Override protected void onDraw(Canvas canvas) {
                float unit = getWidth() / (float) total;
                for (int i = 0; i < total; i++) {
                    paint.setColor(i <= position ? RED : 0xffbba47d);
                    canvas.drawRect(i * unit, 0, (i + 1) * unit - dp(2), getHeight(), paint);
                }
            }
            @Override protected void onMeasure(int widthSpec, int heightSpec) {
                setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), dp(3));
            }
        };
    }

    private void fieldLabel(LinearLayout parent, String label, int gap) {
        TextView title = text(label, 10, MONO, RED);
        title.setPadding(0, 0, 0, dp(4));
        add(parent, title, gap);
    }

    private TextView button(String label, int fill, int color, int stroke) {
        TextView button = text(label, 13, MONO, color);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(49));
        button.setPadding(dp(5), dp(6), dp(5), dp(6));
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, panel(stroke, stroke, 5, 2));
        states.addState(new int[0], panel(fill, stroke, 5, 2));
        button.setBackground(states);
        button.setTextColor(new ColorStateList(
                new int[][]{{android.R.attr.state_pressed}, {}}, new int[]{PAPER, color}));
        button.setFocusable(true);
        button.setClickable(true);
        button.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override public void onInitializeAccessibilityNodeInfo(View host,
                    android.view.accessibility.AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setClassName("android.widget.Button");
            }
        });
        return button;
    }

    private LinearLayout inset(int fill, int stroke, int padding) {
        LinearLayout panel = column();
        panel.setBackground(panel(fill, stroke, 6, 2));
        int pad = dp(padding);
        panel.setPadding(pad, pad, pad, pad);
        return panel;
    }

    private void rule(LinearLayout parent, int color, int above, int below) {
        View line = new View(this);
        line.setBackgroundColor(color);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(1));
        params.topMargin = dp(above);
        params.bottomMargin = dp(below);
        parent.addView(line, params);
    }

    private void add(LinearLayout parent, View child, int top) {
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2);
        size.topMargin = dp(top);
        parent.addView(child, size);
    }

    private TextView text(String value, float size, Typeface face, int color) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(size);
        text.setIncludeFontPadding(false);
        text.setTextColor(color);
        if (face != null) text.setTypeface(face);
        return text;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        return layout;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private Drawable panel(int fill, int edge, int corner, int border) {
        return new PixelPanel(fill, edge, dp(corner), dp(border));
    }

    private final class Art extends View {
        private final RectF bounds = new RectF();
        private final int type, variant;
        Art(int type, int variant) {
            super(MainActivity.this);
            this.type = type;
            this.variant = variant;
            setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
        @Override protected void onDraw(Canvas canvas) {
            bounds.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(),
                    getHeight() - getPaddingBottom());
            switch (type) {
                case 0: JobArt.terminal(canvas, bounds); break;
                case 1: JobArt.employer(canvas, bounds, variant); break;
                case 2: JobArt.avatar(canvas, bounds, variant); break;
                case 3: JobArt.dataSold(canvas, bounds); break;
                default: break;
            }
        }
    }

    private abstract static class FlatDrawable extends Drawable {
        final Paint paint = new Paint();
        @Override public void setAlpha(int alpha) { paint.setAlpha(alpha); }
        @Override public void setColorFilter(ColorFilter filter) { paint.setColorFilter(filter); }
        @Override public int getOpacity() { return PixelFormat.TRANSLUCENT; }
    }

    private final class Dots extends FlatDrawable {
        @Override public void draw(Canvas c) {
            Rect r = getBounds();
            paint.setColor(DARK);
            c.drawRect(r, paint);
            paint.setColor(0xff473b32);
            int step = Math.max(1, dp(10));
            for (int y = r.top + 3; y < r.bottom; y += step) {
                for (int x = r.left + 3; x < r.right; x += step) {
                    c.drawRect(x, y, x + dp(1), y + dp(1), paint);
                }
            }
        }
    }

    private static final class PixelPanel extends FlatDrawable {
        private final int fill, edge, corner, border;
        private final Path path = new Path();

        PixelPanel(int fill, int edge, int corner, int border) {
            this.fill = fill;
            this.edge = edge;
            this.corner = corner;
            this.border = border;
        }

        @Override public void draw(Canvas canvas) {
            Rect r = getBounds();
            shape(canvas, r.left, r.top, r.right, r.bottom, corner, edge);
            shape(canvas, r.left + border, r.top + border, r.right - border, r.bottom - border,
                    Math.max(1, corner - border), fill);
        }

        private void shape(Canvas canvas, int l, int t, int r, int b, int cut, int color) {
            path.reset();
            path.moveTo(l + cut, t);
            path.lineTo(r - cut, t);
            path.lineTo(r - cut, t + cut);
            path.lineTo(r, t + cut);
            path.lineTo(r, b - cut);
            path.lineTo(r - cut, b - cut);
            path.lineTo(r - cut, b);
            path.lineTo(l + cut, b);
            path.lineTo(l + cut, b - cut);
            path.lineTo(l, b - cut);
            path.lineTo(l, t + cut);
            path.lineTo(l + cut, t + cut);
            path.close();
            paint.setColor(color);
            canvas.drawPath(path, paint);
        }
    }
}

// ///////////////////////////////////////////////////////////////////////////
// Fast Fourier transform laboratory.
// Dave Hale, Landmark Graphics, 01/24/96
// ///////////////////////////////////////////////////////////////////////////

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.util.Observable;
import java.util.Observer;

public class FftLab extends java.applet.Applet {
    MainPanel mainPanel;
    ControlPanel controlPanel;

    @Override
    public void init() {
        FftLabController controller = new FftLabController();
        this.setLayout(new BorderLayout());
        this.mainPanel = new MainPanel(controller.fRealView, controller.fImagView, controller.gRealView, controller.gImagView);
        this.add("Center", this.mainPanel);
        this.controlPanel = new ControlPanel(controller);
        this.add("South", this.controlPanel);
    }

    @Override
    public void start() {
        this.mainPanel.enable();
        this.controlPanel.enable();
    }

    @Override
    public void stop() {
        this.mainPanel.disable();
        this.controlPanel.disable();
    }

    @Override
    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            System.exit(0);
        }
        return false;
    }

    public static void main(String args[]) {
        Frame frame = new Frame("FFT Laboratory");
        FftLab fftLab = new FftLab();
        fftLab.init();
        fftLab.start();
        frame.add("Center", fftLab);
        frame.resize(600, 400);
        frame.show();
    }
}

/////////////////////////////////////////////////////////////////////////////
// Main panel for FftLab contains samples views.

class MainPanel extends Panel {

    public MainPanel(SamplesView fRealView, SamplesView fImagView, SamplesView gRealView, SamplesView gImagView) {
        this.setLayout(new GridLayout(2, 1, 1, 1));
        this.add(new ComplexSamplesPanel(fRealView, fImagView, "f(x)"));
        this.add(new ComplexSamplesPanel(gRealView, gImagView, "F(k)"));
    }

    @Override
    public void paint(Graphics g) {
        Dimension d = this.size();
        g.setColor(Color.blue);
        g.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
    }

    @Override
    public Insets insets() {
        return new Insets(1, 1, 1, 1);
    }
}

class ComplexSamplesPanel extends Panel {

    public ComplexSamplesPanel(SamplesView realView, SamplesView imagView, String label) {
        this.setLayout(new BorderLayout());
        this.add("North", new Label(label, Label.CENTER));
        Panel panel = new Panel();
        panel.setLayout(new GridLayout(1, 2, 1, 1));
        panel.add(new SamplesPanel(realView, "Real"));
        panel.add(new SamplesPanel(imagView, "Imaginary"));
        this.add("Center", panel);
    }

    @Override
    public void paint(Graphics g) {
        Dimension d = this.size();
        g.setColor(Color.blue);
        g.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
    }

    @Override
    public Insets insets() {
        return new Insets(1, 1, 1, 1);
    }
}

class SamplesPanel extends Panel {

    public SamplesPanel(SamplesView view, String label) {
        this.setLayout(new BorderLayout());
        this.add("North", new Label(label, Label.CENTER));
        this.add("Center", view);
    }

    @Override
    public void paint(Graphics g) {
        Dimension d = this.size();
        g.setColor(Color.blue);
        g.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
    }

    @Override
    public Insets insets() {
        return new Insets(1, 1, 1, 1);
    }
}

/////////////////////////////////////////////////////////////////////////////
// Control panel for FftLab.

class ControlPanel extends Panel {

    public ControlPanel(FftLabController c) {
        this.c = c;
        this.add(new Checkbox("Origin Centered"));
        this.length = new LabeledChoice("Length:");
        this.length.choice.addItem("16");
        this.length.choice.addItem("32");
        this.length.choice.addItem("64");
        this.length.choice.select("32");
        this.add(this.length);
        this.mode = new LabeledChoice("Editing:");
        this.mode.choice.addItem("Draw");
        this.mode.choice.addItem("Negate");
        this.mode.choice.addItem("Zero");
        this.mode.choice.addItem("Shift");
        this.mode.choice.addItem("None");
        this.mode.choice.select("Draw");
        this.add(this.mode);
        this.add(new Button("Zero All"));
    }

    @Override
    public void paint(Graphics g) {
        Dimension d = this.size();
        g.setColor(Color.blue);
        g.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
    }

    @Override
    public Insets insets() {
        return new Insets(1, 1, 1, 1);
    }

    @Override
    public boolean handleEvent(Event e) {
        if (e.target instanceof Button) {
            this.c.zeroAll();
            return true;
        }
        else if (e.target instanceof Checkbox) {
            Checkbox cb = (Checkbox) e.target;
            this.c.setOriginCentered(cb.getState());
            return true;
        }
        else if (e.target instanceof Choice) {
            if (e.target == this.length.choice) {
                String item = this.length.choice.getSelectedItem();
                this.c.setLength(Integer.parseInt(item));
                return true;
            }
            else if (e.target == this.mode.choice) {
                String item = this.mode.choice.getSelectedItem();
                if (item == "None") {
                    this.c.setEditMode(SamplesView.EDIT_NONE);
                }
                else if (item == "Draw") {
                    this.c.setEditMode(SamplesView.EDIT_DRAW);
                }
                else if (item == "Negate") {
                    this.c.setEditMode(SamplesView.EDIT_NEGATE);
                }
                else if (item == "Zero") {
                    this.c.setEditMode(SamplesView.EDIT_ZERO);
                }
                else if (item == "Shift") {
                    this.c.setEditMode(SamplesView.EDIT_SHIFT);
                }
                return true;
            }
        }
        return false;
    }

    private FftLabController c;
    private LabeledChoice length;
    private LabeledChoice mode;
}

class LabeledChoice extends Panel {

    public Choice choice;

    public LabeledChoice(String label) {
        this.add(new Label(label, Label.RIGHT));
        this.choice = new Choice();
        this.add(this.choice);
    }
}

/////////////////////////////////////////////////////////////////////////////
// Controller creates and keeps Samples and SamplesViews consistent.

class FftLabController implements Observer {

    public Samples fReal, fImag, gReal, gImag;
    public SamplesView fRealView, fImagView, gRealView, gImagView;

    public FftLabController() {

        int origin = (this.originCentered) ? this.length / 2 : 0;
        this.fReal = new Samples(this.length, origin);
        this.fImag = new Samples(this.length, origin);
        this.gReal = new Samples(this.length, origin);
        this.gImag = new Samples(this.length, origin);
        this.initSamples();

        this.fReal.addObserver(this);
        this.fImag.addObserver(this);
        this.gReal.addObserver(this);
        this.gImag.addObserver(this);

        this.fRealView = new SamplesView(this.fReal);
        this.fImagView = new SamplesView(this.fImag);
        this.gRealView = new SamplesView(this.gReal);
        this.gImagView = new SamplesView(this.gImag);
        this.updateSampleValues(this.fRealView, this.fImagView);
        this.updateSampleValues(this.gRealView, this.gImagView);
    }

    public int getEditMode() {
        return this.editMode;
    }

    public void setEditMode(int mode) {
        this.editMode = mode;
        this.fRealView.setEditMode(mode);
        this.fImagView.setEditMode(mode);
        this.gRealView.setEditMode(mode);
        this.gImagView.setEditMode(mode);
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
        this.updateLengths();
        this.updateOrigins();
        this.initSamples();
        this.updateSampleValues(this.fRealView, this.fImagView);
        this.updateSampleValues(this.gRealView, this.gImagView);
        this.repaintViews();
    }

    public boolean getOriginCentered() {
        return this.originCentered;
    }

    public void setOriginCentered(boolean centered) {
        if (centered == this.originCentered) {
            return;
        }
        this.originCentered = centered;
        this.updateOrigins();
        this.repaintViews();
    }

    public void zeroAll() {
        this.fReal.zero();
        this.fImag.zero();
        this.gReal.zero();
        this.gImag.zero();
        this.repaintViews();
    }

    @Override
    public void update(Observable o, Object arg) {
        Samples s = (Samples) o;
        if (s == this.fReal || s == this.fImag) {
            this.transform(1, this.fReal, this.fImag, this.gReal, this.gImag);
            this.updateSampleValues(this.gRealView, this.gImagView);
            this.gRealView.repaint();
            this.gImagView.repaint();
        }
        else {
            this.transform(-1, this.gReal, this.gImag, this.fReal, this.fImag);
            this.updateSampleValues(this.fRealView, this.fImagView);
            this.fRealView.repaint();
            this.fImagView.repaint();
        }
    }

    private int editMode = SamplesView.EDIT_DRAW;
    private int length = 32;
    private boolean originCentered = false;

    private float computeSampleValue(Samples real, Samples imag) {
        float sv = 0.0f;
        float v[];
        v = real.values;
        for (int i = 0; i < this.length; ++i) {
            float si = v[i];
            if (-si > sv) {
                sv = -si;
            }
            else if (si > sv) {
                sv = si;
            }
        }
        v = imag.values;
        for (int i = 0; i < this.length; ++i) {
            float si = v[i];
            if (-si > sv) {
                sv = -si;
            }
            else if (si > sv) {
                sv = si;
            }
        }
        return sv;
    }

    private void updateSampleValues(SamplesView realView, SamplesView imagView) {
        float sv = this.computeSampleValue(realView.samples, imagView.samples);
        realView.setSampleValue(sv);
        imagView.setSampleValue(sv);
    }

    private void transform(int sign, Samples sar, Samples sai, Samples bar, Samples bai) {
        float ar[] = sar.values;
        float ai[] = sai.values;
        float br[] = bar.values;
        float bi[] = bai.values;

        for (int i = 0; i < this.length; ++i) {
            br[i] = ar[i];
            bi[i] = ai[i];
        }

        if (this.originCentered) {
            for (int i = 1; i < this.length; i += 2) {
                br[i] = -br[i];
                bi[i] = -bi[i];
            }
        }

        Fft.complexToComplex(sign, this.length, br, bi);

        if (this.originCentered) {
            for (int i = 1; i < this.length; i += 2) {
                br[i] = -br[i];
                bi[i] = -bi[i];
            }
        }
    }

    private void initSamples() {
        this.fReal.values[this.fReal.origin + 1] = 1.0f;
        this.transform(1, this.fReal, this.fImag, this.gReal, this.gImag);
    }

    private void updateLengths() {
        int length = this.length;
        this.fReal.setLength(length);
        this.fImag.setLength(length);
        this.gReal.setLength(length);
        this.gImag.setLength(length);
    }

    private void updateOrigins() {
        int origin = (this.originCentered) ? this.length / 2 : 0;
        int shift = origin - this.fReal.origin;
        this.fReal.origin = origin;
        this.fImag.origin = origin;
        this.gReal.origin = origin;
        this.gImag.origin = origin;
        this.fReal.rotate(shift);
        this.fImag.rotate(shift);
        this.gReal.rotate(shift);
        this.gImag.rotate(shift);
    }

    private void repaintViews() {
        this.fRealView.repaint();
        this.fImagView.repaint();
        this.gRealView.repaint();
        this.gImagView.repaint();
    }

    private void shiftSamples(Samples s, int shift) {
        float temp[] = new float[this.length];
        int j = shift % this.length;
        for (int i = 0; i < this.length; ++i, ++j) {
            if (j < 0) {
                j += this.length;
            }
            if (j >= this.length) {
                j -= this.length;
            }
            temp[j] = s.values[i];
        }
        s.values = temp;
    }

}

// Simple Fast Fourier Transform.
class Fft {
    public static void complexToComplex(int sign, int n, float ar[], float ai[]) {
        float scale = (float) Math.sqrt(1.0f / n);

        int i, j;
        for (i = j = 0; i < n; ++i) {
            if (j >= i) {
                float tempr = ar[j] * scale;
                float tempi = ai[j] * scale;
                ar[j] = ar[i] * scale;
                ai[j] = ai[i] * scale;
                ar[i] = tempr;
                ai[i] = tempi;
            }
            int m = n / 2;
            while (m >= 1 && j >= m) {
                j -= m;
                m /= 2;
            }
            j += m;
        }

        int mmax, istep;
        for (mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax) {
            float delta = sign * 3.141592654f / mmax;
            for (int m = 0; m < mmax; ++m) {
                float w = m * delta;
                float wr = (float) Math.cos(w);
                float wi = (float) Math.sin(w);
                for (i = m; i < n; i += istep) {
                    j = i + mmax;
                    float tr = wr * ar[j] - wi * ai[j];
                    float ti = wr * ai[j] + wi * ar[j];
                    ar[j] = ar[i] - tr;
                    ai[j] = ai[i] - ti;
                    ar[i] += tr;
                    ai[i] += ti;
                }
            }
            mmax = istep;
        }
    }
}

/////////////////////////////////////////////////////////////////////////////
// Digital lollipop view of samples.

class SamplesView extends Canvas {

    public static int EDIT_NONE = 0;
    public static int EDIT_DRAW = 1;
    public static int EDIT_ZERO = 2;
    public static int EDIT_NEGATE = 3;
    public static int EDIT_SHIFT = 4;

    public Samples samples;

    public SamplesView(Samples s) {
        this.samples = s;
        this.setSampleValue(1.0f);
        this.updateDrawingSizes();
        this.setBackground(Color.yellow);
    }

    public void setSampleValue(float v) {
        int height = this.size().height;
        this.sampleValue = (v != 0.0f) ? v : 1.0f;
        this.sampleScale = -0.25f * height / this.sampleValue;
    }

    public void setEditMode(int mode) {
        this.editMode = mode;
    }

    @Override
    public void paint(Graphics g) {
        this.updateDrawingSizes();
        this.drawSamples(g);
    }

    @Override
    public Dimension minimumSize() {
        return new Dimension(100, 50);
    }

    @Override
    public Dimension preferredSize() {
        return this.minimumSize();
    }

    @Override
    public boolean mouseDown(Event e, int x, int y) {
        if (this.editMode == SamplesView.EDIT_NONE) {
            return true;
        }
        this.lastDrag = -1;
        return this.mouseDrag(e, x, y);
    }

    @Override
    public boolean mouseDrag(Event e, int x, int y) {
        if (this.editMode == SamplesView.EDIT_NONE) {
            return true;
        }

        if (x < 0 || x > this.size().width) {
            return true;
        }
        if (y < this.sampleRadius || y > this.size().height - this.sampleRadius) {
            return true;
        }

        int i = (int) ((float) (x - this.sampleStart) / (float) this.sampleWidth + 0.5);
        if (i < 0) {
            i = 0;
        }
        if (i >= this.samples.values.length) {
            i = this.samples.values.length - 1;
        }

        if (this.editMode == SamplesView.EDIT_NEGATE && i == this.lastDrag) {
            return true;
        }

        Graphics g = this.getGraphics();

        if (this.editMode == SamplesView.EDIT_SHIFT) {
            if (i != this.lastDrag && this.lastDrag >= 0) {
                g.setColor(this.getBackground());
                this.drawSamples(g);
                this.samples.rotate(i - this.lastDrag);
                g.setColor(this.getForeground());
                this.drawSamples(g);
            }
            this.lastDrag = i;
            return true;
        }

        g.setColor(this.getBackground());
        this.drawOneSample(g, i);
        if (this.editMode == SamplesView.EDIT_ZERO) {
            this.samples.values[i] = 0.0f;
        }
        else if (this.editMode == SamplesView.EDIT_NEGATE) {
            this.samples.values[i] = -this.samples.values[i];
        }
        else {
            this.samples.values[i] = (y - this.sampleBase) / this.sampleScale;
        }
        g.setColor(this.getForeground());
        this.drawOneSample(g, i);
        this.lastDrag = i;

        return true;
    }

    @Override
    public boolean mouseUp(Event e, int x, int y) {
        if (this.editMode != SamplesView.EDIT_NONE) {
            this.samples.notifyObservers();
        }
        return true;
    }

    private int editMode = SamplesView.EDIT_DRAW;
    private int sampleStart, sampleBase, sampleWidth, sampleRadius;
    private float sampleScale, sampleValue;
    private int lastDrag;

    private void drawOneSample(Graphics g, int i) {
        int x = this.sampleStart + i * this.sampleWidth;
        int y = this.sampleBase;
        int r = this.sampleRadius;
        int w = this.sampleWidth;
        int h = (int) (this.samples.values[i] * this.sampleScale);
        g.drawLine(x - w / 2, y, x + w / 2, y);
        g.drawLine(x, y, x, y + h);
        if (i == this.samples.origin) {
            g.drawOval(x - r, y + h - r, 2 * r, 2 * r);
        }
        else {
            g.fillOval(x - r, y + h - r, 2 * r + 1, 2 * r + 1);
        }
    }

    private void drawSamples(Graphics g) {
        for (int i = 0; i < this.samples.values.length; ++i) {
            this.drawOneSample(g, i);
        }
    }

    private void updateDrawingSizes() {
        int width = this.size().width;
        int height = this.size().height;
        int nSamples = this.samples.values.length;
        this.sampleWidth = (int) ((float) width / (float) (nSamples + 1));
        this.sampleStart = (width - (nSamples - 1) * this.sampleWidth) / 2;
        this.sampleBase = (int) (0.5f * height);
        this.sampleScale = -0.25f * height / this.sampleValue;
        this.sampleRadius = (int) (0.4f * this.sampleWidth);
        int maxRadius = (int) (0.5f * height);
        if (this.sampleRadius > maxRadius) {
            this.sampleRadius = maxRadius;
        }
    }
}

/////////////////////////////////////////////////////////////////////////////
// Collection of sample values.

class Samples extends Observable {

    public float values[];
    public int origin;

    public Samples(int length, int origin) {
        this.origin = origin;
        this.values = new float[length];
        this.zero();
    }

    public void setLength(int length) {
        if (length == this.values.length) {
            return;
        }
        this.values = new float[length];
        this.zero();
    }

    public void zero() {
        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = 0.0f;
        }
    }

    public void rotate(int n) {
        int length = this.values.length;
        float temp[] = new float[length];
        int j = n % length;
        for (int i = 0; i < length; ++i, ++j) {
            if (j < 0) {
                j += length;
            }
            if (j >= length) {
                j -= length;
            }
            temp[j] = this.values[i];
        }
        this.values = temp;
    }

    @Override
    public void notifyObservers() {
        this.setChanged();
        super.notifyObservers();
    }
}

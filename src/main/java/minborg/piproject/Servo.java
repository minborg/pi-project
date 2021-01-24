package minborg.piproject;

import com.pi4j.io.gpio.*;

import java.util.concurrent.TimeUnit;

public final class Servo {

    public static void main(String[] args) throws Exception {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput outputPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, RaspiPin.GPIO_01.toString(), PinState.LOW);

        System.out.println("outputPin = " + outputPin);

        for (int i = 0; i < 10; i++) {
            outputPin.high();
            Thread.sleep(100);
            outputPin.low();
            Thread.sleep(100);
        }

        final ServoThread thread = new ServoThread(outputPin);
        thread.ratio(0.5f);
        thread.start();

        Thread.sleep(500);

        for (int i = 0; i < 100; i++) {
            thread.ratio(((float) i) / 100);
            Thread.sleep(100);
        }

        outputPin.low();
        thread.close();

        gpio.shutdown();

    }

    private static final class ServoThread extends Thread {

        private static final long ONE_MS_IN_NS = 1_000_000;

        private final GpioPinDigitalOutput output;
        private volatile boolean closed;
        private volatile long durationNs;

        public ServoThread(final GpioPinDigitalOutput output) {
            this.output = output;
        }

        @Override
        public void run() {
            long nextOn = System.nanoTime();
            final long nextOff = nextOn + durationNs;
            while (!closed) {
                if (System.nanoTime() < nextOn) {
                    // spin wait
                }
                output.high();
                if (System.nanoTime() < nextOff) {
                    // spin wait
                }
                output.low();

                nextOn += 20 * ONE_MS_IN_NS;
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                // do nothing
            }
        }

        public void ratio(float ratio) {
            durationNs = ONE_MS_IN_NS +
                    (long) (ratio * ONE_MS_IN_NS);

            System.out.println(String.format("%.2f %d", ratio, durationNs));
        }

        public void close() {
            this.closed = true;
        }

    }

}

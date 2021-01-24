package minborg.piproject;

import com.pi4j.io.gpio.*;

import java.util.concurrent.TimeUnit;

public final class Servo {

    public static void main(String[] args) throws Exception {
        final GpioController gpio = GpioFactory.getInstance();
        final GpioPinDigitalOutput outputPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, RaspiPin.GPIO_01.toString(), PinState.LOW);

        System.out.println("outputPin = " + outputPin);

        final ServoThread thread = new ServoThread(outputPin);

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
            while (!closed) {
                if (System.nanoTime() > nextOn) {
                    final long nextOff = nextOn + durationNs;
                    output.high();
                    if (System.nanoTime() < nextOff) {
                        // spin wait
                    }
                    output.low();
                    //System.out.println(nextOn + " " + nextOff);
                    nextOn += 20 * ONE_MS_IN_NS;
                }
            }
        }

        public void ratio(float ratio) {
            durationNs = ONE_MS_IN_NS +
                    (long) (ratio * ONE_MS_IN_NS);

            System.out.println("ratio = " + ratio);
        }

        public void close() {
            this.closed = true;
        }

    }

}

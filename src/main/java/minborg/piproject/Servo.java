package minborg.piproject;

import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.MaestroServoDriver;
import com.pi4j.component.servo.impl.MaestroServoProvider;
import com.pi4j.io.gpio.Pin;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public final class Servo {

    public static void main(String[] args) throws Exception {
        final ServoProvider servoProvider = new MaestroServoProvider();

        final List<Pin> supportedPins = servoProvider.getDefinedServoPins().stream().sorted().collect(Collectors.toList());

        System.out.println("Supported pins:" + servoProvider.getDefinedServoPins());
        final Pin pin = supportedPins.stream().findFirst().orElseThrow(() -> new NoSuchElementException("No pin!"));

        System.out.println("pin = " + pin);

        final MaestroServoDriver servo0 = (MaestroServoDriver) servoProvider.getServoDriver(pin);

        final long start = System.currentTimeMillis();

        final int min = servo0.getMinValue();
        System.out.println("min = " + min);
        final int max = servo0.getMaxValue();
        System.out.println("max = " + max);
        servo0.setAcceleration(30);

        while (System.currentTimeMillis() - start < 120000) { // 2 minutes
            servo0.setServoPulseWidth(min);
            System.out.println("min");
            Thread.sleep(1500);
            System.out.println("max");
            servo0.setServoPulseWidth(max);
            Thread.sleep(1500);
        }
        System.out.println("Exiting MaestroServoExample");

    }

}

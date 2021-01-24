package minborg.piproject;

import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.MaestroServoDriver;
import com.pi4j.component.servo.impl.MaestroServoProvider;

public final class Servo {

    public static void main(String[] args) throws Exception {
        final ServoProvider servoProvider = new MaestroServoProvider();

        System.out.println("Supported pins:" + servoProvider.getDefinedServoPins());

        final MaestroServoDriver servo0 = (MaestroServoDriver) servoProvider.getServoDriver(servoProvider.getDefinedServoPins().get(0));

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

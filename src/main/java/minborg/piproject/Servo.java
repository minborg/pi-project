package minborg.piproject;

import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.MaestroServoDriver;
import com.pi4j.component.servo.impl.MaestroServoProvider;

public final class Servo {

    public static void main(String[] args) throws Exception {
        ServoProvider servoProvider = new MaestroServoProvider();

        System.out.println("Supported pins:" + servoProvider.getDefinedServoPins());

        MaestroServoDriver servo0 = (MaestroServoDriver) servoProvider.getServoDriver(servoProvider.getDefinedServoPins().get(0));

        System.out.println("servo0 = " + servo0);

        long start = System.currentTimeMillis();

        int min = servo0.getMinValue();
        int max = servo0.getMaxValue();
        servo0.setAcceleration(30);

        while (System.currentTimeMillis() - start < 120000) { // 2 minutes
            servo0.setServoPulseWidth(min);
            Thread.sleep(1500);
            servo0.setServoPulseWidth(max);
            Thread.sleep(1500);
        }
        System.out.println("Exiting MaestroServoExample");

    }

}

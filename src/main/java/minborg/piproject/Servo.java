package minborg.piproject;

import com.pi4j.component.servo.ServoProvider;
import com.pi4j.component.servo.impl.MaestroServoProvider;

import java.io.IOException;

public final class Servo {

    public static void main(String[] args) throws IOException {
        ServoProvider servoProvider = new MaestroServoProvider();

        System.out.println(servoProvider.getDefinedServoPins());

    }

}

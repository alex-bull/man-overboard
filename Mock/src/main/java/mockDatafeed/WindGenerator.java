package mockDatafeed;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by psu43 on 17/07/17.
 * Randomly generate wind speed and direction
 */
class WindGenerator {

    private short windSpeed;
    private short windDirection;

    WindGenerator() {
        windSpeed = 5500;
        // North = 0x0000 (0 or -32768) East = 0x4000 (16384) West = -0x4000 (-16384)
        // North-West = 0x6000 (24576) North-East -0x6000 (-24576) South-West = 0x2000 (8192)
        windDirection = 8192;
    }

    short getWindSpeed() {
        return getRandomWindSpeed();
    }

    short getWindDirection() {
        return getRandomWindDirection();
    }



    private short getRandomWindSpeed() {
        if(windSpeed < 4000) {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(0, 2+1) + windSpeed);
        }
        else if(windSpeed > 8000) {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(-2, 0) + windSpeed);
        }
        else {
            windSpeed = (short) (ThreadLocalRandom.current().nextInt(-2, 2+1) + windSpeed);
        }

        return windSpeed;

    }

    private short getRandomWindDirection() {
        windDirection = (short) (ThreadLocalRandom.current().nextInt(-30, 30+1) + windDirection);
        return windDirection;
    }
}

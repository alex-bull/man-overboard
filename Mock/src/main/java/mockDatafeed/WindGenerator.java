package mockDatafeed;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by psu43 on 17/07/17.
 * Randomly generate wind speed and direction
 */
class WindGenerator {

    private short windSpeedOriginal;
    private short windDirectionOriginal;
    private short windSpeed;
    private short windDirection;
    private long lastUpdateTime=0;
    private final int speedVariance=100;
    private final int directionVariance=300;

    /**
     * Constructs a wind generator
     * @param windSpeed int wind speed initial value
     * @param windDirection int wind direction initial direction
     */
    WindGenerator(int windSpeed, int windDirection) {
        this.windSpeedOriginal = (short) windSpeed;
        this.windDirectionOriginal = (short) windDirection;
    }

    public short getWindSpeed() {
        if(System.currentTimeMillis()-lastUpdateTime>5000){
            lastUpdateTime=System.currentTimeMillis();
            getRandomWindSpeed();
            getRandomWindDirection();
        }
        return windSpeed;
    }

    public short getWindDirection() {
        if(System.currentTimeMillis()-lastUpdateTime>5000){
            lastUpdateTime=System.currentTimeMillis();
            getRandomWindDirection();
            getRandomWindSpeed();
        }
        return windDirection;
    }



    /**
     * Generates a random value for wind speed.

     */
    private void getRandomWindSpeed() {
//        if(windSpeed < 3000) {
//            windSpeed = (short) (ThreadLocalRandom.current().nextInt(0, 2+1) + windSpeed);
//        }
//        else if(windSpeed > 8000) {
//            windSpeed = (short) (ThreadLocalRandom.current().nextInt(-2, 0) + windSpeed);
//        }
//        else {
//            windSpeed = (short) (ThreadLocalRandom.current().nextInt(-2, 2+1) + windSpeed);
//        }
        windSpeed= (short) (windSpeedOriginal+ThreadLocalRandom.current().nextGaussian()*speedVariance);

    }

    /**
     * Generates a random value for wind direction
     */
    private void getRandomWindDirection() {

        windDirection= (short) (windDirectionOriginal+ThreadLocalRandom.current().nextGaussian()*directionVariance);
    }
}

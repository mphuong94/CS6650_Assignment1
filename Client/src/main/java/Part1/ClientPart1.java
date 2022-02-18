package Part1;

import utils.ClientAbstract;
import utils.ClientPartEnum;

public class ClientPart1 extends ClientAbstract {

    public ClientPart1(int numThreads, int numSkiers, int numLifts, int numRuns, String url) {
        super(numThreads, numSkiers, numLifts, numRuns, url);
        this.getPhase1().setPartChosen(ClientPartEnum.PART1);
        this.getPhase2().setPartChosen(ClientPartEnum.PART1);
        this.getPhase3().setPartChosen(ClientPartEnum.PART1);
    }

    @Override
    public void run() {
        try {
            super.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        float throughput = this.getTotalCalls() / this.getWallTime();
        System.out.printf("Success calls: %d\n", this.getTotalSuccess());
        System.out.printf("Failure calls: %d\n", this.getTotalFailure());
        System.out.printf("Wall time: %d\n", this.getWallTime());
        System.out.printf("Throughput: %.2f\n", throughput);
        return;
    }

}

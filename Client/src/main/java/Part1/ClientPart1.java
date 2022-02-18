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
        long startTime = System.currentTimeMillis();
        try {
            super.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long wallTime = endTime - startTime;
        int totalSuccess = this.getPhase1().getSuccessCount() +
                this.getPhase2().getSuccessCount() + this.getPhase3().getSuccessCount();
        int totalFailure = this.getPhase1().getFailureCount() +
                this.getPhase2().getFailureCount() + this.getPhase3().getFailureCount();
        float throughput = (totalFailure + totalSuccess)/wallTime;
        System.out.printf("Success calls: %d\n",totalSuccess);
        System.out.printf("Failure calls: %d\n",totalFailure);
        System.out.printf("Wall time: %d\n",wallTime);
        System.out.printf("Throughput: %.2f\n",throughput);
        return;
    }

}

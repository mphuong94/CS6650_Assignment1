import Part1.ClientPart1;

public class Main {

    public static void main(String[] args) {
//        String url = "http://localhost:8080/ResortServer_war_exploded/skiers/";
        String url = "http://52.35.85.82:8080/ResortServer_war/skiers/";

        int[] numThreadArray = new int[] {128};
        int numSkiers = 20000;
        int numLifts = 40;
        int numRuns = 20;

        for(int numThread : numThreadArray) {
            System.out.printf("Number of threads running: %d\n", numThread);
            System.out.printf("Number of skiers running: %d\n", numSkiers);
            System.out.printf("Number of lifts running: %d\n", numLifts);
            System.out.printf("Number of runs: %d\n", numRuns);
            ClientPart1 clientPart1 = new ClientPart1(numThread,numSkiers,numLifts,numRuns,url);
            clientPart1.run();
        }

    }

}

public class Main {

    public static void main(String[] args) {
        String url = "http://localhost:8080/lab2_ex_war_exploded/skiers";

        int[] numThreadArray = new int[] {32, 64, 128, 256};
        int numSkiers = 20000;
        int numLifts = 40;
        int numRuns = 20;

        for(int numThread : numThreadArray) {
            ClientPart1 clientPart1 = new ClientPart1(numThread,numSkiers,numLifts,numRuns,url);
            clientPart1.run();
        }

    }

}

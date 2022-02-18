package Part2;

import utils.ClientAbstract;
import utils.ClientPartEnum;
import utils.LatencyStat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class ClientPart2 extends ClientAbstract {

    public ClientPart2(int numThreads, int numSkiers, int numLifts, int numRuns, String url) {
        super(numThreads, numSkiers, numLifts, numRuns, url);
        this.getPhase1().setPartChosen(ClientPartEnum.PART2);
        this.getPhase2().setPartChosen(ClientPartEnum.PART2);
        this.getPhase3().setPartChosen(ClientPartEnum.PART2);
    }

    public void writeCSV(List<LatencyStat> stats)  {
        File file = new File("output.csv");
        try{
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);

            bufferWriter.write("Start Time, Request Type, Response Code, Latency");
            bufferWriter.newLine();

            for (LatencyStat stat: stats){
                StringJoiner singleRow = new StringJoiner(",");
                singleRow.add(stat.getStartTime().toString());
                singleRow.add(stat.getRequestType());
                singleRow.add(stat.getResponseCode().toString());
                singleRow.add(stat.getLatency().toString());
                String content = singleRow.toString();
                bufferWriter.write(content);
                bufferWriter.newLine();
            }
            bufferWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            super.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Collapse into 1 list
        List<LatencyStat> output = new ArrayList(this.getPhase1().getHistory());
        output.addAll(this.getPhase2().getHistory());
        output.addAll(this.getPhase3().getHistory());

        // Write CSV
        this.writeCSV(output);
        List<Long> total = output.stream()
                        .map(LatencyStat::getLatency)
                        .filter(x->x>-1)
                        .collect(Collectors.toList());




    }

}


package Main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Torres
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Main.BCP;
import java.util.ArrayList;
import org.apache.commons.lang3.ArrayUtils;
import java.util.List;

/**
 *
 * @author Torres
 */
public class SRTF {

    List<int[]> processes;
    Long tempoTotalEspera; //Tempo total de espera
    Long tempoMediaEspera; //Tempo médio de espera
    int tempoInicio;
    int tempoAtual;
    int flagBloqueio;

    public SRTF(List<int[]> processes) {
        this.processes = processes;

    }

    public void runSRTF() {
        BCP bcp = new BCP();

        List<int[]> procA = new ArrayList<>(processes);//Copia dos processos

//        Divide os processos, os processos sem a fila de io no 'procs' e as filas de io de cada processo no 'io'
        List<int[]> procs = bcp.createProcessIO(processes);
        List<int[]> io = bcp.createIO(processes);

//      Tempo total e medio de espera
        tempoTotalEspera = 0L;
        tempoMediaEspera = 0L;

        tempoInicio = processes.get(0)[3];
        tempoAtual = tempoInicio;

//      Inicia escalonamento 
        while (!procs.isEmpty()) {// Enquanto a fila não estiver vazia

            //Busca o index do proximo processo a ser executado
            int next = bcp.findNextProcess(procs, tempoAtual);

            if (procs.get(next)[4] > 0) {
                while (bcp.verifyAllBlockeds(procs)) {//Enquanto todos os processos estejam bloqueados 

//                    System.out.println("---------------------------------------");
                    tempoAtual += 1;//Atualiza o tempo atual e o tempo total de espera
                    tempoTotalEspera += procs.size();

                    for (int i = 0; i < procs.size(); i++) {//Decrementa o tempo de bloqueio de todos os processos
                        if (procs.get(i)[4] > 0) {
                            procs.get(i)[4] -= 1;
                        }
                    }
                }

                next = bcp.findNextBlockProcess(procs, tempoAtual);//Procura o proximo processo para executar que nao esteja bloqueado
            }

            if (next == -1) {//Ocorrencia de vacuo, nao ha processo com tempo de chegada <= ao tempo atual

                tempoAtual = procs.get(0)[3];
                tempoTotalEspera += (procs.get(0)[3] - tempoAtual) * (procs.size() - 1);
                printGant(procs.get(0)[3] - tempoAtual);
            } else {

                tempoAtual += 1;
                tempoTotalEspera += (procs.size() - 1);
                printGant(procs.get(next)[0]);

                procs.get(next)[1] = (procs.get(next)[1] - 1);//Decrementa o tempo de execucao
                for (int i = 0; i < procs.size(); i++) {//Decrementa o tempo de bloqueio de todos os processos
                    if (i != next) {

                        if (procs.get(i)[4] > 0) {
                            procs.get(i)[4] -= 1;

                        }
                    }
                }

                if ((procA.get(next)[1] - procs.get(next)[1]) == io.get(next)[0]) {//Verificar se o processo e de io
                    //Acrescenta o tempo de bloqueio
                    procs.get(next)[4] = 4;
                    io.set(next, ArrayUtils.remove(io.get(next), 0));

                }
                //Se a execucao terminou, retirar das listas
                if (procs.get(next)[1] == 0) {
                    procs.remove(next);
                    procA.remove(next);
                    io.remove(next);
//                    io.set(0, ArrayUtils.remove(io.get(0), next));
                }

            }

        }

        for (int i = 0; i < processes.size(); i++) {//Retirar o tempo de entrada do tempo total de espera 
            tempoTotalEspera -= processes.get(i)[3];
        }

        tempoMediaEspera = (tempoTotalEspera / processes.size());

        System.out.println("Tempo total de espera : " + tempoTotalEspera);
        System.out.println("Tempo médio de espera : " + tempoMediaEspera);

    }

    public void printGant(int element) {
        System.out.print("Process : ");
        for (int i = 0; i < element; i++) {

            System.out.print("---");
        }
        System.out.println(" " + element);
    }

    public void createOutputFile() {
    }
}

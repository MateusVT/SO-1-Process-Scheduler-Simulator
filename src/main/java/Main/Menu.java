package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import sun.misc.IOUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Torres
 */
public class Menu {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {

        System.out.println("Simulador de Algoritmos de Escalonamento");
        System.out.println("");
        System.out.println("-------------------- ALGORITMOS --------------------------");
        System.out.println("1 - Round-Robin (RR)");
        System.out.println("2 - Shortest Remaining Time First (SRTF)");
        System.out.println("3 - Priority Queues (PQ)");
        System.out.println("-------------------------------------------------------");
        System.out.println("Escolha um Algoritmo : ");
        Scanner scan = new Scanner(System.in);
        int alg = scan.nextInt();

        System.out.println("------------------ ARQUIVOS DE ENTRADA -------------------");
        System.out.println("1 - entrada.txt");
        System.out.println("2 - io.txt");
        System.out.println("3 - misto.txt");
        System.out.println("4 - cpu.txt");
        System.out.println("-------------------------------------------------------");
        System.out.println("Determine o arquivo de entrada : ");
        int fileID = scan.nextInt();

        List<int[]> processes = new Menu().readFile(fileID);//Lista de processos onde processe[i] contém os elementos do processo.

        switch (alg) {
            case 1:
                System.out.println("Defina o Quantum : ");
                int quantumRR = scan.nextInt();
                RR rr = new RR(processes, quantumRR);
                rr.runRR();
                break;
            case 2:
                SRTF srtf = new SRTF(processes);
                srtf.runSRTF();
                break;
            case 3:
                System.out.println("Defina o Quantum : ");
                int quantumPQ = scan.nextInt();
                PQ pq = new PQ(processes, quantumPQ);
                pq.runPQ();
                break;
            default:
                System.out.println("Opção Inválida!");
        }

    }

    public List<int[]> readFile(int file) throws FileNotFoundException, IOException {
        String fileName = "";
       
        switch (file) {
            case 1:
                fileName = "C:\\Users\\Torres\\Desktop\\SO 2\\Process-Scheduler-Simulator\\src\\main\\java\\Resources\\entrada.txt";
                break;
            case 2:
                fileName = "C:\\Users\\Torres\\Desktop\\SO 2\\Process-Scheduler-Simulator\\src\\main\\java\\Resources\\io.txt";
                break;
            case 3:
                fileName = "C:\\Users\\Torres\\Desktop\\SO 2\\Process-Scheduler-Simulator\\src\\main\\java\\Resources\\misto.txt";
                break;
            case 4:
                fileName = "C:\\Users\\Torres\\Desktop\\SO 2\\Process-Scheduler-Simulator\\src\\main\\java\\Resources\\cpu.txt";
                break;
            default:
                System.out.println("Opção Inválida!");
        }

        String line = null;

        try {

            FileReader fileReader
                    = new FileReader(fileName);

            BufferedReader bufferedReader
                    = new BufferedReader(fileReader);

            System.out.println("Lista de processos :");
            List<int[]> listOfProcess = new ArrayList();
            while ((line = bufferedReader.readLine()) != null) {

//                System.out.println(line);
                String[] processString = line.split(" ");
                int[] process = new int[processString.length];
                for (int i = 0; i < processString.length; i++) {
                    process[i] = Integer.parseInt(processString[i]);
                }
                listOfProcess.add(process);
                System.out.println("Processo " + processString[0] + "{ entrada : " + processString[3] + "; duração: " + processString[1] + "}");
            }

            bufferedReader.close();
            return listOfProcess;
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '"
                    + fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                    + fileName + "'");

        }
        return null;

    }
}

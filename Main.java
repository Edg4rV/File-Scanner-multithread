package analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Main {
    private static byte[] pdfSignature = "%PDF-".getBytes();

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.out.println("Please provide the input file");
            System.exit(0);
        }
//        else if (args.length == 3) {
//            Search check = new Search(new Default());
//            check.search(args);
//        }
        else if (args.length == 3) {
            System.out.println("U r in Default option");
            String[] str = new String[4];
            File folder = new File(args[0]);
            File[] listOfFiles = folder.listFiles();
            str[1] = args[1];
            str[2] = args[2];

            System.out.println(Arrays.toString(listOfFiles));
//            System.out.println(">> args is " + args[0] + " " + args[1] + " " + args[2]);
            ExecutorService executor = Executors.newFixedThreadPool(2);
            System.out.println(listOfFiles.length);
            for (int i = 0; i < listOfFiles.length; i++) {
                str[0] = listOfFiles[i].getPath();
                System.out.println(">>>>>>>" + str[0]);
                str[3] = listOfFiles[i].getName();

//                System.out.println(listOfFiles[i].getName());
               executor.submit(() -> {
                   try {
                       new Default().patternChecker(str);
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               });


                if (i == listOfFiles.length - 1) {
                    executor.shutdown();
                }
            }

        }

        else if ("--naive".equals(args[0])) {
//            System.out.println("Entering in naive")
                Search check = new Search(new Naive());
//            for(String x:args) {
//                System.out.println(x);
//            }
//            System.out.println(args[1].length() + " " + args[2].length() + " " + args[3].length());
//            System.out.println(args[1] + " " + args[2] + " " + args[3]);
                check.search(args);

            } else if ("--KMP".equals(args[0])) {
//            System.out.println("Entering in KMP");
//            System.out.println(args[1].length() + " " + args[2].length() + " " + args[3].length());
//            System.out.println(args[1] + " " + args[2] + " " + args[3]);
                Search check = new Search(new KMP());
                check.search(args);

            }
        }
    }

    class Search {

        private SearchingStrategy strategy;

        public Search(SearchingStrategy strategy) {
            // write your code here
            this.strategy = strategy;
        }

        /**
         * It performs the search algorithm according to the given strategy
         */
        public void search(String... args) throws Exception {
            // write your code here
            strategy.patternChecker(args);

        }
    }

    interface SearchingStrategy {

        void patternChecker(String... args) throws Exception;

    }


    class Default implements SearchingStrategy {

        @Override
        public void patternChecker(String[] args) throws Exception {
//            long starttime = System.nanoTime();

            InputStream inputFile = new FileInputStream(args[0]);
            boolean isPDF = false;
            boolean isDOC = false;
            boolean isPEM = false;
            String check = "";

            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLine()) {
                check = scanner.nextLine();
                if (check.contains(args[1]) && args[1].contains("%PDF-")) {
                    isPDF = true;
                    break;
//                } else {
//                    isPDF = false;
                }
                else if (check.contains(args[1]) && args[1].contains("-----BEGIN\\ CERTIFICATE-----")) {
                    isPEM = true;
                    break;
//                } else {
//                    isPEM = false;
                }
                if (check.contains(args[1]) && args[1].contains("%DOC-")) {
                    isDOC = true;
                    break;
//                } else {
//                    isDOC = false;
                }
            }

//            long endtime = System.nanoTime();
//            long timeResult = endtime - starttime;
//            double convertSeconds = (double) timeResult / 1_000_000_000.0;
//            DecimalFormat df = new DecimalFormat("#.###");
//            String seconds = df.format(convertSeconds);
            if (isPDF) {
                System.out.println(args[3]  + ":" + " " + "PDF document");
//                System.out.println("It took" + " " + seconds + " " + "seconds");
            }
            else if (isDOC) {
                System.out.println(args[3] + ":" + " " + "DOC document");
            }
            else if (isPEM) {
                System.out.println(args[3] + ":" + " " + "PEM certificate");
            } else {
                System.out.println(args[3] + ":" + " " + "Unknown file type");
            }


//            else if (!isPDF) {
//                System.out.println(args[3] + ":" + " " + "Unknown file type");
////                System.out.println("It took" + " " + seconds + " " + "seconds");
////                else if (isPDF == false && !isDOC) {
////                System.out.println(args[3] + ":" + "Unknown file type");
////                System.out.println("It took" + " " + seconds + " " + "seconds");
//            } else if (isDOC) {
//                System.out.println(args[3] + ":" + " " + "DOC document");
////                System.out.println("It took" + " " + seconds + " " + "seconds");
//            } else if (isDOC == false) {
//                System.out.println(args[3] + ":" + " " + "Unknown file type");
////                System.out.println("It took" + " " + seconds + " " + "seconds");
//            } else if (isPEM) {
//                System.out.println(args[3] + ":" + " " + "PEM certificate");
////                System.out.println("It took" + " " + seconds + " " + "seconds");
//            } else if (!isPEM) {
//                System.out.println(args[3] + ":" + " " + "Unknown file type");
////                System.out.println("It took" + " " + seconds + " " + "seconds");
//            }


            scanner.close();
            inputFile.close();

        }
    }

    class KMP implements SearchingStrategy {

        int[] generatePrefixTable(String... args) {
            int[] table = new int[args[0].length()];
            int j = 0;
            table[0] = 0;
            for (int i = 1; i < args[0].length(); ) {
                if (args[0].charAt(i) == args[0].charAt(j)) {
                    j++;
                    table[i] = j;
                    i++;
                } else {
                    if (j != 0) {
                        j = table[j - 1];
                    } else {
                        table[i] = 0;
                        i++;
                    }
                }
            }
            return table;
        }

        @Override
        public void patternChecker(String... args) throws Exception {
            long starttime = System.nanoTime();
            InputStream inputFile = new FileInputStream(args[1]);
            boolean isPDF = false;
            boolean isDOC = false;
            boolean isPEM = false;
            String check = "";
//------------------------------------------------------------------
            int[] table = generatePrefixTable(args[2]);

            boolean flag = false;
            boolean status = false;
            int txtPos = 0;
            int patternPos = 0;
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLine()) {
                if (flag) {
                    break;
                }
                check = scanner.nextLine();
                while (txtPos < check.length()) {
                    if (args[2].charAt(patternPos) == check.charAt(txtPos)) {
                        patternPos++;
                        txtPos++;
                        if (patternPos == args[2].length()) {
                            status = true;
                            txtPos = check.length();
                            flag = true;
                        }

                    } else if (args[2].charAt(patternPos) != check.charAt(txtPos)) {

                        status = false;
                        flag = true;
                        if (patternPos != 0) {
                            patternPos = table[patternPos - 1];
                        } else {
                            txtPos++;
                        }
                    }
                }
            }

            long endtime = System.nanoTime();
            long timeResult = endtime - starttime;
            double convertSeconds = (double) timeResult / 1_000_000_000.0;
            DecimalFormat df = new DecimalFormat("#.###");
            String seconds = df.format(convertSeconds);

            if (check.contains(args[2]) && status && args[2].contains("%PDF-") && args[3].contains("DOC document")) {
                System.out.println("DOC document");
                System.out.println("It took" + " " + seconds + " " + "seconds");
                if (check.contains(args[2]) && status && args[2].contains("%PDF-")) {
                    System.out.println("PDF document");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                } else if (check.contains(args[2]) && status == false && args[2].contains("%PDF-")) {
                    System.out.println("Unknown file type");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                } else if (check.contains(args[2]) && status && args[2].contains("%DOC-")) {
                    System.out.println("DOC document");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                } else if (check.contains(args[2]) && status == false && args[2].contains("%DOC-")) {
                    System.out.println("Unknown file type");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                } else if (check.contains(args[2]) && status && args[2].contains("-----BEGIN\\ CERTIFICATE-----")) {
                    System.out.println("PEM certificate");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                } else if (check.contains(args[2]) && status == false && args[2].contains("-----BEGIN\\ CERTIFICATE-----")) {
                    System.out.println("Unknown file type");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                }

                scanner.close();


                inputFile.close();
            }
        }
    }

    class Naive implements SearchingStrategy {
        @Override
        public void patternChecker(String... args) throws Exception {
            long starttime = System.nanoTime();
            InputStream inputFile = new FileInputStream(args[1]);
            boolean isPDF = false;
            boolean isDOC = false;
            String check = "";

            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLine()) {
                check = scanner.nextLine();
                if (check.contains(args[2]) && args[2].contains("PDF-")) {
                    isPDF = true;
                    break;
                } else {
                    isPDF = false;
                }
                if (check.contains(args[2]) && args[2].contains("%DOC-")) {
                    isDOC = true;
                    break;
                } else {
                    isDOC = false;
                }
                long endtime = System.nanoTime();
                long timeResult = endtime - starttime;
                double convertSeconds = (double) timeResult / 1_000_000_000.0;
                DecimalFormat df = new DecimalFormat("#.###");
                String seconds = df.format(convertSeconds);
                if (isPDF) {
                    System.out.println("PDF document");
//            System.out.println("It took" + " " + seconds + " " + "seconds");
                } else if (isPDF == false) {
                    System.out.println("Unknown file type");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                }
                if (isDOC) {
                    System.out.println("DOC document");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                } else if (isPDF == false) {
                    System.out.println("Unknown file type");
                    System.out.println("It took" + " " + seconds + " " + "seconds");
                }

                scanner.close();


                inputFile.close();

            }
        }
    }

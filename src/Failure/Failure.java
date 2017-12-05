package Failure;

import java.util.*;


/*
*
*   Failure can be an error or a warning
*
* */
public class Failure {

    // failure type
    public final static int ERROR = 0;
    public final static int WARNING = 1;

    // global failure list
    // file names and associated failures
    private static ArrayList<String> files;
    private static ArrayList<ArrayList<Failure>> failures;

    // static initializer for global failure list
    static {
        files = new ArrayList<String>();
        failures = new ArrayList<ArrayList<Failure>>();
    }
    private static boolean hasError = false;

    // the line number, position, priority and according message of failures
    private int line;
    private int pos;
    private int prior;
    private String fMesg;

    private Failure(int line, int pos, int prior, String fMesg) {

        this.line = line;
        this.pos = pos;
        this.prior = prior;
        this.fMesg = fMesg;

    }

    public static void addFailure(String fpath, int line, int pos, int prior, String fMesg) {

        if(prior == Failure.ERROR) {
            hasError = true;
        }

        int index = files.indexOf(fpath);
        if(index != -1) {
            failures.get(index).add(new Failure(line, pos, prior, fMesg));
        } else {
            files.add(fpath);
            ArrayList<Failure> failList = new ArrayList<Failure>();
            failList.add(new Failure(line, pos, prior, fMesg));
            failures.add(failList);
        }

    }

    public static boolean canContinue() {
        return !hasError;
    }

    public static void clearFailure() {

        files.clear();
        failures.clear();

    }

    public static void reportFailure() {

        Iterator<String> fileIter = files.iterator();
        for(int i = 0; fileIter.hasNext(); i++) {
            String fpath = fileIter.next();
            ArrayList<Failure> failList = failures.get(i);

            // sort the failure list by line number, position and failure prior
            Collections.sort(failList, new Comparator<Failure>() {
                @Override
                public int compare(Failure f1, Failure f2) {
                    if(f1.line < f2.line) {
                        return -1;
                    } else if(f1.line > f2.line) {
                        return 1;
                    } else {
                        if(f1.pos < f2.pos) {
                            return -1;
                        } else if(f1.pos > f2.pos) {
                            return 1;
                        } else {
                            if(f1.prior < f2.prior) {
                                return -1;
                            } else if(f1.prior > f2.prior) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            });

            Iterator<Failure> failureIter = failList.iterator();
            while(failureIter.hasNext()) {
                Failure f = failureIter.next();
                if(f.prior == Failure.ERROR) {
                    System.out.println(fpath + ":" + f.line + ":" + f.pos + ": "
                    + "\033[31m" + "error: " + "\033[0m"
                    + f.fMesg);
                } else if(f.prior == Failure.WARNING) {
                    System.out.println(fpath + ":" + f.line + ":" + f.pos + ": "
                    + "\033[33m" + "warning: " + "\033[0m"
                    + f.fMesg);
                }
            }
        }

    }


}


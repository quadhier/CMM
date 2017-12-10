
public class Main {

    public static void main(String[] args) {

        if(args == null || args.length != 2) {
            System.out.println("Usage: java -jar cmm.jar [-tree|-compile|-viewasm|-execute|-interpret] filename");
            return;
        }
        String mode = args[0];
        String filepath = args[1];
        if(mode.equals("-tree")) {
            CMMcompiler.printTree(filepath);
        } else if(mode.equals("-compile")) {
            CMMcompiler.compile(filepath);
        } else if(mode.equals("-viewasm")){
            CMMcompiler.view(filepath);
        } else if(mode.equals("-execute")) {
            CMMcompiler.execute(filepath);
        } else if(mode.equals("-interpret")) {
            CMMcompiler.interpret(filepath);
        } else {
            System.out.println("invalid options");
        }

    }

}

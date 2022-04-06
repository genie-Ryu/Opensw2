package practice;

public class kuir {
	public static void main(String[] args) throws Exception {
		String command = args[0];   
		String path = args[1];

		if(command.equals("-c")) {
			makeCollection collection = new makeCollection(path);
			collection.makeXml();
		}
		else if(command.equals("-k")) {
			makeKeyword keyword = new makeKeyword(path);
			keyword.convertXml();
		}
		else if(command.equals("-i")) {
			indexer index = new indexer(path);
			index.invertXml();
		} else if(command.equals("-s")) {
			if(args[2].equals("-q")) {
				String query = args[3];
				searcher searcher = new searcher(path, query);
				searcher.CalcSim();
			}
		} else {
			System.out.println("잘못된 명령어입니다.");
		}
	}

}

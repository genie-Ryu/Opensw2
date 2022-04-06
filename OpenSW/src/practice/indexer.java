package practice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class indexer {
	public String path;
	// 생성자
	public indexer(String path) {
		this.path = path;
	}
	//
	public void invertXml() throws Exception {
		// 수정할 index.xml 파일 불러와서 document 객체 생성
		Document document = getXML(new File(path));
		String[] bodytextArr = getBody(document);
		HashMap<String, ArrayList> map3 = createMap(bodytextArr);
		// 최종 map
		HashMap<String, String> map_final = new HashMap<>();
		
		Iterator<String> it = map3.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			ArrayList<String> list = map3.get(key);
			String value = String.join(" ", list);
			// System.out.println(key + " → " + value);
			map_final.put(key, value);
		}
		
		
		// 파일 저장 경로 : index.post
		FileOutputStream fileStream_output = new FileOutputStream("index.post");
		// fileStream -> Object로 만들기
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream_output);
		objectOutputStream.writeObject(map_final);
		objectOutputStream.close();
		
		// (확인용) 파일에서 객체 불러와 읽어오기
		FileInputStream fileStream_input = new FileInputStream("index.post");
		ObjectInputStream objectInputStream = new ObjectInputStream(fileStream_input);
		// object 읽어오기
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		// 우선 읽어온 object 가 무슨 타입인지 확인하고
		System.out.println("읽어온 객체의 type → " + object.getClass());
		// 해당 타입에 맞게 형변환 하여 object 담기
		HashMap hashMap = (HashMap) object;
		Iterator<String> it2 = hashMap.keySet().iterator();
		// hashMap 의 value 값인 arrayList 를 합쳐서 String 으로 만들기
		
		
		while(it2.hasNext()) {
			String key = it2.next();
			String value = (String)hashMap.get(key);
			System.out.println(key + " → " + value);
		}
		
	}
	
	// 1) 파일 생성 메소드
	public static Document getXML(File file) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.parse(file);
		// 문서 구조 안정화
		document.getDocumentElement().normalize();
		return document;
	}
	
	// 2) xml 문서의 수정할 body 노드 내용 불러오기 메소드
	public static String[] getBody(Document document) throws Exception {
		// body 이름으로 생성한 태그를 list 로 가져오기
		NodeList nList = document.getElementsByTagName("body");
		// 아래 반복문에서 받아올 bodytext 변수 내용을 담는 String[] 변수
		String[] bodytextArr = new String[nList.getLength()];
		
		for(int i = 0; i < nList.getLength(); i++) {
			// 반복문을 통해 <body> 태그에 해당하는 값들을 차례로 가져와 bodytext 변수에 담음
			Node nItem = nList.item(i);
			String bodytext = nItem.getTextContent();
			// System.out.println(bodytext);
			bodytextArr[i] = bodytext;
		}
		return bodytextArr;
	}
	
	// 3) 키워드 , tf, df 뽑아와야 함
	public static HashMap<String, ArrayList> createMap(String[] bodytextArr) {
		// map1 = <키워드, df>
		HashMap<String, Integer> map1 = new HashMap<>();
		// 최종 hashMap : map3 = <키워드, list>
		HashMap<String, ArrayList> map3 = new HashMap<>(); 
		
		int df = 1;
		
		for(int i = 0; i < bodytextArr.length; i++) {
			String[] keywordTf = bodytextArr[i].split(":|#");
			for(int j = 0; j < keywordTf.length; j+=2) {
				// 다음 문서 확인할 때, 중복되는 key 값이 있으면, df++ (default = 1)
				if(map1.containsKey(keywordTf[j])) {
					map1.put(keywordTf[j], map1.get(keywordTf[j]) + 1);
				} else {
					map1.put(keywordTf[j], df);
				}
			}
		}
		// -> 여기까지 각 키워드에 대한 df 값 추출 완료

		Iterator<String> it = map1.keySet().iterator();
		
		// list 완성
		// 키워드 하나하나에 대응하는 list 생성
		while(it.hasNext()) {
			String key = it.next();
			int df_final = map1.get(key);
			// 최종 hashMap 에 담을 value list
			ArrayList<String> list = new ArrayList<>();
			
			// 5개 body 돌면서
			for(int j = 0; j < bodytextArr.length; j++) {
				// 각 body 의 <키워드, tf 값> 뽑고
				String[] keywordTf = bodytextArr[j].split(":|#");
				// map2 = <키워드, tf>
				HashMap<String, Double> map2 = new HashMap<>();
				
				for(int k = 0; k < keywordTf.length; k+=2) {
					// map2 에 담기
					map2.put(keywordTf[k], Double.parseDouble(keywordTf[k + 1]));
				}
				
				// 각 문서에서의 키워드의 가중치 계산
				double weight = 0.0;
				if(map2.containsKey(key)) {
					weight = map2.get(key) * Math.log(bodytextArr.length / df_final);					
					list.add(j + " " + String.format("%.2f", weight) + " ");
				} else {
					list.add(j + " " + weight + " ");					
				}
				
			}
			map3.put(key, list);
		}
		return map3;
	}

	
	
}


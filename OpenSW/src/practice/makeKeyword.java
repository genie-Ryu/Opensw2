package practice;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class makeKeyword {
	
	public static String path;
	// 생성자
	public makeKeyword(String path) {
		this.path = path;
	}

	public void convertXml() throws Exception {
		// 수정할 collection.xml 파일 불러와서 document 객체 생성
		Document document = getXML(new File(path));
		// getBody() 메소드에서 값을 수정하고자 하는 <Body> 태그의 값을 String[] 형태로 받아옴
		String[] bodytextArr = getBody(document);
		String[] bodyData = kkma(bodytextArr);
		makeXML(document, bodyData);
	}

	// 파일 생성 메소드
	public static Document getXML(File file) throws Exception {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.parse(file);
		// 문서 구조 안정화
		document.getDocumentElement().normalize();
		return document;
	}
	
	// xml 문서의 수정할 body 노드 내용 불러오기 메소드
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
	
	// 꼬고마 분석기 사용 메소드
	public static String[] kkma(String[] bodytextArr) throws Exception {
		// xml 파일의 body 부분 꼬고마 분석기로 수정한 값을 담을 변수
		String[] bodyData = new String[bodytextArr.length];
		// 꼬고마 분석기 이용(키워드1:빈도수#키워드2:빈도수#키워드3:빈도수#...)
		KeywordExtractor ke = new KeywordExtractor();
		
		for(int i = 0; i < bodytextArr.length; i++) {
			KeywordList kl = ke.extractKeyword(bodytextArr[i], true);
			for(int j = 0; j < kl.size(); j++) {
				Keyword kwrd = kl.get(j);
				if(bodyData[i] == null) {
					bodyData[i] = kwrd.getString() + ":" + kwrd.getCnt() + "#";
				} else {
					bodyData[i] = bodyData[i] + kwrd.getString() + ":" + kwrd.getCnt() + "#";
				}
			}
			//System.out.println(bodyData[i]);
		}
		return bodyData;
	}
	
	// 최종 index.xml 파일 생성 메소드
	public static void makeXML(Document document, String[] bodyData) throws Exception {
		// body 이름으로 생성한 태그를 list 로 가져오기
		NodeList nList = document.getElementsByTagName("body");
		for(int i = 0; i < nList.getLength(); i++) {
			Element body = (Element) nList.item(i);
			// <body> 태그의 내용을 꼬고마 분석 결과 내용으로 수정
			body.setTextContent(bodyData[i]);
		}
		
		// xml 파일 생성
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new FileOutputStream(new File("index.xml")));
		
		transformer.transform(source, result);
	}

	
}

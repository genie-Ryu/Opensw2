package practice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class makeCollection {
	static String path;
	public makeCollection() {
		
	}
	
	public makeCollection(String path) {
		this.path = path;
	}

	public void makeXml() throws Exception {
		// xml 파일 생성
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		
		Document document = makeDocument();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new FileOutputStream(new File("./collection.xml")));
		
		transformer.transform(source, result);
		//System.out.println(collection.xml);
	}
	
	// html 파일을 가져와 file[] 리스트로 반환하는 메서드
	public static File[] makeFileList(String path) {
		File dir = new File(path);
		return dir.listFiles();
	}
	
	// xml 파일 생성 메서드
	public static Document makeDocument() throws ParserConfigurationException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		
		// docs 엘리먼트
		Element docs = document.createElement("docs");
		document.appendChild(docs);
		
			
		// .html 파일 가져와서 파싱
		File files[] = makeFileList(path);
		// 반복문을 통해 .html 파일 5개의 tag 제외한 내용 가져오기
		for(int i = 0; i < files.length; i++) {
//			System.out.println(files[i]);
			org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");
			
			// doc 엘리먼트
			Element doc = document.createElement("doc");
			// doc 엘리먼트 id 속성값 부여 id = 0 ~ 4
			doc.setAttribute("id", Integer.toString(i));
			docs.appendChild(doc);
			
			// tile 엘리먼트
			Element title = document.createElement("title");
			String titleData = html.title();
			title.appendChild(document.createTextNode(titleData));
			doc.appendChild(title);
			
			// body 엘리먼트
			Element body = document.createElement("body");
			String bodyData = html.body().text();
			body.appendChild(document.createTextNode(bodyData));
			doc.appendChild(body);
			
		}
		return document;
	}

}

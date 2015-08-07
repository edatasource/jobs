package dto;

import java.util.List;
import java.util.Map;

public class ElementDto {

	private String name;
	private String id;
	private String body;
	private Map<String, String> attributesMap;
	private List<ElementDto> elements;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Map<String, String> getAttributesMap() {
		return attributesMap;
	}
	public void setAttributesMap(Map<String, String> attributesMap) {
		this.attributesMap = attributesMap;
	}
	public List<ElementDto> getElements() {
		return elements;
	}
	public void setElements(List<ElementDto> elements) {
		this.elements = elements;
	}

}

package dto;

import java.util.List;

public class DocumentDto {
	
	private String title;
	private List<ElementDto> headElements;
	private List<ElementDto> bodyElements;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ElementDto> getHeadElements() {
		return headElements;
	}

	public void setHeadElements(List<ElementDto> headElements) {
		this.headElements = headElements;
	}

	public List<ElementDto> getBodyElements() {
		return bodyElements;
	}

	public void setBodyElements(List<ElementDto> bodyElements) {
		this.bodyElements = bodyElements;
	}
	

}

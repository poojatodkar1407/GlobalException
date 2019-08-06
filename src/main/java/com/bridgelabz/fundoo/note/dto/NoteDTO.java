package com.bridgelabz.fundoo.note.dto;

import java.io.Serializable;

public class NoteDTO  implements Serializable{
	private static final long serialVersionUID = 3891749725421598901L;
	private String title;
	private String description;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "NoteDTO [title=" + title + ", description=" + description + "]";
	}

}

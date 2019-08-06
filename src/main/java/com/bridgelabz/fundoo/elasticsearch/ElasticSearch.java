package com.bridgelabz.fundoo.elasticsearch;

import java.util.List;

import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.response.Response;

public interface ElasticSearch {

	Response createNote(Note note);

	Response updateNote(Note note);

	Response deleteNote(long noteId);

	List<Note> searchData(String query, String token);

}

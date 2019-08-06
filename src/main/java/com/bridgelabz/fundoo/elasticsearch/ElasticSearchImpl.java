package com.bridgelabz.fundoo.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoo.note.model.Note;
import com.bridgelabz.fundoo.response.Response;
import com.bridgelabz.fundoo.utility.ResponseHelper;
import com.bridgelabz.fundoo.utility.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("ElasticSearch")
public class ElasticSearchImpl implements ElasticSearch {

	private final String INDEX = "boot";
	private final String TYPE = "note";

	@Autowired
	private TokenUtil userToken;

	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Environment environment;

	@Override
	public Response createNote(Note note) {
		@SuppressWarnings("unchecked")
		Map<String, Object> documentMapper = objectMapper.convertValue(note, Map.class);

		IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, String.valueOf(note.getNoteId()))
				.source(documentMapper);
		try {
			client.index(indexRequest, RequestOptions.DEFAULT);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.notes.created"));
		return response;
	}

	@Override
	public Response updateNote(Note note) {
		@SuppressWarnings("unchecked")
		Map<String, Object> documentMapper = objectMapper.convertValue(note, Map.class);

		UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, String.valueOf(note.getNoteId()))
				.doc(documentMapper);

		try {
			client.update(updateRequest, RequestOptions.DEFAULT);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Response response = ResponseHelper.statusResponse(200,
				environment.getProperty("status.notes.updatedSuccessfull"));
		return response;

	}

	@Override
	public Response deleteNote(long noteId) {
		DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, String.valueOf(noteId));
		try {
			client.delete(deleteRequest, RequestOptions.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.note.deleted"));
		return response;
	}

	@Override
	public List<Note> searchData(String query, String token) {
		long userId = userToken.decodeToken(token);
		SearchRequest searchRequest = new SearchRequest(INDEX).types(TYPE);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery("*" + query + "*")
				.analyzeWildcard(true).field("title").field("description"))
				.filter(QueryBuilders.termsQuery("userId", String.valueOf(userId)));
		System.out.println();
		searchSourceBuilder.query(queryBuilder);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = null;
		try {
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			System.out.println(searchResponse);
		} catch (IOException e) {

			e.printStackTrace();
		}

		List<Note> allnote = getSearchResult(searchResponse);

		return allnote;
	}

	private List<Note> getSearchResult(SearchResponse response) {
		SearchHit[] searchHits = response.getHits().getHits();
		List<Note> notes = new ArrayList<>();
		for (SearchHit hit : searchHits) {
			notes.add(objectMapper.convertValue(hit.getSourceAsMap(), Note.class));
		}
		System.out.println(notes);
		return notes;
	}

}
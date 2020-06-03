package com.SE.SearchEngineAPI;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import Models.Suggestion;
public interface SuggestionsRepository extends
		MongoRepository<Suggestion, String> {
	
	
	@Query("{'searchString': ?0 } , {'$inc' : {'frequency':1}} ")
	public void incFreq(String query);
	
	/*
	@Query("{ 'searchString':{$regex:?0*} , $sort : { frequency : -1 } , $limit : 7 } , { searchString: 1}")
	public List<String> matchQuery(String query);
	*/
	
	public List<Suggestion> findBySearchStringStartingWithOrderByFrequencyDesc(String queryBegin , PageRequest pageRequest);
	
}
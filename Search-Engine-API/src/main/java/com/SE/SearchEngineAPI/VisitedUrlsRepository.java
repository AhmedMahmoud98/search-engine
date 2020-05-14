package com.SE.SearchEngineAPI;

import org.springframework.data.mongodb.repository.MongoRepository;

import Models.VisitedUrl;
public interface VisitedUrlsRepository extends 
		MongoRepository<VisitedUrl, String> {
	}
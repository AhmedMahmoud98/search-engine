package com.SE.SearchEngineAPI;
import java.util.*;

import Models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class ApiController {

  @Autowired
  SuggestionsRepository suggestionsRepository;
  @Autowired
  private TrendsService trendsService;
  @Autowired
  private SuggestionsService suggestionsService;
  @Autowired
  private RankingService rankingService;
  @Autowired
  private VisitedUrlsService visitedUrlsService;
  @Autowired
  private PhraseService phraseService;
  @Autowired
  private PageGenerationService pageGenerationService;

 
  @GetMapping("/Pages")
  public ResponseEntity<List<Page>> getPages (@RequestParam String query,
		  									  @RequestParam String country,
		  									  @RequestParam String pageNumber) {
	  try {
		    long timeBefore = 0, timeAfter= 0, Time = 0;
		  	CustomQuery _query = new CustomQuery(query, country, Integer.parseInt(pageNumber));
		  	
		  	timeBefore = System.currentTimeMillis();
		    trendsService.extractTrends(_query);
		    timeAfter = System.currentTimeMillis();
		    Time = timeAfter - timeBefore;
		    System.out.println("Trends Time: " + Time + " ms");
		    
		    timeBefore = System.currentTimeMillis();
		    suggestionsService.saveSuggestion(query);
		    timeAfter = System.currentTimeMillis();
		    Time = timeAfter - timeBefore;
		    System.out.println("Suggestions Time: " + Time + " ms");

		    timeBefore = System.currentTimeMillis();
			ArrayList<String> sortedLinks = rankingService.rank(_query);
			timeAfter = System.currentTimeMillis();
		    Time = timeAfter - timeBefore;
		    System.out.println("Ranking Time: " + Time + " ms");
	
			timeBefore = System.currentTimeMillis();
			int sizeOfPage = 10;
			int fromIdx = (Integer.parseInt(pageNumber) - 1) * sizeOfPage;
		    int toIdx = Math.min(fromIdx + sizeOfPage, sortedLinks.size());
		    List<Page> Pages = new ArrayList<Page>();
		    Pages = pageGenerationService.generatPages(sortedLinks.subList(fromIdx, toIdx), query);
		  	timeAfter = System.currentTimeMillis();
		    Time = timeAfter - timeBefore;
		    System.out.println("Paging Time: " + Time + " ms");

		    if (Pages.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }

		    return new ResponseEntity<>(Pages, HttpStatus.OK);
		  } catch (Exception e) {
			System.out.println(e);
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }
  
  @GetMapping("/Images")
  public ResponseEntity<List<Image>> getImages(@RequestParam String query,
			  									@RequestParam String country,
			  									@RequestParam String pageNumber) {
	  try {
		    CustomQuery _query = new CustomQuery(query, country, Integer.valueOf(pageNumber));
		    List<Image> Images = new ArrayList<Image>();
		    
		    if (Images.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }
		    return new ResponseEntity<>(Images, HttpStatus.OK);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }

  @GetMapping("/Trends")
  public ResponseEntity<List<Trend>> getTrends (@RequestParam String country) {
	  try {
		    List<Trend> trends = new ArrayList<Trend>();
		    trends = trendsService.getTrends(country);
		    
		    if (trends.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }

		    return new ResponseEntity<>(trends, HttpStatus.OK);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }
  
  @GetMapping("/Suggestions")
  public ResponseEntity<List<String>> getSuggestions (@RequestParam String query) {
	  
	  try {
		    List<Suggestion> suggestions = new ArrayList<>();
		    suggestions = suggestionsRepository.findBySearchStringStartingWithOrderByFrequencyDesc(query.toLowerCase() , PageRequest.of(0, 7) );
		    if (suggestions.isEmpty()) {
		      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		    }

		    return new ResponseEntity<>(Suggestion.searchStrings(suggestions), HttpStatus.OK);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
  }

  @PostMapping("/VisitedUrls")
  public ResponseEntity<VisitedUrl> createVisitedUrl(@RequestParam String query,
												   @RequestParam String visitedUrl) {
	  try {
		  VisitedUrl _visitedUrl = visitedUrlsService.saveVisitedUrl(visitedUrl, query);
		  return new ResponseEntity<>(_visitedUrl, HttpStatus.CREATED);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		  }
  }
  
  @GetMapping("/Size")
  public ResponseEntity<Integer> getSize(@RequestParam String query,
										 @RequestParam String requestType) {
	  try {
		  int Result = 0;
		  /*2l klam dh het7t f while 2n HashMap["query"] == null
		   * 34an efdl mstny l7d ma 2l ranker e5ls f n3rf 2l size */
		  if(requestType.equals("Pages")) 
			  Result = 35;
		  else if(requestType.equals("Images"))
			  Result = 22;

		  return new ResponseEntity<>(Result, HttpStatus.CREATED);
		  } catch (Exception e) {
		    return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		  }
  }
}

  
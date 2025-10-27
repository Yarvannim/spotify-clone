package com.yarvannim.stream_service.repository;

import com.yarvannim.stream_service.domain.document.SongDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SongSearchRepository extends ElasticsearchRepository<SongDocument, String>{

    @Query("""
    {
      "bool": {
        "should": [
          {
            "multi_match": {
              "query": "?0",
              "type": "bool_prefix",
              "fields": ["title^10", "artistName"]
            }
          },
          {
            "multi_match": {
              "query": "?0",
              "fuzziness": "AUTO",
              "prefix_length": 1,
              "type": "best_fields",
              "fields": ["title", "artistName"],
              "boost": 0.2
            }
          }
        ],
        "minimum_should_match": 1
      }
    }
    """)
    List<SongDocument> fuzzySearch(String query);
}
